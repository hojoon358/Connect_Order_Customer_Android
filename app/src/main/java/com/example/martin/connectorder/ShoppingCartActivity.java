package com.example.martin.connectorder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.connectorder.Data_Structures.ContainerData;
import com.example.martin.connectorder.Data_Structures.ItemDataChild;
import com.example.martin.connectorder.Data_Structures.ItemOptionDataChild;
import com.example.martin.connectorder.Data_Structures.ItemOptionDataParent;
import com.example.martin.connectorder.Data_Structures.StoreData;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class ShoppingCartActivity extends AppCompatActivity {
    StoreData storeData = ShoppingCartData.getStoreData();
    ShoppingCartListAdapter listAdapter;
    ArrayList<ContainerData> shoppingList = ShoppingCartData.getShoppingList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        Toolbar myToolbar = findViewById(R.id.temp_action_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);

        // use later if necessary (probably not)
        /*
        Bundle extras = getIntent().getExtras();
        mainListData = getIntent().getParcelableExtra("MainListData");
        storeItemDataArrayList = getIntent().getParcelableArrayListExtra("StoreItemData");
        final int storeItemDataArrayListPosition = extras.getInt("StoreItemDataPosition");
        itemOptionDataArrayList = getIntent().getParcelableArrayListExtra("StoreItemOptionData");
        itemOptionDataListSelected = getIntent().getParcelableArrayListExtra("itemOptionDataListSelected");
        */

        TextView storename = findViewById(R.id.storename);
        final TextView shoppingprice = findViewById(R.id.shoppingprice);

        storename.setText(storeData.getSubjectName());
        shoppingprice.setText(String.valueOf(ShoppingCartData.getSum()));

        final RecyclerView expandableShoppingList = findViewById(R.id.shoppinglist);
        listAdapter = new ShoppingCartListAdapter(this);
        RecyclerView.LayoutManager manager = new LinearLayoutManagerWithSmoothScroller(this);
        expandableShoppingList.setLayoutManager(manager);
        expandableShoppingList.setAdapter(listAdapter);

        listAdapter.setOnItemClickListener(new ShoppingCartListAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {

            }

            @Override
            public void onDeleteClick(int position) {
                ItemDataChild itemDataChild = (ItemDataChild) shoppingList.get(position);
                if (itemDataChild.isExpanded()) {
                    int temp = itemDataChild.getItemOptionDataChildArrayList().size();
                    shoppingList.subList(position + 1, position + temp + 1).clear();
                    listAdapter.notifyItemRangeRemoved(position + 1, temp);
                    itemDataChild.setExpanded(false);
                }
                ShoppingCartData.removeItem(itemDataChild);
                listAdapter.notifyItemRemoved(position);
                shoppingprice.setText(String.valueOf(ShoppingCartData.getSum()));
            }

            @Override
            public void onDecreaseClick(int position) {
                ItemDataChild itemDataChild = (ItemDataChild) shoppingList.get(position);
                int temp = itemDataChild.getQuantity();
                if (temp > 1) {
                    temp--;
                    int cost = temp * itemDataChild.getTotalCost() / (temp + 1);
                    ShoppingCartData.reduceSum(itemDataChild.getTotalCost());
                    itemDataChild.setQuantity(temp);
                    itemDataChild.setTotalCost(cost);
                    ShoppingCartData.increaseSum(cost);
                    listAdapter.notifyItemChanged(position);
                    shoppingprice.setText(String.valueOf(ShoppingCartData.getSum()));
                }
            }

            @Override
            public void onIncreaseClick(int position) {
                ItemDataChild itemDataChild = (ItemDataChild) shoppingList.get(position);
                int temp = itemDataChild.getQuantity() + 1;
                int cost = temp * itemDataChild.getTotalCost() / (temp - 1);
                ShoppingCartData.reduceSum(itemDataChild.getTotalCost());
                itemDataChild.setQuantity(temp);
                itemDataChild.setTotalCost(cost);
                ShoppingCartData.increaseSum(cost);
                listAdapter.notifyItemChanged(position);
                shoppingprice.setText(String.valueOf(ShoppingCartData.getSum()));
            }

            @Override
            public void onLabelClick(int position) {
                ItemDataChild label = (ItemDataChild) shoppingList.get(position);
                ArrayList<ItemOptionDataChild> itemoptions = label.getItemOptionDataChildArrayList();
                if (!label.isExpanded()) {
                    shoppingList.addAll(position + 1, itemoptions);
                    listAdapter.notifyItemRangeInserted(position + 1, itemoptions.size());
                    label.setExpanded(true);
                    if (shoppingList.size() == label.getItemOptionDataChildArrayList().size() + position + 1) {
                        ((RecyclerView) findViewById(R.id.shoppinglist)).smoothScrollToPosition(shoppingList.size() - 1);
                    }
                } else {
                    shoppingList.subList(position + 1, position + itemoptions.size() + 1).clear();
                    listAdapter.notifyItemRangeRemoved(position + 1, itemoptions.size());
                    label.setExpanded(false);
                }
            }
        });
    }

    public void titleClick(View view) {
        Toast.makeText(ShoppingCartActivity.this, "You have clicked title", Toast.LENGTH_LONG).show();
        ((RecyclerView) findViewById(R.id.shoppinglist)).smoothScrollToPosition(0);
    }
}

