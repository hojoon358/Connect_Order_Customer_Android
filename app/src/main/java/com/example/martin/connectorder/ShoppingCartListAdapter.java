package com.example.martin.connectorder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.support.annotation.NonNull;

import com.example.martin.connectorder.Data_Structures.ContainerData;
import com.example.martin.connectorder.Data_Structures.ItemData;
import com.example.martin.connectorder.Data_Structures.ItemDataChild;
import com.example.martin.connectorder.Data_Structures.ItemOptionData;
import com.example.martin.connectorder.Data_Structures.ItemOptionDataChild;

import java.util.List;
import java.util.Locale;

class ShoppingCartListAdapter extends RecyclerView.Adapter {
    private Context context;
    ArrayList<ContainerData> shoppingList = ShoppingCartData.getShoppingList();
    private OnItemClickListener mListener;

    class LabelViewHolder extends RecyclerView.ViewHolder {
        TextView itemname, itemquantity, itemcost;
        Button editbutton, deletebutton, decreasebutton, increasebutton;

        public LabelViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemname = itemView.findViewById(R.id.itemname);
            itemquantity = itemView.findViewById(R.id.itemquantity);
            itemcost = itemView.findViewById(R.id.itemcost);
            editbutton = itemView.findViewById(R.id.editbutton);
            deletebutton = itemView.findViewById(R.id.deletebutton);
            decreasebutton = itemView.findViewById(R.id.decreasebutton);
            increasebutton = itemView.findViewById(R.id.increasebutton);

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
            editbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onEditClick(position);
                        }
                    }
                }
            });
            deletebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
            decreasebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDecreaseClick(position);
                        }
                    }
                }
            });
            increasebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onIncreaseClick(position);
                        }
                    }
                }
            });
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title, cost;

        public ItemViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.optionname);
            cost = itemView.findViewById(R.id.optioncost);
            //use if anything should be clicked here
            /*
            itemView.setOnClickListener(new View.OnClickListener() {
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
            */
        }
    }

    public interface OnItemClickListener {

        void onEditClick(int position);

        void onDeleteClick(int position);

        void onDecreaseClick(int position);

        void onIncreaseClick(int position);

        void onLabelClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ShoppingCartListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return ((ContainerData) shoppingList.get(position)).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case ItemData.ITEM_DATA_CHILD:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.shopping_cart_list_label, viewGroup, false);
                return new LabelViewHolder(view, mListener);
            case ItemOptionData.ITEM_OPTION_DATA_CHILD:
            case ItemOptionData.ITEM_OPTION_DATA_CHILD_MANDATORY:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.shopping_cart_item, viewGroup, false);
                return new ItemViewHolder(view, mListener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ContainerData data = shoppingList.get(position);
        if (viewHolder != null) {
            switch (data.getType()) {
                case ItemData.ITEM_DATA_CHILD:
                    ItemDataChild itemData = (ItemDataChild) data;
                    ((LabelViewHolder) viewHolder).itemname.setText(itemData.getSubjectName());
                    ((LabelViewHolder) viewHolder).itemcost.setText(String.format(Locale.getDefault(), "%d", ((ItemDataChild) data).getTotalCost()));
                    ((LabelViewHolder) viewHolder).itemquantity.setText(String.valueOf(itemData.getQuantity()));
                    break;
                case ItemOptionData.ITEM_OPTION_DATA_CHILD:
                case ItemOptionData.ITEM_OPTION_DATA_CHILD_MANDATORY:
                    ((ItemViewHolder) viewHolder).title.setText(data.getSubjectName());
                    ((ItemViewHolder) viewHolder).cost.setText(String.format(Locale.getDefault(), "%d", ((ItemOptionDataChild) data).getCost()));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }
}

