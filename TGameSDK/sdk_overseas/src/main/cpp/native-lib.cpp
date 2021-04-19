#include <jni.h>
#include <string>
#include <stdlib.h>
#include "MD5.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_tobin_tgame_sdk_common_getEncodeKey(JNIEnv *env, jobject /* this */) {
    std::string encodekey = "abcdefg123456";
    return env->NewStringUTF(encodekey.c_str());
}

JNIEXPORT jstring JNICALL
Java_com_tobin_tgame_sdk_common_getMd5(JNIEnv *env, jobject, jstring str) {

    const char *originStr;
    //将jstring转化成char *类型
    originStr = env->GetStringUTFChars(str, false);

    //c -> jstring
    char *encodekey = "abcdefg123456";

    // 向系统申请分配指定size(1024*1024)个字节的内存空间
    char* dest = (char*) malloc(1024*1024);

    strcat(dest, originStr);
    strcat(dest, encodekey);
    char *result = dest;
    free(dest);
    MD5 md5 = MD5(result);
    std::string md5Result = md5.hexdigest();
    //将char *类型转化成jstring返回给Java层
    return env->NewStringUTF(md5Result.c_str());
}