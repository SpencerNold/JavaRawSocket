#include <iostream>

#include <jni.h>

// JNI method naming convention: Java_[package]_[ClassName]_[MethodName]
extern "C" JNIEXPORT jint JNICALL
Java_me_spencernold_jrs_Binding_tun_1create(JNIEnv* env, jclass clazz) {
    std::cout << "Create" << std::endl;
    return 0;
}

extern "C" JNIEXPORT void JNICALL
Java_me_spencernold_jrs_Binding_tun_1bind(JNIEnv* env, jclass clazz, jint fd) {
    std::cout << "Bind" << std::endl;
}

extern "C" JNIEXPORT void JNICALL
Java_me_spencernold_jrs_Binding_tun_1listen(JNIEnv* env, jclass clazz, jint fd) {
    std::cout << "Listen" << std::endl;
}

extern "C" JNIEXPORT void JNICALL
Java_me_spencernold_jrs_Binding_tun_1free(JNIEnv* env, jclass clazz, jint fd) {
    std::cout << "Free" << std::endl;
}