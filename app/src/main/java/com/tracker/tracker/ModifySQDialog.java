package com.tracker.tracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.tracker.tracker.Modelos.Usuario;
import com.tracker.tracker.UIHelpers.Fragment.MyContactoRecyclerViewAdapter;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Clases: ModifySQDialog: esta clase se encarga de manipular el dialogo para modificar los datos
 * de un ser querido.
 */

public class ModifySQDialog extends DialogFragment implements View.OnClickListener{
    private Usuario usuario;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private static final String TAG = "ModifySQDialog";
    private EditText txtModifyNombre, txtModifyPhone;
    private ImageButton btnEditarSQ, btnCancelar, btnEliminarSQ;

    /**
     * Método OnCreate, especifica que al crear el dialogo, la información del serQuerido
     * seleccionado se muestre en los campos de nombre y telefono para su posterior modificación
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.usuario = (Usuario) this.getActivity().getIntent().getParcelableExtra("user");
        View view = inflater.inflate(R.layout.dialog_fragment_sq, container, false);
        /**
         * Pasar información del ser querido a los campos de Nombre y Teléfono
         */
        txtModifyNombre = view.findViewById(R.id.txtModifyNombre);
        txtModifyPhone = view.findViewById(R.id.txtModifyPhone);
        txtModifyNombre.setText(this.getArguments().getString("Nombre"));
        txtModifyPhone.setText(this.getArguments().getString("Telf"));

        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();
        this.db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        this.db.setFirestoreSettings(settings);

        btnEditarSQ = view.findViewById(R.id.btnEditarSQ);
        btnEliminarSQ = view.findViewById(R.id.btnEliminarSQ);
        btnCancelar = view.findViewById(R.id.btnCancelar);

        btnEditarSQ.setOnClickListener(this);
        btnEliminarSQ.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnEditarSQ:
                this.modificarSQ();
            break;
            case R.id.btnCancelar:
                getDialog().dismiss();
            break;
            case R.id.btnEliminarSQ:
                this.eliminarSQ();
            break;
        }
    }

    /**
     * ModificarSQ: método para modificar los datos del ser querido seleccionado.
     * Modifica los campos de nombre y telf en la base de datos
     * Muestra un Toast indicando el éxito o fracaso de la operación
     */
    private void modificarSQ(){
        final Context ActivityContext = getActivity();
        final Activity act = this.getActivity();

        Map<String, Object> serQueridoModify = new HashMap<>();
        serQueridoModify.put("nombre", String.valueOf(txtModifyNombre.getText()));
        serQueridoModify.put("telf", String.valueOf(txtModifyPhone.getText()));
        /**
         * Actualización de la lista de Contactos de SeresQueridos del usuario
         */
        ((seresQueridos)getActivity()).user.modificarContacto(this.getArguments().getInt("position"),
                String.valueOf(txtModifyNombre.getText()), String.valueOf(txtModifyPhone.getText()));

        db.collection("users").document(this.user.getUid())
            .collection("contactos").document(this.getArguments().getString("id"))
            .set(serQueridoModify)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.e(TAG, "DocumentSnapshot successfully written!");
                    getDialog().dismiss();
                    act.recreate();
                    Toast.makeText(ActivityContext, "Ser Querido modificado.", Toast.LENGTH_LONG).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error writing document");
                    Toast.makeText(ActivityContext, "Operación fallida.", Toast.LENGTH_LONG).show();
                }
            });
    }

    /**
     * eliminarSQ: método para eliminar el ser querido seleccionado.
     * Muestra un Toast indicando el éxito o fracaso de la operación
     */
    private void eliminarSQ(){
        /**
         * Actualización de la lista de Contactos de SeresQueridos del usuario.
         */
        ((seresQueridos)getActivity()).user.eliminarContacto(this.getArguments().getString("Nombre"), this.getArguments().getString("Telf"));

        db.collection("users").document(this.user.getUid()).collection("contactos")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                  if(task.getResult() != null) {
                      for (DocumentSnapshot document: task.getResult()) {
                          if(document.getString("nombre").equals(getArguments().getString("Nombre")) && document.getString("telf").equals(getArguments().getString("Telf"))) {
                              String ID = document.getId();
                              db.document("users/"+user.getUid()+"/contactos/"+ID).delete();
                              break;
                          }
                      }
                  }
                }
            }
        });
        getDialog().dismiss();
        this.getActivity().recreate();
    }
}
