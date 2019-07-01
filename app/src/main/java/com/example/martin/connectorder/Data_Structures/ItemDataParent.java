package com.example.martin.connectorder.Data_Structures;

import java.util.ArrayList;

public class ItemDataParent extends ItemData {
    private boolean expanded;
    private ArrayList<ItemDataChild> itemDataChildren;

    public ItemDataParent(String subjectName, String description, String identifier, String image, boolean expanded, ArrayList<ItemDataChild> itemDataChildren) {
        super(subjectName, description, identifier, image, ItemData.ITEM_DATA_PARENT);
        this.itemDataChildren = itemDataChildren;
        this.expanded = expanded;
    }

    public ItemDataParent(String subjectName, String description, int identifier, String image, boolean expanded, ArrayList<ItemDataChild> itemDataChildren) {
        super(subjectName, description, Integer.toString(identifier), image, ItemData.ITEM_DATA_PARENT);
        this.itemDataChildren = itemDataChildren;
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public ArrayList<ItemDataChild> getItemDataChildren() {
        return itemDataChildren;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void setItemDataChildren(ArrayList<ItemDataChild> itemDataChildren) {
        this.itemDataChildren = itemDataChildren;
    }
}
