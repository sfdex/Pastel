#include <jni.h>
#include <string>
#include "include/tun2socks_rust.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_sfdex_tun2socks_Tun2Socks_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT void JNICALL
Java_com_sfdex_tun2socks_Tun2Socks_main(JNIEnv *env, jobject thiz, jint fd, jstring log_path) {
    tun2socks(fd, env->GetStringUTFChars(log_path, NULL));
}

/*
extern "C" JNIEXPORT jint JNICALL
Java_com_sfdex_tun2socks_Tun2Socks_testNum(JNIEnv *env, jobject thiz, jint fd) {
    return test_num(fd);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_sfdex_tun2socks_Tun2Socks_testStr(JNIEnv *env, jobject thiz, jstring log_path) {
    const char *result = test_cstr(env->GetStringUTFChars(log_path, NULL));
    return env->NewStringUTF(result);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_sfdex_tun2socks_Tun2Socks_testStrWithLen(JNIEnv *env, jobject thiz, jstring str,
                                                  jint len) {
    const char *result = test_cstr_with_len(env->GetStringUTFChars(str, NULL), len);
    return env->NewStringUTF(result);
}*/
