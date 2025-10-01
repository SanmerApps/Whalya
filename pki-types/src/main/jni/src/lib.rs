use tls_pki_types::{
    DerObject, Pkcs8Parameters, PrivateKeyDer, PrivatePkcs1KeyDer, PrivatePkcs8KeyDer,
    PrivateSec1KeyDer,
};
use typed_jni::sys::{jint, JavaVM, JNI_VERSION_1_6};
use typed_jni::{
    define_java_class, Array, AsRaw, Context, JString, LocalClass, LocalObject, TrampolineClass,
    TrampolineObject,
};

define_java_class!(JRuntimeException, "java/lang/RuntimeException");
define_java_class!(JPkiTypes, "dev/sanmer/pki/PkiTypes");
define_java_class!(JPrivatePkcs8KeyDer, "dev/sanmer/pki/PkiTypes$PrivatePkcs8KeyDer");

fn jni_throw(ctx: &Context, message: &str) {
    let message = LocalObject::<JString>::new_string(ctx, message);
    let runtime_exception = LocalClass::<JRuntimeException>::find_class(ctx).unwrap();
    let runtime_exception = runtime_exception.new_object(ctx, &message).unwrap();
    unsafe {
        ctx.throw(runtime_exception.as_raw());
    }
}

macro_rules! jni_throw {
    ($ctx:expr, $block:expr) => {
        match $block {
            Ok(v) => v,
            Err(e) => {
                jni_throw($ctx, &format!("{e:?}"));
                return Default::default();
            }
        }
    };
}

macro_rules! bytes_array {
    ($ctx:expr, $buf:expr) => {{
        let array = LocalObject::<Array<i8>>::new_primitive($ctx, $buf.len() as _).unwrap();
        let mut elements = array.get_bytes_elements($ctx);
        elements.copy_from_slice($buf);
        elements.commit();
        array
    }};
}

#[no_mangle]
pub extern "C" fn JNI_OnLoad(vm: *mut JavaVM, _: *const ()) -> jint {
    typed_jni::attach_vm(vm);
    JNI_VERSION_1_6 as _
}

#[no_mangle]
pub extern "C" fn Java_dev_sanmer_pki_PkiTypes_loadToPkcs8<'ctx>(
    ctx: &'ctx Context,
    _class: TrampolineClass<'ctx, JPkiTypes>,
    pem: TrampolineObject<'ctx, Array<i8>>,
) -> Option<LocalObject<'ctx, JPrivatePkcs8KeyDer>> {
    let pem = pem.get_bytes_elements(ctx);
    let pkcs8 = match jni_throw!(ctx, PrivateKeyDer::from_pem_slice(&pem)) {
        PrivateKeyDer::Pkcs8(pk) => pk,
        PrivateKeyDer::Sec1(pk) => jni_throw!(ctx, PrivatePkcs8KeyDer::try_from(&pk)),
        PrivateKeyDer::Pkcs1(pk) => jni_throw!(ctx, PrivatePkcs8KeyDer::try_from(&pk)),
    };

    let der = pkcs8.der_encoded().unwrap();
    let encoded = bytes_array!(ctx, &der);

    let algorithm = if let Some(parameters) = pkcs8.algorithm.parameters {
        match parameters {
            Pkcs8Parameters::Ec(_) => "EC",
            Pkcs8Parameters::Rsa(_) => "RSA",
            Pkcs8Parameters::Unknown(_) => "",
        }
    } else {
        ""
    };
    let algorithm = LocalObject::<JString>::new_string(ctx, algorithm);

    let key = LocalClass::<JPrivatePkcs8KeyDer>::find_class(ctx).unwrap();
    Some(key.new_object(ctx, (&encoded, &algorithm)).unwrap())
}

macro_rules! converter {
    ($name:ident, $input:ty, $output:ty) => {
        #[no_mangle]
        pub extern "C" fn $name<'ctx>(
            ctx: &'ctx Context,
            _class: TrampolineClass<'ctx, JPkiTypes>,
            der: TrampolineObject<'ctx, Array<i8>>,
        ) -> Option<LocalObject<'ctx, Array<i8>>> {
            let der = der.get_bytes_elements(ctx);
            let input = jni_throw!(ctx, <$input>::from_der_slice(&der));
            let output = jni_throw!(ctx, <$output>::try_from(&input));

            let der = output.der_encoded().unwrap();
            Some(bytes_array!(ctx, &der))
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
