package com.tracker.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.tracker.tracker.modelos.Contacto;
import com.tracker.tracker.modelos.Usuario;
import com.tracker.tracker.UIHelpers.Fragment.ContactoFragment;

import java.util.Objects;

/**
 * Clases: SeresQueridos esta clase se encarga de manejar La lista de seres Queridos
 */
public class SeresQueridos extends AppCompatActivity implements ContactoFragment.OnListFragmentInteractionListener {

    public Usuario user;

    /**
     * Método onCreate:
     * @param savedInstanceState {Bundle}
     */
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
        ContactoFragment contactoFragment = new ContactoFragment();
        contactoFragment.setArguments(bundle);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seres_queridos);

        Toolbar toolbar = findViewById(R.id.tbAddSer);
        toolbar.setTitle("Lista de Seres Queridos");

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * Método que permite desplegar el Diálogo al hacer click en el usuario para modificarlo.
     */
    @Override
    public void onListFragmentInteraction(@NonNull Contacto item) {
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        args.putString("Nombre", item.getNombre());
        args.putString("Telf", item.getTelf());
        args.putString("id", item.getId());
        args.putInt("position", item.getPosition());

        ModifySQDialog dialog = new ModifySQDialog();
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "ModifySQDialog");
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
