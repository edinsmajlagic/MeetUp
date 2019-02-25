package com.example.meetup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.meetup.Adapteri.RCVDogadjajiProf;
import com.example.meetup.Helper.MyUtils;
import com.example.meetup.Model.Dogadjaj;
import com.example.meetup.Model.Prijava;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;
    private Uri urlSlika;
    private String urlSlike;
    public List<Prijava> UserPrijave = new ArrayList<>();
    public TextView TV_Username;
    public ProgressBar PB_UcitavanjeSlike;
    public CircleImageView IV_UserImage;
    public FirebaseAuth mAuth;
    public FirebaseUser mUser;
    public FirebaseFirestore mFirestore;
    public FloatingActionButton back;
    private ImageView settings;
    private StorageReference storageReference;
    public RecyclerView recyclerView;
    public RCVDogadjajiProf rcvDogadjaji;
    public List<Dogadjaj> dogadjaji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        settings = findViewById(R.id.IV_settings);
        IV_UserImage = findViewById(R.id.IV_UserImage);
        back = findViewById(R.id.FAB_Back);
        TV_Username=findViewById(R.id.TV_Username);
        PB_UcitavanjeSlike = findViewById(R.id.PB_UcitavanjeSlike);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        recyclerView = findViewById(R.id.RV_DogadjajiProfil);
        dogadjaji = new ArrayList<>();
        rcvDogadjaji = new RCVDogadjajiProf(dogadjaji, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(rcvDogadjaji);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Settings.class);
                startActivity(i);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyUtils.pristupInternetu(getApplicationContext()))
                    ProfileActivity.this.finish();
                else
                    Toast.makeText(ProfileActivity.this, "Došlo je do greške. Problem sa konekcijom", Toast.LENGTH_SHORT).show();
            }
        });

        mFirestore.collection("User").document(mUser.getUid()).collection("Prijave").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            public String id;

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Prijava p = new Prijava(document.getId(), document.get("KategorijaID").toString(), document.get("Ocjena"));
                        UserPrijave.add(p);
                    }
                    for (final Prijava item : UserPrijave) {
                        id = item.KategorijaID;
                        mFirestore.collection("Kategorija").document(item.KategorijaID).collection("Dogadjaji").document(item.PrijavaID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Dogadjaj d = new Dogadjaj(task.getResult().getId(), task.getResult().getData().get("Tema").toString(),
                                            task.getResult().getData().get("Predavac").toString(),
                                            task.getResult().getData().get("Slika").toString(),
                                            (GeoPoint) task.getResult().getData().get("Lokacija"),
                                            task.getResult().getData().get("LokacijaNaziv").toString(),
                                            task.getResult().getTimestamp("VrijemeDogadjaja"),
                                            task.getResult().getData().get("KategorijaTitle").toString(),
                                            id,
                                            task.getResult().getData().get("BrMijesta").toString(),
                                            task.getResult().getData().get("Opis").toString(),
                                            item.Ocjena
                                    );
                                    dogadjaji.add(d);
                                    rcvDogadjaji.notifyDataSetChanged();

                                }
                            }
                        });
                    }

                }
            }
        });


        mFirestore.collection("User").document(mUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {

                    urlSlike = task.getResult().get("Slika").toString();
                    TV_Username.setText(task.getResult().get("Username").toString());


                    Glide.with(getApplicationContext())
                            .load(task.getResult().get("Slika"))
                            .into(IV_UserImage);
                }

            }
        });


        IV_UserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prikaziDialogSlika();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            urlSlika = data.getData();
            String slikaIme = data.getDataString();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), urlSlika);
                IV_UserImage.setImageBitmap(bitmap);
                uploadImageToFirebase(slikaIme);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadImageToFirebase(String slikaIme) {
        storageReference = FirebaseStorage.getInstance().getReference("User/" + mUser.getUid());

        if (urlSlika != null) {
            PB_UcitavanjeSlike.setVisibility(View.VISIBLE);
            storageReference.putFile(urlSlika)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            PB_UcitavanjeSlike.setVisibility(View.GONE);
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    urlSlike = uri.toString();
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("Slika", urlSlike);
                                    mFirestore.collection("User").document(mUser.getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProfileActivity.this, "Slika uspjesno izmjenjena", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            PB_UcitavanjeSlike.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void prikaziDialogSlika() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Odaberi profilnu sliku"), CHOOSE_IMAGE);
    }


}
