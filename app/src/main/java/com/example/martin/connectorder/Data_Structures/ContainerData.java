package com.example.martin.connectorder.Data_Structures;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class ContainerData {
    public static final int STORE_DATA = 0;
    public static final int ITEM_DATA = 1;
    public static final int ITEM_OPTION_DATA = 2;
    private String subjectName;
    private String description;
    private String identifier;
    private int type;
    private String image;

    protected ContainerData(String subjectName, String description, String identifier, int type, String image) {
        this.subjectName = subjectName;
        this.description = description;
        this.identifier = identifier;
        this.type = type;
        this.image = image;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getDescription() {
        return description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getType() {
        return type;
    }

    public String getImage() {
        return image;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setImage(String image) {
        this.image = image;
    }
}







