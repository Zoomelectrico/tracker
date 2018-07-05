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

import com.tracker.tracker.modelos.Rutina;
import com.tracker.tracker.modelos.Usuario;
import com.tracker.tracker.UIHelpers.Fragment.RutineFragment;

import java.util.Objects;

public class Rutine extends AppCompatActivity implements RutineFragment.OnListFragmentInteractionListener {

    public Usuario user;

    private FloatingActionButton fabAddRutina;

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
        RutineFragment rutineFragment = new RutineFragment();
        rutineFragment.setArguments(bundle);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutine);

        Toolbar toolbar = findViewById(R.id.tbRutine);
        toolbar.setTitle("Rutinas");

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fabConfigRutinas();
    }

    @Override
    public void onListFragmentInteraction(@NonNull Rutina item) {
        Log.e("bla", "El id del item seleccionado es: " + item.getId() );
        if (item.getId() != null) {
            Bundle args = new Bundle();
            args.putParcelable("rutina", item);
            args.putString("Nombre", item.getNombre());
            args.putParcelable("Destino", item.getDestino());
            args.putParcelableArrayList("seresQueridos", item.getSeresQueridos());
            args.putStringArrayList("dias", item.getDias());
            args.putString("hora", item.getHora());
            args.putBoolean("nueva", item.isNueva());

            mRutinasDialog dialog = new mRutinasDialog();
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "mRutinasDialog");
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

    private void fabConfigRutinas (){
        fabAddRutina = findViewById(R.id.fabAddRutina);

        fabAddRutina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putParcelable("user", user);

                AddRoutineDialog addRoutineDialog = new AddRoutineDialog();
                addRoutineDialog.setArguments(args);
                addRoutineDialog.show(getFragmentManager(), "AddRoutineDialog");
            }
        });
    }
}
