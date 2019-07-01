package com.example.martin.connectorder;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.RecyclerView;

import com.amazonaws.amplify.generated.graphql.NearbyStoresQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.martin.connectorder.Data_Structures.StoreData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Iterator;

import javax.annotation.Nonnull;

import type.LocationInput;

public class MainActivity extends AppCompatActivity {
    private static final int SEARCH_FIRST_INDEX = -1;
    private static final int SEARCH_LAST_INDEX = -2;
    int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    private AWSAppSyncClient mAWSAppSyncClient;
    private LocationManager locationManager;
    private LocationListener listener;
    private FusedLocationProviderClient fusedLocationClient;
    TextView locationText;
    SwipeRefreshLayout swipeLayout;
    Location currentLocation;
    ArrayList<NearbyStoresQuery.Item> queriedStores;
    ArrayList<StoreData> arrayList;
    MainListAdapter mainListAdapter;
    double searchIndex = SEARCH_FIRST_INDEX;
    int radiusInKm = 5000;
    int querySize = 10;
    double defaultLat = 0;
    double defaultLon = 0;
    boolean loadMoreItems = false;
    boolean endOfList = false;
    boolean mIsLoading = false;
    int itemQueryBufferSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        Toolbar myToolbar = findViewById(R.id.temp_action_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);
        FloatingActionButton shoppingcartbutton = findViewById(R.id.shoppingcartbutton);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationText = findViewById(R.id.location_text);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                currentLocation = location;
                                locationText.setText("latitude: " + location.getLatitude() + "\nlongitude: " + location.getLongitude());
                            } else {
                                // maybe do something more to get location
                            }
                        }
                    });
        }

        final RecyclerView list = findViewById(R.id.main_list);
        arrayList = new ArrayList<StoreData>();
        /*
        arrayList.add(new StoreData("원할머니보쌈", "보쌈보쌈보쌈", "", "", 3, false));
        arrayList.add(new StoreData("원할머니쌈보", "Ssambo", "", "", 5, true));
        arrayList.add(new StoreData("원할머니파스타", "24시간영업", 3, "", 10, false));
        arrayList.add(new StoreData("원할머니카페", "컾이커피컵밥", 4, "", 2, false));
        arrayList.add(new StoreData("원할머니가즈아", "가즈아아아아", 5, "", 3, true));
        arrayList.add(new StoreData("원할머니뭐임", "몰랑", 6, "", 6, false));
        arrayList.add(new StoreData("원할머니의원할머니", "할MONEY", 7, "", 7, true));
        arrayList.add(new StoreData("원할아버지보쌈", "ㅎㅎㅎㅎㅎㅎ", 8, "", 8, false));
        arrayList.add(new StoreData("박할매감자탕", "감자감자감자", 9, "", 3, false));*/
        mainListAdapter = new MainListAdapter(this, arrayList);
        final LinearLayoutManagerWithSmoothScroller manager = new LinearLayoutManagerWithSmoothScroller(this);
        list.setLayoutManager(manager);
        list.setAdapter(mainListAdapter);
        query();

        mainListAdapter.setOnItemClickListener(new MainListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                if (ShoppingCartData.isInitialized()) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    Intent appInfo = new Intent(MainActivity.this, StoreInfoActivity.class);
                                    ShoppingCartData.clearCart();
                                    ShoppingCartData.setStoreData(arrayList.get(position));
                                    startActivity(appInfo);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.empty_shopping_cart_prompt).setPositiveButton(R.string.yes, dialogClickListener).setNegativeButton(R.string.no, dialogClickListener).show();
                } else {
                    ShoppingCartData.setInitialized(true);
                    Intent appInfo = new Intent(MainActivity.this, StoreInfoActivity.class);
                    ShoppingCartData.setStoreData(arrayList.get(position));
                    startActivity(appInfo);
                }
            }

            @Override
            public void onCheckClick(int position) {
                // to be used to favorite stores, implement more later
                Toast.makeText(MainActivity.this, "You have clicked item number " + position, Toast.LENGTH_LONG).show();
            }
        });

        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (endOfList)
                    return;
                else if (loadMoreItems) {
                    int totalItemCount = manager.getItemCount();
                    int pastVisibleItems = manager.findLastVisibleItemPosition();
                    if (pastVisibleItems + itemQueryBufferSize >= totalItemCount) {
                        query();
                    }
                }
            }
        });

        // Getting SwipeContainerLayout
        swipeLayout = findViewById(R.id.swipeContainer);
        // Adding Listener
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMoreItems = false;
                mainListAdapter.clear();
                searchIndex = SEARCH_FIRST_INDEX;
                endOfList = false;
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


        shoppingcartbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ShoppingCartData.isInitialized()) {
                    Toast.makeText(MainActivity.this, "Shopping cart is empty", Toast.LENGTH_LONG).show();
                } else {
                    Intent appInfo = new Intent(MainActivity.this, ShoppingCartActivity.class);
                    startActivity(appInfo);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.qr_reader:
                //
                Toast.makeText(MainActivity.this, "You have clicked qr reader", Toast.LENGTH_LONG).show();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void titleClick(View view) {
        Toast.makeText(MainActivity.this, "You have clicked title", Toast.LENGTH_LONG).show();
        ((RecyclerView) findViewById(R.id.main_list)).smoothScrollToPosition(0);
    }

    public void query() {
        if (!endOfList) {
            if (currentLocation != null) {
                final LocationInput locationInput = LocationInput.builder().lat(currentLocation.getLatitude()).lon(currentLocation.getLongitude()).build();
                mAWSAppSyncClient.query(NearbyStoresQuery.builder().km(radiusInKm).location(locationInput).size(querySize).searchIndex(searchIndex).build())
                        .responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)
                        .enqueue(storesCallback);
            } else {
                final LocationInput locationInput = LocationInput.builder().lat(defaultLat).lon(defaultLon).build();
                mAWSAppSyncClient.query(NearbyStoresQuery.builder().km(radiusInKm).location(locationInput).size(querySize).searchIndex(searchIndex).build())
                        .responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)
                        .enqueue(storesCallback);
            }
        }
    }

    private GraphQLCall.Callback<NearbyStoresQuery.Data> storesCallback = new GraphQLCall.Callback<NearbyStoresQuery.Data>() {
        @Override
        public void onResponse(@Nonnull final Response<NearbyStoresQuery.Data> response) {
            try {
                queriedStores = new ArrayList<>(response.data().nearbyStores().items());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            searchIndex = response.data().nearbyStores().nextToken();
                            for (NearbyStoresQuery.Item store : queriedStores) {
                                StoreData tempStore = new StoreData(store.subjectName(), "", store.id(), "", 777, false, store.location().lat(), store.location().lon());
                                mainListAdapter.add(tempStore);
                            }
                            loadMoreItems = true;
                        } catch (NullPointerException e) {
                            endOfList = true;
                            loadMoreItems = false;
                        }
                    }
                });
            } catch (NullPointerException e) {
                endOfList = true;
                loadMoreItems = false;
            }
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("STORESRESULT3", e.toString());
        }
    };
}