package com.tracker.tracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.tracker.tracker.Fragment.ContactoFragment;
import com.tracker.tracker.Modelos.Contacto;

public class seresQueridos extends AppCompatActivity implements ContactoFragment.OnListFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seres_queridos);
        Toolbar toolbar = findViewById(R.id.tbAddSer);
        toolbar.setTitle("Lista de Seres Queridos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onListFragmentInteraction(Contacto item) {
        Log.e("jejejeje", item.toString());
    }

    public void selectSeresQueridos(View v){
    }
}
