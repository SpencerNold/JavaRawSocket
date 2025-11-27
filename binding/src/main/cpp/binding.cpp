#include <jni.h>
#include <string>
#include <iostream>

#include "tun.hpp"

// JNI method naming convention: Java_[package]_[ClassName]_[MethodName]
extern "C" JNIEXPORT jint JNICALL
Java_me_spencernold_jrs_Binding_tun_1create(JNIEnv* env, jclass clazz) {
    return tun::create();
}

extern "C" JNIEXPORT void JNICALL
Java_me_spencernold_jrs_Binding_tun_1free(JNIEnv* env, jclass clazz, jint fd) {
    return tun::free(fd);
}