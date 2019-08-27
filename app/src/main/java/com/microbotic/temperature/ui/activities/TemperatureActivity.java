package com.microbotic.temperature.ui.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.cyberlink.faceme.DetectionModelSpeedLevel;
import com.cyberlink.faceme.EnginePreference;
import com.cyberlink.faceme.ExtractConfig;
import com.cyberlink.faceme.ExtractionModelSpeedLevel;
import com.cyberlink.faceme.FaceAttribute;
import com.cyberlink.faceme.FaceFeature;
import com.cyberlink.faceme.FaceInfo;
import com.cyberlink.faceme.FaceMeRecognizer;
import com.cyberlink.faceme.LicenseManager;
import com.cyberlink.facemedemo.data.Config;
import com.cyberlink.facemedemo.sdk.ThumbnailUtil;
import com.jumper.bluetoothdevicelib.device.temperature.TemperatureResult;
import com.microbotic.temperature.R;
import com.microbotic.temperature.app.DataProcessor;
import com.microbotic.temperature.libraries.sweetAlert.SweetAlertDialog;
import com.microbotic.temperature.model.StudentTest;
import com.microbotic.temperature.services.BaseReceiveService;
import com.microbotic.temperature.ui.adapters.TestStudentListAdapter;
import com.microbotic.temperature.ui.window.FaceListWindow;
import com.microbotic.temperature.ui.window.RegisterFaceWindow;
import com.microbotic.temperature.utils.MaintainRequestQueue;
import com.microbotic.temperature.utils.TemperatureDeviceHandler;
import com.nstudio.camera.CameraFragment;
import com.nstudio.camera.OnPictureCaptureListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("SetTextI18n")
public class TemperatureActivity extends AppCompatActivity implements OnPictureCaptureListener {

    private static final String TAG = TemperatureActivity.class.getSimpleName();
    private Context context;
    private TextView tvMessage, tvResult, tvFaceCount,tvFaceResult,btnSaveStudent,btnShowList;
    private FrameLayout viewCamera;
    private Button btnCamera;
    private boolean isCameraOpen = false;
    private ImageView imgPicture;
    private CameraFragment cameraFragment;
    private ImageView btnTakePic;
    private final float thr = 0.80f;

    private FaceMeRecognizer recognizer;
    private ExtractConfig extractConfig;
    private boolean isReady = true;

    private boolean cropFaceBitmap = true;
    private ArrayList<StudentTest> studentList ;
    private Bitmap imageBitmap;
    private SweetAlertDialog alertDialog;
    private RecyclerView rvMatchedFaces;
    private ProgressBar progressBar;

    private TemperatureDeviceHandler.OnTemperatureScanListener temperatureScanListener = new TemperatureDeviceHandler.OnTemperatureScanListener() {
        @Override
        public void onResult(TemperatureResult result) {
            Log.e(TAG, "Temperature Result");

            if (result != null) {



                Log.e(TAG, "Temperature Result >> Not Null");
                final String res = "Body temperature (C):" + result.getTemp4C();


                Log.d(TAG, "result ---->" + result.temperature);

                runOnUiThread(() -> {
                    if (isCameraOpen){
                        btnTakePic.performClick();
                    }
                    tvMessage.setText(res);
                    sendData("1", "1", String.valueOf(result.getTemp4C()));
                });


                return;
            }


            Log.e(TAG, "Temperature Result >> NULL");
        }

        @Override
        public void onDeviceConnect(int state) {
            Log.e(TAG, "Temperature Device Connected");
            runOnUiThread(() -> tvMessage.setText("Temperature Device Connected!"));
        }

        @Override
        public void onError(String msg) {
            showMessage(msg);
        }
    };

    private RegisterFaceWindow.OnStudentRegisteredListener registeredListener = new RegisterFaceWindow.OnStudentRegisteredListener() {
        @Override
        public void onRegister() {
            btnSaveStudent.setVisibility(View.GONE);
            loadStudentList();
        }
    };

    String requestBody;


    //bluetooth adapter
    ProgressDialog progressDialog;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        context = TemperatureActivity.this;

        tvMessage = findViewById(R.id.tvMessage);
        tvResult = findViewById(R.id.tvResult);
        btnCamera = findViewById(R.id.btnCamera);
        viewCamera = findViewById(R.id.viewCamera);
        imgPicture = findViewById(R.id.imgPicture);
        btnTakePic = findViewById(R.id.btnTakePicture);
        tvFaceCount = findViewById(R.id.tvFaceCount);
        tvFaceResult = findViewById(R.id.tvFaceResult);
        btnSaveStudent = findViewById(R.id.btnSaveStudent);
        btnShowList = findViewById(R.id.btnStudentList);
        rvMatchedFaces = findViewById(R.id.rvMatchedFaces);
        progressBar = findViewById(R.id.progressBar);

