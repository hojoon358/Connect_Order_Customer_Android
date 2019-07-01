package com.example.martin.connectorder;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.ListItemDataChildTestsQuery;
import com.amazonaws.amplify.generated.graphql.ListItemDataParentTestsQuery;
import com.amazonaws.amplify.generated.graphql.ListItemDataParentTestsQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.martin.connectorder.Data_Structures.ItemData;
import com.example.martin.connectorder.Data_Structures.ItemDataChild;
import com.example.martin.connectorder.Data_Structures.ItemDataParent;
import com.example.martin.connectorder.Data_Structures.StoreData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import type.LocationInput;
import type.ModelIDFilterInput;
import type.ModelItemDataParentTestFilterInput;

public class StoreInfoActivity extends AppCompatActivity {
    RecyclerView expandableListView;
    StoreItemExpandableListAdapter expandableListAdapter;
    ArrayList<ItemData> itemDataList = new ArrayList<>();
    private AWSAppSyncClient mAWSAppSyncClient;
    ArrayList<ListItemDataParentTestsQuery.Item> queriedItems;
    int queryLimit = 1000;
    SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_info);

        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        Toolbar myToolbar = findViewById(R.id.temp_action_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);

        String SubjectName = ShoppingCartData.getStoreData().getSubjectName();

        ((TextView) findViewById(R.id.storename)).setText(SubjectName);
        ((TextView) findViewById(R.id.cardistance)).setText("car distance <placeholder>");
        ((TextView) findViewById(R.id.walkdistance)).setText("walk distance <placeholder>");

        expandableListView = findViewById(R.id.storeitems);
        FloatingActionButton shoppingcartbutton = findViewById(R.id.shoppingcartbutton);

        /*ArrayList<ItemDataChild> itemData1 = new ArrayList<>();
        itemData1.add(new ItemDataChild("아메리카노", "", 1, "", 4000));
        itemData1.add(new ItemDataChild("컾이", "", 1, "", 3000));
        itemData1.add(new ItemDataChild("갈색물", "", 1, "", 4000));
        itemData1.add(new ItemDataChild("쓴물", "", 1, "", 1000));
        ArrayList<ItemDataChild> itemData2 = new ArrayList<>();
        itemData2.add(new ItemDataChild("밥", "", 1, "", 10000));
        itemData2.add(new ItemDataChild("밥밥", "", 1, "", 20000));
        itemData2.add(new ItemDataChild("밥밥밥", "", 1, "", 300000));
        itemData2.add(new ItemDataChild("밥밥밥밥", "", 1, "", 700));
        itemData2.add(new ItemDataChild("밥밥밥밥밥", "", 1, "", 100));
        itemData2.add(new ItemDataChild("밥밥밥밥밥밥", "", 1, "", 32320));
        itemData2.add(new ItemDataChild("밥밥밥밥밥밥밥", "", 1, "", 7023));
        itemData2.add(new ItemDataChild("밥밥밥밥밥밥밥밥", "", 1, "", 1100));
        ArrayList<ItemDataChild> itemData3 = new ArrayList<>();
        itemData3.add(new ItemDataChild("스무디", "", 1, "", 32432));
        itemData3.add(new ItemDataChild("스무디이", "", 1, "", 2222));
        itemData3.add(new ItemDataChild("딸기*스무디", "", 1, "", 90321));
        ArrayList<ItemDataChild> itemData4 = new ArrayList<>();
        itemData4.add(new ItemDataChild("빵", "", 1, "", 1111111));
        itemData4.add(new ItemDataChild("빵-빵", "", 1, "", 232));
        itemData4.add(new ItemDataChild("팥빵", "", 1, "", 111));
        itemData4.add(new ItemDataChild("빵팥", "", 1, "", 0000));
        itemData4.add(new ItemDataChild("식빵", "", 1, "", 2323));
        ArrayList<ItemDataChild> itemData5 = new ArrayList<>();
        itemData5.add(new ItemDataChild("얼그레이", "", 1, "", 2323));
        itemData5.add(new ItemDataChild("브렉퍼스트", "", 1, "", 2211));
        itemData5.add(new ItemDataChild("과일블렌드", "", 1, "", 111342));
        itemData5.add(new ItemDataChild("보리차", "", 1, "", 234332));

        itemDataList.add(new ItemDataParent("커피", "", 1, "", false, itemData1));
        itemDataList.add(new ItemDataParent("밥", "", 1, "", false, itemData2));
        itemDataList.add(new ItemDataParent("슴우딛", "", 1, "", false, itemData3));
        itemDataList.add(new ItemDataParent("빵야", "", 1, "", false, itemData4));
        itemDataList.add(new ItemDataParent("찿", "", 1, "", false, itemData5));*/

        expandableListAdapter = new StoreItemExpandableListAdapter(this, itemDataList);
        RecyclerView.LayoutManager manager = new LinearLayoutManagerWithSmoothScroller(this);
        expandableListView.setLayoutManager(manager);
        expandableListView.setAdapter(expandableListAdapter);
        query();

        expandableListAdapter.setOnItemClickListener(new StoreItemExpandableListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent appInfo = new Intent(StoreInfoActivity.this, StoreItemActivity.class);
                ShoppingCartData.setTempItemDataChild((ItemDataChild) itemDataList.get(position));
                startActivity(appInfo);
                //get the current item child by accessing temp item data child
            }

            @Override
            public void onLabelClick(int position) {
                ItemDataParent label = (ItemDataParent) itemDataList.get(position);
                ArrayList<ItemDataChild> items = label.getItemDataChildren();
                if (!label.isExpanded()) {
                    itemDataList.addAll(position + 1, items);
                    expandableListAdapter.notifyItemRangeInserted(position + 1, items.size());
                    label.setExpanded(true);
                    if (itemDataList.size() == label.getItemDataChildren().size() + position + 1) {
                        ((RecyclerView) findViewById(R.id.storeitems)).smoothScrollToPosition(itemDataList.size() - 1);
                    }
                } else {
                    itemDataList.subList(position + 1, position + items.size() + 1).clear();
                    expandableListAdapter.notifyItemRangeRemoved(position + 1, items.size());
                    label.setExpanded(false);
                }
            }
        });
        shoppingcartbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShoppingCartData.getStoreData() == null) {
                    Toast.makeText(StoreInfoActivity.this, "Shopping cart is empty", Toast.LENGTH_LONG).show();
                } else {
                    Intent appInfo = new Intent(StoreInfoActivity.this, ShoppingCartActivity.class);
                    startActivity(appInfo);
                }
            }
        });

        // need to add some more to this for initializing
        // Getting SwipeContainerLayout
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
        final ModelItemDataParentTestFilterInput modelItemDataParentTestFilterInput = ModelItemDataParentTestFilterInput.builder().id(ModelIDFilterInput.builder().eq(ShoppingCartData.getStoreData().getIdentifier()).build()).build();
        mAWSAppSyncClient.query(ListItemDataParentTestsQuery.builder().limit(queryLimit).filter(modelItemDataParentTestFilterInput).build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(itemsCallback);
    }


    private GraphQLCall.Callback<ListItemDataParentTestsQuery.Data> itemsCallback = new GraphQLCall.Callback<ListItemDataParentTestsQuery.Data>() {
        @Override
        public void onResponse(@Nonnull final Response<ListItemDataParentTestsQuery.Data> response) {
            try {
                queriedItems = new ArrayList<ListItemDataParentTestsQuery.Item>(response.data().listItemDataParentTests().items());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("ITEMRESULT1", response.data().listItemDataParentTests().items().toString());
                            for (ListItemDataParentTestsQuery.Item item : queriedItems) {
                                ItemDataParent tempItemParent = new ItemDataParent(item.subjectName(), "", item.id(), "", false, null);
                                ArrayList<ItemDataChild> itemDataChildren = new ArrayList<>();
                                List<ListItemDataParentTestsQuery.Item3> queriedItemOptions = item.itemDataChildTest().items();
                                for (ListItemDataParentTestsQuery.Item3 itemOption : queriedItemOptions) {
                                    itemDataChildren.add(new ItemDataChild(itemOption.subjectName(), "", itemOption.id(), "", itemOption.cost()));
                                }
                                tempItemParent.setItemDataChildren(itemDataChildren);
                                expandableListAdapter.add(tempItemParent);
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

    public void titleClick(View view) {
        Toast.makeText(StoreInfoActivity.this, "You have clicked title", Toast.LENGTH_LONG).show();
        ((RecyclerView) findViewById(R.id.storeitems)).smoothScrollToPosition(0);
    }
}