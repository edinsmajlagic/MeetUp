package com.example.meetup.Fragmenti;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetup.Adapteri.RCVDogadjaji;
import com.example.meetup.Helper.MyUtils;
import com.example.meetup.Main2Activity;
import com.example.meetup.Model.Dogadjaj;
import com.example.meetup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class DogadjajiFragment extends Fragment {
    public TextView TV_NemaDogadjaja;
    public RecyclerView recyclerView;
    public FloatingActionButton back;


    public RCVDogadjaji rcvDogadjaji;
    public List<Dogadjaj> dogadjaji;
    public FirebaseFirestore db;
    public List<String> IDDocuments = new ArrayList<>();

    public DogadjajiFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dogadjaji, container, false);
        TV_NemaDogadjaja = view.findViewById(R.id.TV_NemaDogadjaja);
        Main2Activity.IV_BurgerMenu.setVisibility(View.GONE);



        back = view.findViewById(R.id.FAB_Back);
        recyclerView = view.findViewById(R.id.RV_Dogadjaji);
        db = FirebaseFirestore.getInstance();
        String KategorijaID = "";
        Bundle bundle = getArguments();
        if (bundle != null) {
            KategorijaID = getArguments().getString("kategorijaID");
        }
        dogadjaji = new ArrayList<>();
        rcvDogadjaji = new RCVDogadjaji(dogadjaji, getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(rcvDogadjaji);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUtils.pristupInternetu(getActivity()))
                    getActivity().finish();
                else
                    Toast.makeText(getActivity(), "Došlo je do greške. Problem sa konekcijom.", Toast.LENGTH_SHORT).show();
            }
        });

        if (KategorijaID.equals("")) {
            db.collection("Kategorija").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            IDDocuments.add(document.getId());
                        }
                        for (final String id : IDDocuments) {

                            db.collection("Kategorija")
                                    .document(id).collection("Dogadjaji")
                                    .whereGreaterThan("VrijemeDogadjaja", Timestamp.now())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                            if (task2.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task2.getResult()) {

                                                    Dogadjaj d = new Dogadjaj(document.getId(), document.getData().get("Tema").toString(),
                                                            document.getData().get("Predavac").toString(),
                                                            document.getData().get("Slika").toString(),
                                                            (GeoPoint) document.getData().get("Lokacija"),
                                                            document.getData().get("LokacijaNaziv").toString(),
                                                            document.getTimestamp("VrijemeDogadjaja"),
                                                            document.getData().get("KategorijaTitle").toString(),
                                                            id,
                                                            document.getData().get("BrMijesta").toString(),
                                                            document.getData().get("Opis").toString());
                                                    dogadjaji.add(d);
                                                    if (dogadjaji.size() == 0)
                                                        TV_NemaDogadjaja.setVisibility(View.VISIBLE);
                                                }


                                                rcvDogadjaji.notifyDataSetChanged();
                                            } else {
                                                Toast.makeText(getActivity(), "Oops, nešto je pošlo po zlu. Pokušajte ponovo.", Toast.LENGTH_SHORT).show();

                                                Log.w("tag", "Error getting documents.", task2.getException());
                                            }
                                        }
                                    });
                        }

                    }
                }
            });
        } else {

            final String finalKategorijaID = KategorijaID;
            db.collection("Kategorija").document(KategorijaID)
                    .collection("Dogadjaji")
                    .whereGreaterThan("VrijemeDogadjaja", Timestamp.now())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                            if (task2.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task2.getResult()) {

                                    Dogadjaj d = new Dogadjaj(document.getId(), document.getData().get("Tema").toString(),
                                            document.getData().get("Predavac").toString(),
                                            document.getData().get("Slika").toString(),
                                            (GeoPoint) document.getData().get("Lokacija"),
                                            document.getData().get("LokacijaNaziv").toString(),
                                            document.getTimestamp("VrijemeDogadjaja"),
                                            document.getData().get("KategorijaTitle").toString(),
                                            finalKategorijaID,
                                            document.getData().get("BrMijesta").toString(),
                                            document.getData().get("Opis").toString());
                                    dogadjaji.add(d);

                                }
                                if (dogadjaji.size() == 0)
                                    TV_NemaDogadjaja.setVisibility(View.VISIBLE);
                                rcvDogadjaji.notifyDataSetChanged();

                            } else {
                                Toast.makeText(getActivity(), "Oops, nešto je pošlo po zlu. Pokušajte ponovo.", Toast.LENGTH_SHORT).show();
                                Log.w("tag", "Error getting documents.", task2.getException());
                            }
                        }
                    });
        }


        return view;
    }

}
