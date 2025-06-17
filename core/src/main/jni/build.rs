fn main() {
    println!("cargo:rustc-cdylib-link-arg=-Wl,--build-id");
}
