package com.tracker.tracker;

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

import com.tracker.tracker.modelos.Frecuente;
import com.tracker.tracker.modelos.Usuario;
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
    public void onListFragmentInteraction(@NonNull Frecuente item) {
        Log.e("bla", "El id del item seleccionado es: " + item.getLatitud() );
        if (item.getId() != null) {
            Bundle args = new Bundle();
            args.putParcelable("user", user);
            args.putString("Nombre", item.getNombre());
            args.putDouble("Latitud", item.getLatitud());
            args.putDouble("Longitud", item.getLongitud());
            args.putString("Direccion", item.getDireccion());
            args.putString("id", item.getId());
            args.putString("PlaceId", item.getPlaceId());

            mLugarFrecuenteDialog dialog = new mLugarFrecuenteDialog();
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "mLugarFrecuenteDialog");
        } else {
            Toast.makeText(this, "Espere a que el objeto cargue en la BD", Toast.LENGTH_LONG).show();
        }


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
                Toast.makeText(getApplicationContext(), "Se agregó un nuevo lugar frecuente", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
