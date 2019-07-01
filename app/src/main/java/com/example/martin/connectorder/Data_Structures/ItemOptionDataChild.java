package com.example.martin.connectorder.Data_Structures;

import java.util.ArrayList;

public class ItemOptionDataChild extends ItemOptionData {
    private int cost;
    private boolean checked;
    private int position;

    public ItemOptionDataChild(String subjectName, String description, String identifier, String image, int cost, boolean checked, int position, int type) {
        super(subjectName, description, identifier, image, type);
        this.cost = cost;
        this.checked = checked;
        this.position = position;
    }

    public ItemOptionDataChild(String subjectName, String description, int identifier, String image, int cost, boolean checked, int position, int type) {
        super(subjectName, description, Integer.toString(identifier), image, type);
        this.cost = cost;
        this.checked = checked;
        this.position = position;
    }

    public int getCost() {
        return cost;
    }

    public boolean isChecked() {
        return checked;
    }

    public int getPosition() {
        return position;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}