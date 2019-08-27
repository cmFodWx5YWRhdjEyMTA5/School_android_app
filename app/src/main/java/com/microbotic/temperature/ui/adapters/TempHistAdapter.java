package com.microbotic.temperature.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.microbotic.temperature.R;
import com.microbotic.temperature.model.Temperature;

import java.util.ArrayList;

public class TempHistAdapter extends RecyclerView.Adapter<TempHistAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Temperature> temperatures;
    private ArrayList<Temperature> sortedList;
    private OnTemperatureClickListener temperatureClickListener;


    public TempHistAdapter(Context context, ArrayList<Temperature> temperatures, OnTemperatureClickListener temperatureClickListener) {
        this.context = context;
        this.temperatures = temperatures;
        this.sortedList = temperatures;
        this.temperatureClickListener = temperatureClickListener;
    }


    @NonNull
    @Override
    public TempHistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_temperature, parent, false);
        return new MyViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull final TempHistAdapter.MyViewHolder holder, final int position) {

        final Temperature temperature = temperatures.get(position);
        holder.tvTemperature.setText(temperature.getTemperature() + "");
        holder.tvDate.setText(temperature.getDate());
        String name = temperature.getFirstName() + " " + temperature.getLastName();
        holder.tvName.setText(temperature.getFirstName());
        holder.tvClass.setText(temperature.getDate());

    }


    @Override
    public int getItemCount() {
        return temperatures.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvClass, tvTemperature, tvDate;
        private ImageView imgStudent;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClass = itemView.findViewById(R.id.tvClass);
            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTemperature = itemView.findViewById(R.id.tvTemperature);
            imgStudent = itemView.findViewById(R.id.imgStudent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    temperatureClickListener.onTemperatureClick(temperatures.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    public void showTemperatureByClass(String classId) {
        if (classId.isEmpty() || classId.equals("All Classes")) {
            sortedList = temperatures;
        } else {
            ArrayList<Temperature> filteredList = new ArrayList<>();
            for (Temperature temperature : temperatures) {
                if (temperature.getClassId().equals(classId)) {
                    filteredList.add(temperature);
                }
            }
            sortedList = filteredList;
        }
        notifyDataSetChanged();
    }

    public interface OnTemperatureClickListener {

        void onTemperatureClick(Temperature temperature, int pos);

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String text = charSequence.toString();
                if (text.isEmpty()) {
                    sortedList = temperatures;
                } else {
                    ArrayList<Temperature> filteredList = new ArrayList<>();
                    for (Temperature temperature : temperatures) {

                        if (temperature.getFirstName().contains(text) || temperature.getLastName().contains(text)) {
                            filteredList.add(temperature);
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
                sortedList = (ArrayList<Temperature>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
