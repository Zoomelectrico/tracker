package com.tracker.tracker;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.tracker.tracker.Modelos.Frecuente;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class AddLugarFrecuenteDialog extends DialogFragment implements NavigationView.OnNavigationItemSelectedListener {
    private static final int PLACE_PICKER_REQUEST = 2;

    private FirebaseAuth auth;
    @Nullable
    private FirebaseUser user;
    private FirebaseFirestore db;
    private CollectionReference frecuentes;

    private TextView txtLFCoordenadas;
    private EditText txtLFNombre;
    private String nombreLF;
    private Place place;
    private Frecuente destino;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_add_lugar_frecuente, container, false);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnLFFindPlace:
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        try {
                            Intent intent = builder.build(getActivity());
                            startActivityForResult(intent, PLACE_PICKER_REQUEST);
                        }  catch (@NonNull GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                            Log.e("ERROR", e.getMessage(), e);
                        }
                    break;
                    case R.id.btnAgregarLF:
                        agregarLugarFrecuente();
                        break;
                    case R.id.btnALFCancelar:
                        getDialog().dismiss();
                }

            }
        };
        view.findViewById(R.id.btnLFFindPlace).setOnClickListener(listener);
        view.findViewById(R.id.btnAgregarLF).setOnClickListener(listener);
        view.findViewById(R.id.btnALFCancelar).setOnClickListener(listener);
        this.txtLFCoordenadas = view.findViewById(R.id.txtLFCoordenadas);
        this.txtLFNombre = view.findViewById(R.id.txtLFNombre);
        /*
         * Ocultar el boton de seleccionar destino si ya se pasa desde el MainActivity
         */
        if(this.getArguments().getBoolean("haveDestino")){
            this.destino = new Frecuente(null, this.getArguments().getString("id"), this.getArguments().getDouble("destLat"),
                    this.getArguments().getDouble("destLon"), this.getArguments().getString("destDireccion"), false);
            this.destino.setFrecuente(true);
            txtLFCoordenadas.setText("[" + String.valueOf(this.destino.getLatitud()) + ", " + String.valueOf(this.destino.getLongitud()) + "]");
            view.findViewById(R.id.btnLFFindPlace).setVisibility(View.GONE);
        }
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

                this.txtLFCoordenadas.setText("["+place.getLatLng().latitude + ", " + place.getLatLng().longitude+"]");
            }
        }
    }

    /**
     * agregarLugarFrecuente: permite agregar un lugar frecuente tanto a Firebase como a la Lista de
     * LugaresFrecuentes en el dispositivo, logra actualizar ambas listas de forma casi simultáneo,
     * cargando primero el LugarFrecuente en firebase para luego pasarle el id del LugarFrecuente y
     * guardarlo en la lista del dispositivo.
     */

    private void agregarLugarFrecuente(){
        nombreLF = String.valueOf(this.txtLFNombre.getText());
        final Boolean haveDestino = this.getArguments().getBoolean("haveDestino");
        if(nombreLF.length() > 0 && destino != null) {
            destino.setNombre(nombreLF);
            frecuentes.add(destino.toMap())
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(@NonNull DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            destino.setId(documentReference.getId());
                            if(!haveDestino){
                                ((LugaresFrecuentes)getActivity()).user.addFrecuentes(destino);
                                getDialog().dismiss();
                                getActivity().recreate();
                            } else{
                                ((MainActivity)getActivity()).usuario.addFrecuentes(destino);
                                getDialog().dismiss();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                            Toast.makeText(getActivity(), "Ha ocurrido un error", Toast.LENGTH_LONG).show();
                            getDialog().dismiss();
                        }
                    });
        }
        if (nombreLF.length() <= 0) {
            txtLFNombre.setError("El campo de nombre no puede estar vacío");
        }
        if (txtLFCoordenadas.getText().length() <= 0) {
            Toast.makeText(getActivity(), "Por favor, escoja un destino", Toast.LENGTH_LONG).show();
        }

    }

}
