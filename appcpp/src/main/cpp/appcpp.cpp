#include <jni.h>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/features2d.hpp>
#include <vector>
#include <string>
#include <jni.h>

using namespace cv;

extern "C"{
    JNIEXPORT void JNICALL Java_com_example_appcpp_MainActivityCPP_FindFeatures(JNIEnv* jniEnv, jobject, jlong addrGray, jlong addrRGBA){
        Mat* mGray = (Mat*)addrGray;
        Mat* mRGBA = (Mat*)addrRGBA;
        std::vector<Point2f> corners;
        goodFeaturesToTrack(*mGray,corners,20,0.01,10,Mat(),3,false,0.04);

        for(int i=0;i<corners.size();i++){
            circle(*mRGBA,corners[i],10,Scalar(0,255,255),2);

        }
    }
}
