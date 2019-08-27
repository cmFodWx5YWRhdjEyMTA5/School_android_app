package com.microbotic.temperature.ui.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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
import com.microbotic.temperature.model.Temperature;
import com.microbotic.temperature.ui.adapters.TempHistAdapter;
import com.microbotic.temperature.utils.SchoolDataProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.internal.EverythingIsNonNull;

public class TemperatureHistoryActivity extends AppCompatActivity {

    private Context context;
    private RecyclerView rvTemperature;
    private ArrayList<Temperature> temperatures;
    private TempHistAdapter tempHistAdapter;
    private TempHistAdapter.OnTemperatureClickListener temperatureClickListener;
    private final String tag = TemperatureHistoryActivity.class.getSimpleName();
    private String selectedDate = "";
    private int mYear, mMonth, mDay;
    private TextView tvDate, tvClass;
    private DateFormat printDateFormat, dateFormat;
    private SchoolDataProvider dataProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_history);

        context = TemperatureHistoryActivity.this;
        dataProvider = new SchoolDataProvider(context);


        printDateFormat = new SimpleDateFormat("EE dd MMM yy", Locale.ENGLISH);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        temperatures = new ArrayList<>();
        temperatureClickListener = new TempHistAdapter.OnTemperatureClickListener() {
            @Override
            public void onTemperatureClick(Temperature temperature, int pos) {

            }
        };

        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rvTemperature = findViewById(R.id.rvTemperature);
      /*  tempHistAdapter = new TempHistAdapter(context, temperatures, temperatureClickListener);*/
      /*  rvTemperature.setLayoutManager(new GridLayoutManager(context, 3));*/
      /*  rvTemperature.setAdapter(tempHistAdapter);*/
                                                                                                /**/
        tvClass = findViewById(R.id.tvClass);


        tvDate = findViewById(R.id.tvDate);

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker v, int year, int monthOfYear, int dayOfMonth) {
                                setDate(dayOfMonth, (monthOfYear + 1), year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();

            }
        });

        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tempHistAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 1);
        setDate(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));


        getTemperatureList();
    //   getClassList();
    }

    private void setDate(int day, int month, int year) {

        Date date = null;
        try {
            date = dateFormat.parse(year + "-" + month + "-" + day);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date == null) {
            Log.e(tag, "date is null");
            return;
        }

        String mDay, mMonth;

        if (month < 10) {
            mMonth = "0" + month;
        } else {
            mMonth = "" + month;
        }


        if (day < 10) {
            mDay = "0" + day;
        } else {
            mDay = "" + day;
        }


        this.selectedDate = year + "-" + mMonth + "-" + mDay;

        tvDate.setText(printDateFormat.format(date));

        Log.e(tag, "date " + date);
    }


    private void getClassList() {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiInterface.getClassList("list",dataProvider.getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {



                    try {
                        if (response.code() == 200 && response.body() != null) {

                        String res = response.body().string();
                        Log.d("res",res);
                        JSONObject object = new JSONObject(res);
                        if (object.getInt("success")==1){
                            if (object.has("data")){

                                Type listType = new TypeToken<List<ClassModel>>() {
                                }.getType();
                                ArrayList<ClassModel> classes = new Gson().fromJson(object.getJSONArray("data").toString(), listType);

                                if (classes != null) {
                                    final Spinner spinner = findViewById(R.id.spnClass);
                                    final ArrayList<String> classNames = new ArrayList<>();
                                    final ArrayList<String> classIds = new ArrayList<>();
                                    classNames.add("All Classes");
                                    classIds.add("All Classes");
                                    for (ClassModel c : classes) {
                                        classNames.add(c.getName());
                                        classIds.add(c.getId());
                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, classNames);
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
                                            tvClass.setText(classNames.get(pos));
                                            tempHistAdapter.showTemperatureByClass(classIds.get(pos));
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });

                                }

                            }
                        }


                    }

                } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();}
                        catch (Exception e) {
                    e.printStackTrace();
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

    private void getTemperatureList() {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiInterface.getTemperatureListBySchool("list",dataProvider.getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                if (response.code() == 200 && response.body() != null) {

                    try {

                        temperatures = new ArrayList<>();
                        String res = response.body().string();
                       Log.e("temp" , res);
                        JSONObject object = new JSONObject(res);

                           if (object.getInt("success")==1)
                        { if (object.has("data"))

                        { Type listType = new TypeToken<List<Temperature>>() {
                                }.getType();
                                temperatures = new Gson().fromJson(object.getJSONArray("data").toString(), listType);
                                if(temperatures!=null){
                            tempHistAdapter = new TempHistAdapter(context, temperatures, temperatureClickListener);
                            rvTemperature.setLayoutManager(new GridLayoutManager(context, 2));
                            rvTemperature.setAdapter(tempHistAdapter);

                            return;}}
                        }

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }

                Log.e(tag, "no temperature found " + response.errorBody());
                Toast.makeText(context, "unable to get temperature", Toast.LENGTH_SHORT).show();
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(tag, "temp fail >> " + t.getMessage());
                Toast.makeText(context, "Failed to get class list", Toast.LENGTH_SHORT).show();

            }
        });


    }

}



