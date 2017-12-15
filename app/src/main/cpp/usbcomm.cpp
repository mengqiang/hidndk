#include <jni.h>
#include <string>
#include <android/log.h>
#include <string.h>

#include <stdlib.h>
#include <stdio.h>
#include <assert.h>

#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

//log定义
#define  LOG    "JNILOG" // 这个是自定义的LOG的TAG
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG,__VA_ARGS__) // 定义LOGD类型
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG,__VA_ARGS__) // 定义LOGI类型
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG,__VA_ARGS__) // 定义LOGW类型
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG,__VA_ARGS__) // 定义LOGE类型
#define  LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG,__VA_ARGS__) // 定义LOGF类型

using namespace std;

//发送缓冲区
static const int package_len = 512;
unsigned char sendbuf[package_len];

extern "C"
JNIEXPORT jint
JNICALL
Java_com_sdses_ndk_UsbComm_open(
        JNIEnv *env,
        jobject /* this */,
        jstring path) {
    //int len = env->GetStringLength(device);
    //LOGD("temp123中文len=%d\n",len);
    //将jstring转换成const char*指针，使用const修饰符表示其内容不可被修改
    const char* path_utf=env->GetStringUTFChars(path, NULL);
    LOGD("path_utf=%s \n",path_utf);
    jint fd = open(path_utf,O_RDWR);
    LOGD("open() fd = %d", fd);
    env->ReleaseStringUTFChars(path, path_utf);
    return fd;
}

extern "C"
JNIEXPORT jint
JNICALL
Java_com_sdses_ndk_UsbComm_close(
        JNIEnv *env,
        jobject /* this */,
        jint fd) {
    LOGD("fd=%d\n",fd);
    return close(fd);
}

extern "C"
JNIEXPORT jint
JNICALL
Java_com_sdses_ndk_UsbComm_read(
        JNIEnv *env,
        jobject /* this */,
        jint fd,
        jbyteArray buf,
        jint len) {
    jsize count = env->GetArrayLength(buf);
    if(count<len)
        return -1;
    jbyte * bytes = env->GetByteArrayElements(buf,JNI_FALSE);
    jint back = read(fd,bytes,len);
    env->ReleaseByteArrayElements(buf,bytes,JNI_FALSE);
    return back;
}


extern "C"
JNIEXPORT jint
JNICALL
Java_com_sdses_ndk_UsbComm_write(
        JNIEnv *env,
        jobject /* this */,
        jint fd,
        jbyteArray buf,
        jint len) {
    //LOGD("in Java_com_sdses_ndk_MainActivity_write()");
    jsize count = env->GetArrayLength(buf);
    if(count<len)
        return -1;
    jbyte * bytes = env->GetByteArrayElements(buf,JNI_FALSE);
    //LOGD("buf实际长度=%d",count);
    //LOGD("buf需要发送长度=%d\n",len);

    int flag = 0;
    int packageLen = 0;//实际一包发送数据长度
    while(flag<len){
        //初始化发送缓冲区
        memset(sendbuf,0x00,package_len);
        packageLen = (flag+package_len)<len?package_len:len-flag;
        memcpy(sendbuf,&bytes[flag],packageLen);
        write(fd,sendbuf,packageLen);
        flag+=packageLen;
    }
    env->ReleaseByteArrayElements(buf,bytes,JNI_FALSE);
    return flag;
}