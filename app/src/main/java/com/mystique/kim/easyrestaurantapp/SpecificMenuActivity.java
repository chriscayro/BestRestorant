package com.mystique.kim.easyrestaurantapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SpecificMenuActivity extends AppCompatActivity {

    private RecyclerView mSpecificMenuList;
    private DatabaseReference mChildRef;
    private String childMenu="";

    public static double totalPrice=0;
    public static int qty=0;

    private Intent cartIntent=null;
    public static   ArrayList<CartModel> selectedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cartIntent=new Intent(SpecificMenuActivity.this,CartActivity.class);

        Intent intent=getIntent();
        childMenu=intent.getStringExtra(MainActivity.INTENT_EXTRA);
        mChildRef= FirebaseDatabase.getInstance().getReference().child(childMenu);
        mSpecificMenuList=(RecyclerView) findViewById(R.id.specific_menu_recycler);
        mSpecificMenuList.setHasFixedSize(true);
        mSpecificMenuList.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedItems.size()>0) {
                    cartIntent.putParcelableArrayListExtra("selectedItems", selectedItems);
                    startActivity(cartIntent);
                }
                else {
                    AlertDialog.Builder builder=new AlertDialog.Builder(SpecificMenuActivity.this);
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

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<SpecificMenu,SpecificViewHolder> specificFirebaseAdapter=new FirebaseRecyclerAdapter<SpecificMenu, SpecificViewHolder>(
                SpecificMenu.class,R.layout.specific_menu_item, SpecificViewHolder.class,mChildRef
        ) {
            @Override
            protected void populateViewHolder(final SpecificViewHolder viewHolder, final SpecificMenu model, int position) {
                viewHolder.setDescription(model.getDescription());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setAvailability(model.getAvailability());
                viewHolder.setSpecificImage(getApplicationContext(),model.getSpecificImage());

                viewHolder.mSView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewHolder.itemSelectedBox.setChecked(true);
                    }
                });

                viewHolder.mSView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        viewHolder.itemSelectedBox.setChecked(false);
                        CartActivity.MyOnClickListener.removeItem(view);
                        return true;
                    }
                });
                viewHolder.itemSelectedBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            selectedItems.add(new CartModel(model.getDescription(),model.getSpecificImage(),model.getPrice()));

                            totalPrice+=model.getPrice();

                            viewHolder.qtyText.setText("Quantity: "+1);
                        }
                        else {
                        }
                    }
                });

                viewHolder.qtyText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu=new PopupMenu(SpecificMenuActivity.this,viewHolder.qtyText);
                        popupMenu.getMenuInflater().inflate(R.menu.qty_menu,popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                qty= Integer.parseInt(String.valueOf(item.getTitle()));
                                viewHolder.qtyText.setText("Quantity: "+qty);
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }
        };
        mSpecificMenuList.setAdapter(specificFirebaseAdapter);
    }

    public static class SpecificViewHolder extends RecyclerView.ViewHolder{
        View mSView;
        TextView qtyText;
        CheckBox itemSelectedBox;
        public SpecificViewHolder(View itemView) {
            super(itemView);
            mSView=itemView;

            itemSelectedBox= mSView.findViewById(R.id.checkbox_selected);
            qtyText= mSView.findViewById(R.id.quantity_text);

        }

        public void setDescription(String description){
            TextView description_text=  mSView.findViewById(R.id.food_description);
            description_text.setText(description);
        }

        public void setPrice(double price){
            TextView price_text=  mSView.findViewById(R.id.food_price);
            price_text.setText(""+price);
        }

        public void setAvailability(String availability){
            TextView availability_text= mSView.findViewById(R.id.availability_text);
            availability_text.setText(availability);
        }
        public void setSpecificImage(Context context, String image){
            ImageView specificIcon= mSView.findViewById(R.id.food_icon);
            Picasso.with(context).load(image).into(specificIcon);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

