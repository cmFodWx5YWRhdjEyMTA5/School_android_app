package com.microbotic.temperature.ui.window;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.microbotic.temperature.R;
import com.microbotic.temperature.model.StudentTest;
import com.microbotic.temperature.ui.adapters.TestStudentListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class FaceListWindow extends Dialog {

    private Activity activity;
    private RecyclerView rvStudents;
    private ArrayList<StudentTest> studentList;
    private final String tag = FaceListWindow.class.getSimpleName();

    public FaceListWindow(@NonNull Activity activity, ArrayList<StudentTest> studentList) {
        super(activity);
        this.activity = activity;
        this.studentList = studentList;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.window_face_list);
        Objects.requireNonNull(getWindow()).setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.window_bg));

        findViewById(R.id.tvTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        rvStudents = findViewById(R.id.rvStudents);
        getSavedImages();
        showStudentList();


    }

    private void showStudentList() {
        rvStudents.setLayoutManager(new LinearLayoutManager(activity));
        rvStudents.setAdapter(new TestStudentListAdapter(studentList));
    }

    private ArrayList<String> getSavedImages(){
        ArrayList<String> f = new ArrayList<String>();// list of file paths
        File[] listFile;

        File file= new File( Environment.getExternalStorageDirectory(),"SchoolApp");

        if (file.isDirectory())
        {
            listFile = file.listFiles();
            for (File file1 : listFile) {
                f.add(file1.getAbsolutePath());
                Log.e(tag,"path > "+file1.getAbsolutePath());
            }
        }

        return f;
    }


}
