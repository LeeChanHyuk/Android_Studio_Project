#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_kr_co_softcampus_stone_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jfloatArray JNICALL
Java_kr_co_softcampus_stone_Check2_floatarray(JNIEnv *env, jobject instance, jlong matAddrInput,
                                              jlong matAddrResult) {

    // TODO
    Mat &matInput = *(Mat *) matAddrInput; // 여기 input은 다른곳의 result.
    //28바이 28의 인풋을 받으면 그걸 쭉 늘려서 1*784의 mat로 만들고 그걸 floatarray로 변환하지 해서 return 하기.
    Mat image(Size(1, 784), CV_8U);
    int i = 0;
    int j = 0;
    uchar *histo = (uchar *) matInput.data;
    uchar *histo2 = (uchar *) image.data;
    jfloat *floatarray2 = new jfloat[784];

    for (i = 0; i < 28; i++) {
        for (j = 0; j < 28; j++) {
            floatarray2[i * 28 + j] = histo[i * 28 + j];
        }
    }
    jfloatArray floatArray = env->NewFloatArray(784);
    env->SetFloatArrayRegion(floatArray, 0, 784, floatarray2);
    return floatArray;

}