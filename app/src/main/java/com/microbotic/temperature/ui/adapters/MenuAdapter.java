package com.microbotic.temperature.ui.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.microbotic.temperature.R;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

    private String[] titles;
    private int[] icons;
    private OnMenuOptionClickListener optionClickListener;

    public MenuAdapter(String[] titles, int[] icons, OnMenuOptionClickListener optionClickListener) {
        this.titles = titles;
        this.icons = icons;
        this.optionClickListener = optionClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {

        holder.imgIcon.setImageResource(icons[pos]);
        holder.tvTitle.setText(titles[pos]);

    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgIcon;
        private TextView tvTitle;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgIcon = itemView.findViewById(R.id.imgIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionClickListener.onOptionClick(getAdapterPosition());
                }
            });

        }
    }

    public interface OnMenuOptionClickListener {
        void onOptionClick(int pos);
    }

}
