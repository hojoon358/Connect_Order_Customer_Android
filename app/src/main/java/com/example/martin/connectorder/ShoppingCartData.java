package com.example.martin.connectorder;

import com.example.martin.connectorder.Data_Structures.ContainerData;
import com.example.martin.connectorder.Data_Structures.ItemData;
import com.example.martin.connectorder.Data_Structures.ItemDataChild;
import com.example.martin.connectorder.Data_Structures.ItemOptionData;
import com.example.martin.connectorder.Data_Structures.ItemOptionDataChild;
import com.example.martin.connectorder.Data_Structures.StoreData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ShoppingCartData {
    private static StoreData storeData = new StoreData("no store selected <placeholder>", "", 0, "", 0, false,0,0);
    private static int sum = 0;
    private static ArrayList<ContainerData> shoppingList = new ArrayList<>();
    private static HashMap<ItemData, ArrayList<ItemOptionData>> itemList = new HashMap<>();
    private static boolean initialized = false;
    private static ItemDataChild tempItemDataChild;

    public static void setTempItemDataChild(ItemDataChild tempItemDataChild) {
        ShoppingCartData.tempItemDataChild = tempItemDataChild;
    }

    public static ItemDataChild getTempItemDataChild() {
        return tempItemDataChild;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void setInitialized(boolean initialized) {
        ShoppingCartData.initialized = initialized;
    }

    public static StoreData getStoreData() {
        return storeData;
    }

    public static int getSum() {
        return sum;
    }

    public static void setStoreData(StoreData storeData) {
        ShoppingCartData.storeData = storeData;
    }

    static void addItem(ItemDataChild itemData, ArrayList<ItemOptionDataChild> itemOptionDataChildrenSelected, ArrayList<ItemOptionData> itemOptionDataArrayList) {
        itemList.put(itemData, itemOptionDataArrayList);
        itemData.setItemOptionDataChildArrayList(itemOptionDataChildrenSelected);
        itemData.setExpanded(false);
        shoppingList.add(itemData);
        sum += itemData.getTotalCost();
    }

    static void reduceSum(int amount) {
        sum -= amount;
    }

    static void increaseSum(int amount) {
        sum += amount;
    }

    static void removeItem(ItemDataChild itemDataChild) {
        itemList.remove(itemDataChild);
        shoppingList.remove(itemDataChild);
        sum -= itemDataChild.getTotalCost();
    }

    public static ArrayList<ContainerData> getShoppingList() {
        return shoppingList;
    }

    static void clearCart() {
        storeData = new StoreData("no store selected <placeholder>", "", 0, "", 0, false,0,0);
        sum = 0;
        shoppingList = new ArrayList<>();
        itemList = new HashMap<>();
        initialized = false;
        tempItemDataChild = null;
    }
}
