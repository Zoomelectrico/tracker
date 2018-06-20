package com.tracker.tracker;

import android.app.DialogFragment;
import android.content.Intent;
import android.location.Location;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
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

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static java.lang.String.valueOf;

public class AddLugarFrecuenteDialog extends DialogFragment implements NavigationView.OnNavigationItemSelectedListener {
    private static final int PLACE_PICKER_REQUEST = 2;

    private FirebaseAuth auth;
    @Nullable
    private FirebaseUser user;
    private FirebaseFirestore db;
    private CollectionReference frecuentes;

    private TextView txtLFCoordenadas;
    private EditText txtLFNombre;
    private ImageButton btnAgregarLF;

    private Location destinationLF;
    private String nombreLF;
    private String direccion;
    private Place place;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
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
                }

            }
        };
        view.findViewById(R.id.btnLFFindPlace).setOnClickListener(listener);
        view.findViewById(R.id.btnAgregarLF).setOnClickListener(listener);
        this.txtLFCoordenadas = view.findViewById(R.id.txtLFCoordenadas);
        this.txtLFNombre = view.findViewById(R.id.txtLFNombre);
        /**
         * Especificaciones de la base de datos
         */
        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();
        this.db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        this.db.setFirestoreSettings(settings);
        frecuentes = db.collection("users/"+user.getUid()+"/frecuentes");

        return view;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        if(requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(getActivity(), data);
                this.txtLFCoordenadas.setText("["+place.getLatLng().latitude + ", " + place.getLatLng().longitude+"]");
            }
        }
    }

    private void agregarLugarFrecuente(){
        nombreLF = String.valueOf(this.txtLFNombre.getText());
        if(nombreLF.length() > 0 && place != null) {
            Frecuente frecuente = new Frecuente(nombreLF, place.getId(), place.getLatLng().latitude, place.getLatLng().longitude, this.place.getAddress().toString());
            ((LugaresFrecuentes)getActivity()).user.addFrecuentes(frecuente);
            getDialog().dismiss();
            getActivity().recreate();
            frecuentes.add(frecuente.toMap())
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
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
