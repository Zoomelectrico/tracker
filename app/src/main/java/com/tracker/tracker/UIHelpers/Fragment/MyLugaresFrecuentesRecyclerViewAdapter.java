package com.tracker.tracker.UIHelpers.Fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tracker.tracker.R;
import com.tracker.tracker.UIHelpers.Fragment.LugaresFrecuentesFragment.OnListFragmentInteractionListener;
import com.tracker.tracker.modelos.Frecuente;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Frecuente} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyLugaresFrecuentesRecyclerViewAdapter extends RecyclerView.Adapter<MyLugaresFrecuentesRecyclerViewAdapter.ViewHolder> {

    private final List<Frecuente> frecuentes;
    private final OnListFragmentInteractionListener mListener;

    public MyLugaresFrecuentesRecyclerViewAdapter(List<Frecuente> items, OnListFragmentInteractionListener listener) {
        this.frecuentes = items;
        this.mListener = listener;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_lugaresfrecuentes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.item = frecuentes.get(position);
        holder.txtNombreF.setText(frecuentes.get(position).getNombre());
        Log.e(TAG, "Lo que deberia mostrar es: position " + position + frecuentes.get(position).getNombre());
        // holder.txtCoordenadasF.setText(String.valueOf(frecuentes.get(position).getLatitud()) + ", " + String.valueOf(frecuentes.get(position).getLongitud()));
        holder.txtDireccionF.setText(frecuentes.get(position).getDireccion());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return frecuentes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        public final View view;
        private TextView txtNombreF;
        private TextView txtDireccionF;
        public Frecuente item;

        private ViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
            txtNombreF = view.findViewById(R.id.frecNombre);
            txtDireccionF = view.findViewById(R.id.frecDireccion);
        }

        @Override
        public String toString() {
            return item.toString();
        }
    }
}
