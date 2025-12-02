#include <jni.h>

#include "upcap.hpp"

// JNI method naming convention: Java_[package]_[ClassName]_[MethodName]
extern "C" JNIEXPORT jstring JNICALL
Java_me_spencernold_jrs_PacketCaptureBinding_getDefaultDevice(JNIEnv* env, jclass clazz) {
    try {
        std::string device = upcap::getDefaultDevice();
        if (device.empty()) 
            return nullptr;
        return env->NewStringUTF(device.c_str());
    } catch (const std::exception& e) {
        jclass exc = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(exc, e.what());
        return nullptr;
    }
}