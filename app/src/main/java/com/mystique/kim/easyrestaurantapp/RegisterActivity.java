package com.mystique.kim.easyrestaurantapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText fNameField;
    private EditText lNameField;
    private EditText emailField;
    private EditText passwordField;
    private Button signUpBtn;
    private ImageButton mUserImageBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;

    private static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth= FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mProgress=new ProgressDialog(this);

        fNameField=(EditText) findViewById(R.id.fname_field);
        lNameField=(EditText) findViewById(R.id.lname_field);
        emailField=(EditText) findViewById(R.id.email_field);
        passwordField=(EditText) findViewById(R.id.pass_field);
        signUpBtn=(Button) findViewById(R.id.sign_up_btn);
        mUserImageBtn = (ImageButton) findViewById(R.id.userImage);

        mUserImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegistration();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALLERY_REQUEST && resultCode == RESULT_OK){

        }
    }

    private void startRegistration() {
        final String firstname= fNameField.getText().toString().trim();
        final String lastname=lNameField.getText().toString().trim();
        String password=passwordField.getText().toString().trim();
        String email=emailField.getText().toString().trim();

        if (!TextUtils.isEmpty(firstname) && !TextUtils.isEmpty(lastname) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgress.setMessage("Registering User...");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        String userID=mAuth.getCurrentUser().getUid();
                        DatabaseReference currentUserDb=mDatabase.child(userID);
                        currentUserDb.child("firstname").setValue(firstname);
                        currentUserDb.child("lastname").setValue(lastname);

                        mProgress.dismiss();

                        Intent mainIntent=new Intent(RegisterActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }

                }
            });

        }
    }
}
