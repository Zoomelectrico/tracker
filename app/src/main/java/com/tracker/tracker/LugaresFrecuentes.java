package com.tracker.tracker;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.tracker.tracker.Modelos.Frecuente;
import com.tracker.tracker.Modelos.Usuario;
import com.tracker.tracker.UIHelpers.Fragment.LugaresFrecuentesFragment;

import java.util.Objects;

/**
 * Clase LugaresFrecuentes: su funcionalidad es administrar los lugares frecuentes del usuario,
 * permitiendo visualizar una lista de todos estos, crear nuevos, modificarlos o eliminarlos
 * según el deseo del usuario.
 */
public class LugaresFrecuentes extends AppCompatActivity implements LugaresFrecuentesFragment.OnListFragmentInteractionListener {

    public Usuario user;

    private FloatingActionButton fabAddLugarFrecuente;

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

        fabConfigLugaresFrecuentes();
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

    private void fabConfigLugaresFrecuentes (){
        fabAddLugarFrecuente = findViewById(R.id.fabAddLugarFrecuente);

        fabAddLugarFrecuente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putBoolean("haveDestino", false);

                AddLugarFrecuenteDialog addLF = new AddLugarFrecuenteDialog();
                addLF.setArguments(args);
                addLF.show(getFragmentManager(), "AddLugarFrecuenteDialogFragment");
                Toast.makeText(getApplicationContext(), "Se agregó un nuevo lugar frecuente", Toast.LENGTH_SHORT);
            }
        });
    }
}
