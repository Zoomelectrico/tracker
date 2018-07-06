package com.tracker.tracker;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.tracker.tracker.modelos.Frecuente;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class mLugarFrecuenteDialog extends DialogFragment implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    @Nullable
    private FirebaseUser user;
    private FirebaseFirestore db;
    private CollectionReference frecuentes;

    private TextView txtMLFDireccion;
    private EditText txtMLFNombre;

    private LinearLayout layoutMLFButton;
    private ProgressBar readyMLFProgressBar;

    private static final int PLACE_PICKER_REQUEST = 2;
    private Place place;
    private Frecuente destino;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_m_lugar_frecuente_dialog, container, false);
        // Determinación de los campos para su uso.
        txtMLFDireccion = view.findViewById(R.id.txtMLFDireccion);
        txtMLFNombre = view.findViewById(R.id.txtMLFNombre);
        txtMLFNombre.setText(this.getArguments().getString("Nombre"));
        txtMLFDireccion.setText(String.valueOf(this.getArguments().getString("Direccion")));
        layoutMLFButton = view.findViewById(R.id.layoutMLFButtons);
        readyMLFProgressBar = view.findViewById(R.id.readyMLFProgressBar);
        readyMLFProgressBar.setVisibility(View.GONE);
        // Creacion de un Destino a partir de los datos del LugarFrecuente que fue seleccionado
        this.destino = new Frecuente(String.valueOf(this.txtMLFNombre.getText()), this.getArguments().getString("PlaceId"),
                this.getArguments().getDouble("Latitud"), this.getArguments().getDouble("Longitud"), this.getArguments().getString("Direccion"), false);
        this.destino.setId(this.getArguments().getString("id"));
        // Especificación de las funciones que se desarrollaran al hacer click en algun boton especifico
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnMLFFindPlace:
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        try {
                            Intent intent = builder.build(getActivity());
                            startActivityForResult(intent, PLACE_PICKER_REQUEST);
                        }  catch (@NonNull GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                            Log.e("ERROR", e.getMessage(), e);
                        }
                        break;
                    case R.id.btnMLFAceptar:
                        modificaMLF();
                        break;
                    case R.id.btnMLFCancelar:
                        getDialog().dismiss();
                        break;
                    case R.id.btnMLFEliminar:
                        eliminarLF();
                        break;
                }

            }
        };
        // Determianción del método setOnClickListener de cada uno de los botones para ponerlos a funcionar
        // dependiendo de la función que les corresponda.
        view.findViewById(R.id.btnMLFFindPlace).setOnClickListener(listener);
        view.findViewById(R.id.btnMLFAceptar).setOnClickListener(listener);
        view.findViewById(R.id.btnMLFCancelar).setOnClickListener(listener);
        view.findViewById(R.id.btnMLFEliminar).setOnClickListener(listener);

        /*
         * Especificaciones de la base de datos
         */
        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();
        this.db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        this.db.setFirestoreSettings(settings);
        frecuentes = db.collection("users/"+ Objects.requireNonNull(user).getUid()+"/frecuentes");

        return view;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    /**
     * onActivityResult: determina que debe desplegarse la actividad que contiene la API de Google Places
     * para determinar el lugar que se quiere guardar
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        if(requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(getActivity(), data);
                destino = new Frecuente(null, place.getId(), place.getLatLng().latitude, place.getLatLng().longitude, Objects.requireNonNull(this.place.getAddress()).toString(), true);
                this.txtMLFDireccion.setText(place.getAddress());
            }
        }
    }

    /**
     * modificarMLF: permite modificar un LugarFrecuente tanto en la lista de LugaresFrecuentes
     * del dispositivo como en Firebase.
     */
    public void modificaMLF(){
        modificarUI();
        if(this.getArguments().getString("id")!=null){

            this.destino.setNombre(String.valueOf(this.txtMLFNombre.getText()));
            this.destino.setId(this.getArguments().getString("id"));
            this.destino.setFrecuente(true);

            final AppCompatActivity act = (AppCompatActivity) this.getActivity();

            this.frecuentes.document(Objects.requireNonNull(this.getArguments().getString("id"))).set(destino.toMap())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG, "DocumentSnapshot successfully written!");
                            ((LugaresFrecuentes)getActivity()).user.modifyFrecuenteById(destino.getId(), destino);
                            getDialog().dismiss();
                            getActivity().recreate();
                            Toast.makeText(getActivity(), "Lugar Frecuente modificado.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error writing document");
                            Toast.makeText(getActivity(), "Operación fallida.", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "Ha habido un problema.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * eliminarLF: permite eliminar un LugarFrecuente tanto de la lista de LugaresFrecuentes del usuario
     * del dispositivo como en Firebase.
     */
    private void eliminarLF(){
        modificarUI();
        this.frecuentes.document(Objects.requireNonNull(this.getArguments().getString("id"))).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "DocumentSnapshot successfully deleted!");
                ((LugaresFrecuentes)getActivity()).user.deleteFrecuenteById(destino.getId());
                getDialog().dismiss();
                getActivity().recreate();
                Toast.makeText(getActivity(), "Lugar Frecuente eliminado.", Toast.LENGTH_LONG).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error writing document");
                        Toast.makeText(getActivity(), "Operación fallida.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void modificarUI(){
        layoutMLFButton.setVisibility(View.GONE);
        readyMLFProgressBar.setVisibility(View.VISIBLE);
    }
}
