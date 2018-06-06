package com.tracker.tracker;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Clases: ModifySQDialog: esta clase se encarga de manipular el dialogo para modificar los datos
 * de un ser querido.
 */

public class ModifySQDialog extends DialogFragment {
    private static final String TAG = "ModifySQDialog";
    private EditText txtModifyNombre, txtModifyPhone;

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
        View view = inflater.inflate(R.layout.dialog_fragment_sq, container, false);
        txtModifyNombre = view.findViewById(R.id.txtModifyNombre);
        txtModifyPhone = view.findViewById(R.id.txtModifyPhone);
        Log.e(TAG, "Hoooollaaaaaa" + this.getArguments().getString("Nombre"));
        txtModifyNombre.setText(this.getArguments().getString("Nombre"));
        txtModifyPhone.setText(this.getArguments().getString("Telf"));

        return view;
    }
}
