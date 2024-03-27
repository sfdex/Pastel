#include <jni.h>
#include <string>
#include "include/tun2socks_rust.h"

extern "C" JNIEXPORT void JNICALL
Java_com_sfdex_tun2socks_Tun2Socks_main(JNIEnv *env, jobject thiz, jint fd, jstring log_path) {
    tun2socks(fd, env->GetStringUTFChars(log_path, NULL));
}

