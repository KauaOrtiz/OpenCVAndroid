#include <jni.h>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/features2d.hpp>
#include <vector>
#include <string>
#include <opencv2/objdetect.hpp>

using namespace cv;

extern "C"{

    CascadeClassifier face_cascade;

    JNIEXPORT void JNICALL Java_com_example_appcpp_MainActivityCPP_Initfacedetector(JNIEnv* jniEnv, jobject, jstring jFilePath){

        const char * jnamestr = jniEnv->GetStringUTFChars(jFilePath, nullptr);
        std::string filePath(jnamestr);
        face_cascade.load(filePath);

    }


    JNIEXPORT void JNICALL Java_com_example_appcpp_MainActivityCPP_DetectFaces(JNIEnv* jniEnv, jobject, jlong addrRGBA, jlong addrGRAY){
        Mat* frame = (Mat*)addrRGBA;
        Mat* framegray = (Mat*)addrGRAY;

        std::vector<Rect> faces;
        face_cascade.detectMultiScale(*framegray, faces);

        for (auto & face : faces) {
            rectangle(*frame, Point(face.x, face.y), Point(face.x + face.width, face.y + face.height), Scalar(0,255,0), 2);
        };

    }
}
