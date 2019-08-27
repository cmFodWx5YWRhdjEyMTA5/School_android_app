package com.microbotic.temperature.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.microbotic.temperature.R;
import com.microbotic.temperature.app.ApiClient;
import com.microbotic.temperature.app.ApiInterface;
import com.microbotic.temperature.libraries.sweetAlert.SweetAlertDialog;
import com.microbotic.temperature.model.School;
import com.microbotic.temperature.utils.SchoolDataProvider;

import org.json.JSONObject;

import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;


public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editUsername, editPassword;
    private Context context;
    private final String tag = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        editUsername = findViewById(R.id.edit_user_name);
        editPassword = findViewById(R.id.edit_password);
        Button btnLogin = findViewById(R.id.button_login);

        context = LoginActivity.this;


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Objects.requireNonNull(editUsername.getText()).toString().trim();
                String password = Objects.requireNonNull(editPassword.getText()).toString().trim();

                if (editUsername.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(context, "Enter username", Toast.LENGTH_SHORT).show();
                } else if (editPassword.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(context, "Enter password", Toast.LENGTH_SHORT).show();
                } else {

                    login(username, password);
                }
            }
        });

    }

    private void login(final String username, final String password) {

        SweetAlertDialog alertDialog = new SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE);
        alertDialog.setTitle("Validating Account");
        alertDialog.setContentText("please wait");
        alertDialog.setCancelable(false);
        alertDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);//http://192.168.1.209/school/api/login.php
        Call<ResponseBody> call = apiInterface.login(username,password);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.code()==200){
                        assert response.body() != null;
                        String res = response.body().string();
                        Log.d("res....",res);
                        JSONObject object = new JSONObject(res);

                        if (object.getInt("success")==1){
                            School school = new Gson().fromJson(object.getJSONObject("data").toString(),School.class);

                            if (school !=null){
                                SchoolDataProvider dataProvider = new SchoolDataProvider(context);
                                dataProvider.saveSchool(username,password,school);

                                alertDialog.dismissWithAnimation();

                                startActivity(new Intent(context,MainActivity.class));
                                LoginActivity.this.finish();
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