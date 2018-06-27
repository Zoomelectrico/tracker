package com.tracker.tracker.UIHelpers.Fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tracker.tracker.UIHelpers.Fragment.ContactoFragment.OnListFragmentInteractionListener;
import com.tracker.tracker.Modelos.Contacto;
import com.tracker.tracker.R;

import java.util.List;

/**
 *
 */
public class MyContactoRecyclerViewAdapter extends RecyclerView.Adapter<MyContactoRecyclerViewAdapter.ViewHolder> {

    private final List<Contacto> contactos;
    private final OnListFragmentInteractionListener listener;

    /**
     *
     */
    public MyContactoRecyclerViewAdapter(List<Contacto> items, OnListFragmentInteractionListener listener) {
        this.contactos = items;
        this.listener = listener;
    }

    /**
     *
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contacto , parent, false);
        return new ViewHolder(view);
    }

    /**
     *
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.item = contactos.get(position);
        holder.item.setPosition(position);
        holder.txtNombre.setText(
                contactos.get(position).getNombre());
        holder.txtTelf.setText(contactos.get(position).getTelf());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onListFragmentInteraction(holder.item);
                }
            }
        });
    }

    /**
     *
     */
    @Override
    public int getItemCount() {
        return contactos.size();
    }

    /**
     *
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        public final View view;
        public TextView txtNombre;
        private TextView txtTelf;
        public Contacto item;

        private ViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
            txtNombre = view.findViewById(R.id.item_number);
            txtTelf = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return item.toString();
        }
    }
}
