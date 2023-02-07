package com.example.gender_age_detection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Myadapter extends RecyclerView.Adapter<Myadapter.MyViewHolder> {

    Context context;
    ArrayList<userModel> list;

    public Myadapter(Context context, ArrayList<userModel> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.userrecord,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        userModel user = list.get(position);

        holder.age.setText(String.format("Age: %s", user.getAge()));
        holder.gender.setText(String.format("Gender: %s", user.getGender()));
        holder.date.setText(String.format("Time: %s", user.getTimeStamp()));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView age,gender,date;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            age = itemView.findViewById(R.id.tv_age);
            gender = itemView.findViewById(R.id.tv_gender);
            date = itemView.findViewById(R.id.tv_date);
        }
    }
}
