package com.microbotic.temperature.ui.window;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
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
import com.microbotic.temperature.R;
import com.microbotic.temperature.app.ApiClient;
import com.microbotic.temperature.app.ApiInterface;
import com.microbotic.temperature.app.DataProcessor;
import com.microbotic.temperature.libraries.sweetAlert.SweetAlertDialog;
import com.microbotic.temperature.model.StudentTest;
import com.microbotic.temperature.utils.SchoolDataProvider;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.microbotic.temperature.ui.activities.TemperatureActivity.getFaceBitmap;

public class RegisterClassWindow extends Dialog {

    private Context context;
    private OnClassRegisterListener registeredListener;
    private final String TAG = RegisterClassWindow.class.getSimpleName();
    private boolean isClassCreated = false;
    private  EditText etClass,etTeacher;

    public RegisterClassWindow(@NonNull Context context, OnClassRegisterListener registeredListener) {
        super(context);
        this.context = context;
        this.registeredListener = registeredListener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.window_register_class);
        Objects.requireNonNull(getWindow()).setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(context.getResources().getDrawable(R.drawable.window_bg));

        findViewById(R.id.tvTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClassCreated){
                    registeredListener.onClassAdd();
                }
                dismiss();
            }
        });


        etClass = findViewById(R.id.etClass);
        etTeacher = findViewById(R.id.etTeacher);


        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = etClass.getText().toString().trim();
                String teacherName = etTeacher.getText().toString().trim();

                if (className.isEmpty()){
                    etClass.setError("Enter class name");
                    return;
                }

                if (teacherName.isEmpty()){
                    etTeacher.setError("Enter Teacher Name");
                    return;
                }

                createClass(className,teacherName);
            }
        });

    }

    private void createClass(String className, String teacherName) {

        SweetAlertDialog alertDialog = new SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE);
        alertDialog.setTitle("Loading");
        alertDialog.setContentText("creating new class");
        alertDialog.setCancelable(false);
        alertDialog.show();


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        SchoolDataProvider dataProvider = new SchoolDataProvider(context);

        JSONObject object = new JSONObject();
        try {
            object.put("class_name",className);
            object.put("teacher_name",teacherName);
        }catch (Exception e){
            e.printStackTrace();
        }
        Call<ResponseBody> call = apiInterface.addClass("add",dataProvider.getId(),object.toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200 && response.body()!=null){
                    try {

                        JSONObject o = new JSONObject(response.body().string());

                        if (o.getInt("success")==1){
                            isClassCreated = true;

                            etClass.setText("");
                            etTeacher.setText("");

                            alertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            alertDialog.setTitle("Success");
                            alertDialog.setContentText("Class created successfully");
                            alertDialog.setConfirmText("ok");
                            alertDialog.setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);


                        }


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }


    public interface OnClassRegisterListener {
        void onClassAdd();
    }

}