        cameraFragment = new CameraFragment();

        id = getIntent().getStringExtra("id");

        alertDialog = new SweetAlertDialog(context);

        progressDialog = new ProgressDialog(TemperatureActivity.this);

        btnCamera.setOnClickListener(v -> {

            if (isCameraOpen) {

                isCameraOpen = false;
                btnCamera.setText("Open Camera");
                detachFragment();
                btnTakePic.setVisibility(View.GONE);

                return;
            }
            btnCamera.setText("Close Camera");
            isCameraOpen = true;
            attachCameraFragment();
            btnTakePic.setVisibility(View.VISIBLE);

        });

        btnSaveStudent.setOnClickListener(v -> {
            if (imageBitmap!=null){
                new RegisterFaceWindow(TemperatureActivity.this,imageBitmap,recognizer,registeredListener).show();
            }

        });

        btnShowList.setOnClickListener(v -> new FaceListWindow(TemperatureActivity.this,studentList).show());


        btnTakePic.setOnClickListener(view -> {
            cameraFragment.takePicture();
            progressBar.setVisibility(View.VISIBLE);
        }
        );

        findViewById(R.id.imgBack).setOnClickListener(view -> finish());

        Display display = getWindowManager().getDefaultDisplay();
        Point mSize = new Point();
        display.getSize(mSize);

        new RegisterLicenseAsyn().execute();
        loadStudentList();


