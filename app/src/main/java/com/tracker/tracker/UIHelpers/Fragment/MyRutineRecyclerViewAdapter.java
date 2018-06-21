package com.tracker.tracker.UIHelpers.Fragment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tracker.tracker.Modelos.Contacto;
import com.tracker.tracker.Modelos.Frecuente;
import com.tracker.tracker.Modelos.Rutina;
import com.tracker.tracker.R;
import com.tracker.tracker.UIHelpers.Fragment.RutineFragment.OnListFragmentInteractionListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Rutina} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRutineRecyclerViewAdapter extends RecyclerView.Adapter<MyRutineRecyclerViewAdapter.ViewHolder> {

    private final List<Rutina> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyRutineRecyclerViewAdapter(List<Rutina> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_rutina, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNombreRutina.setText(mValues.get(position).getrNombre());
        holder.mDireccionRutina.setText(mValues.get(position).getrDestino().getNombre());
        holder.mSeresQueridosRutina.setText(String.valueOf(mValues.get(position).getrSeresQueridosName()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNombreRutina;
        public final TextView mDireccionRutina;
        public final TextView mSeresQueridosRutina;
        public Rutina mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNombreRutina = (TextView) view.findViewById(R.id.nombreRutina);
            mDireccionRutina = (TextView) view.findViewById(R.id.direccionRutina);
            mSeresQueridosRutina = (TextView) view.findViewById(R.id.seresQueridosRutina);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDireccionRutina.getText() + "'";
        }
    }
}
