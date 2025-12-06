#include <jni.h>

#include "checksum/checksum.hpp"
#include "system/system.hpp"

extern "C" JNIEXPORT jshort JNICALL
Java_me_spencernold_jrs_ChecksumBinding_calculateIPv4Checksum(JNIEnv* env, jclass clazz, jbyteArray data, jint offset, jint length) {
    jboolean isCopy;
    jbyte* bytes = env->GetByteArrayElements(data, &isCopy);
    if (!bytes)
        return 0;
    uint32_t sum = 0;
    ipsum::update(&sum, (uint16_t*) bytes, (int) offset, (int) length);
    uint16_t checksum = (uint16_t) ipsum::finish(&sum);
    return sys::toNetworkOrder(checksum);
}