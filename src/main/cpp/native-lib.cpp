#include <jni.h>
#include <string>
#include <android/log.h>

// Определяем тэг для логирования
#define LOG_TAG "NativeLib"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

// Функция для очистки текста в EditText
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_c2_AuthActivity_clearFieldNative(JNIEnv *env, jobject thiz, jobject edit_text) {
    // Находим класс EditText
    jclass editTextClass = env->GetObjectClass(edit_text);
    if (editTextClass == nullptr) {
        LOGD("Не удалось найти класс EditText.");
        return JNI_FALSE;
    }

    // Получаем ID метода setText(String text)
    jmethodID setTextMethod = env->GetMethodID(editTextClass, "setText", "(Ljava/lang/CharSequence;)V");
    if (setTextMethod == nullptr) {
        LOGD("Не удалось найти метод setText.");
        return JNI_FALSE;
    }

    // Вызываем setText("") для очистки текста в EditText
    jstring emptyString = env->NewStringUTF("");
    env->CallVoidMethod(edit_text, setTextMethod, emptyString);

    // Освобождаем локальную ссылку
    env->DeleteLocalRef(emptyString);

    // Логируем успешное выполнение и возвращаем true
    LOGD("Поле успешно очищено.");
    return JNI_TRUE;
}