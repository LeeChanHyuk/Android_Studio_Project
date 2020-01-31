package kr.co.softcampus.stone;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Check2 extends AppCompatActivity {

    final int REQ_CODE_SELECT_IMAGE=1; // 갤러리에서 원본 이미지 선택 시의 플래그값
    final int CROP_FROM_PICTURE=2; // 갤러리에서 이미지 선택 후 크롭 시의 플래그값

    public Mat img_input=new Mat(); // 원본 이미지. 갤러리에서 받아옴
    public List<Mat> img_crop = new ArrayList<Mat>(); // 원본 이미지에서 크롭된 이미지들이 저장될 배열객체
    public int img_crop_pt[][]; // 원본에서 크롭된 이미지의 좌표. x,y값을 저장. 아직 사용 안됐음
    public boolean is_img; // 원본 이미지를 갤러리로부터 가져왔는지를 판단하는 플래그값
    public boolean is_cropped; // 크롭 이미지가 1개라도 존재하는지를 판단하는 플래그값
    public int count_corp_img = 0; // 크롭된 이미지 개수
    private TensorFlowInferenceInterface inferenceInterface;
    String classes[]={"carry1","carry2","carry3","carry4"};
    public native float[] floatarray(long matAddrInput, long matAddrResult);
    private static final String INPUT_NODE = "X:0"; // input tensor name
    private static final String INPUT_NODETWO = "Y:0";
    private static final String OUTPUT_NODE = "doit3:0"; // output tensor name
    private static final String[] OUTPUT_NODES = {"doit3:0"};
    private static final int OUTPUT_SIZE = 4; // number of classes
    private static final int INPUT_SIZE = 784; // size of the input
    float[] resultss = new float[4]; // get the output probabilities for each class


    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }
    private static final String TAG = "opencv";
    ImageView imageView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.check2);
        is_img=false;
        Button picture = (Button) findViewById(R.id.Btn_Find); // 갤러리에서 사진 불러오는 버튼
        Button b1=(Button)findViewById(R.id.back); // 이전 액티비티로 돌아가는 버튼
        Button b2=(Button)findViewById(R.id.crop); // 크롭용 버튼
        Button b3=(Button)findViewById(R.id.starttensor); // 이미지 크롭됐으면, 텐서 모델 시작하는 함수
        imageView = (ImageView)findViewById(R.id.imageView); // 이미지 뷰 객체
        AssetManager assetManager = this.getAssets();
        inferenceInterface=new TensorFlowInferenceInterface(assetManager,"freeze.pb");

        b1.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

        picture.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {  // 클릭하면 ACTION_PICK 연결로 기본 갤러리를 불러옴
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);

            }
        });

        b2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {  // 이미지 크롭 시작
                if(is_img!=true) { // 원본 이미지 선택 안됐으면 크롭 불가능하게 함
                    Toast.makeText(getApplicationContext(), "원본사진 먼저 선택해주세요!", Toast.LENGTH_SHORT).show();
                }
                else{ // 원본 이미지 선택됐으면, 크롭을 위해 원본사진과 동일하게 선택하고 진행하게 함.
                    Toast.makeText(getApplicationContext(), "원본사진과 동일한 사진을 선택하세요!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 0);
                    intent.putExtra("aspectY", 0);
                    intent.putExtra("outputX", 200);
                    intent.putExtra("outputY", 150);

                    try {
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, CROP_FROM_PICTURE);
                    } catch (ActivityNotFoundException e) {
                        // Do nothing for now
                    }
                }
            }
        });

        b3.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                if ((is_img != true) || (is_cropped != true)) { // 원본이미지가 없거나 충치 의심되는 부분을 선택하지 않으면 이 조건문이 실행
                    Toast.makeText(getApplicationContext(), "원본사진을 선택하거나 충치를 선택하세요!", Toast.LENGTH_SHORT).show();
                } else {
                    // Tensorflow 모델에 넘겨주거나 실행
                    // 원본 사진은 변수 img_input에, 충치가 의심되는 사진은 img_crop에 배열 형태로 저장되어있음
                    // 추가!
                    for(int i=0 ; i<img_crop.size() ; i++) {
                        Mat matResult2 = new Mat();
                        float floatarrays[] = new float[784];
                        Imgproc.resize(img_crop.get(i), matResult2, new Size(28, 28));
                        floatarrays = floatarray(matResult2.getNativeObjAddr(), img_crop.get(i).getNativeObjAddr());
                        resultss[0] = (float) 0;
                        resultss[1] = (float) 0;
                        resultss[2] = (float) 0;
                        resultss[3]=(float)0;

                        inferenceInterface.feed(INPUT_NODE, floatarrays, 1, INPUT_SIZE); //1-D input (1,INPUT_SIZE);
                        inferenceInterface.run(OUTPUT_NODES);
                        inferenceInterface.fetch(OUTPUT_NODE, resultss);
                        Log.d("1단계 = " , Float.toString(resultss[0]));
                        Log.d("2단계 = " , Float.toString(resultss[1]));
                        Log.d("3단계 = " , Float.toString(resultss[2]));
                        Log.d("4단계 = " , Float.toString(resultss[3]));
                    }
                }
            }
        });
    }





    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE_SELECT_IMAGE) { // 갤러리에서 원본 이미지 선택 시 아래가 시작
            if (resultCode == Activity.RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성

                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    Utils.bitmapToMat(img, img_input); // Mat 형식인 img_input 파일에 원본사진을 저장하기 위한 부분
                    //Bitmap temp = Bitmap.createBitmap(img_input.cols(), img_input.rows(), Bitmap.Config.ARGB_8888);
                    in.close();
                    // 이미지 표시
                    //Utils.matToBitmap(img_input, temp);
                    is_img = true; // 원본 이미지가 선택됐다고 플래그 세움
                    imageView.setImageBitmap(img); // 원본 이미지 보여줌

                } catch (Exception e) { // 예외부
                    e.printStackTrace();
                }
            }
        }
        if(requestCode == CROP_FROM_PICTURE) // 만약 이미지를 크롭한다고 선택 시,
        {
            if(data!=null) // 이미지 데이터가 있으면
            {
                Bundle bundle = data.getExtras();
                Bitmap cropped=bundle.getParcelable("data"); // 크롭된 비트맵 이미지
                Mat temp = new Mat(); // Mat 이미지 일시적으로 생성
                Utils.bitmapToMat(cropped, temp); // 크롭된 비트맵 이미지를 Mat 이미지로 변환
                is_cropped = true; // 크롭 됐다는 플래그 세움
                imageView.setImageBitmap(cropped); // 크롭된 이미지 보여줌
                img_crop.add(temp); // 크롭되고 Mat 이미지로 변환시킨 걸 Mat 배열에 하나씩 박아넣음
                count_corp_img+=1; // 크롭된 이미지 개수 + 1

                Toast.makeText(getApplicationContext(), count_corp_img+"개의 충치 의심 영역", Toast.LENGTH_SHORT).show();
            }
        }
    }




    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        return imgName;
    }
}
