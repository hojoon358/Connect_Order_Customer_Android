package com.example.martin.connectorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.connectorder.Data_Structures.ItemData;
import com.example.martin.connectorder.Data_Structures.ItemDataChild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

class StoreItemExpandableListAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<ItemData> storeItemList;
    private OnItemClickListener mListener;

    class LabelViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;


        public LabelViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.listTitle);
            image = itemView.findViewById(R.id.listImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onLabelClick(position);
                        }
                    }
                }
            });
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title, cost;
        ImageView image;

        public ItemViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.expandedListName);
            image = itemView.findViewById(R.id.expandedListImage);
            cost = itemView.findViewById(R.id.expandedListCost);

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
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onLabelClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public StoreItemExpandableListAdapter(Context context, ArrayList<ItemData> storeItemList) {
        this.storeItemList = storeItemList;
        this.context = context;
    }

    public void add(ItemData itemData) {
        storeItemList.add(itemData);
        notifyItemInserted(storeItemList.size() - 1);
    }

    public void clear() {
        storeItemList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return storeItemList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case ItemData.ITEM_DATA_PARENT:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.store_item_list_group, viewGroup, false);
                return new LabelViewHolder(view, mListener);
            case ItemData.ITEM_DATA_CHILD:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.store_item_list_item, viewGroup, false);
                return new ItemViewHolder(view, mListener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ItemData data = storeItemList.get(position);
        if (viewHolder != null) {
            switch (data.getType()) {
                case ItemData.ITEM_DATA_PARENT:
                    ((LabelViewHolder) viewHolder).title.setText(data.getSubjectName());
                    break;
                case ItemData.ITEM_DATA_CHILD:
                    ((ItemViewHolder) viewHolder).title.setText(data.getSubjectName());
                    ((ItemViewHolder) viewHolder).cost.setText(String.format(Locale.getDefault(),"%d",((ItemDataChild)data).getCost()));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return storeItemList.size();
    }
}

