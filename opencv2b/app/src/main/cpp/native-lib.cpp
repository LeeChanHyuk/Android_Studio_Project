#include <jni.h>
#include <string>

#include <opencv2/opencv.hpp>

using namespace cv;

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_opencv2_MainActivity_ConvertRGBtoGray(JNIEnv *env, jobject instance,
                                                       jlong matAddrInput,
                                                       jlong matAddrResult) {

    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;




    int a[100];
    // TODO
    Mat hue;
    int bins = 16;
    int firsttime = 0;
    Mat src;
    Mat mask(src.size(), CV_8U);
    Mat mask2;
    Mat pixelZone;
    Mat hist(Size(1,16),CV_8U);

    uchar* histo = (uchar*)hist.data;
    histo[0] = 0;
    histo[1] = 0;
    histo[2] = 127;
    histo[3] = 67;
    histo[4] = 241;
    histo[5] = 101;
    histo[6] = 46;
    histo[7] = 65;
    histo[8] = 0;
    histo[9] = 0;
    histo[10] = 0;
    histo[11] = 0;
    histo[12] = 0;
    histo[13] = 0;
    histo[14] = 0;
    histo[15] = 0;
    Mat src3;
    Mat hue2;
    int diff_threshold = 30;
    Mat afterbackproj;
    Mat src2;
    int vmin = 10, vmax = 256, smin = 30;
    //int &result_1=*(int *)result;
    //int &result_2=*(int *)result2;
    int result_1=0;
    int result_2=0;
    int level1 = 0;
    int level2 = 0;
    int level3 = 0;
    int level4 = 0;
    Mat hsv;
    Size size=matInput.size();
    Mat backproj(size,CV_8U);
    Mat motion(size,CV_8U);
    int file = 0;
    float hue_range[] = { 0, 180 }; // 여기서는 b->h가 되었으므로 h에서 밝기가 0~180인 픽셀을 찾아서 하얗게 만드는듯 하다.
    //파란색일수록 까맣게한다. b->h이므로 높을수록 까맣게 낮을수록 하얗게 한다.
    const float* ranges = { hue_range };
    src=matInput;
    Mat srca(size,CV_8UC3);
    int chap[] = { 0, 0,1,1,2,2 };
    cvtColor(src,src,COLOR_BGRA2BGR);
    cvtColor(src, hsv, COLOR_BGR2HSV);
    int _vmin = vmin, _vmax = vmax;
    inRange(hsv, Scalar(0, smin, MIN(_vmin, _vmax)),
            Scalar(180, 256, MAX(_vmin, _vmax)), mask);
    hue.create(hsv.size(), hsv.depth());
    int ch[] = { 0, 0 };
    //
    mixChannels(&hsv, 1, &hue, 1, ch, 1); // ch={0,0}이니까 bgr의 0번째 즉 b를 hsv의 첫번째 즉 h로 b->h로 변환해서 hue에 저장하겠다는
    // 의미이다. 물론 원본영상은 hsv를 의미하는 것이다.

    calcBackProject(&hue, 1, 0, hist, backproj, &ranges, 1, true);
    backproj&=mask;
    cv::Mat maskss = cv::getStructuringElement(cv::MORPH_RECT, cv::Size(3, 3), cv::Point(1, 1));
    cv::dilate(backproj, backproj, maskss, cv::Point(-1, -1), 3);
    cv::dilate(backproj, backproj, maskss, cv::Point(-1, -1), 3);
    cv::erode(backproj, backproj, maskss, cv::Point(-1, -1), 3);
    cv::erode(backproj, backproj, maskss, cv::Point(-1, -1), 3);
    cv::dilate(backproj, backproj, maskss, cv::Point(-1, -1), 3);






    Mat backporj = backproj.clone();
    // Wait until user exits the program
    Mat smallcontours(motion.size(), CV_8UC1, Scalar(0,0,0));
    Mat bigcontours(motion.size(), CV_8UC1, Scalar(0,0,0));
    std::vector<std::vector<Point>> contour;
    afterbackproj=backporj;
    findContours(afterbackproj, contour, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

    int max1 = 0;
    int in = 0;
    int max2 = 0;
    for (int i = 0; i < contour.size(); i++)
    {
        if (in < contourArea(contour[i]))
        {
            in = (int)contourArea(contour[i]);
            max1 = i;
        }
    }

    in = 0;
    for (int i = 0; i < contour.size(); i++)
    {
        if (i == max1)
            i++;
        if (i >= contour.size())
            break;
        if (in < contourArea(contour[i]))
        {
            in = (int)contourArea(contour[i]);
            max2 = i;
        }
    }
/*
        std::vector<std::vector<Point>> kai;
        kai.push_back(contour[max1]);
    drawContours(bigcontours, kai, -1, Scalar(255, 255, 255), -1);
        kai.push_back(contour[max2]);
        drawContours(smallcontours, kai, -1, Scalar(255, 255, 255), -1);
        kai.pop_back();
        kai.push_back(contour[max1]);


        Rect smallcontourrect = boundingRect(contour[max2]);

        int smallcontournum = smallcontourrect.x + smallcontourrect.width / 2;

        drawContours(bigcontours, kai, -1, Scalar(255, 255, 255), -1);

            */
    Rect bigcontourrect = boundingRect(contour[max1]);
    int bigcontournum = bigcontourrect.x + bigcontourrect.width / 2;
    if (contourArea(contour[max1]) > 10000) {
        if (bigcontournum < (int) size.width / 3)
            result_1 = 6;
        else if (bigcontournum >= (int) size.width * 1 / 3 &&
                 bigcontournum < (int) size.width * 2 / 3)
            result_1 = 7;
        else if (bigcontournum >= (int) size.width * 2 / 3)
            result_1 = 8;
    } else
        result_1 = 9;
    matResult=backproj;
    return result_1;
}
extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_example_opencv2_MainActivity_floatarray(JNIEnv *env, jobject instance, jlong matAddrInput,
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