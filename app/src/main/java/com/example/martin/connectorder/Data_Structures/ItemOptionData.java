package com.example.martin.connectorder.Data_Structures;

public class ItemOptionData extends ContainerData {
    public static final int ITEM_OPTION_DATA_PARENT = 5;
    public static final int ITEM_OPTION_DATA_CHILD = 6;
    public static final int ITEM_OPTION_DATA_PARENT_MANDATORY = 7;
    public static final int ITEM_OPTION_DATA_CHILD_MANDATORY = 8;
    private int type;

    ItemOptionData(String subjectName, String description, String identifier, String image, int type) {
        super(subjectName, description, identifier, ContainerData.ITEM_OPTION_DATA, image);
        this.type = type;
    }

    ItemOptionData(String subjectName, String description, int identifier, String image, int type) {
        super(subjectName, description, Integer.toString(identifier), ContainerData.ITEM_OPTION_DATA, image);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}