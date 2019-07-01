package com.example.martin.connectorder.Data_Structures;

import java.util.ArrayList;

public class ItemOptionDataParent extends ItemOptionData {
    private boolean expanded;
    private ArrayList<ItemOptionDataChild> itemOptionDataChildren;

    public ItemOptionDataParent(String subjectName, String description, String identifier, String image, boolean expanded, ArrayList<ItemOptionDataChild> itemOptionDataChildren) {
        super(subjectName, description, identifier, image, ItemOptionData.ITEM_OPTION_DATA_PARENT);
        this.itemOptionDataChildren = itemOptionDataChildren;
        this.expanded = expanded;
    }

    public ItemOptionDataParent(String subjectName, String description, int identifier, String image, boolean expanded, ArrayList<ItemOptionDataChild> itemOptionDataChildren) {
        super(subjectName, description, Integer.toString(identifier), image, ItemOptionData.ITEM_OPTION_DATA_PARENT);
        this.itemOptionDataChildren = itemOptionDataChildren;
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public ArrayList<ItemOptionDataChild> getItemOptionDataChildren() {
        return itemOptionDataChildren;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void setItemOptionDataChildren(ArrayList<ItemOptionDataChild> itemOptionDataChildren) {
        this.itemOptionDataChildren = itemOptionDataChildren;
    }
}