package com.tracker.tracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.tracker.tracker.Modelos.Frecuente;
import com.tracker.tracker.Modelos.Usuario;
import com.tracker.tracker.UIHelpers.Fragment.LugaresFrecuentesFragment;

import java.util.Objects;

public class LugaresFrecuentes extends AppCompatActivity implements LugaresFrecuentesFragment.OnListFragmentInteractionListener {

    public Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        Usuario usuario = this.getIntent().getParcelableExtra("user");
        this.user = usuario;
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", usuario);
        LugaresFrecuentesFragment lFrecuentesFragment = new LugaresFrecuentesFragment();
        lFrecuentesFragment.setArguments(bundle);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugares_frecuentes);

        Toolbar toolbar = findViewById(R.id.tbLugaresFrecuentes);
        toolbar.setTitle("Lugares Frecuentes");

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onListFragmentInteraction(Frecuente item) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        if(user != null) {
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        } else {
            Log.e("", "CHUPALO");
        }
    }
}
