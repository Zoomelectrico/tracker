package com.tracker.tracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tracker.tracker.Modelos.Contacto;

public class seresQueridos extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seres_queridos);
    }

    @Override
    public void onListFragmentInteraction(Contacto item) {

    }

    public void selectSeresQueridos(View v){
    }
}
