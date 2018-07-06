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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.tracker.tracker.modelos.Contacto;
import com.tracker.tracker.modelos.Frecuente;
import com.tracker.tracker.modelos.Rutina;
import com.tracker.tracker.modelos.Usuario;
import com.tracker.tracker.modelos.fecha.Dia;

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

    private LinearLayout layoutAButton;
    private ProgressBar readyAProgressBar;

    private MultiSpinner spinnerMultiARSQ;
    private MultiSpinner spinnerMultiARDias;
    private Spinner spinnerAAMPM;
    private Boolean isAM;

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
                        if(confirmValues()){
                            agregarRutina();
                        }
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
        layoutAButton = view.findViewById(R.id.layoutAButtons);
        readyAProgressBar = view.findViewById(R.id.readyAProgressBar);
        readyAProgressBar.setVisibility(View.GONE);

        this.spinnerAAMPM = view.findViewById(R.id.spinnerAAMPM);

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
        this.spinnerConfigTiempo();

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
        modificarUI();
        String nombreAR = String.valueOf(txtARNombre.getText());
        String horaAR = null;
        if(!isAM){
            String horaChange = String.valueOf(Integer.parseInt(String.valueOf(this.txtARHora.getText())) + 12);
            horaAR = horaChange + ":" + String.valueOf(this.txtARMinuto.getText()) + ":" + "00";
        } else {
            horaAR = String.valueOf(txtARHora.getText()) + ":" + String.valueOf(txtARMinuto.getText()) + ":" + "00";
        }
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
        for(String s: Dia.getDias()) {
            adapter.add(s);
        }
        for(String a: adapterList){
            adapter.add(a);
        }
        this.spinnerMultiARDias.setAdapter(adapter, false, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(@NonNull boolean[] selected) {
                diasSel.clear();
                for (int i = 0; i < selected.length; i++) {
                    if(selected[i]) {
                        String dia = Dia.getShortDiaFromDia(adapter.getItem(i));
                        diasSel.add(dia);
                    }
                }
            }
        });
    }

    private void spinnerConfigTiempo() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_ampm);
        adapter.add("AM");
        adapter.add("PM");
        this.spinnerAAMPM.setAdapter(adapter);
        this.spinnerAAMPM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    Log.e(TAG, "onItemSelected: Esto es " + adapter.getItem(position) );
                    isAM = false;
                } else {
                    isAM = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void modificarUI(){
        layoutAButton.setVisibility(View.GONE);
        readyAProgressBar.setVisibility(View.VISIBLE);
    }

    private boolean confirmValues(){
        boolean checked = true;
        if (txtARNombre.getText().length() <= 0) {
            checked = false;
            txtARNombre.setError("El campo de nombre no puede estar vacío");
        }
        if (txtARHora.getText().length() <= 0) {
            checked = false;
            txtARHora.setError("El campo de hora no puede estar vacío");
        } else {
            Log.e(TAG, "confirmValues: El numero de hora es : " + Integer.parseInt(String.valueOf(txtARHora.getText())) );
            if(Integer.parseInt(String.valueOf(txtARHora.getText()))>12 || Integer.parseInt(String.valueOf(txtARHora.getText()))<0 ){
                checked = false;
                txtARHora.setError("Debe seleccionar un valor entre las 0 hrs y las 12 hrs");
            }
        }
        if (txtARMinuto.getText().length() <= 0) {
            checked = false;
            txtARMinuto.setError("El campo de minutos no puede estar vacío");
        } else {
            if(Integer.parseInt(String.valueOf(txtARMinuto.getText()))>59 || Integer.parseInt(String.valueOf(txtARMinuto.getText()))<0 ){
                checked = false;
                txtARMinuto.setError("Debe seleccionar un valor entre los 0 y 59");
            }
        }
        return checked;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
