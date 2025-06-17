#!/usr/bin/env sh

LIB_NAME='pki_types'

build() {
    OUTPUT_DIR=../libs/$1
    TARGET=$2

    cargo build --profile release --target $TARGET
    mkdir -p $OUTPUT_DIR
    cp target/$TARGET/release/lib${LIB_NAME}.so $OUTPUT_DIR/
}

build "arm64-v8a" "aarch64-linux-android"
build "x86_64" "x86_64-linux-android"