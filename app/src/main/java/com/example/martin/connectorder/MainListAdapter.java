package com.example.martin.connectorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.connectorder.Data_Structures.StoreData;

import java.util.ArrayList;
import java.util.List;

class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.MyViewHolder> {
    ArrayList<StoreData> arrayList;
    Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onCheckClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public MainListAdapter(Context context, ArrayList<StoreData> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    public void add(StoreData storeData) {
        arrayList.add(storeData);
        notifyItemInserted(arrayList.size() - 1);
    }

    public void clear() {
        arrayList.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<StoreData> storeDataArrayList) {
        arrayList.addAll(storeDataArrayList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.main_list_row, viewGroup, false);
        return new MyViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        StoreData data = arrayList.get(i);
        viewHolder.title.setText(data.getSubjectName());
        viewHolder.description.setText(data.getDescription());
        viewHolder.checkBox.setChecked(data.isChecked());
        viewHolder.distance.setText(data.getDistance() + " " + context.getString(R.string.km));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, distance;
        CheckBox checkBox;

        public MyViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            checkBox = itemView.findViewById(R.id.storecheckbox);
            distance = itemView.findViewById(R.id.storedistance);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onCheckClick(position);
                        }
                    }
                }
            });
        }
    }
}