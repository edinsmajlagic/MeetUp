package com.example.meetup;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.meetup.Fragmenti.DatePickerDialogFragment;
import com.example.meetup.Helper.MyUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public EditText ET_Email;
    public String Email;
    public EditText ET_Username;
    public String Username;
    public RadioGroup RG_Spol;
    public RadioButton RB_Spol;
    public String Spol;
    public Timestamp DOB;
    private ImageView IV_Back;
    public EditText ET_DOB;
    public String DOBString;
    public ImageView IV_DOB;
    public Button B_SacuvajPromjene;
    public FirebaseAuth mAuth;
    public FirebaseUser mUser;
    public FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ET_Email = findViewById(R.id.ET_Email);
        ET_Username = findViewById(R.id.ET_Username);
        ET_DOB = findViewById(R.id.ET_DOB);
        RG_Spol = findViewById(R.id.RG_Spol);

        IV_DOB = findViewById(R.id.IV_DOB);
        B_SacuvajPromjene = findViewById(R.id.B_SacuvajPromjene);
        IV_Back = findViewById(R.id.IV_Back);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        IV_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyUtils.pristupInternetu(getApplicationContext()))
                    Settings.this.finish();
                else
                    Toast.makeText(Settings.this, "Došlo je do greške. Problem sa konekcijom", Toast.LENGTH_SHORT).show();

            }
        });
        mFirestore.collection("User").document(mUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {

                    Email = mUser.getEmail();
                    Username = task.getResult().get("Username").toString();
                    DOB = task.getResult().getTimestamp("DOB");


                    final Date date = new Date(DOB.toDate().getTime());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    String Datum = sdf.format(date).toString();


                    Spol = task.getResult().get("Spol").toString();
                    if (Spol.compareTo("Musko") == 0)
                        RG_Spol.check(R.id.RB_Musko);
                    else
                        RG_Spol.check(R.id.RB_Zensko);


                    ET_Email.setText(Email);
                    ET_Username.setText(Username);
                    ET_DOB.setText(Datum);


                }

            }
        });
        B_SacuvajPromjene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyUtils.pristupInternetu(getApplicationContext())) {
                    snimiUserInfo();
                }
                else
                    Toast.makeText(Settings.this, "Izmjena nije uspjela. Problem sa konekcijom", Toast.LENGTH_SHORT).show();
            }
        });

        IV_DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment dialogFragment = new DatePickerDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    private void snimiUserInfo() {

        ET_Username = findViewById(R.id.ET_Username);
        RG_Spol = findViewById(R.id.RG_Spol);

        RB_Spol = findViewById(RG_Spol.getCheckedRadioButtonId());

        Username = ET_Username.getText().toString();
        Spol = RB_Spol.getText().toString();


        Map<String, Object> map = new HashMap<String, Object>();

        map.put("Username", Username);
        map.put("Spol", Spol);
        map.put("DOB", DOB);

        mFirestore.collection("User").document(mUser.getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Settings.this, "Profil uspješno izmjenjen", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);


        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date = calendar.getTime();
        DOBString = sdf.format(date);

        Timestamp timestamp = new Timestamp(calendar.getTime());
        DOB = timestamp;
        ET_DOB.setText(DOBString);
    }
}
