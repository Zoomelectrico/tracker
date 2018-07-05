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
import android.widget.ArrayAdapter;
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
import com.thomashaertel.widget.MultiSpinner;
import com.tracker.tracker.modelos.Contacto;
import com.tracker.tracker.modelos.Frecuente;
import com.tracker.tracker.modelos.Rutina;
import com.tracker.tracker.modelos.fecha.Dia;

import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class mRutinasDialog extends DialogFragment implements NavigationView.OnNavigationItemSelectedListener  {

    private FirebaseAuth auth;
    @Nullable
    private FirebaseUser user;
    private FirebaseFirestore db;
    private CollectionReference rutinas;

    private TextView txtMRDireccion;
    private EditText txtMRNombre;
    private EditText txtMRHora;
    private EditText txtMRMinutos;
    private EditText txtMRSegundos;

    private LinearLayout layoutButton;
    private ProgressBar readyProgressBar;

    private ArrayList<Contacto> contactosSel;
    private ArrayList<String> diasSel;

    private MultiSpinner spinnerMultiMRSQ;
    private MultiSpinner spinnerMultiMRDias;


    private static final int PLACE_PICKER_REQUEST = 2;
    private Place place;
    private Frecuente destino;

    private Rutina rutina;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_m_rutinas_dialog, container, false);
        // Determinación de los campos para su uso.
        this.rutina = this.getArguments().getParcelable("rutina");
        this.destino = rutina.getDestino();
        txtMRDireccion = view.findViewById(R.id.txtMRDireccion);
        txtMRNombre = view.findViewById(R.id.txtMRNombre);
        /*this.rutina = new Rutina(String.valueOf(this.txtMRNombre), (Frecuente) this.getArguments().getParcelable("Destino"), this.getArguments().<Contacto>getParcelableArrayList("seresQueridos"),
                this.getArguments().getStringArrayList("dias"), this.getArguments().getString("hora"), this.getArguments().getBoolean("nueva"));*/
        txtMRHora = view.findViewById(R.id.txtMRHora);
        txtMRMinutos = view.findViewById(R.id.txtMRMinuto);
        txtMRSegundos = view.findViewById(R.id.txtMRSegundo);
        txtMRNombre.setText(rutina.getNombre());
        txtMRDireccion.setText(rutina.getDestino().getDireccion());
        String[] horaVec = this.rutina.getHora().split(":");
        txtMRHora.setText(horaVec[0]);
        txtMRMinutos.setText(horaVec[1]);
        txtMRSegundos.setText(horaVec[2]);
        layoutButton = view.findViewById(R.id.layoutButtons);
        readyProgressBar = view.findViewById(R.id.readyProgressBar);
        readyProgressBar.setVisibility(View.GONE);

        this.contactosSel = new ArrayList<>();
        this.diasSel = new ArrayList<>();
        this.spinnerMultiMRSQ = view.findViewById(R.id.spinnerMultiMRSQ);
        this.spinnerMultiMRDias = view.findViewById(R.id.spinnerMultiMRDias);

        // Especificación de las funciones que se desarrollaran al hacer click en algun boton especifico
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnMRFindPlace:
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        try {
                            Intent intent = builder.build(getActivity());
                            startActivityForResult(intent, PLACE_PICKER_REQUEST);
                        }  catch (@NonNull GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                            Log.e("ERROR", e.getMessage(), e);
                        }
                        break;
                    case R.id.btnMRAceptar:
                        if(confirmValues()){
                            modificaMR();
                        }
                        break;
                    case R.id.btnMRCancelar:
                        getDialog().dismiss();
                        break;
                    case R.id.btnMREliminar:
                        eliminarR();
                        break;
                }

            }
        };
        // Determianción del método setOnClickListener de cada uno de los botones para ponerlos a funcionar
        // dependiendo de la función que les corresponda.
        view.findViewById(R.id.btnMRFindPlace).setOnClickListener(listener);
        view.findViewById(R.id.btnMRAceptar).setOnClickListener(listener);
        view.findViewById(R.id.btnMRCancelar).setOnClickListener(listener);
        view.findViewById(R.id.btnMREliminar).setOnClickListener(listener);

        this.spinnerConfigSQ();
        this.spinnerConfigDias();

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
        rutinas = db.collection("users/"+ Objects.requireNonNull(user).getUid()+"/rutinas");

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        if(requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(getActivity(), data);
                destino = new Frecuente(null, place.getId(), place.getLatLng().latitude, place.getLatLng().longitude, Objects.requireNonNull(this.place.getAddress()).toString(), true);
                this.txtMRDireccion.setText(place.getAddress());
            }
        }
    }

    private void spinnerConfigSQ() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        if(((Rutine)getActivity()).user.haveContactos()) {
            for (Contacto c : ((Rutine)getActivity()).user.getContactos()) {
                adapter.add(c.getNombre());
            }
            this.spinnerMultiMRSQ.setAdapter(adapter, false, new MultiSpinner.MultiSpinnerListener() {
                @Override
                public void onItemsSelected(@NonNull boolean[] selected) {
                    contactosSel.clear();
                    for (int i = 0; i < selected.length; i++) {
                        if(selected[i]) {
                            contactosSel.add(((Rutine)getActivity()).user.getContacto(i));
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
        this.spinnerMultiMRDias.setAdapter(adapter, false, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(@NonNull boolean[] selected) {
                diasSel.clear();
                for (int i = 0; i < selected.length; i++) {
                    if(selected[i]) {
                        String diaShort = Dia.getShortDiaFromDia(adapter.getItem(i).toString());
                        diasSel.add(diaShort);
                    }
                }
            }
        });
    }

    /**
     * modificarMLF: permite modificar un LugarFrecuente tanto en la lista de LugaresFrecuentes
     * del dispositivo como en Firebase.
     */
    public void modificaMR(){
        modificarUI();
        if(this.rutina.getId()!=null && txtMRNombre.getText().length()>0 && destino!=null && txtMRHora.getText().length()>0 && txtMRMinutos.getText().length()>0
                && txtMRSegundos.getText().length()>0){
            this.rutina.setNombre(String.valueOf(this.txtMRNombre.getText()));
            this.rutina.setDestino(destino);
            if(!contactosSel.isEmpty()){
                this.rutina.setSeresQueridos(contactosSel);
            }
            if(!diasSel.isEmpty()){
                this.rutina.setDias(diasSel);
            }
            this.rutina.setHora(String.valueOf(this.txtMRHora.getText()) + ":" + String.valueOf(this.txtMRMinutos.getText()) + ":" + String.valueOf(this.txtMRSegundos.getText()));

            final AppCompatActivity act = (AppCompatActivity) this.getActivity();

            this.rutinas.document(Objects.requireNonNull(this.rutina.getId())).set(rutina.toMap())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG, "DocumentSnapshot successfully written!");
                            ((Rutine)getActivity()).user.modifyRutinaById(rutina.getId(), rutina);
                            getDialog().dismiss();
                            getActivity().recreate();
                            Toast.makeText(getActivity(), "Rutina modificada.", Toast.LENGTH_LONG).show();
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
    private void eliminarR(){
        this.rutinas.document(Objects.requireNonNull(rutina.getId())).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "DocumentSnapshot successfully deleted!");

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
        layoutButton.setVisibility(View.GONE);
        readyProgressBar.setVisibility(View.VISIBLE);
    }

    private boolean confirmValues(){
        boolean checked = true;
        if (txtMRNombre.getText().length() <= 0) {
            checked = false;
            txtMRNombre.setError("El campo de nombre no puede estar vacío");
        }
        if (txtMRHora.getText().length() <= 0) {
            checked = false;
            txtMRHora.setError("El campo de hora no puede estar vacío");
        } else {
            Log.e(TAG, "confirmValues: El numero de hora es : " + Integer.parseInt(String.valueOf(txtMRHora.getText())) );
            if(Integer.parseInt(String.valueOf(txtMRHora.getText()))>23 || Integer.parseInt(String.valueOf(txtMRHora.getText()))<0 ){
                checked = false;
                txtMRHora.setError("Debe seleccionar un valor entre las 0 hrs y las 24 hrs");
            }
        }
        if (txtMRMinutos.getText().length() <= 0) {
            checked = false;
            txtMRMinutos.setError("El campo de minutos no puede estar vacío");
        } else {
            if(Integer.parseInt(String.valueOf(txtMRMinutos.getText()))>59 || Integer.parseInt(String.valueOf(txtMRMinutos.getText()))<0 ){
                checked = false;
                txtMRMinutos.setError("Debe seleccionar un valor entre los 0 y 59");
            }
        }
        if (txtMRSegundos.getText().length() <= 0) {
            checked = false;
            txtMRSegundos.setError("El campo de segundos no puede estar vacío");
        } else {
            if (Integer.parseInt(String.valueOf(txtMRSegundos.getText())) > 59 || Integer.parseInt(String.valueOf(txtMRSegundos.getText())) < 0) {
                checked = false;
                txtMRSegundos.setError("Debe seleccionar un valor entre los 0 y 59");
            }
        }
        return checked;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
