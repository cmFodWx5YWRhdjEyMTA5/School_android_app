package com.microbotic.temperature.ui.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.microbotic.temperature.R;
import com.microbotic.temperature.model.Student;

import java.util.ArrayList;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Student> students;
    private ArrayList<Student> sortedList;

    private OnStudentClickListener onStudentClickListener;

    public StudentListAdapter(Context context, ArrayList<Student> students, OnStudentClickListener onStudentClickListener) {
        this.context = context;
        this.students = students;
        this.sortedList = students;
        this.onStudentClickListener = onStudentClickListener;
    }

    @NonNull
    @Override
    public StudentListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final StudentListAdapter.MyViewHolder holder, int position) {

        final Student student = sortedList.get(position);
        String name = student.getFirstName() + " " + student.getLastName();
        holder.tvName.setText(name);
        holder.tvClass.setText(student.getStdClass());


    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public void showStudentsByClass(String className) {
        Log.e("studentAdapter","sort "+className);
        if (className.isEmpty() || className.equals("All Classes")) {
            sortedList = students;
        } else {
            ArrayList<Student> filteredList = new ArrayList<>();
            for (Student student : students) {
                if (student.getStdClass().equals(className)) {
                    filteredList.add(student);
                }
            }
            sortedList = filteredList;
        }
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvClass;
        private ImageView imgPhoto;

        MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            imgPhoto = itemView.findViewById(R.id.imgStudent);
            tvClass = itemView.findViewById(R.id.tvClass);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onStudentClickListener.onStudentClick(students.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    public interface OnStudentClickListener {
        void onStudentClick(Student student, int position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String text = charSequence.toString();
                if (text.isEmpty()) {
                    sortedList = students;
                } else {
                    ArrayList<Student> filteredList = new ArrayList<>();
                    for (Student student : students) {

                        if (student.getFirstName().contains(text) || student.getLastName().contains(text) || student.getStdClass().contains(text)) {
                            sortedList.add(student);
                        }
                    }
                    sortedList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = sortedList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                sortedList = (ArrayList<Student>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}