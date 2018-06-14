package com.mystique.kim.easyrestaurantapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.mystique.kim.easyrestaurantapp.CartActivity.cartAdapter;
import static com.mystique.kim.easyrestaurantapp.CartActivity.recyclerView;
import static com.mystique.kim.easyrestaurantapp.SpecificMenuActivity.totalPrice;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mCategoryList;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static final String INTENT_EXTRA="";
    private DatabaseReference mDatabaseUsers;

    private ArrayList<CartModel> selectedItems;

    Intent cartIntent=null;

    String user=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        mRef= FirebaseDatabase.getInstance().getReference().child("Menu");
        mRef.keepSynced(true);

        mDatabaseUsers= FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        selectedItems=SpecificMenuActivity.selectedItems;

        mCategoryList=(RecyclerView) findViewById(R.id.category_menu_recycler);
        mCategoryList.setHasFixedSize(true);
        mCategoryList.setLayoutManager(new GridLayoutManager(this,1));


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent loginIntent=new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        cartIntent = new Intent(MainActivity.this, CartActivity.class);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedItems.size()!=0) {
                    cartIntent.putParcelableArrayListExtra("selectedItems", selectedItems);
                    startActivity(cartIntent);
                }
                else {
                    AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Cart is Empty, Please select items from the menus.");
                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser()!=null){
            user=mAuth.getCurrentUser().getEmail();
        }

        mAuth.addAuthStateListener(mAuthListener);


        FirebaseRecyclerAdapter<CategoryMenu,CategoryViewHolder> categoryFirebaseAdapter=new FirebaseRecyclerAdapter<CategoryMenu, CategoryViewHolder>(
                CategoryMenu.class,R.layout.category_menu, CategoryViewHolder.class,mRef
        ) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, final CategoryMenu model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setImage(getApplicationContext(),model.getImage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getApplicationContext(),SpecificMenuActivity.class);
                        intent.putExtra(INTENT_EXTRA,model.getName());
                        startActivity(intent);
                    }
                });
            }
        };
        mCategoryList.setAdapter(categoryFirebaseAdapter);
    }


    public static class CategoryViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public CategoryViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setName(String name){
            TextView category_name= (TextView) mView.findViewById(R.id.category_title);
            category_name.setText(name);
        }
        public void setImage(Context context, String image){
            ImageView categoryIcon=(ImageView) mView.findViewById(R.id.category_icon);
            Picasso.with(context).load(image).into(categoryIcon);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

   /* @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.user_indicator).setTitle( user);

        return super.onPrepareOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_cart) {
            if (selectedItems.size()!=0) {
                cartIntent.putParcelableArrayListExtra("selectedItems", selectedItems);
                startActivity(cartIntent);
            }
            else {
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Cart is Empty, Please select items from the menus.");
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        }

        if (id == R.id.action_clear_cart) {
            selectedItems.clear();
            SpecificMenuActivity.selectedItems.clear();
            recyclerView.setAdapter(cartAdapter);
            totalPrice=0;
            return true;
        }

        if (id==R.id.action_logout){
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
