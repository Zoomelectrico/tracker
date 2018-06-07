package com.tracker.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Cargando extends AppCompatActivity {

    // Referencias a los servicios de Firebase
    private FirebaseAuth auth;
    private FirebaseUser user;

    // Esta Clase sirve como preloader a la App
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargando);
        // Creo un referencia al servicio de auth de firebase
        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();

        Intent intent; // EL objeto intento que sirve para intentar una acción
        if(user != null) { // Si ya se logeo vamos pal main de una
            intent = new Intent(this, MainActivity.class);
        } else { // Si no se logeo vamos al login
            intent = new Intent(this, Login.class);
        }

        startActivity(intent); //Inicia la actividad
        this.finish(); // Mata esta, ahorrar batteria
    }

}
