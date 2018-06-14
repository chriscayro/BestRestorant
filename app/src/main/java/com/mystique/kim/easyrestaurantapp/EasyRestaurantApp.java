package com.mystique.kim.easyrestaurantapp;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Kim on 7/17/2017.
 */

public class EasyRestaurantApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //FirebaseAuth.getInstance().getCurrentUser();

    }
}
