package com.example.martin.connectorder.Data_Structures;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ItemDataChild extends ItemData {
    private int cost;
    private ArrayList<ItemOptionDataChild> itemOptionDataChildArrayList;
    private boolean expanded;

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public ItemDataChild(String subjectName, String description, String identifier, String image, int cost) {
        super(subjectName, description, identifier, image, ItemData.ITEM_DATA_CHILD);
        this.cost = cost;
    }

    public ItemDataChild(String subjectName, String description, int identifier, String image, int cost) {
        super(subjectName, description, Integer.toString(identifier), image, ItemData.ITEM_DATA_CHILD);
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setItemOptionDataChildArrayList(ArrayList<ItemOptionDataChild> itemOptionDataChildArrayList) {
        this.itemOptionDataChildArrayList = itemOptionDataChildArrayList;
    }

    public ArrayList<ItemOptionDataChild> getItemOptionDataChildArrayList() {
        return itemOptionDataChildArrayList;
    }
}

