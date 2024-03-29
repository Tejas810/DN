package com.example.deliverynest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.HashMap;

public class UserDashboard extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    SessionManager sessionManager;
    static final float END_SCALE = 0.7f;
    int i=0,j=0;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LottieAnimationView menuIcon;
    LinearLayout contentView;
    ChipNavigationBar chipNavigationBar;
    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    DatabaseReference database;
    MyAdapter myAdapter;
    ArrayList<RecentOrders> list;
    ImageSlider imageSlider;
    String fullname,username;
    TextView userfullname;
    String user,status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        // find by id
         userfullname = findViewById(R.id.hello);
         imageSlider = findViewById(R.id.imageslider);
        //getting name from session and displaying on textview
        sessionManager = new SessionManager(this);
        HashMap<String, String> usersDetails = sessionManager.getUsersDetailsFromSession();
        fullname = usersDetails.get(SessionManager.KEY_FULLNAME);
         username = usersDetails.get(SessionManager.KEY_USERNAME);


        //offer section
         offerSection();
        //menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.navigationdrawer);
        contentView = findViewById(R.id.content);
        //logout process
        LottieAnimationView logoutButton = findViewById(R.id.usericon);
        // set the default checked item in the sidebar menu
        navigationView.setCheckedItem(R.id.nav_home);

        //bottom nav
        bottomNavigationView = findViewById(R.id.bottomnavigationview);
        bottomNavigationView.setBackground(null);
        //Recent Orders
        recyclerView = findViewById(R.id.Recent_Orders);

        database = FirebaseDatabase.getInstance().getReference("orders");
        recyclerView(username);
        getRecords();
        animateNavigationDrawer();
    }
    public void getRecords(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("users").child(username);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot: snapshot.getChildren()) {
                    if(datasnapshot.getKey().equals("name")){
                        userfullname.setText("Hey! , "+datasnapshot.getValue().toString()+".");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void offerSection() {
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.discount_five, ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.discount_five, ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.discount_five, ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.discount_five, ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.discount_five, ScaleTypes.CENTER_INSIDE));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
    }

    private void recyclerView(String userName) {
        String[] FinalString =new String[50];
        recyclerView = findViewById(R.id.Recent_Orders);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        list = new ArrayList<>();
        myAdapter = new MyAdapter(this, list);
        recyclerView.setAdapter(myAdapter);

        database = FirebaseDatabase.getInstance().getReference("Orders");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    FinalString[j] = snapshot1.getKey();
                    j++;
                }
                for (i = 0; i < j; i++) {
                    database = FirebaseDatabase.getInstance().getReference("Orders").child(FinalString[i]);
                    String orderId = FinalString[i];
                    database.addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if(dataSnapshot.getKey().equals("Status")){
                                   status=dataSnapshot.getValue().toString();
                                }
                                else if(dataSnapshot.getKey().equals("LoggedUsername")){
                                    user=dataSnapshot.getValue().toString();
                                }
                            }
                            if(user.equals(userName)) {
                                RecentOrders recentOrders = new RecentOrders();
                                recentOrders.order_id = orderId;
                                recentOrders.status = status;
                                if(list.size()<5) {
                                    list.add(recentOrders);
                                }
                                myAdapter.notifyDataSetChanged();
                                recyclerView.setVisibility(myAdapter.getItemViewType(list.size()));
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        return true;
    }

    public void naviagtionDrawer(View view) {
        //Naviagtion Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else drawerLayout.openDrawer(GravityCompat.START);

            }
        });
        animateNavigationDrawer();

    }

    public void animateNavigationDrawer() {
        //drawerLayout.setScrimColor(getResources().getColor(R.color.secondaryappcolor));
        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);
                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void logout(MenuItem item) {
        sessionManager.logout();
        Intent intent = new Intent(UserDashboard.this, LoginScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public void ShowComplaints(MenuItem item){
        Intent i = new Intent(getApplicationContext(),ComplaintSection.class);
        startActivity(i);
    }
    public void CreateOrder(MenuItem item) {
        Intent i = new Intent(getApplicationContext(),CreateOrder.class);
        startActivity(i);
    }
    public void TrackOrder(MenuItem item) {
        Intent i = new Intent(getApplicationContext(),Track_Order.class);
        startActivity(i);
    }
    public void ShowProfile(MenuItem item){
        Intent i = new Intent(getApplicationContext(),User_Profile.class);
        startActivity(i);
    }
    public void ShowProfile(View view){
        Intent i = new Intent(getApplicationContext(),User_Profile.class);
        startActivity(i);
    }
    public void ShowAbout(MenuItem item){
        Intent i = new Intent(getApplicationContext(),AboutUs.class);
        startActivity(i);
    }
    public void ShowOrderHistory(MenuItem item){
        Intent i = new Intent(getApplicationContext(),All_Orders.class);
        startActivity(i);
    }
    public void ShowOrderHistory(View view){
        Intent i = new Intent(getApplicationContext(),All_Orders.class);
        startActivity(i);
    }
}
