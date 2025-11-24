#include <jni.h>
#include <string>

// JNI method naming convention: Java_[package]_[ClassName]_[MethodName]
extern "C" JNIEXPORT jstring JNICALL
Java_me_spencernold_jrs_Main_helloFromCpp(JNIEnv* env, jclass clazz) {
    std::string msg = "Hello from C++ binding!";
    return env->NewStringUTF(msg.c_str());
}