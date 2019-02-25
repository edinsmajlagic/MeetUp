package com.example.meetup.Fragmenti;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meetup.Adapteri.RCVKategorija;
import com.example.meetup.Model.Kategorija;
import com.example.meetup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {


    public HomeFragment() {
    }

    public RecyclerView recyclerView;
    public RCVKategorija rcvKategorija;
    public List<Kategorija> kategorije;
    public FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.RV_Kategorije);
        db = FirebaseFirestore.getInstance();

        kategorije = new ArrayList<>();
        rcvKategorija = new RCVKategorija(kategorije, getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(rcvKategorija);

        db.collection("Kategorija")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Kategorija k = new Kategorija(document.getId(), document.getData().get("Title").toString(), document.getData().get("Slika").toString());
                                kategorije.add(k);

                            }
                            rcvKategorija.notifyDataSetChanged();
                        } else {
                            Log.w("tag", "Error getting documents.", task.getException());
                        }
                    }
                });

        return view;
    }


}
