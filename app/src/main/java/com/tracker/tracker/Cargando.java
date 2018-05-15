package com.tracker.tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Cargando extends Activity {

    private FirebaseAuth auth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargando);
        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();

        Intent intent;
        if(user != null) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, Login.class);
        }

        startActivity(intent);
        this.finish();
    }




}
