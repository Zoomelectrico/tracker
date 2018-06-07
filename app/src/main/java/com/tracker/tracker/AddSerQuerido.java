package com.tracker.tracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;
import com.tracker.tracker.Modelos.Contacto;
import com.tracker.tracker.Modelos.Usuario;

/**
 *
 */
public class AddSerQuerido extends AppCompatActivity implements View.OnClickListener{

    private Button btnAdd;
    private EditText txtNombre;
    private EditText txtPhone;
    private Usuario usuario;

    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // UI
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ser_querido);

        this.usuario = (Usuario) ((Bundle) this.getIntent().getParcelableExtra("user")).getParcelable("user");

        this.btnAdd = findViewById(R.id.btnAdd);
        this.txtNombre = findViewById(R.id.txtNombre);
        this.txtPhone = findViewById(R.id.txtPhone);

        Toolbar toolbar = findViewById(R.id.tbAddSer);
        toolbar.setTitle("Agregar un ser querido");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Add Event Listener
        this.btnAdd.setOnClickListener(this);
    }

    /**
     *
     */
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
            this.usuario.addContacto(new Contacto(name, phone, true));
            this.usuario.saveData(FirebaseFirestore.getInstance());
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
