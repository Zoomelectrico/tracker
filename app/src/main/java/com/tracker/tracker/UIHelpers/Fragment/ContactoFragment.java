package com.tracker.tracker.UIHelpers.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tracker.tracker.Modelos.Contacto;
import com.tracker.tracker.Modelos.Usuario;
import com.tracker.tracker.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ContactoFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int columns = 1;
    private OnListFragmentInteractionListener listener;
    private List<Contacto> contactos = new ArrayList<>();

    /**
     *
     */
    public ContactoFragment() { }

    /**
     *
     */
    @SuppressWarnings("unused")
    public static ContactoFragment newInstance(int columnCount) {
        ContactoFragment fragment = new ContactoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            columns = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    /**
     *
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacto_list, container, false);
        Bundle bundle = getActivity().getIntent().getExtras();
        if(bundle != null) {
            Usuario usuario = (Usuario) bundle.getParcelable("user");
            contactos = usuario.getContactos();
        }
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (columns <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, columns));
            }
            MyContactoRecyclerViewAdapter adapter = new MyContactoRecyclerViewAdapter(contactos, listener);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    /**
     *
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            listener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    /**
     *
     */
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     *
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Contacto item);
    }
}
