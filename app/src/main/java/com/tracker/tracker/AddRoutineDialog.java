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
import android.widget.ArrayAdapter;
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
import com.thomashaertel.widget.MultiSpinner;
import com.tracker.tracker.Modelos.Contacto;
import com.tracker.tracker.Modelos.Frecuente;
import com.tracker.tracker.Modelos.Rutina;
import com.tracker.tracker.Modelos.Usuario;
import com.tracker.tracker.Modelos.fecha.Dia;

import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class AddRoutineDialog extends DialogFragment implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PLACE_PICKER_REQUEST = 2;

    private FirebaseAuth auth;
    @Nullable
    private FirebaseUser user;
    private FirebaseFirestore db;
    private CollectionReference rutinas;

    @Nullable
    private Usuario usuario;
    private ArrayList<Contacto> contactosSel;
    private ArrayList<String> diasSel;

    private TextView txtARCoordenadas;
    private EditText txtARNombre;
    private EditText txtARHora;
    private EditText txtARMinuto;
    private EditText txtARSegundo;
    private String nombreAR;
    private Place place;
    private Frecuente destino;
    private Rutina rutina;

    private MultiSpinner spinnerMultiARSQ;
    private MultiSpinner spinnerMultiARDias;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.usuario = this.getArguments().getParcelable("user");
        View view = inflater.inflate(R.layout.dialog_fragment_add_routine, container, false);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnARFindPlace:
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        try {
                            Intent intent = builder.build(getActivity());
                            startActivityForResult(intent, PLACE_PICKER_REQUEST);
                        }  catch (@NonNull GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                            Log.e("ERROR", e.getMessage(), e);
                        }
                        break;
                    case R.id.btnAgregarR:
                        agregarRutina();
                        break;
                    case R.id.btnARCancelar:
                        getDialog().dismiss();
                }

            }
        };
        this.contactosSel = new ArrayList<>();
        this.diasSel = new ArrayList<>();
        this.spinnerMultiARSQ = view.findViewById(R.id.spinnerMultiARSQ);
        this.spinnerMultiARDias = view.findViewById(R.id.spinnerMultiARDias);
        view.findViewById(R.id.btnARFindPlace).setOnClickListener(listener);
        view.findViewById(R.id.btnAgregarR).setOnClickListener(listener);
        view.findViewById(R.id.btnARCancelar).setOnClickListener(listener);
        this.txtARCoordenadas = view.findViewById(R.id.txtARCoordenadas);
        this.txtARNombre = view.findViewById(R.id.txtARNombre);
        this.txtARHora = view.findViewById(R.id.txtARHora);
        this.txtARMinuto = view.findViewById(R.id.txtARMinuto);
        this.txtARSegundo = view.findViewById(R.id.txtARSegundo);

        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();
        this.db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        this.db.setFirestoreSettings(settings);
        rutinas = db.collection("users/"+ Objects.requireNonNull(user).getUid()+"/rutinas");

        this.spinnerConfigSQ();
        this.spinnerConfigDias();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        if(requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(getActivity(), data);
                destino = new Frecuente(null, place.getId(), place.getLatLng().latitude, place.getLatLng().longitude, Objects.requireNonNull(this.place.getAddress()).toString(), true);

                this.txtARCoordenadas.setText("["+place.getLatLng().latitude + ", " + place.getLatLng().longitude+"]");
            }
        }
    }

    private void agregarRutina(){
        String nombreAR = String.valueOf(txtARNombre.getText());
        String horaAR = String.valueOf(txtARHora.getText()) + ":" + String.valueOf(txtARMinuto.getText()) + ":" + String.valueOf(txtARSegundo.getText());
        if(txtARNombre.getText() != null && destino != null && !contactosSel.isEmpty()) {
            rutina = new Rutina(String.valueOf(txtARNombre.getText()), destino, contactosSel, diasSel, horaAR, true);
            rutinas.add(rutina.toMap())
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(@NonNull DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            rutina.setId(documentReference.getId());
                            ((Rutine)getActivity()).user.addRutina(rutina);
                            getDialog().dismiss();
                            getActivity().recreate();
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
        if (nombreAR.length() <= 0) {
            txtARNombre.setError("El campo de nombre no puede estar vacío");
        }
        if (txtARCoordenadas.getText().length() <= 0) {
            Toast.makeText(getActivity(), "Por favor, escoja un destino", Toast.LENGTH_LONG).show();
        }

    }

    private void spinnerConfigSQ() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        if(this.usuario.haveContactos()) {
            for (Contacto c : this.usuario.getContactos()) {
                adapter.add(c.getNombre());
            }
            this.spinnerMultiARSQ.setAdapter(adapter, false, new MultiSpinner.MultiSpinnerListener() {
                @Override
                public void onItemsSelected(@NonNull boolean[] selected) {
                    contactosSel.clear();
                    for (int i = 0; i < selected.length; i++) {
                        if(selected[i]) {
                            contactosSel.add(usuario.getContacto(i));
                        }
                    }
                    Log.e("Contacto", "contacto good");
                }
            });
        } else {
            Toast.makeText(getActivity(), "Debe añadir un ser Querido", Toast.LENGTH_SHORT).show();
        }
    }

    private void spinnerConfigDias() {
        final ArrayList<String> adapterList = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        adapter.add("Lunes");
        adapter.add("Martes");
        adapter.add("Miércoles");
        adapter.add("Jueves");
        adapter.add("Viernes");
        adapter.add("Sábado");
        adapter.add("Domingo");
        for(String a: adapterList){
            adapter.add(a);
        }
        this.spinnerMultiARDias.setAdapter(adapter, false, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(@NonNull boolean[] selected) {
                diasSel.clear();
                for (int i = 0; i < selected.length; i++) {
                    if(selected[i]) {
                        diasSel.add(adapter.getItem(i));
                    }
                }
            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
