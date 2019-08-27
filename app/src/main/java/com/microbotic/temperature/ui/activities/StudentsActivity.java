package com.microbotic.temperature.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microbotic.temperature.R;
import com.microbotic.temperature.app.ApiClient;
import com.microbotic.temperature.app.ApiInterface;
import com.microbotic.temperature.model.ClassModel;
import com.microbotic.temperature.model.Student;
import com.microbotic.temperature.ui.adapters.StudentListAdapter;
import com.microbotic.temperature.ui.window.RegisterClassWindow;
import com.microbotic.temperature.utils.SchoolDataProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.internal.EverythingIsNonNull;

public class StudentsActivity extends AppCompatActivity {

    private RecyclerView rvStudents;
    private ArrayList<Student> students;
    private StudentListAdapter studentsAdapter;
    private ProgressDialog progressDialog;
    private ArrayList<Student> sortedList;
    private Context context;
    private ArrayList<ClassModel> classes;
    private StudentListAdapter.OnStudentClickListener studentClickListener;
    private final String tag = StudentsActivity.class.getSimpleName();
    private SchoolDataProvider dataProvider;
    private TextView tvClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        context = StudentsActivity.this;
        dataProvider = new SchoolDataProvider(context);


        tvClass = findViewById(R.id.tvClass);

        rvStudents = findViewById(R.id.rvStudents);
        EditText etSearch = findViewById(R.id.etSearch);
        progressDialog = new ProgressDialog(StudentsActivity.this);

        studentClickListener = new StudentListAdapter.OnStudentClickListener() {
            @Override
            public void onStudentClick(Student student, int position) {
                Toast.makeText(context, student.getFirstName() + " " + student.getLastName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, TemperatureActivityOld.class);
                intent.putExtra("id", student.getStudentId());
                startActivity(intent);
            }
        };

        sortedList = new ArrayList<>();


        findViewById(R.id.tvClassAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterClassWindow.OnClassRegisterListener registerListener = new RegisterClassWindow.OnClassRegisterListener() {
                    @Override
                    public void onClassAdd() {
                        getClassList();
                    }
                };

                RegisterClassWindow classWindow = new RegisterClassWindow(context,registerListener);
                classWindow.setCancelable(false);
                classWindow.show();
            }
        });

        findViewById(R.id.imgBack).setOnClickListener(view -> finish());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                studentsAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });


        getClassList();
        getStudentList();
    }

    private void getStudentList() {
        progressDialog.show();
        progressDialog.setMessage("Please wait...");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getStudentsBySchool("list",dataProvider.getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();

                if (response.code() == 200 && response.body() != null) {

                    try {
                        students = new ArrayList<>();

                        String res = response.body().string();
                        JSONObject object = new JSONObject(res);
                        if (object.getInt("success")==1){

                            if(object.has("data")){
                                Type listType = new TypeToken<List<Student>>() {}.getType();
                                students = new Gson().fromJson(object.getJSONArray("data").toString(), listType);
                                if (students!=null){
                                    studentsAdapter = new StudentListAdapter(context, students, studentClickListener);
                                    rvStudents.setAdapter(studentsAdapter);
                                    studentsAdapter.notifyDataSetChanged();
                                    return;
                                }
                            }
                            Toast.makeText(context, "No Students found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Log.e(tag, "student error >> " + response.errorBody());
                Toast.makeText(context, "Unable to get student list", Toast.LENGTH_SHORT).show();
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(tag, "student fail >> " + t.getMessage());
                Toast.makeText(context, "Failed to get student list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getClassList() {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiInterface.getClassList("list",dataProvider.getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                if (response.code() == 200 && response.body() != null) {

                    try {

                        String res = response.body().string();

                        JSONObject object = new JSONObject(res);
                        if (object.getInt("success")==1){
                            if (object.has("data")){

                                Type listType = new TypeToken<List<ClassModel>>() {
                                }.getType();
                                classes = new Gson().fromJson(object.getJSONArray("data").toString(), listType);

                                if (classes != null) {
                                    final Spinner spinner = findViewById(R.id.spnClass);
                                    final ArrayList<String> classNames = new ArrayList<>();
                                    classNames.add("All Classes");
                                    for (ClassModel c : classes) {
                                        classNames.add(c.getName());
                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                                            R.layout.item_spinner_text,
                                            classNames);
                                    spinner.setAdapter(adapter);

                                    tvClass.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            spinner.performClick();
                                        }
                                    });

                                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                                            Log.e(tag,"Spinner click "+pos +" > "+classNames.get(pos));
                                            try {
                                                tvClass.setText(classNames.get(pos));
                                                studentsAdapter.showStudentsByClass(classNames.get(pos));
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });

                                    return;
                                }

                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Log.e(tag, "student error >> " + response.errorBody());
                Toast.makeText(context, "Unable to get class list", Toast.LENGTH_SHORT).show();
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(tag, "student fail >> " + t.getMessage());
                Toast.makeText(context, "Failed to get class list", Toast.LENGTH_SHORT).show();

            }
        });


    }

}


