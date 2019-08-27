package com.microbotic.temperature.ui.window;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.cyberlink.faceme.FaceInfo;
import com.cyberlink.faceme.FaceMeRecognizer;
import com.microbotic.temperature.R;
import com.microbotic.temperature.app.DataProcessor;
import com.microbotic.temperature.model.StudentTest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.Random;

import static com.microbotic.temperature.ui.activities.TemperatureActivity.getFaceBitmap;

public class RegisterFaceWindow extends Dialog {

    private Context context;
    private FaceMeRecognizer recognizer;
    private Bitmap bitmap;
    private OnStudentRegisteredListener registeredListener;
    private final String TAG = RegisterFaceWindow.class.getSimpleName();

    public RegisterFaceWindow(@NonNull Activity activity, Bitmap image, FaceMeRecognizer recognizer,OnStudentRegisteredListener registeredListener) {
        super(activity);
        this.context = activity;
        this.bitmap = image;
        this.recognizer = recognizer;
        this.registeredListener = registeredListener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.window_register_face);
        Objects.requireNonNull(getWindow()).setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(context.getResources().getDrawable(R.drawable.window_bg));

        EditText etName = findViewById(R.id.etName);
        EditText etClass = findViewById(R.id.etClass);

        ImageView imgStudent = findViewById(R.id.imgStudent);

        FaceInfo faceInfo = recognizer.getFaceInfo(0, 0);
        Rect detectFaceRect = new Rect();
        detectFaceRect.left = Math.max(faceInfo.boundingBox.left, 0);
        detectFaceRect.top = Math.max(faceInfo.boundingBox.top, 0);
        detectFaceRect.right = Math.min(faceInfo.boundingBox.right, bitmap.getWidth());
        detectFaceRect.bottom = Math.min(faceInfo.boundingBox.bottom, bitmap.getHeight());
        final Bitmap faceBitmap = getFaceBitmap(bitmap, detectFaceRect);
        imgStudent.setImageBitmap(faceBitmap);



        findViewById(R.id.tvTitle).setOnClickListener(v -> dismiss());

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()){
                etName.setError("Enter Name");
                return;
            }

            String className = etClass.getText().toString().trim();
            if (className.isEmpty()){
                etClass.setError("Enter Class Name");
                return;
            }

            String imageName = saveImage(faceBitmap);

            new DataProcessor(context).registerFace(new StudentTest(
                    0,
                    name,
                    className,
                    imageName
            ));

            registeredListener.onRegister();

            dismiss();
        });


    }

    private String saveImage(Bitmap faceBitmap) {


        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/SchoolApp");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fName = "Image-" + n + ".jpg";
        File file = new File(myDir, fName);
        Log.i(TAG, "" + file);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            faceBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        return fName;
    }

    public interface OnStudentRegisteredListener{
        void onRegister();
    }

}