        //temperature
        TemperatureDeviceHandler handler = new TemperatureDeviceHandler();
        handler.init(context,temperatureScanListener);

    }

    private void attachCameraFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.viewCamera,cameraFragment, CameraFragment.TAG)
                .addToBackStack(CameraFragment.class.getSimpleName())
                .commit();
    }

    private void detachFragment(){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CameraFragment.class.getSimpleName());
        if (fragment==null){
            return;
        }
        getSupportFragmentManager().popBackStackImmediate();
    }


    private void sendData(final String id, final String studentId, final String temp) {
        String url = "http://192.168.1.5:8080/healthrecord";
        url = url.replace(" ", "%20");
        url = url.replace("\n", "%0A");
        progressDialog.show();
        progressDialog.setMessage("Please wait...");

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("studentId", "20");
            jsonBody.put("temperature", temp);

            requestBody = jsonBody.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest req = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("Response", response);
            try {
                JSONObject jsonObject = new JSONObject(response);

                progressDialog.dismiss();

            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        }, error -> {
            Log.e("TAG", "Error " + error.getMessage());
            progressDialog.dismiss();
        }) {
            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }

            @Override
            public byte[] getBody() {
                return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
            }


        };
        MaintainRequestQueue.getInstance(this).addToRequestQueue(req, "tag");
    }

    private void showMessage(String msg) {
        runOnUiThread(() -> {
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();

        });
    }

    public float compareFaceFeature(FaceFeature faceA, FaceFeature faceB) {
        if (recognizer == null) {
            Log.e(TAG, "compareFaceFeature but didn't initialize yet");
            return -1;
        }

        return recognizer.compareFaceFeature(faceA, faceB);
    }

    public static Bitmap getFaceBitmap(Bitmap source, Rect detectFaceRect) {
        Size sourceSize = new Size(source.getWidth(), source.getHeight());
        // For each trained face, save the color frame (640 x 480) to the database.
        if (detectFaceRect.width() > detectFaceRect.height()) {
            int space = (detectFaceRect.width() - detectFaceRect.height()) / 2;
            detectFaceRect = ThumbnailUtil.enlargeThumbnail(detectFaceRect, sourceSize, 0, space, 0, space, false);
        } else if (detectFaceRect.width() < detectFaceRect.height()) {
            int space = -(detectFaceRect.width() - detectFaceRect.height()) / 2;
            detectFaceRect = ThumbnailUtil.enlargeThumbnail(detectFaceRect, sourceSize, space, 0, space, 0, false);
        }
        Rect enlargeRect = ThumbnailUtil.enlargeThumbnail(detectFaceRect, sourceSize, 0.25f, false);


        //adjust rect to Square
        int centerX = enlargeRect.centerX();
        int centerY = enlargeRect.centerY();
        int minEdge = Math.min(enlargeRect.width(), enlargeRect.height());
        int maxEdge = Math.max(enlargeRect.width(), enlargeRect.height());
        int squareEdge = minEdge;
        if (centerX - Math.ceil(maxEdge / 2.0) >= 0 && centerX + Math.ceil(maxEdge / 2.0) <= sourceSize.getWidth() &&
                centerY - Math.ceil(maxEdge / 2.0) >= 0 && centerY + Math.ceil(maxEdge / 2.0) <= sourceSize.getHeight()) {
            squareEdge = maxEdge;
        }
        Rect enlargeSquareFaceRect = new Rect();
        enlargeSquareFaceRect.left = Math.max(0, centerX - (int)Math.ceil(squareEdge / 2.0));
        enlargeSquareFaceRect.top = Math.max(0, centerY - (int)Math.ceil(squareEdge / 2.0));
        enlargeSquareFaceRect.right = enlargeSquareFaceRect.left + squareEdge;
        enlargeSquareFaceRect.bottom = enlargeSquareFaceRect.top + squareEdge;
        return Bitmap.createBitmap(source, enlargeSquareFaceRect.left, enlargeSquareFaceRect.top, enlargeSquareFaceRect.width(), enlargeSquareFaceRect.height());
    }

    private void loadStudentList() {
        alertDialog = new SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE);
        alertDialog.setTitleText("Loading Student List");
        alertDialog.setCancelable(false);
        alertDialog.show();

        new Thread(() -> {
            ArrayList<StudentTest> list = new DataProcessor(context).getAllStudents();
            Log.e(TAG,"LIST >> "+list.size());
            studentList = list;
            if (list.size()>0){
                for(StudentTest student : studentList){
                    File file = new File(Environment.getExternalStorageDirectory(), "SchoolApp/" + student.getImageName());

                    if (!file.exists()) {
                        Log.e(TAG, "Face not exist " + student.getId() + " " + student.getImageName());
                        continue;
                    }

                    Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                    student.setPhoto(bitmap);

                }

                recogniseFaces();
            }else {
                runOnUiThread(() -> {
                    if (alertDialog!=null && alertDialog.isShowing()){
                        alertDialog.dismissWithAnimation();
                    }
                });
            }
        }).start();
    }

    private void recogniseFaces() {
        new Thread(() -> {

            for (StudentTest face : studentList) {
                try {

                    Bitmap bitmap = face.getPhoto();

                    if (bitmap == null) {
                        Log.e(TAG,face.getName()+" FACE IS NULL");

                        continue;
                    }

                    if (recognizer == null) {
                        Log.e(TAG, "extractFace but didn't initialize yet");
                        return;
                    }

                    int facesCount = recognizer.extractFace(extractConfig, Collections.singletonList(bitmap));

                    if (facesCount > 0) {
                        for (int faceIndex = 0; faceIndex < facesCount; faceIndex++) {
                            FaceFeature faceFeature = recognizer.getFaceFeature(0, faceIndex);
                            FaceAttribute faceAttr = recognizer.getFaceAttribute(0, faceIndex);
                            face.setPhoto(bitmap);
                            face.setFaceFeature(faceFeature);
                            face.setFaceAttribute(faceAttr);

                            Log.e(TAG, faceIndex + " >> age " + faceAttr.age + " | gender " + faceAttr.gender + " | emotion " + faceAttr.emotion);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.e(TAG, "SAVED FACES >> " + studentList.size());

            runOnUiThread(() ->{
                btnShowList.setVisibility(View.VISIBLE);
                alertDialog.dismissWithAnimation();
            });

        }).start();
    }

    @Override
    public void onPictureCapture(final Bitmap bitmap) {
        imageBitmap = bitmap;
        imgPicture.setImageBitmap(bitmap);

        if (!isReady){
            return;
        }

        isReady = false;


        if (bitmap==null){
            Log.e(TAG,"BITMAP IS NULL");
        }

        new Thread(() -> {
            int faceCount = recognizer.extractFace(extractConfig, Collections.singletonList(bitmap));

            Log.d(TAG, "CAPTURE FACE COUNT >> " + faceCount);

            if (faceCount > 0) {
                for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
                    FaceInfo faceInfo = recognizer.getFaceInfo(0, faceIndex);

                    FaceFeature faceFeature = recognizer.getFaceFeature(0, faceIndex);

                    Bitmap faceBitmap = null;
                    if (cropFaceBitmap) {
                        long tsBitmap = System.currentTimeMillis();

                        Rect detectFaceRect = new Rect();
                        detectFaceRect.left = Math.max(faceInfo.boundingBox.left, 0);
                        detectFaceRect.top = Math.max(faceInfo.boundingBox.top, 0);
                        assert bitmap != null;
                        detectFaceRect.right = Math.min(faceInfo.boundingBox.right, bitmap.getWidth());
                        detectFaceRect.bottom = Math.min(faceInfo.boundingBox.bottom, bitmap.getHeight());
                        faceBitmap = getFaceBitmap(bitmap, detectFaceRect);

                        long duration = System.currentTimeMillis() - tsBitmap;
                        if (duration > 10) {
                            Log.v(TAG, "   [" + faceIndex + "] Bitmap face took " + duration + "ms");
                        }

                        Log.e(TAG,"crop face");
                    }

                    ArrayList<StudentTest> matchedFaces = new ArrayList<>();
                    ArrayList<Bitmap> matchedBitmaps = new ArrayList<>();

                    if (studentList!=null && studentList.size()>0){

                        Log.e(TAG,"start comparing face");

                        for (int f=0;f<studentList.size();f++){
                            StudentTest student = studentList.get(f);

                            if (student.getFaceFeature()==null){

                                Log.e(TAG,"FACE FEATURES ARE NULL");

                                continue;
                            }
                            Log.e(TAG,"comparing student "+student.getName());

                            float c = compareFaceFeature(student.getFaceFeature(),faceFeature);
                            Log.e(TAG, student.getName()+" face ["+faceIndex+ "] feature ["+f+"] >> result >> "+c);
                            if (c > thr){
                                Log.d(TAG,"Face Result "+f);

                                matchedBitmaps.add(faceBitmap);
                                matchedFaces.add(student);
                            }

                        }
                    }


                    showMatchedFace(matchedFaces,matchedBitmaps,faceCount);


                }
            }else{
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(context,"Face is not there in captured photo",Toast.LENGTH_SHORT).show();
                });
            }

//            if (faceCount>0){
//                for (int index = 0; index < faceCount; index++) {
//                    FaceInfo faceInfo = recognizer.getFaceInfo(0, index);
//                    FaceAttribute faceAttribute = recognizer.getFaceAttribute(0, index);
//
//                    String result =  "RESULT confidence " + faceInfo.confidence +
//                            "\nage " + faceAttribute.age +
//                            "\ngender " + faceAttribute.gender +
//                            "\nemotion " + faceAttribute.emotion;
//
//                    Log.e(tag, "face result >> "+ result);
//                    showFaceResults(result,faceCount);
//                }
//
//            }else {
//                showFaceResults("",faceCount);
//            }

            isReady = true;
        }).start();
    }

    public void showMatchedFace(ArrayList<StudentTest> students, ArrayList<Bitmap> faceBitmaps, int faceCount){
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            tvFaceCount.setText("Face Count: "+faceCount);
            if(students.size()>0){
                TestStudentListAdapter studentListAdapter = new TestStudentListAdapter(students);
                rvMatchedFaces.setVisibility(View.VISIBLE);
                rvMatchedFaces.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
                rvMatchedFaces.setAdapter(studentListAdapter);
            }else {
                Toast.makeText(context,"No match found for captured photo",Toast.LENGTH_SHORT).show();
                rvMatchedFaces.setVisibility(View.GONE);
                btnSaveStudent.setVisibility(View.VISIBLE);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    class RegisterLicenseAsyn extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            LicenseManager licenseManager = new LicenseManager(context);
            boolean isRegistered = licenseManager.initialize(Config.API_KEY);
            int result = licenseManager.registerLicense();
            Log.e(TAG,"Result "+ isRegistered +" >> "+ result);

            if (result !=0){
                showMessage("Registration Failed!");
                return null;
            }

            recognizer = new FaceMeRecognizer(context);
            recognizer.initialize(
                    EnginePreference.PREFER_NONE,
                    2,
                    DetectionModelSpeedLevel.DEFAULT,
                    ExtractionModelSpeedLevel.HIGH,
                    Config.API_KEY
            );

            extractConfig = new ExtractConfig();
            extractConfig.extractFeature = true;
            extractConfig.extractAge = true;
            extractConfig.extractEmotion = true;
            extractConfig.extractGender = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadStudentList();
        }
    }

}