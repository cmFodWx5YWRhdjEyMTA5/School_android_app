package com.microbotic.temperature.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.microbotic.temperature.R;
import com.microbotic.temperature.model.StudentTest;

import java.util.ArrayList;

public class TestStudentListAdapter extends RecyclerView.Adapter<TestStudentListAdapter.MyViewHolder> {

    private ArrayList<StudentTest> list;
    public TestStudentListAdapter(ArrayList<StudentTest> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        StudentTest student = list.get(position);
        if (student.getPhoto()!=null){
            holder.imgStudent.setImageBitmap(student.getPhoto());
        }else {
            holder.imgStudent.setImageResource(R.drawable.img_student);
        }
        holder.tvName.setText(student.getName());
        holder.tvClass.setText(student.getClassName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName,tvClass;
        private ImageView imgStudent;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvClass = itemView.findViewById(R.id.tvClass);
            imgStudent = itemView.findViewById(R.id.imgStudent);

        }
    }
}
