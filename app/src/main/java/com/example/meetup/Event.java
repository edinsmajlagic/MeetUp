package com.example.meetup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.meetup.Helper.MyUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class Event extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    public ImageView IV_PredavacSlika;
    public TextView TV_PredavacIme;
    public TextView TV_NazivTema;
    public TextView TV_KategorijaTitle;
    public TextView TV_Lokacija;
    public TextView TV_Opis;
    private RatingBar ratingBar;
    public TextView TV_Datum;
    public TextView TV_BrMijesta;
    private FloatingActionButton prijava;
    private FloatingActionButton back;
    private Boolean Zavrsilo;
    private MapView mapView;
    private GoogleMap map;
    private double lon;
    private double lat;
    protected LocationManager locationManager;
    private FloatingActionButton Ukloniprijava;
    private String LokTitle;
    private int MY_PERMISSIONS = 20;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onStart() {
        super.onStart();

        prijava.setVisibility(View.GONE);


    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        IV_PredavacSlika = findViewById(R.id.IV_PredavacSlika);
        TV_PredavacIme = findViewById(R.id.TV_Predavac);
        TV_NazivTema = findViewById(R.id.TV_NazivTema);
        TV_Opis = findViewById(R.id.TV_Opis);
        TV_KategorijaTitle = findViewById(R.id.TV_KategorijaTitle);
        TV_Lokacija = findViewById(R.id.TV_Lokacija);
        TV_Datum = findViewById(R.id.TV_Datum);
        prijava = findViewById(R.id.FAB_Prijava);
        Ukloniprijava = findViewById(R.id.FAB_UkloniPrijavu);
        TV_BrMijesta = findViewById(R.id.TV_BrojPrijavljeni);
        mAuth = FirebaseAuth.getInstance();
        ratingBar = findViewById(R.id.RB_zvijezdice);
        ratingBar.setClickable(false);
        mUser = mAuth.getCurrentUser();
        mapView = findViewById(R.id.mapView);
        back = findViewById(R.id.FAB_Back);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mFirestore = FirebaseFirestore.getInstance();

        final String DogadjajID = getIntent().getStringExtra("DogadjajID");
        String KategorijaTitle = getIntent().getStringExtra("KategorijaTitle");
        Zavrsilo = getIntent().getBooleanExtra("Zavrsilo", false);
        lat = getIntent().getDoubleExtra("LokacijaLatitude", 0);
        lon = getIntent().getDoubleExtra("LokacijaLongitude", 0);
        String LokacijaNaziv = getIntent().getStringExtra("LokacijaNaziv");
        LokTitle = LokacijaNaziv;
        String Predavac = getIntent().getStringExtra("Predavac");
        ratingBar.setNumStars(5);
        String Slika = getIntent().getStringExtra("Slika");
        String Tema = getIntent().getStringExtra("Tema");
        String VrijemeDogadjaja = getIntent().getStringExtra("VrijemeDogadjaja");
        String Opis = getIntent().getStringExtra("Opis");
        final int BrMijesta = getIntent().getIntExtra("BrMijesta", 0);
        final String KategorijaID = getIntent().getStringExtra("KategorijaID");
        if (DogadjajID != null) {
            Glide.with(getApplicationContext())
                    .load(Slika)
                    .into(IV_PredavacSlika);
            TV_PredavacIme.setText(Predavac);
            TV_NazivTema.setText(Tema);
            TV_KategorijaTitle.setText(KategorijaTitle);
            TV_Lokacija.setText(LokacijaNaziv);
            TV_Datum.setText(VrijemeDogadjaja);
            TV_BrMijesta.setText("Prijave: 0/" + BrMijesta);
            TV_Opis.setText(Opis);
        }



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUtils.pristupInternetu(getApplicationContext()))
                    Event.this.finish();
                else
                    Toast.makeText(Event.this, "Došlo je do greške. Problem sa konekcijom", Toast.LENGTH_SHORT).show();

            }
        });

        mFirestore.collection("Kategorija").document(KategorijaID).collection("Dogadjaji").document(DogadjajID).collection("Ocjene").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    float ocjena = 0;
                    for (DocumentSnapshot d : task.getResult()) {
                        ocjena += Float.valueOf(d.get("Ocjena").toString());
                    }
                    ratingBar.setRating(ocjena / task.getResult().size());
                    if (ocjena > 0)
                        ratingBar.setVisibility(View.VISIBLE);

                }
            }
        });
        mFirestore.collection("User").document(mUser.getUid()).collection("Prijave").document(DogadjajID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Ukloniprijava.setVisibility(View.VISIBLE);
                        if (Zavrsilo) {
                            Ukloniprijava.setVisibility(View.GONE);
                            prijava.setVisibility(View.GONE);
                        }
                    } else {
                        mFirestore.collection("Kategorija").document(KategorijaID).collection("Dogadjaji").document(DogadjajID).collection("Prijave").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    int a = task.getResult().getDocuments().size();
                                    TV_BrMijesta.setText("Prijave: " + a + " /" + BrMijesta);
                                    if (a >= BrMijesta) {
                                        prijava.setVisibility(View.GONE);
                                    } else {
                                        prijava.setVisibility(View.VISIBLE);

                                    }
                                    if (Zavrsilo) {
                                        Ukloniprijava.setVisibility(View.GONE);
                                        prijava.setVisibility(View.GONE);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
        mFirestore.collection("Kategorija").document(KategorijaID).collection("Dogadjaji").document(DogadjajID).collection("Prijave").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                int a = queryDocumentSnapshots.getDocuments().size();
                TV_BrMijesta.setText("Prijave: " + a + " /" + BrMijesta);
                if (a >= BrMijesta) {
                    prijava.setVisibility(View.GONE);
                } else {
                    prijava.setVisibility(View.VISIBLE);

                }
                if (Zavrsilo) {
                    Ukloniprijava.setVisibility(View.GONE);
                    prijava.setVisibility(View.GONE);
                }
            }
        });
        prijava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Map<String, Object> m = new HashMap<>();
                m.put("VrijemePrijave", Timestamp.now());
                m.put("KategorijaID", KategorijaID);
                mFirestore.collection("Kategorija").document(KategorijaID).collection("Dogadjaji").document(DogadjajID).collection("Prijave").document(mUser.getUid()).set(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            mFirestore.collection("User").document(mUser.getUid()).collection("Prijave").document(DogadjajID).set(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @SuppressLint("RestrictedApi")
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Event.this, "Uspješno ste se prijavili na događaj.", Toast.LENGTH_SHORT).show();
                                        Ukloniprijava.setVisibility(View.VISIBLE);
                                        prijava.setVisibility(View.GONE);
                                    } else {
                                        mFirestore.collection("Kategorija").document(KategorijaID).collection("Dogadjaji").document(DogadjajID).collection("Prijave").document(mUser.getUid()).delete();

                                    }
                                    if (Zavrsilo) {

                                        Ukloniprijava.setVisibility(View.GONE);
                                        prijava.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        Ukloniprijava.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {

                mFirestore.collection("Kategorija").document(KategorijaID).collection("Dogadjaji").document(DogadjajID).collection("Prijave").document(mUser.getUid()).delete();
                mFirestore.collection("User").document(mUser.getUid()).collection("Prijave").document(DogadjajID).delete();
                Ukloniprijava.setVisibility(View.GONE);
                prijava.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Event.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS);
        }
        map.setMyLocationEnabled(true);
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(LokTitle));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 20.f);
        map.animateCamera(cameraUpdate);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 16.f));
    }
}
