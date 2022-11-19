package com.example.opencvandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.lang.reflect.AccessibleObject;
import java.util.Collections;
import java.util.List;

public class MainActivity extends CameraActivity { //extender para uma atividade de camera, pode ser uma redflag pra integração futura
    private CameraBridgeViewBase cameraBridgeViewBase;

    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:{
                    Log.e("TAG", "OpenCv carregado");
                    cameraBridgeViewBase.enableView();

                }break;
                default:{
                    super.onManagerConnected(status);
                }break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (OpenCVLoader.initDebug()){
            Log.e("TAG", "deu bom");
        }
        cameraBridgeViewBase=(CameraBridgeViewBase) findViewById(R.id.opencv_surface_view);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(cvCameraViewListener2);
    }


    protected List<?extends CameraBridgeViewBase> getCameraViewList(){
        return Collections.singletonList(cameraBridgeViewBase);
    }

    private CameraBridgeViewBase.CvCameraViewListener2 cvCameraViewListener2 = new CameraBridgeViewBase.CvCameraViewListener2() {
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

            //Exemplo simples de buscas de cantos
            //Aqui basicamente estamos analisando o canal em escala de cinzas sobre a função
            //goodFeaturesToTrack e após adquirir os cantos faz-se o desenho sobre o canal RGB
            MatOfPoint corners = new MatOfPoint();
            Imgproc.goodFeaturesToTrack(input_gray,corners,20,0.01,10, new Mat(),3,false);
            Point[] cornersArr = corners.toArray();
            for (int i=0; i<corners.rows(); i++){
                Imgproc.circle(input_rgba,cornersArr[i],10, new Scalar(0,255,0),2);
            }
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