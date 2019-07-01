package com.example.martin.connectorder.Data_Structures;

public abstract class ItemData extends ContainerData {
    public static final int ITEM_DATA_PARENT = 3;
    public static final int ITEM_DATA_CHILD = 4;
    private int type;
    private int totalCost = 0;
    private int quantity = 0;

    ItemData(String subjectName, String description, String identifier, String image, int type) {
        super(subjectName, description, identifier, ContainerData.ITEM_DATA, image);
        this.type = type;
    }

    ItemData(String subjectName, String description, int identifier, String image, int type) {
        super(subjectName, description, Integer.toString(identifier), ContainerData.ITEM_DATA, image);
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
