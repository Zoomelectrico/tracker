package com.tracker.tracker;

import android.app.Fragment;
import android.nfc.Tag;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tracker.tracker.dummy.DummyContent;
import com.tracker.tracker.tareas.SeresQueridosAsync;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class seresQueridos extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seres_queridos);
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    public void selectSeresQueridos(View v){
    }
}
