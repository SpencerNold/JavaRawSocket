#include <jni.h>

#include "upcap/upcap.hpp"

#include <iostream>

JavaVM* globalJvm = nullptr;
jobject globalListener = nullptr;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    globalJvm = vm;
    return JNI_VERSION_1_6;
}

// JNI method naming convention: Java_[package]_[ClassName]_[MethodName]
extern "C" JNIEXPORT jstring JNICALL
Java_me_spencernold_jrs_PacketCaptureBinding_getDefaultDevice(JNIEnv* env, jclass clazz) {
    try {
        std::string device = upcap::getDefaultDevice();
        if (device.empty()) 
            return nullptr;
        return env->NewStringUTF(device.c_str());
    } catch (const std::exception& e) {
        jclass exc = env->FindClass("java/io/IOException");
        env->ThrowNew(exc, e.what());
        return nullptr;
    }
}

extern "C" JNIEXPORT jlong JNICALL
Java_me_spencernold_jrs_PacketCaptureBinding_open(JNIEnv* env, jclass clazz, jstring device) {
    try {
        const char* name = env->GetStringUTFChars(device, NULL);
        std::string interface(name);
        upcap::Handle* handle = upcap::open(interface);
        if (handle == nullptr)
            return 0;
        return (long) handle;
    } catch (const std::exception& e) {
        jclass exc = env->FindClass("java/io/IOException");
        env->ThrowNew(exc, e.what());
        return 0;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_me_spencernold_jrs_PacketCaptureBinding_send(JNIEnv* env, jclass clazz, jlong address, jbyteArray data, jint offset, jint length) {
    upcap::Handle* handle = (upcap::Handle*) address;
    jboolean isCopy;
    jbyte* bytes = env->GetByteArrayElements(data, &isCopy);
    if (!bytes)
        return;
    jbyte* start = bytes + offset;
    upcap::send(handle, (unsigned char*) start, length);
    env->ReleaseByteArrayElements(data, bytes, 0);
}

void listener_function(upcap::Handle* handle, const unsigned char* data, int length) {
    if (!globalJvm || !globalListener)
        return;
    JNIEnv* env = nullptr;
    jint res = globalJvm->GetEnv((void**) &env, JNI_VERSION_1_6);
    if (res != JNI_OK)
        return;
    jclass listenerClass = env->GetObjectClass(globalListener);
    jmethodID listenMethod = env->GetMethodID(listenerClass, "listen", "(J[BI)V");
    if (!listenMethod)
        return;
    jbyteArray array = env->NewByteArray((jsize) length);
    env->SetByteArrayRegion(array, 0, length, (const jbyte*) data);
    env->CallVoidMethod(globalListener, listenMethod, (jlong) handle, array, length);
    env->DeleteLocalRef(array);
}

extern "C" JNIEXPORT void JNICALL
Java_me_spencernold_jrs_PacketCaptureBinding_listen(JNIEnv* env, jclass clazz, jlong address, jobject listener) {
    upcap::Handle* handle = (upcap::Handle*) address;
    if (globalListener)
        env->DeleteGlobalRef(globalListener);
    globalListener = env->NewGlobalRef(listener);
    upcap::listen(handle, listener_function);
}

extern "C" JNIEXPORT void JNICALL
Java_me_spencernold_jrs_PacketCaptureBinding_ignore(JNIEnv* env, jclass clazz, jlong address) {
    upcap::Handle* handle = (upcap::Handle*) address;
    upcap::ignore(handle);
}

extern "C" JNIEXPORT void JNICALL
Java_me_spencernold_jrs_PacketCaptureBinding_close(JNIEnv* env, jclass clazz, jlong address) {
    if (globalListener) {
        env->DeleteGlobalRef(globalListener);
        globalListener = nullptr;
    }
    upcap::Handle* handle = (upcap::Handle*) address;
    upcap::close(handle);
}