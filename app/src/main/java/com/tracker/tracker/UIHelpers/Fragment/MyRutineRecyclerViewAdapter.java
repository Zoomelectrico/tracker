package com.tracker.tracker.UIHelpers.Fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tracker.tracker.Modelos.Rutina;
import com.tracker.tracker.R;
import com.tracker.tracker.UIHelpers.Fragment.RutineFragment.OnListFragmentInteractionListener;

import java.util.List;

public class MyRutineRecyclerViewAdapter extends RecyclerView.Adapter<MyRutineRecyclerViewAdapter.ViewHolder> {

    private final List<Rutina> rutinas;
    private final OnListFragmentInteractionListener listener;

    public MyRutineRecyclerViewAdapter(List<Rutina> items, OnListFragmentInteractionListener listener) {
        this.rutinas = items;
        this.listener = listener;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_rutina, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.rutina = rutinas.get(position);
        holder.nombreRutina.setText(rutinas.get(position).getNombre());
        holder.horaRutina.setText(rutinas.get(position).getHora());
        holder.diasRutina.setText(String.valueOf(rutinas.get(position).getDias()));
        holder.direccionRutina.setText(rutinas.get(position).getDestino().getDireccion());
        holder.seresQueridosRutina.setText(String.valueOf(rutinas.get(position).getSeresQueridosName()));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onListFragmentInteraction(holder.rutina);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rutinas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final View view;
        private final TextView nombreRutina;
        private final TextView horaRutina;
        private final TextView diasRutina;
        private final TextView direccionRutina;
        private final TextView seresQueridosRutina;
        public Rutina rutina;

        private ViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
            nombreRutina = view.findViewById(R.id.nombreRutina);
            horaRutina = view.findViewById(R.id.horaRutina);
            diasRutina = view.findViewById(R.id.diasRutina);
            direccionRutina = view.findViewById(R.id.direccionRutina);
            seresQueridosRutina = view.findViewById(R.id.seresQueridosRutina);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + direccionRutina.getText() + "'";
        }
    }
}
