package com.tracker.tracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.instrumentation.stats.Tag;
import com.tracker.tracker.UIHelpers.Fragment.ContactoFragment;
import com.tracker.tracker.Modelos.Contacto;
import com.tracker.tracker.Modelos.Usuario;

/**
 * Clases: SeresQueridos esta clase se encarga de manejar La lista de seres Queridos
 */
public class seresQueridos extends AppCompatActivity implements ContactoFragment.OnListFragmentInteractionListener {

    ImageButton btnEditarContacto;
    private static final String TAG = "ModifySQDialog";
    /**
     * Método onCreate:
     * @param savedInstanceState {Bundle}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Usuario usuario = (Usuario) this.getIntent().getParcelableExtra("user");
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", usuario);
        ContactoFragment contactoFragment = new ContactoFragment();
        contactoFragment.setArguments(bundle);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seres_queridos);

        Toolbar toolbar = findViewById(R.id.tbAddSer);
        toolbar.setTitle("Lista de Seres Queridos");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    /**
     *
     */
    @Override
    public void onListFragmentInteraction(Contacto item) {
        Log.e("jejejeje", item.toString());
    }

    public void onClickSQ(View v) {
        Log.e(TAG,"hooooooooooooooooola");

                ModifySQDialog dialog = new ModifySQDialog();
                dialog.show(getFragmentManager(), "ModifySQDialog");
    }


}
