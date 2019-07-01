package com.example.martin.connectorder;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.ListItemDataParentTestsQuery;
import com.amazonaws.amplify.generated.graphql.ListItemOptionDataParentTestsQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.martin.connectorder.Data_Structures.ItemData;
import com.example.martin.connectorder.Data_Structures.ItemDataChild;
import com.example.martin.connectorder.Data_Structures.ItemDataParent;
import com.example.martin.connectorder.Data_Structures.ItemOptionData;
import com.example.martin.connectorder.Data_Structures.ItemOptionDataChild;
import com.example.martin.connectorder.Data_Structures.ItemOptionDataParent;
import com.example.martin.connectorder.Data_Structures.StoreData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import type.ModelIDFilterInput;
import type.ModelItemDataParentTestFilterInput;
import type.ModelItemOptionDataParentTestFilterInput;

public class StoreItemActivity extends AppCompatActivity {
    StoreItemOptionExpandableListAdapter expandableListAdapter;
    ArrayList<ItemOptionData> itemOptionDataList = new ArrayList<>();
    StoreData storeData;
    ArrayList<ItemData> storeItemDataArrayList;
    ArrayList<ItemOptionDataChild> itemOptionDataListSelected = new ArrayList<>();
    ItemDataChild itemDataChild;
    private AWSAppSyncClient mAWSAppSyncClient;
    ArrayList<ListItemOptionDataParentTestsQuery.Item> queriedItemOptions;
    int queryLimit = 1000;
    SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_item);

        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        Toolbar myToolbar = findViewById(R.id.temp_action_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);

        Button increaseQuantity = findViewById(R.id.itemcountincrease);
        Button decreaseQuantity = findViewById(R.id.itemcountdecrease);
        final TextView quantity = findViewById(R.id.itemcount);
        final TextView itemcost = findViewById(R.id.itemcost);
        Button putInBag = findViewById(R.id.putinbag);

        storeData = ShoppingCartData.getStoreData();
        itemDataChild = ShoppingCartData.getTempItemDataChild();
        String SubjectName = itemDataChild.getSubjectName();
        int Cost = itemDataChild.getCost();

        ((TextView) findViewById(R.id.itemname)).setText(SubjectName);
        itemcost.setText("0");
        quantity.setText("1");
        itemDataChild.setQuantity(1);
        updateCost(Cost, quantity, itemcost);

        RecyclerView expandableListView = findViewById(R.id.itemoptionlist);

        // need to set one of the mandatory options as checked (set the info in the server when adding the data)
        ArrayList<ItemOptionDataChild> itemData1 = new ArrayList<>();
        itemData1.add(new ItemOptionDataChild("large", "", 1, "", 500, false, 1, ItemOptionData.ITEM_OPTION_DATA_CHILD_MANDATORY));
        itemData1.add(new ItemOptionDataChild("medium", "", 1, "", 300, false, 2, ItemOptionData.ITEM_OPTION_DATA_CHILD_MANDATORY));
        itemData1.add(new ItemOptionDataChild("small", "", 1, "", 200, true, 3, ItemOptionData.ITEM_OPTION_DATA_CHILD_MANDATORY));
        ArrayList<ItemOptionDataChild> itemData2 = new ArrayList<>();
        itemData2.add(new ItemOptionDataChild("ice", "", 1, "", 50, false, 1, ItemOptionData.ITEM_OPTION_DATA_CHILD_MANDATORY));
        itemData2.add(new ItemOptionDataChild("hot", "", 1, "", 00, true, 2, ItemOptionData.ITEM_OPTION_DATA_CHILD_MANDATORY));
        itemData2.add(new ItemOptionDataChild("no ice cold", "", 1, "", 100, false, 3, ItemOptionData.ITEM_OPTION_DATA_CHILD_MANDATORY));
        ArrayList<ItemOptionDataChild> itemData3 = new ArrayList<>();
        itemData3.add(new ItemOptionDataChild("cream", "", 1, "", 600, false, 1, ItemOptionData.ITEM_OPTION_DATA_CHILD));
        itemData3.add(new ItemOptionDataChild("sugar", "", 1, "", 50, false, 2, ItemOptionData.ITEM_OPTION_DATA_CHILD));
        itemData3.add(new ItemOptionDataChild("SALT", "", 1, "", 100, false, 3, ItemOptionData.ITEM_OPTION_DATA_CHILD));

        itemOptionDataList.add(new ItemOptionDataParent("Size", "", 1, "", true, itemData1));
        itemOptionDataList.addAll(itemData1);
        itemOptionDataList.add(new ItemOptionDataParent("Temperature", "", 1, "", true, itemData2));
        itemOptionDataList.addAll(itemData2);
        itemOptionDataList.add(new ItemOptionDataParent("Toppings", "", 1, "", false, itemData3));


        expandableListAdapter = new StoreItemOptionExpandableListAdapter(this, itemOptionDataList);
        RecyclerView.LayoutManager manager = new LinearLayoutManagerWithSmoothScroller(this);
        expandableListView.setLayoutManager(manager);
        expandableListView.setAdapter(expandableListAdapter);

        int tempcost = 0;
        ItemOptionData itemOptionDataTemp;
        ItemOptionDataChild itemOptionDataChild;
        for (int i = 0; i < itemOptionDataList.size(); i++) {
            itemOptionDataTemp = itemOptionDataList.get(i);
            if (itemOptionDataTemp.getType() == ItemOptionData.ITEM_OPTION_DATA_CHILD_MANDATORY) {
                itemOptionDataChild = ((ItemOptionDataChild) itemOptionDataTemp);
                if (itemOptionDataChild.isChecked()) {
                    tempcost += itemOptionDataChild.getCost();
                    itemOptionDataListSelected.add((ItemOptionDataChild) itemOptionDataTemp);
                }
            }
        }
        int temp = updateCost(tempcost, quantity, itemcost);
        itemDataChild.setTotalCost(temp);

        expandableListAdapter.setOnItemClickListener(new StoreItemOptionExpandableListAdapter.OnItemClickListener() {
            @Override
            public void onCheckClick(int position) {
                ItemOptionDataChild itemOption = (ItemOptionDataChild) itemOptionDataList.get(position);
                if (itemOption.isChecked()) {
                    updateCost(-itemOption.getCost(), quantity, itemcost);
                    itemOption.setChecked(false);
                    itemOptionDataListSelected.remove(itemOption);
                } else {
                    updateCost(itemOption.getCost(), quantity, itemcost);
                    itemOption.setChecked(true);
                    itemOptionDataListSelected.add(itemOption);
                }
                expandableListAdapter.notifyItemChanged(position);
            }

            @Override
            public void onRadioClick(int position) {
                ItemOptionDataChild itemOption = (ItemOptionDataChild) itemOptionDataList.get(position);
                if (!itemOption.isChecked()) {
                    int querypos = position - 1;
                    ItemOptionData temp = itemOptionDataList.get(querypos);
                    boolean radiounchecked = false;
                    while (temp.getType() != ItemOptionData.ITEM_OPTION_DATA_PARENT_MANDATORY && temp.getType() != ItemOptionData.ITEM_OPTION_DATA_PARENT) {
                        if (((ItemOptionDataChild) temp).isChecked()) {
                            radiounchecked = true;
                            updateCost(-((ItemOptionDataChild) temp).getCost(), quantity, itemcost);
                            ((ItemOptionDataChild) temp).setChecked(false);
                            expandableListAdapter.notifyItemChanged(querypos);
                            itemOptionDataListSelected.remove(temp);
                            break;
                        }
                        querypos--;
                        temp = itemOptionDataList.get(querypos);
                    }
                    querypos = position + 1;
                    temp = itemOptionDataList.get(querypos);
                    while (!radiounchecked && temp.getType() != ItemOptionData.ITEM_OPTION_DATA_PARENT_MANDATORY && temp.getType() != ItemOptionData.ITEM_OPTION_DATA_PARENT) {
                        if (((ItemOptionDataChild) temp).isChecked()) {
                            updateCost(-((ItemOptionDataChild) temp).getCost(), quantity, itemcost);
                            ((ItemOptionDataChild) temp).setChecked(false);
                            expandableListAdapter.notifyItemChanged(querypos);
                            itemOptionDataListSelected.remove(temp);
                            break;
                        }
                        querypos++;
                        temp = itemOptionDataList.get(querypos);
                    }
                    updateCost(itemOption.getCost(), quantity, itemcost);
                    itemOption.setChecked(true);
                    expandableListAdapter.notifyItemChanged(position);
                    itemOptionDataListSelected.add(itemOption);
                }
            }

            @Override
            public void onLabelClick(int position) {
                ItemOptionDataParent label = (ItemOptionDataParent) itemOptionDataList.get(position);
                ArrayList<ItemOptionDataChild> itemoptions = label.getItemOptionDataChildren();
                if (!label.isExpanded()) {
                    itemOptionDataList.addAll(position + 1, itemoptions);
                    expandableListAdapter.notifyItemRangeInserted(position + 1, itemoptions.size());
                    label.setExpanded(true);
                    if (itemOptionDataList.size() == label.getItemOptionDataChildren().size() + position + 1) {
                        ((RecyclerView) findViewById(R.id.itemoptionlist)).smoothScrollToPosition(itemOptionDataList.size() - 1);
                    }
                } else {
                    itemOptionDataList.subList(position + 1, position + itemoptions.size() + 1).clear();
                    expandableListAdapter.notifyItemRangeRemoved(position + 1, itemoptions.size());
                    label.setExpanded(false);
                }
            }
        });

        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = Integer.valueOf(quantity.getText().toString());
                int amount = Integer.valueOf(itemcost.getText().toString());
                amount = (amount / temp) * (temp + 1);
                temp++;
                quantity.setText(String.valueOf(Integer.valueOf(quantity.getText().toString()) + 1));
                itemcost.setText(String.format(Locale.getDefault(), "%d", amount));
                itemDataChild.setQuantity(temp);
                itemDataChild.setTotalCost(amount);
            }
        });

        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = Integer.valueOf(quantity.getText().toString());
                if (temp > 1) {
                    int amount = Integer.valueOf(itemcost.getText().toString());
                    amount = (amount / temp) * (temp - 1);
                    temp--;
                    quantity.setText(String.valueOf(Integer.valueOf(quantity.getText().toString()) - 1));
                    itemcost.setText(String.format(Locale.getDefault(), "%d", amount));
                    itemDataChild.setQuantity(temp);
                    itemDataChild.setTotalCost(amount);
                }
            }
        });

        putInBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ;
                ShoppingCartData.addItem(itemDataChild, itemOptionDataListSelected, itemOptionDataList);
                finish();

                //use this when implementing the floating button
                /*
                Intent appInfo = new Intent(StoreItemActivity.this, ShoppingCartActivity.class);
                appInfo.putExtra("StoreData", storeData);
                appInfo.putParcelableArrayListExtra("ItemData", storeItemDataArrayList);
                appInfo.putExtra("ItemDataPosition", storeItemDataArrayListPosition);
                appInfo.putParcelableArrayListExtra("ItemOptionData", itemOptionDataList);
                appInfo.putParcelableArrayListExtra("itemOptionDataListSelected", itemOptionDataListSelected);
                startActivity(appInfo);
                */
            }
        });

        // Getting SwipeContainerLayout
        // need to add some more to this for initializing
        swipeLayout = findViewById(R.id.swipeContainer);
        // Adding Listener
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                expandableListAdapter.clear();
                query();
                swipeLayout.setRefreshing(false);
            }
        });

        // Scheme colors for animation
        swipeLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
    }

    public void query() {
        final ModelItemOptionDataParentTestFilterInput modelItemOptionDataParentTestFilterInput = ModelItemOptionDataParentTestFilterInput.builder().id(ModelIDFilterInput.builder().eq(ShoppingCartData.getTempItemDataChild().getIdentifier()).build()).build();
        mAWSAppSyncClient.query(ListItemOptionDataParentTestsQuery.builder().limit(queryLimit).filter(modelItemOptionDataParentTestFilterInput).build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(itemOptionsCallback);
    }


    private GraphQLCall.Callback<ListItemOptionDataParentTestsQuery.Data> itemOptionsCallback = new GraphQLCall.Callback<ListItemOptionDataParentTestsQuery.Data>() {
        @Override
        public void onResponse(@Nonnull final Response<ListItemOptionDataParentTestsQuery.Data> response) {
            try {
                queriedItemOptions = new ArrayList<ListItemOptionDataParentTestsQuery.Item>(response.data().listItemOptionDataParentTests().items());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("ITEMRESULT1", response.data().listItemOptionDataParentTests().items().toString());
                            for (ListItemOptionDataParentTestsQuery.Item item : queriedItemOptions) {
                                ItemOptionDataParent tempItemOptionParent = new ItemOptionDataParent(item.subjectName(), "", item.id(), "", false, null);
                                ArrayList<ItemOptionDataChild> itemOptionDataChildren = new ArrayList<>();
                                List<ListItemOptionDataParentTestsQuery.Item3> queriedItemOptions = item.itemOptionDataChildTest().items();
                                int i = 1;
                                for (ListItemOptionDataParentTestsQuery.Item3 itemOption : queriedItemOptions) {
                                    int type;
                                    if (item.isMandatory()) {
                                        type = ItemOptionData.ITEM_OPTION_DATA_CHILD_MANDATORY;
                                    } else {
                                        type = ItemOptionData.ITEM_OPTION_DATA_CHILD;
                                    }
                                    itemOptionDataChildren.add(new ItemOptionDataChild(itemOption.subjectName(), "", itemOption.id(), "", itemOption.cost(), false, i, type));
                                    i++;
                                }
                                tempItemOptionParent.setItemOptionDataChildren(itemOptionDataChildren);
                                expandableListAdapter.add(tempItemOptionParent);
                            }
                            //save the id of the item
                            /*searchIndex = response.data().nearbyStores().nextToken();
                            for (ListItemDataParentTestsQuery.Item store : queriedStores) {
                                StoreData tempStore = new StoreData(store.subjectName(), "", store.id(), "", 777, false, store.location().lat(), store.location().lon());
                                mainListAdapter.add(tempStore);
                            }
                            loadMoreItems = true;*/
                        } catch (NullPointerException e) {
                            Log.e("ITEMRESULT2", e.toString());
                        }
                    }
                });
            } catch (NullPointerException e) {
                Log.e("ITEMRESULT3", e.toString());

            }
        }

        public void onFailure(@Nonnull ApolloException e) {
            Log.e("ITEMRESULT4", e.toString());
        }
    };

    int updateCost(int changeAmount, TextView quantity, TextView itemcost) {
        int amount = Integer.valueOf(itemcost.getText().toString());
        int number = Integer.valueOf(quantity.getText().toString());
        int temp = (amount / number + changeAmount) * number;
        itemcost.setText(String.format(Locale.getDefault(), "%d", temp));
        itemDataChild.setTotalCost(temp);
        return temp;
    }

    public void titleClick(View view) {
        Toast.makeText(StoreItemActivity.this, "You have clicked title", Toast.LENGTH_LONG).show();
        ((RecyclerView) findViewById(R.id.itemoptionlist)).smoothScrollToPosition(0);
    }
}

