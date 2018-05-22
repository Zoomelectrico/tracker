package com.tracker.tracker.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.tracker.tracker.Modelos.Contacto;
import com.tracker.tracker.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ContactoFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int columns = 1;
    private OnListFragmentInteractionListener listener;
    private List<Contacto> l = new ArrayList<>();

    public ContactoFragment() { }

    @SuppressWarnings("unused")
    public static ContactoFragment newInstance(int columnCount) {
        ContactoFragment fragment = new ContactoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            columns = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_contacto_list, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference contactosRef = db.collection("users/" + user.getUid() + "/contactos");
        contactosRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Contacto c = new Contacto(document.getString("nombre"), document.getString("telf"));
                        l.add(c);
                    }
                    if (view instanceof RecyclerView) {
                        Context context = view.getContext();
                        RecyclerView recyclerView = (RecyclerView) view;
                        if (columns <= 1) {
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        } else {
                            recyclerView.setLayoutManager(new GridLayoutManager(context, columns));
                        }
                        MyContactoRecyclerViewAdapter adapter = new MyContactoRecyclerViewAdapter(l, listener);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        return view;
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Contacto item);
    }
}
