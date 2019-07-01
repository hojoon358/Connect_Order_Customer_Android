package com.example.martin.connectorder.Data_Structures;

public class StoreData extends ContainerData {
    private int distance;
    private double latitude;
    private double longitude;
    private boolean checked;

    public StoreData(String subjectName, String description, String identifier, String image, int distance, boolean checked,double latitude,double longitude) {
        super(subjectName, description, identifier, ContainerData.STORE_DATA, image);
        this.checked = checked;
        this.distance = distance;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public StoreData(String subjectName, String description, int identifier, String image, int distance, boolean checked,double latitude,double longitude) {
        super(subjectName, description, Integer.toString(identifier), ContainerData.STORE_DATA, image);
        this.checked = checked;
        this.distance = distance;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
