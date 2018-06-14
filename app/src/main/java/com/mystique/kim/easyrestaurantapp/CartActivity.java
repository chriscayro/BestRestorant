package com.mystique.kim.easyrestaurantapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.mystique.kim.easyrestaurantapp.SpecificMenuActivity.totalPrice;

public class CartActivity extends AppCompatActivity {

    public static RecyclerView recyclerView;
    public static RecyclerView.Adapter cartAdapter;
    static View.OnClickListener myOnClickListener;
    public static int tableNo=0;

    private static ArrayList<CartModel> selectedItems;


    static TextView totalAmountText;
    Button checkoutBtn;
    TextView emptyCartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        selectedItems=getIntent().getParcelableArrayListExtra("selectedItems");

        totalAmountText=(TextView) findViewById(R.id.cart_total_amount_view);
        checkoutBtn=(Button) findViewById(R.id.checkout_btn) ;
        emptyCartView=(TextView) findViewById(R.id.cart_empty_message);


        recyclerView = (RecyclerView) findViewById(R.id.cart_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myOnClickListener = new MyOnClickListener(this);
        cartAdapter = new CartAdapter(CartActivity.this,selectedItems);
        recyclerView.setAdapter(cartAdapter);

        if(selectedItems.size()==0){
           emptyCartView.setVisibility(View.VISIBLE);
        }

        totalAmountText.setText("KSH. "+totalPrice);

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder tBuilder=new AlertDialog.Builder(CartActivity.this);
                tBuilder.setTitle("Enter the table number");
                final EditText input =new EditText(CartActivity.this);

                tBuilder.setView(input);
                tBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!TextUtils.isEmpty(input.getText())){
                            tableNo=Integer.parseInt(input.getText().toString());

                            AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                            builder.setTitle("Are you sure you want to place order with total amount of KSH. "+totalPrice +"?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int i) {
                                    AlertDialog.Builder paymentBuilder=new AlertDialog.Builder(CartActivity.this);
                                    paymentBuilder.setTitle("Choose a payment option: ");

                                    final ListView paymentOptionList=new ListView(CartActivity.this);
                                    final String[] optionsArray= new String[]{"EasyLife Account","M-Pesa","Equitel","PayPal","Cash"};
                                    final ArrayAdapter<String> adapter=new ArrayAdapter<>(CartActivity.this, android.R.layout.simple_selectable_list_item,optionsArray);
                                    paymentOptionList.setAdapter(adapter);

                                    paymentBuilder.setView(paymentOptionList);
                                    paymentBuilder.setCancelable(true);

                                    paymentOptionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                            String option=adapter.getItem(position);

                                            if (option.equals("M-Pesa")){
                                                Intent payIntent=new Intent(CartActivity.this, PaymentActivity.class);
                                                payIntent.putParcelableArrayListExtra("orderedItems",selectedItems);
                                                startActivity(payIntent);
                                            }
                                        }
                                    });

                                    paymentBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });

                                    paymentBuilder.show();
                                }
                            });
                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            builder.show();
                        }

                        else
                            Toast.makeText(CartActivity.this, "Enter a valid table number", Toast.LENGTH_SHORT).show();


                    }

                });
                tBuilder.show();

            }
        });
    }

    public static class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder>{

        public static ArrayList<CartModel> dataSet;
        Context context;

        static double totalPrice=0;
        public static class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textViewDesc;
            TextView textViewPrice;
            ImageView imageViewIcon;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.textViewDesc = (TextView) itemView.findViewById(R.id.cart_item_desc);
                this.textViewPrice = (TextView) itemView.findViewById(R.id.cart_item_price);
                this.imageViewIcon = (ImageView) itemView.findViewById(R.id.cart_item_icon);

            }
        }

        public CartAdapter(Context context, ArrayList<CartModel> data) {
            this.dataSet = data;
            this.context=context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.cart_item, parent, false);

            view.setOnClickListener(myOnClickListener);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            totalPrice=+dataSet.get(listPosition).getPrice();

            TextView textViewDesc = holder.textViewDesc;
            TextView textViewPrice = holder.textViewPrice;
            ImageView imageView = holder.imageViewIcon;

            textViewDesc.setText(dataSet.get(listPosition).getDesc());
            textViewPrice.setText("KSh. "+dataSet.get(listPosition).getPrice());

            Picasso.with(context).load(dataSet.get(listPosition).getImage()).into(imageView);

        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

    }

    public static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            int selectedItemPosition = recyclerView.getChildPosition(v);
            RecyclerView.ViewHolder viewHolder
                    = recyclerView.findViewHolderForPosition(selectedItemPosition);
            if (selectedItems.size()!=0){
                selectedItems.remove(selectedItemPosition);
                SpecificMenuActivity.selectedItems.remove(selectedItemPosition);
                cartAdapter.notifyItemRemoved(selectedItemPosition);
                //totalPrice=totalPrice-CartAdapter.dataSet.get(selectedItemPosition).getPrice();
                totalAmountText.setText("KSh. "+totalPrice);
            }

        }

        public static void removeItem(View v) {
            int selectedItemPosition = recyclerView.getChildPosition(v);
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForPosition(selectedItemPosition);
            SpecificMenuActivity.selectedItems.remove(selectedItemPosition);
            selectedItems.remove(selectedItemPosition);
            cartAdapter.notifyItemRemoved(selectedItemPosition);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_clear_cart) {
            selectedItems.clear();
            SpecificMenuActivity.selectedItems.clear();
            recyclerView.setAdapter(cartAdapter);
            totalPrice=0;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
