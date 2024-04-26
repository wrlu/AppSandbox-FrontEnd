#include <jni.h>
#include <string>
#include <unistd.h>
#include <sys/system_properties.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/un.h>
#include <android/log.h>

#define TAG "sandbox_native"
#define MAX_HEADER_LEN 500
#define FILE_RW_BUF_SIZE 10240
#define BINDER_DATA_RECEIVE_BUF_LEN 1024 * 1024
#define PROPERTY_WATCHED_UID_DEX "sandbox.dex.watched.uid"
#define PROPERTY_WATCHED_UID_BINDER "sandbox.binder.watched.uid"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_wrlus_app_sandbox_config_PropertyManager_get(JNIEnv *env, jclass clazz, jstring key) {
    const char *c_key = env->GetStringUTFChars(key, 0);
    char value[PROP_VALUE_MAX];
    __system_property_get(c_key, value);
    env->ReleaseStringUTFChars(key, c_key);
    return env->NewStringUTF(value);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wrlus_app_sandbox_config_PropertyManager_set(JNIEnv *env, jclass clazz, jstring key,
                                                      jstring value) {
    const char *c_key = env->GetStringUTFChars(key, 0);
    const char *c_value = env->GetStringUTFChars(value, 0);
    __system_property_set(c_key, c_value);
    env->ReleaseStringUTFChars(key, c_key);
    env->ReleaseStringUTFChars(value, c_value);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_wrlus_app_sandbox_service_BaseHookService_listenNative(JNIEnv *env, jclass clz,
                                                                      jstring local_socket_name) {
    const char *ls_name = env->GetStringUTFChars(local_socket_name, nullptr);
    jsize ls_name_len = env->GetStringUTFLength(local_socket_name);

    char* ls_name_with_prefix = static_cast<char *>(malloc(ls_name_len + 2));
    snprintf(ls_name_with_prefix, ls_name_len + 2, "#%s", ls_name);

    struct sockaddr_un server_address;
    memset(&server_address, 0, sizeof(server_address));
    server_address.sun_family = AF_UNIX;
    strcpy(server_address.sun_path, ls_name_with_prefix);
    server_address.sun_path[0] = 0;

    unlink(ls_name_with_prefix);
    free(ls_name_with_prefix);

    int server_fd;
    if((server_fd = socket(AF_UNIX, SOCK_STREAM|SOCK_CLOEXEC, 0)) >= 0) {
        if (bind(server_fd, (struct sockaddr*)&server_address,
                 offsetof(struct sockaddr_un, sun_path) + 1 + ls_name_len) == 0) {
            if (listen(server_fd, 5) == 0) {
                return server_fd;
            }
        }
        close(server_fd);
    }
    return -1;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_wrlus_app_sandbox_service_BaseHookService_acceptNative(JNIEnv *env, jclass clz,
                                                                jint server_fd) {
    if (server_fd < 0) return -1;

    struct sockaddr_un client_address;
    socklen_t client_address_len = sizeof(client_address);

    int client_fd = accept(server_fd, (struct sockaddr*) &client_address,
            &client_address_len);
    return client_fd;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wrlus_app_sandbox_service_BaseHookService_closeFdNative(JNIEnv *env, jclass clz,
                                                                 jint fd) {
    if (fd >= 0) close(fd);
}

int readLine(int fd, char *buf, int len) {
    if (fd < 0 || buf == nullptr || len < 0) return -1;
    char c[1];
    int i = 0;
    while (recv(fd, c, 1, MSG_WAITALL) > 0) {
        if (c[0] != '\n' && i < len - 1) {
            buf[i] = c[0];
            i++;
        } else {
            buf[i] = '\0';
            break;
        }
    }

    return i;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wrlus_app_sandbox_entity_BaseData_openStreamNative(JNIEnv *env, jclass clazz,
                                                            jint client_fd, jobject base_data) {
    char buf[MAX_HEADER_LEN];
    if (readLine(client_fd, buf, sizeof(buf)) > 0) {
        int uid = atoi(buf);
        jmethodID setUidMethod = env->GetMethodID(clazz, "setUid", "(I)V");
        env->CallVoidMethod(base_data, setUidMethod, uid);
    }
    if (readLine(client_fd, buf, sizeof(buf)) > 0) {
        int pid = atoi(buf);
        jmethodID setPidMethod = env->GetMethodID(clazz, "setPid", "(I)V");
        env->CallVoidMethod(base_data, setPidMethod, pid);
    }
    if (readLine(client_fd, buf, sizeof(buf)) > 0) {
        long timestamp = atoi(buf);
        jmethodID setTimestampMethod = env->GetMethodID(clazz, "setTimestamp", "(J)V");
        env->CallVoidMethod(base_data, setTimestampMethod, static_cast<jlong>(timestamp));
    }
}

bool checkWatchedUid(JNIEnv *env, jclass clazz, jobject base_data, const char *property_name) {
    char watchedUidStr[PROP_VALUE_MAX];
    __system_property_get(property_name, watchedUidStr);
    int watchedUid = atoi(watchedUidStr);

    jmethodID getUidMethod = env->GetMethodID(clazz, "getUid", "()I");
    int uid = env->CallIntMethod(base_data, getUidMethod);
    return watchedUid == uid;
}

jobject newDexFileData(JNIEnv *env) {
    jclass cls = env->FindClass("com/wrlus/app/sandbox/entity/DexFileData");
    jmethodID constructorID = env->GetMethodID(cls, "<init>", "()V");
    return env->NewObject(cls, constructorID);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_wrlus_app_sandbox_entity_DexFileData_openStreamNative(JNIEnv *env, jclass clazz,
                                                               jint client_fd,
                                                               jstring dex_save_file) {
    if (client_fd < 0) return nullptr;

    jobject dexFileData = newDexFileData(env);
    if (dexFileData == nullptr) {
        return nullptr;
    }

    Java_com_wrlus_app_sandbox_entity_BaseData_openStreamNative(env, clazz, client_fd,
                                                                dexFileData);
    if (!checkWatchedUid(env, clazz, dexFileData,
                         PROPERTY_WATCHED_UID_DEX)) {
        return nullptr;
    }

    jmethodID setDexSaveFileMethod = env->GetMethodID(clazz, "setDexSaveFile",
                                                    "(Ljava/lang/String;)V");
    env->CallVoidMethod(dexFileData, setDexSaveFileMethod, dex_save_file);

    char buf[MAX_HEADER_LEN];
    if (readLine(client_fd, buf, sizeof(buf)) > 0) {
        jmethodID setOriginDexPathMethod = env->GetMethodID(clazz, "setOriginDexPath",
                                                            "(Ljava/lang/String;)V");
        jstring originDexPath = env->NewStringUTF(buf);
        env->CallVoidMethod(dexFileData, setOriginDexPathMethod, originDexPath);
    }

    const char *c_dex_file_path = env->GetStringUTFChars(dex_save_file, 0);

    FILE *dexFileFd = fopen(c_dex_file_path, "w");
    if (dexFileFd == nullptr) {
        return nullptr;
    }

    char fileBuf[FILE_RW_BUF_SIZE];
    int recvLen = 0;
    while ((recvLen = recv(client_fd, fileBuf,
                           FILE_RW_BUF_SIZE, MSG_WAITALL)) > 0) {
        fwrite(fileBuf, sizeof(char), recvLen, dexFileFd);
    }
    fclose(dexFileFd);

    return dexFileData;
}

jobject newBinderData(JNIEnv *env) {
    jclass cls = env->FindClass("com/wrlus/app/sandbox/entity/BinderData");
    jmethodID constructorID = env->GetMethodID(cls, "<init>", "()V");
    return env->NewObject(cls, constructorID);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_wrlus_app_sandbox_entity_BinderData_openStreamNative(JNIEnv *env, jclass clazz,
                                                              jint client_fd) {
    if (client_fd < 0) return nullptr;

    jobject binderData = newBinderData(env);
    if (binderData == nullptr) {
        return nullptr;
    }

    Java_com_wrlus_app_sandbox_entity_BaseData_openStreamNative(env, clazz, client_fd,
                                                                binderData);
    if (!checkWatchedUid(env, clazz, binderData,
                         PROPERTY_WATCHED_UID_BINDER)) {
        return nullptr;
    }

    char buf[MAX_HEADER_LEN];
    if (readLine(client_fd, buf, sizeof(buf)) > 0) {
        int code = atoi(buf);
        jmethodID setCodeMethod = env->GetMethodID(clazz, "setCode",
                                                            "(I)V");
        env->CallVoidMethod(binderData, setCodeMethod, code);
    }
    if (readLine(client_fd, buf, sizeof(buf)) > 0) {
        int dataLen = atoi(buf);
        if (dataLen > BINDER_DATA_RECEIVE_BUF_LEN) {
            return nullptr;
        }
        void *buffer = malloc(dataLen);
        int recvLen = recv(client_fd, buffer, dataLen, MSG_WAITALL);
        if (recvLen != dataLen) {
            __android_log_print(ANDROID_LOG_WARN, TAG,
                                "readSize != dataLen, excepted %d, found %d",
                                dataLen, recvLen);
        }
        return binderData;
    }
    return nullptr;
}
