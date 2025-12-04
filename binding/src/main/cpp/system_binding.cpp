#include <jni.h>

#include "system/system.hpp"
#include "upcap/upcap.hpp"

extern "C" JNIEXPORT jint JNICALL
Java_me_spencernold_jrs_SystemBinding_getIPv4Address(JNIEnv* env, jclass clazz) {
    std::string interface = upcap::getDefaultDevice();
    std::pair<std::vector<uint8_t>, uint32_t> addresses = sys::getSystemAddresses(interface);
    return addresses.second;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_me_spencernold_jrs_SystemBinding_getMacAddress(JNIEnv* env, jclass clazz) {
    std::string interface = upcap::getDefaultDevice();
    std::pair<std::vector<uint8_t>, uint32_t> addresses = sys::getSystemAddresses(interface);
    std::vector<uint8_t> mac = addresses.first;
    const int length = 6;
    jbyteArray array = env->NewByteArray((jsize) length);
    env->SetByteArrayRegion(array, 0, length, (const jbyte*) mac.data());
    return array;
}

