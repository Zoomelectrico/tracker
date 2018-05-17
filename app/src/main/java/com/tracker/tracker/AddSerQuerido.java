package com.tracker.tracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.tracker.tracker.tareas.AddSerQueridoAsync;

public class AddSerQuerido extends AppCompatActivity implements View.OnClickListener{

    // UI
    private Button btnAdd;
    private EditText txtNombre;
    private EditText txtPhone;
    // Firebase
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ser_querido);
        this.btnAdd = (Button) findViewById(R.id.btnAdd);
        this.txtNombre = (EditText) findViewById(R.id.txtNombre);
        this.txtPhone = (EditText) findViewById(R.id.txtPhone);

        // Firebase
        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();

        // Add Event Listener
        this.btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String nombre = String.valueOf(txtNombre.getText());
        String telf = String.valueOf(txtPhone.getText());

        boolean notEmpty = nombre.length() > 0 && telf.length() > 0;
        boolean numeric = android.text.TextUtils.isDigitsOnly(telf);
        if(notEmpty && numeric) {
            new AddSerQueridoAsync(nombre, telf).execute(this.user);
            Toast.makeText(this, "Ser Querido registrado",Toast.LENGTH_SHORT).show();
            finish();
        } else if (!notEmpty) {
            Toast.makeText(this, "Los campos no pueden estar vacios", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "El campo de Telf solo puede tener numero", Toast.LENGTH_SHORT ).show();
        }
    }

}
