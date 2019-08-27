package com.microbotic.temperature.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.microbotic.temperature.R;
import com.microbotic.temperature.app.ApiClient;
import com.microbotic.temperature.app.ApiInterface;
import com.microbotic.temperature.libraries.sweetAlert.SweetAlertDialog;
import com.microbotic.temperature.model.School;
import com.microbotic.temperature.utils.SchoolDataProvider;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class SplashActivity extends AppCompatActivity {

    private Context context;
    private SweetAlertDialog progressDialog;
    private final String tag = SplashActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = SplashActivity.this;


        progressDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setTitleText("Validating Credentials");


        SchoolDataProvider dataProvider = new SchoolDataProvider(context);
        if (dataProvider.isLoggedIn()) {
            login(dataProvider.getUsername(), dataProvider.getPassword());
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1500);
        }

    }


    private void login(final String username, final String password) {

        SweetAlertDialog alertDialog = new SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE);
        alertDialog.setTitle("Validating Account");
        alertDialog.setContentText("please wait");
        alertDialog.setCancelable(false);
        alertDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiInterface.login(username,password);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.code()==200){
                        assert response.body() != null;
                        String res = response.body().string();
                        JSONObject object = new JSONObject(res);

                        if (object.getInt("success")==1){
                            School school = new Gson().fromJson(object.getJSONObject("data").toString(),School.class);

                            if (school !=null){
                                SchoolDataProvider dataProvider = new SchoolDataProvider(context);
                                dataProvider.saveSchool(username,password,school);

                                alertDialog.dismissWithAnimation();

                                startActivity(new Intent(context,MainActivity.class));
                                SplashActivity.this.finish();
                                return;
                            }

                        }




                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                alertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                alertDialog.setTitle("Login Failed");
                alertDialog.setContentText("Invalid username or password");
                alertDialog.setConfirmText("OK");
                alertDialog.setConfirmClickListener(sweetAlertDialog -> alertDialog.dismissWithAnimation());
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                alertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                alertDialog.setTitle("Login Failed");
                alertDialog.setContentText("Cannot connect to server\nplease check your internet connection and try agian");
                alertDialog.setConfirmText("Retry");
                alertDialog.setConfirmClickListener(sweetAlertDialog -> login(username,password));
                alertDialog.setConfirmText("Cancel");
                alertDialog.setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);

            }
        });
    }

}


