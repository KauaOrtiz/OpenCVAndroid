package com.example.appcpp;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class MainActivityCPP extends CameraActivity {

    private CameraBridgeViewBase cameraBridgeViewBase;
    static {
       System.loadLibrary("appcpp");
    }

    public native void Initfacedetector(String filePath);
    public native void DetectFaces(long addrRGBA, long addrGRAY);

    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.e("TAG", "OpenCv carregado");
                cameraBridgeViewBase.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cpp);
        if (OpenCVLoader.initDebug()){
            Log.e("TAG", "deu bom");
        }

        try{
            File cascadeFile = new File(getCacheDir(), "haarcascade_frontalface_default.xml");

            if (!cascadeFile.exists()) {
                InputStream inputStream = getAssets().open("haarcascade_frontalface_default.xml");
                FileOutputStream outputStream = new FileOutputStream(cascadeFile);
                byte[] buffer = new byte[2048];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1){
                    outputStream.write(buffer, 0, bytesRead);
                }

                inputStream.close();
                outputStream.close();
            }
            Initfacedetector(cascadeFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }

        cameraBridgeViewBase=(CameraBridgeViewBase) findViewById(R.id.opencv_surface_view);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(cvCameraViewListener2);
    }


    protected List<?extends CameraBridgeViewBase> getCameraViewList(){
        return Collections.singletonList(cameraBridgeViewBase);
    }

    private final CameraBridgeViewBase.CvCameraViewListener2 cvCameraViewListener2 = new CameraBridgeViewBase.CvCameraViewListener2() {
        @Override
        public void onCameraViewStarted(int width, int height) {

        }

        @Override
        public void onCameraViewStopped() {

        }

        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            //Mat é uma classe de array do opencv que pdoe ser de canal uníco ou multi canais
            //Basicamente é ela que usamos pra fazer as analises das imagens

            Mat input_rgba = inputFrame.rgba(); //Canal em RGB
            Mat input_gray = inputFrame.gray(); //Canal em escala de cinza

            //Implementação do processamento em C++
            DetectFaces(input_rgba.getNativeObjAddr(), input_gray.getNativeObjAddr());
            return input_rgba;
        }
    };
    @Override
    public void onPause(){
        super.onPause();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        if (!OpenCVLoader.initDebug()){
            Log.e("TAG", "Não foi encontrado opncv, tentando iniciar");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION,this,baseLoaderCallback);
        }
        else{
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }
}