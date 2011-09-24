#include <string.h>
#include <jni.h>

jstring
Java_com_adamcarruthers_foundry_APTActivity_lawlJNI (JNIEnv *env, jobject thiz) {
	return (*env)->NewStringUTF(env, "Whuddup, Foundry?");
}