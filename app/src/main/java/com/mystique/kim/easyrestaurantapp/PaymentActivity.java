package com.mystique.kim.easyrestaurantapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.mystique.kim.easyrestaurantapp.CartActivity.tableNo;

public class PaymentActivity extends AppCompatActivity {

    private Button confirmBtn;
    private DatabaseReference mOrderDB;

    private ArrayList<CartModel> orderedItems;

    private ProgressDialog mProgress;
    private DatabaseReference mUserIdRef;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mOrderDB= FirebaseDatabase.getInstance().getReference().child("Orders");

        userId= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        mUserIdRef= FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mProgress=new ProgressDialog(this);

        orderedItems=getIntent().getParcelableArrayListExtra("orderedItems");
        confirmBtn=(Button) findViewById(R.id.confirm_btn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOrder();
            }
        });

    }

    private void sendOrder() {

        mProgress.setMessage("Sending Order...");
        mProgress.show();

        final String key= mOrderDB.child("orderDetails").push().getKey();
        final DatabaseReference orderDetailsRef=mOrderDB.child("orderDetails").child(key);
        final DatabaseReference orderItemsRef=mOrderDB.child("orderItems").child(key);

        mUserIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderDetailsRef.child("key").setValue(key);
                orderDetailsRef.child("orderNumber").setValue(00045);
                orderDetailsRef.child("OrderTime").setValue("16:25");
                orderDetailsRef.child("tableNumber").setValue(tableNo);
                orderDetailsRef.child("userName").setValue(dataSnapshot.child("firstname").getValue());

                for (int i=0; i<orderedItems.size(); i++){

                    DatabaseReference itemsDB=orderItemsRef.push();

                    itemsDB.child("desc").setValue(orderedItems.get(i).getDesc());
                    itemsDB.child("price").setValue(orderedItems.get(i).getPrice());
                    itemsDB.child("qty").setValue(2);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProgress.dismiss();

        Toast.makeText(PaymentActivity.this, "Order Sent successfully. You'll be served shortly",Toast.LENGTH_LONG).show();
    }
}
