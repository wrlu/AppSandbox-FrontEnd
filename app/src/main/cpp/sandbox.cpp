#include <jni.h>
#include <string>
#include <sys/system_properties.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_wrlus_app_sandbox_config_PropertyManager_get(JNIEnv *env, jclass clazz, jstring key) {
    const char *c_key = env->GetStringUTFChars(key, 0);
    char value[PROP_VALUE_MAX];
    __system_property_get(c_key, value);
    return env->NewStringUTF(value);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wrlus_app_sandbox_config_PropertyManager_set(JNIEnv *env, jclass clazz, jstring key,
                                                      jstring value) {
    const char *c_key = env->GetStringUTFChars(key, 0);
    const char *c_value = env->GetStringUTFChars(value, 0);
    __system_property_set(c_key, c_value);
}