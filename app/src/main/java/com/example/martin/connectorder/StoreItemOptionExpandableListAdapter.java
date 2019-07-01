
package com.example.martin.connectorder;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.support.annotation.NonNull;

import com.example.martin.connectorder.Data_Structures.ItemOptionData;
import com.example.martin.connectorder.Data_Structures.ItemOptionDataChild;

class StoreItemOptionExpandableListAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<ItemOptionData> itemOptionDataArrayList;
    private OnItemClickListener mListener;

    class LabelViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        TextView label;


        public LabelViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.listTitle);
            image = itemView.findViewById(R.id.listImage);
            label = itemView.findViewById(R.id.listLabel);

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

    class ItemViewHolderMandatory extends RecyclerView.ViewHolder {
        TextView title, cost;
        RadioButton radioButton;

        public ItemViewHolderMandatory(View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.expandedListName);
            cost = itemView.findViewById(R.id.expandedListCost);
            radioButton = itemView.findViewById(R.id.checkButton);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onRadioClick(position);
                        }
                    }
                }
            });
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title, cost;
        CheckBox checkBox;

        public ItemViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.expandedListName);
            cost = itemView.findViewById(R.id.expandedListCost);
            checkBox = itemView.findViewById(R.id.checkButton);

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
        }
    }

    public interface OnItemClickListener {

        void onCheckClick(int position);

        void onRadioClick(int position);

        void onLabelClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public StoreItemOptionExpandableListAdapter(Context context, ArrayList<ItemOptionData> itemOptionDataArrayList) {
        this.itemOptionDataArrayList = itemOptionDataArrayList;
        this.context = context;
    }

    public void add(ItemOptionData itemOptionData) {
        itemOptionDataArrayList.add(itemOptionData);
        notifyItemInserted(itemOptionDataArrayList.size() - 1);
    }

    public void clear() {
        itemOptionDataArrayList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return itemOptionDataArrayList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case ItemOptionData.ITEM_OPTION_DATA_PARENT:
            case ItemOptionData.ITEM_OPTION_DATA_PARENT_MANDATORY:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.store_item_option_label_list_item, viewGroup, false);
                return new LabelViewHolder(view, mListener);
            case ItemOptionData.ITEM_OPTION_DATA_CHILD:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.store_item_option_list_item, viewGroup, false);
                return new ItemViewHolder(view, mListener);
            case ItemOptionData.ITEM_OPTION_DATA_CHILD_MANDATORY:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.store_item_option_mandatory_list_item, viewGroup, false);
                return new ItemViewHolderMandatory(view, mListener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ItemOptionData data = itemOptionDataArrayList.get(position);
        if (viewHolder != null) {
            switch (data.getType()) {
                case ItemOptionData.ITEM_OPTION_DATA_PARENT_MANDATORY:
                    ((LabelViewHolder) viewHolder).title.setText(data.getSubjectName());
                    ((LabelViewHolder) viewHolder).label.setText(R.string.mandatory);
                    break;
                case ItemOptionData.ITEM_OPTION_DATA_PARENT:
                    ((LabelViewHolder) viewHolder).title.setText(data.getSubjectName());
                    ((LabelViewHolder) viewHolder).label.setText(R.string.optional);
                    break;
                case ItemOptionData.ITEM_OPTION_DATA_CHILD:
                    ((ItemViewHolder) viewHolder).title.setText(data.getSubjectName());
                    ((ItemViewHolder) viewHolder).cost.setText(String.format(Locale.getDefault(), "%d", ((ItemOptionDataChild) data).getCost()));
                    ((ItemViewHolder) viewHolder).checkBox.setChecked(((ItemOptionDataChild) data).isChecked());
                    break;
                case ItemOptionData.ITEM_OPTION_DATA_CHILD_MANDATORY:
                    ((ItemViewHolderMandatory) viewHolder).title.setText(data.getSubjectName());
                    ((ItemViewHolderMandatory) viewHolder).cost.setText(String.format(Locale.getDefault(), "%d", ((ItemOptionDataChild) data).getCost()));
                    ((ItemViewHolderMandatory) viewHolder).radioButton.setChecked(((ItemOptionDataChild) data).isChecked());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemOptionDataArrayList.size();
    }
}

