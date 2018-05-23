package com.tracker.tracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tracker.tracker.tareas.AddSerQueridoAsync;
import com.tracker.tracker.tareas.SeresQueridosAsync;

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
        this.btnAdd = findViewById(R.id.btnAdd);
        this.txtNombre = findViewById(R.id.txtNombre);
        this.txtPhone = findViewById(R.id.txtPhone);
        Toolbar toolbar = findViewById(R.id.tbAddSer);
        toolbar.setTitle("Agregar un ser querido");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Firebase
        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();

        // Add Event Listener
        this.btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String name = String.valueOf(txtNombre.getText());
        String phone = String.valueOf(txtPhone.getText());

        // Booleans
        boolean notEmpty = name.length() > 0 && phone.length() > 0;
        boolean isAlpha = name.matches("[ a-zA-Z]+");
        boolean isNumeric = android.text.TextUtils.isDigitsOnly(phone);
        boolean isLongEnough = phone.length() == 11;

        if(notEmpty && isAlpha && isNumeric && isLongEnough) {
            new AddSerQueridoAsync(name, phone).execute(this.user);
            Toast.makeText(this, "Ser querido registrado",Toast.LENGTH_SHORT).show();
            //Obtener información de los seres queridos
            new SeresQueridosAsync().execute(this.user);
            finish();
        }

        if (name.length() <= 0) {
            txtNombre.setError("El campo de nombre no puede estar vacío");
        }

        if (phone.length() <= 0) {
            txtPhone.setError("El campo de teléfono no puede estar vacío");
        }

        if (!isAlpha){
            txtNombre.setError("El campo de nombre solo puede contener letras");
        }

        if (!isNumeric){
            txtPhone.setError("El campo de teléfono solo puede contener números");
        }

        if (!isLongEnough) {
            txtPhone.setError("El teléfono ingresado debe contener 11 dígitos");
        }
    }

}
