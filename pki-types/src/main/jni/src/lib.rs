use tls_pki_types::{DerObject, Pkcs8Parameters, PrivateKeyDer, PrivatePkcs8KeyDer};
use typed_jni::core::JNIEnv;
use typed_jni::{
    Array, TrampolineClass, TrampolineObject, TypedCallExt, TypedClassExt, TypedPrimitiveArrayExt,
    TypedRef, TypedStringExt, TypedThrowableExt, define_java_class,
};

define_java_class!(JRuntimeException, "java/lang/RuntimeException");
define_java_class!(JPkiTypes, "dev/sanmer/pki/PkiTypes");
define_java_class!(
    JPrivatePkcs8KeyDer,
    "dev/sanmer/pki/PkiTypes$PrivatePkcs8KeyDer"
);

macro_rules! call {
    ($env:expr, $ret:expr) => {
        match $ret {
            Ok(r) => r,
            Err(e) => {
                $env.typed_throw(&e);
                return Default::default();
            }
        }
    };
}

fn jni_throw<S: AsRef<str>>(env: &JNIEnv, message: S) {
    let message = env.typed_new_string(message);
    let cls = call!(env, env.typed_find_class::<JRuntimeException>());
    let obj = call!(env, env.typed_new_object(&cls, (message,)));
    unsafe { env.throw(&obj.into_ref()) }
}

macro_rules! jni_throw {
    ($env:expr, $block:expr) => {
        match $block {
            Ok(v) => v,
            Err(e) => {
                jni_throw($env, format!("{e:?}"));
                return Default::default();
            }
        }
    };
}

macro_rules! bytes_array {
    ($env:expr, $buf:expr) => {{
        let array = call!($env, $env.typed_new_primitive_array::<i8>($buf.len() as _));
        let mut elements = call!($env, $env.typed_get_bytes_array_elements(&array));
        elements.copy_from_slice($buf);
        elements.commit();
        array
    }};
}

#[unsafe(export_name = "Java_dev_sanmer_pki_PkiTypes_loadToPkcs8")]
pub extern "C" fn load_to_pkcs8<'env>(
    env: &'env JNIEnv,
    _class: TrampolineClass<'env, JPkiTypes>,
    pem: TrampolineObject<'env, Array<i8>>,
) -> Option<TrampolineObject<'env, JPrivatePkcs8KeyDer>> {
    let pem = call!(env, env.typed_get_bytes_array_elements(&pem));
    let pkcs8 = match jni_throw!(env, PrivateKeyDer::from_pem_slice(&pem)) {
        PrivateKeyDer::Pkcs8(pk) => pk,
        PrivateKeyDer::Sec1(pk) => jni_throw!(env, PrivatePkcs8KeyDer::try_from(pk)),
        PrivateKeyDer::Pkcs1(pk) => jni_throw!(env, PrivatePkcs8KeyDer::try_from(pk)),
    };
    let der = jni_throw!(env, pkcs8.der_encoded());
    let encoded = bytes_array!(env, &der);
    let algorithm = if let Some(parameters) = pkcs8.algorithm.parameters {
        match parameters {
            Pkcs8Parameters::Ec(_) => "EC",
            Pkcs8Parameters::Rsa(_) => "RSA",
            Pkcs8Parameters::Unknown(_) => "",
        }
    } else {
        ""
    };
    let algorithm = env.typed_new_string(algorithm);
    let cls = call!(env, env.typed_find_class::<JPrivatePkcs8KeyDer>());
    let obj = call!(env, env.typed_new_object(&cls, (&encoded, &algorithm)));
    Some(obj.into_trampoline())
}
