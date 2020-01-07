package com.example.opencv2;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.opencv.core.CvType.CV_32F;

public class MainActivity extends YouTubeBaseActivity
        implements CameraBridgeViewBase.CvCameraViewListener2,YouTubePlayer.OnInitializedListener {
    private static final String TAG = "opencv";
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat matInput;
    private Mat matResult;
    int lefttop_x=0;
    int lefttop_y=0;
    int rightdown_x=0;
    int rightdown_y=0;
    int motionclicked=0;
    int result=0;
    int result2=0;
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private YouTubePlayer player;
    int level1 = 0;
    int level2 = 0;
    int level3 = 0;
    int level4 = 0;
    int level5=0;
    int prevnum=0;
    int prevtime=0;
    int nowtime=0;
    float pause=1;
    int count=0;
    int num=0;
    int start=0;
    int starttime=999;
    int okstart=0;
    int oktime=999;
    int ok=0;
    int ok2=0;
    int ok3=0;
    int t=0;
    int stop=0;
    int stopt=0;
    int nameindex=0;
    private TensorFlowInferenceInterface inferenceInterface;


    private static final String INPUT_NODE = "X:0"; // input tensor name
    private static final String INPUT_NODETWO = "Y:0";
    private static final String OUTPUT_NODE = "doit3:0"; // output tensor name
    private static final String[] OUTPUT_NODES = {"doit3:0"};
    private static final int OUTPUT_SIZE = 1; // number of classes
    private static final int INPUT_SIZE = 784; // size of the input
    float[] resultss = new float[3]; // get the output probabilities for each class
    String strs="0";
    String afterstrs;
    Handler handler = new Handler();



    // editText = findViewById(R.id.Edittext);
    //String prevedittext="";



    public native int ConvertRGBtoGray(long matAddrInput, long matAddrResult);

    public native float[] floatarray(long matAddrInput, long matAddrResult);


    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }



    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        AssetManager assetManager = this.getAssets();
        inferenceInterface=new TensorFlowInferenceInterface(assetManager,"freeze.pb");










        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {

                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(1); // front-camera(1),  back-camera(0)
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize("AIzaSyBcgq2Wc0KADzC50pxg_2medcUvc1nX3Hc", this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo("fhWaJi1Hsfo"); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
            this.player = player;
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize("AIzaSyBcgq2Wc0KADzC50pxg_2medcUvc1nX3Hc", this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        matInput = inputFrame.rgba();

            //if ( matResult != null ) matResult.release(); fix 2018. 8. 18

            if (matResult == null)

                matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());
            Mat matResult2 = new Mat(matResult.rows(), matResult.cols(), CV_32F);
            float floatarrays[] = new float[784];
            result2 = result;
            result = ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
            SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            long now = System.currentTimeMillis();
            String str = dayTime.format(new Date(now));
            char last = str.charAt(str.length() - 1);
            char last2 = str.charAt(str.length() - 2);
            String a = Character.toString(last);
            String b = Character.toString(last2);
            String ab = b + a;
            Log.d("ab = ", ab);
            nowtime = Integer.parseInt(ab);
            if ((starttime + 10) % 60 == nowtime) {
                starttime = 999;
                start = 0;
            }


            if (matResult != null) {



                Imgproc.resize(matResult, matResult2, new Size(28, 28));
                floatarrays = floatarray(matResult2.getNativeObjAddr(), matResult.getNativeObjAddr());
                resultss[0] = (float) 0;
                resultss[1] = (float) 0;
                resultss[2] = (float) 0;

                inferenceInterface.feed(INPUT_NODE, floatarrays, 1, INPUT_SIZE); //1-D input (1,INPUT_SIZE);
                inferenceInterface.run(OUTPUT_NODES);
                inferenceInterface.fetch(OUTPUT_NODE, resultss);
                Log.d("five = ", Float.toString(resultss[0]));
                Log.d("ok = ", Float.toString(resultss[1]));
                Log.d("palm = ", Float.toString(resultss[2]));
                Log.d("num = ", Integer.toString(num));
                Log.d("pause = ", Float.toString(pause));
                afterstrs = strs;
                if (resultss[0] == 1.0)
                    strs = "five";
                else if (resultss[1] == 1.0)
                    strs = "ok";
                else if (resultss[2] == 1.0)
                    strs = "palm";
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 10 seconds
                    }
                }, 3000);
                if (result != 9 && okstart != 2) {
                    if (afterstrs.equals("ok")) {
                        if (strs.equals("ok"))
                            ok++;
                    }
                }

                if (ok > 20) {
                    okstart = 1;
                    //
                    long nows = System.currentTimeMillis();
                    String strs = dayTime.format(new Date(nows));
                    char lasts = str.charAt(strs.length() - 1);
                    char last2s = str.charAt(strs.length() - 2);
                    String as = Character.toString(lasts);
                    String bs = Character.toString(last2s);
                    String abs = bs + as;
                    oktime = Integer.parseInt(abs);
                    ok = 0;
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "이제 손바닥모양 해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }, 0);
                }
                if (okstart == 1) {
                    if (afterstrs.equals("palm")) {
                        if (strs.equals("palm"))
                            ok2++;
                        if (ok2 > 20) {
                            num++;
                            okstart = 0;
                            oktime = 999;
                            ok2 = 0;
                        }
                        if ((oktime + 2) % 60 == nowtime) {
                            okstart = 0;
                            oktime = 999;
                            ok2 = 0;
                        }
                    }
                }
                if (result != 9 && okstart != 1) {
                    if (afterstrs.equals("palm")) {
                        if (strs.equals("palm"))
                            ok3++;
                    }
                }
                if (ok3 > 20) {
                    okstart = 2;
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "이제 ok모양 해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }, 0);
                    long nows = System.currentTimeMillis();
                    String strs = dayTime.format(new Date(nows));
                    char lasts = str.charAt(strs.length() - 1);
                    char last2s = str.charAt(strs.length() - 2);
                    String as = Character.toString(lasts);
                    String bs = Character.toString(last2s);
                    String abs = bs + as;
                    oktime = Integer.parseInt(abs);
                    ok3 = 0;
                }

                if (okstart == 2) {
                    if (afterstrs.equals("ok")) {
                        if (strs.equals("ok"))
                            t++;
                        if (t > 20) {
                            num++;
                            okstart = 0;
                            oktime = 999;
                            t = 0;
                        }
                        if ((oktime + 2) % 60 == nowtime) {
                            okstart = 0;
                            oktime = 999;
                            t = 0;
                        }
                    }
                }

                if (num == 2) {
                    if (pause == 0) {
                        num = 0;
                        pause = 1;
                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "모션인식멈춤", Toast.LENGTH_SHORT).show();
                            }
                        }, 0);
                    } else {
                        num = 0;
                        pause = 0;
                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "모션인식시작", Toast.LENGTH_SHORT).show();
                            }
                        }, 0);
                    }
                    start = 0;
                    starttime = 999;
                }
                if (pause == 0) {
                    if (result != 9) {
                        if (afterstrs == "palm") {
                            if (strs == "palm") {
                                stopt++;
                            }
                        }
                        if (afterstrs == "ok") {
                            if (strs == "ok") {
                                stopt--;
                            }
                        }
                        if (stopt >= 40) {
                            Handler mHandler = new Handler(Looper.getMainLooper());
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (player.isPlaying()) {
                                        player.pause();
                                        Toast.makeText(getApplicationContext(), "영상멈춤", Toast.LENGTH_SHORT).show();
                                        stopt = 0;
                                    }
                                }
                            }, 0);
                        }

                        if (stopt == -40) {
                            Handler mHandler = new Handler(Looper.getMainLooper());
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (player.isPlaying() != true) {
                                        player.play();
                                        Toast.makeText(getApplicationContext(), "영상재생", Toast.LENGTH_SHORT).show();
                                        stopt = 0;
                                    }
                                }
                            }, 0);
                        }
                    }
                    if (result != result2) {
                        if (result == 7 && (result2 == 0 || result2 == 6)) {
                            level1 = 1;
                            level2 = 0;
                            level3 = 0;
                            level4 = 0;
                            long nows = System.currentTimeMillis();
                            String strs = dayTime.format(new Date(nows));
                            char lasts = str.charAt(strs.length() - 1);
                            char last2s = str.charAt(strs.length() - 2);
                            String as = Character.toString(lasts);
                            String bs = Character.toString(last2s);
                            String abs = bs + as;
                            prevtime = Integer.parseInt(abs);
                        } else if (result == 8 && result2 == 7) {
                            if (level1 == 1) {
                                player.seekRelativeMillis(-10 * 1000);
                                Handler mHandler = new Handler(Looper.getMainLooper());
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "10초전으로 이동", Toast.LENGTH_SHORT).show();
                                    }
                                }, 0);
                            }

                            level1 = 0;
                            level2 = 1;
                            level3 = 0;
                            level4 = 0;
                            long nows = System.currentTimeMillis();
                            String strs = dayTime.format(new Date(nows));
                            char lasts = str.charAt(strs.length() - 1);
                            char last2s = str.charAt(strs.length() - 2);
                            String as = Character.toString(lasts);
                            String bs = Character.toString(last2s);
                            String abs = bs + as;
                            prevtime = Integer.parseInt(abs);

                        } else if (result == 7 && result2 == 8) {
                            level1 = 0;
                            level2 = 0;
                            level3 = 1;
                            level4 = 0;
                            long nows = System.currentTimeMillis();
                            String strs = dayTime.format(new Date(nows));
                            char lasts = str.charAt(strs.length() - 1);
                            char last2s = str.charAt(strs.length() - 2);
                            String as = Character.toString(lasts);
                            String bs = Character.toString(last2s);
                            String abs = bs + as;
                            prevtime = Integer.parseInt(abs);
                        } else if (result == 6 && result2 == 7) {
                            if (level3 == 1) {
                                player.seekRelativeMillis(10 * 1000);
                                Handler mHandler = new Handler(Looper.getMainLooper());
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "10초후로 이동", Toast.LENGTH_SHORT).show();
                                    }
                                }, 0);
                            }


                            level1 = 0;
                            level2 = 0;
                            level3 = 0;
                            level4 = 1;
                            long nows = System.currentTimeMillis();
                            String strs = dayTime.format(new Date(nows));
                            char lasts = str.charAt(strs.length() - 1);
                            char last2s = str.charAt(strs.length() - 2);
                            String as = Character.toString(lasts);
                            String bs = Character.toString(last2s);
                            String abs = bs + as;
                            prevtime = Integer.parseInt(abs);

                        }

                        //
                        if ((prevtime + 5) % 60 == nowtime) {
                            level1 = 0;
                            level2 = 0;
                            level3 = 0;
                            level4 = 0;
                        }
                        if (result == 4)
                            count++;
                        if (count == 10) {
                            level1 = 0;
                            level2 = 0;
                            level3 = 0;
                            level4 = 0;
                            count = 0;
                        }


                        //bmp.getPixels(intValues, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
                        //Mat intMat = new Mat();
                        //matResult2.convertTo(intMat, CV_32S);
                        //IntBuffer intBuffer = intMat.createBuffer();
                        //int[] intArray = new int[intBuffer.capacity()];
                        //intBuffer.get(intArray);
                        //for (int i = 0; i < intValues.length; ++i) {
                        //    final int val = intValues[i];
                /*
                preprocess image if required
                floatValues[i * 3 + 0] = (((val >> 16) & 0xFF) - imageMean) / imageStd;
                floatValues[i * 3 + 1] = (((val >> 8) & 0xFF) - imageMean) / imageStd;
                floatValues[i * 3 + 2] = ((val & 0xFF) - imageMean) / imageStd;
                */


                        // convert from 0-255 range to floating point value
                        //    floatValues[i] = (float)val;
                        //}


                    }
                }
            }
            return matResult;
    }



    //여기서부턴 퍼미션 관련 메소드
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS  = {"android.permission.CAMERA"};


    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED){
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }



}
