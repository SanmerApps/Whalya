use tls_pki_types::{
    DerObject, Pkcs8Parameters, PrivateKeyDer, PrivatePkcs1KeyDer, PrivatePkcs8KeyDer,
    PrivateSec1KeyDer,
};
use typed_jni::core::JNIEnv;
use typed_jni::{
    define_java_class, Array, TrampolineClass, TrampolineObject, TypedCallExt, TypedClassExt,
    TypedPrimitiveArrayExt, TypedRef, TypedStringExt,
};

define_java_class!(JRuntimeException, "java/lang/RuntimeException");
define_java_class!(JPkiTypes, "dev/sanmer/pki/PkiTypes");
define_java_class!(
    JPrivatePkcs8KeyDer,
    "dev/sanmer/pki/PkiTypes$PrivatePkcs8KeyDer"
);

fn jni_throw(env: &JNIEnv, message: &str) {
    let message = env.typed_new_string(message);
    let re_cls = env.typed_find_class::<JRuntimeException>().unwrap();
    let re = env.typed_new_object(&re_cls, (message,)).unwrap();
    unsafe { env.throw(&re.into_ref()) }
}

macro_rules! jni_throw {
    ($env:expr, $block:expr) => {
        match $block {
            Ok(v) => v,
            Err(e) => {
                jni_throw($env, &format!("{e:?}"));
                return Default::default();
            }
        }
    };
}

macro_rules! bytes_array {
    ($env:expr, $buf:expr) => {{
        let array = $env
            .typed_new_primitive_array::<i8>($buf.len() as _)
            .unwrap();
        let mut elements = $env.typed_get_bytes_array_elements(&array).unwrap();
        elements.copy_from_slice($buf);
        elements.commit();
        array
    }};
}

#[no_mangle]
pub extern "C" fn Java_dev_sanmer_pki_PkiTypes_loadToPkcs8<'env>(
    env: &'env JNIEnv,
    _class: TrampolineClass<'env, JPkiTypes>,
    pem: TrampolineObject<'env, Array<i8>>,
) -> Option<TrampolineObject<'env, JPrivatePkcs8KeyDer>> {
    let pem = env.typed_get_bytes_array_elements(&pem).unwrap();
    let pkcs8 = match jni_throw!(env, PrivateKeyDer::from_pem_slice(&pem)) {
        PrivateKeyDer::Pkcs8(pk) => pk,
        PrivateKeyDer::Sec1(pk) => jni_throw!(env, PrivatePkcs8KeyDer::try_from(&pk)),
        PrivateKeyDer::Pkcs1(pk) => jni_throw!(env, PrivatePkcs8KeyDer::try_from(&pk)),
    };

    let der = pkcs8.der_encoded().unwrap();
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

    let cls = env.typed_find_class::<JPrivatePkcs8KeyDer>().unwrap();
    let obj = env.typed_new_object(&cls, (&encoded, &algorithm)).unwrap();
    Some(obj.into_trampoline())
}

macro_rules! converter {
    ($name:ident, $input:ty, $output:ty) => {
        #[no_mangle]
        pub extern "C" fn $name<'env>(
            env: &'env JNIEnv,
            _class: TrampolineClass<'env, JPkiTypes>,
            der: TrampolineObject<'env, Array<i8>>,
        ) -> Option<TrampolineObject<'env, Array<i8>>> {
            let der = env.typed_get_bytes_array_elements(&der).unwrap();
            let input = jni_throw!(env, <$input>::from_der_slice(&der));
            let output = jni_throw!(env, <$output>::try_from(&input));

            let der = output.der_encoded().unwrap();
            let array = bytes_array!(env, &der);
            Some(array.into_trampoline())
        }
    };
}

converter!(
    Java_dev_sanmer_pki_PkiTypes_pkcs8ToSec1,
    PrivatePkcs8KeyDer,
    PrivateSec1KeyDer
);

converter!(
    Java_dev_sanmer_pki_PkiTypes_sec1ToPkcs8,
    PrivateSec1KeyDer,
    PrivatePkcs8KeyDer
);

converter!(
    Java_dev_sanmer_pki_PkiTypes_pkcs8ToPkcs1,
    PrivatePkcs8KeyDer,
    PrivatePkcs1KeyDer
);

converter!(
    Java_dev_sanmer_pki_PkiTypes_pkcs1ToPkcs8,
    PrivatePkcs1KeyDer,
    PrivatePkcs8KeyDer
);
