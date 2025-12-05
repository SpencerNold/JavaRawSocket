#include <jni.h>

#include "system/system.hpp"
#include "upcap/upcap.hpp"

extern "C" JNIEXPORT jint JNICALL
Java_me_spencernold_jrs_SystemBinding_getIPv4Address(JNIEnv* env, jclass clazz) {
    try {
        std::string interface = upcap::getDefaultDevice();
        std::pair<std::vector<uint8_t>, uint32_t> addresses = sys::getSystemAddresses(interface);
        return addresses.second;
    } catch (const std::exception& e) {
        jclass exc = env->FindClass("java/io/IOException");
        env->ThrowNew(exc, e.what());
        return 0;
    }
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_me_spencernold_jrs_SystemBinding_getMacAddress(JNIEnv* env, jclass clazz) {
    try {
        std::string interface = upcap::getDefaultDevice();
        std::pair<std::vector<uint8_t>, uint32_t> addresses = sys::getSystemAddresses(interface);
        std::vector<uint8_t> mac = addresses.first;
        const int length = 6;
        jbyteArray array = env->NewByteArray((jsize) length);
        env->SetByteArrayRegion(array, 0, length, (const jbyte*) mac.data());
        return array;
    } catch (const std::exception& e) {
        jclass exc = env->FindClass("java/io/IOException");
        env->ThrowNew(exc, e.what());
        return nullptr;
    }
}

extern "C" JNIEXPORT jint JNICALL
Java_me_spencernold_jrs_SystemBinding_getDefaultGateway(JNIEnv* env, jclass clazz) {
    try {
        return sys::getDefaultGateway();
    } catch (const std::exception& e) {
        jclass exc = env->FindClass("java/io/IOException");
        env->ThrowNew(exc, e.what());
        return 0;
    }
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_me_spencernold_jrs_SystemBinding_getDeviceMacAddress(JNIEnv* env, jclass clazz, jint ipv4) {
    try {
        std::vector<uint8_t> mac = sys::getRouterMacAddress((uint32_t) ipv4);
        const int length = 6;
        jbyteArray array = env->NewByteArray((jsize) length);
        env->SetByteArrayRegion(array, 0, length, (const jbyte*) mac.data());
        return array;
    } catch (const std::exception& e) {
        jclass exc = env->FindClass("java/io/IOException");
        env->ThrowNew(exc, e.what());
        return nullptr;
    }
}
