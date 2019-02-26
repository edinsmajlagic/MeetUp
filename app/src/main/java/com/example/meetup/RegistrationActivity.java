package com.example.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.meetup.Helper.MyUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private EditText ET_Email;
    private EditText ET_Password;
    private EditText ET_PasswordConfirm;
    private Button B_Register;
    private Button B_HaveAcc;
    private String email;
    private String password;
    private String passwordConfirm;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        MyUtils.HideBar(this);
        ET_Email = findViewById(R.id.ET_Email);
        ET_Password = findViewById(R.id.ET_Password);
        ET_PasswordConfirm = findViewById(R.id.ET_PasswordConfirm);
        B_Register = findViewById(R.id.B_Register);
        B_HaveAcc = findViewById(R.id.B_HaveAcc);
        progressBar = findViewById(R.id.PB_Regist);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        B_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = ET_Email.getText().toString();
                password = ET_Password.getText().toString();
                passwordConfirm = ET_PasswordConfirm.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                B_Register.setVisibility(View.GONE);
                B_HaveAcc.setVisibility(View.GONE);

                if (email.isEmpty()) {
                    ET_Email.setError("Email je obavezno polje");
                    ET_Email.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    B_Register.setVisibility(View.VISIBLE);
                    B_HaveAcc.setVisibility(View.VISIBLE);
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    ET_Email.setError("Email nije validan");
                    ET_Email.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    B_Register.setVisibility(View.VISIBLE);
                    B_HaveAcc.setVisibility(View.VISIBLE);
                    return;
                }
                if (password.isEmpty()) {
                    ET_Password.setError("Lozinka je obavezno polje");
                    ET_Password.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    B_Register.setVisibility(View.VISIBLE);
                    B_HaveAcc.setVisibility(View.VISIBLE);
                    return;
                }
                if (password.length() < 6) {
                    ET_Password.setError("Lozinka je prekratka");
                    ET_Password.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    B_Register.setVisibility(View.VISIBLE);
                    B_HaveAcc.setVisibility(View.VISIBLE);
                    return;
                }
                if (passwordConfirm.isEmpty()) {
                    ET_PasswordConfirm.setError("Morate potvrditi lozinku");
                    ET_PasswordConfirm.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    B_Register.setVisibility(View.VISIBLE);
                    B_HaveAcc.setVisibility(View.VISIBLE);
                    return;
                }
                if (!passwordConfirm.equals(password)) {
                    ET_PasswordConfirm.setError("Lozinke se moraju podudarati");
                    ET_PasswordConfirm.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    B_Register.setVisibility(View.VISIBLE);
                    B_HaveAcc.setVisibility(View.VISIBLE);
                    return;
                }
                if(MyUtils.pristupInternetu(getApplicationContext())) {
                    RegisterUser(email, password);
                }
                else
                {
                    B_Register.setVisibility(View.VISIBLE);
                    B_HaveAcc.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegistrationActivity.this, "Problem sa konekcijom. Pokušajte ponovo.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        B_HaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
                if(MyUtils.pristupInternetu(getApplicationContext())) {
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else
                {

                    Toast.makeText(RegistrationActivity.this, "Problem sa konekcijom. Pokušajte ponovo.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        MyUtils.HideBar(this);

    }
    private void RegisterUser(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(RegistrationActivity.this, "Uspješno ste se registrovali", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(RegistrationActivity.this, ProfileActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
                    u.sendEmailVerification().addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {

                            }
                            Log.e("verificaiton", "onComplete: ");
                        }
                    });


                    //Kreiranje Usera
                    Map<String, Object> map = new HashMap<>();
                    map.put("Slika", "https://firebasestorage.googleapis.com/v0/b/meetup-646e0.appspot.com/o/User%2Fcliente.png?alt=media&token=03a385d5-f3a4-4740-8593-844f5b22a737");
                    int indexEmail = u.getEmail().indexOf("@");
                    map.put("Username", u.getEmail().toString().substring(0, indexEmail));
                    map.put("Spol", "");
                    map.put("DOB", new Timestamp(2000, 00));
                    db.collection("User").document(u.getUid())
                            .set(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("", "Korisnik uspjesno kreiran");
                                }
                            })
                    ;


                    startActivity(i);
                } else {
                    B_Register.setVisibility(View.VISIBLE);
                    B_HaveAcc.setVisibility(View.VISIBLE);


                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(RegistrationActivity.this, "Registracija nije uspjela", Toast.LENGTH_SHORT).show();
                        ET_Email.setError("Korisnik vec postoji");
                        ET_Email.requestFocus();
                    } else{
                        if(MyUtils.pristupInternetu(getApplicationContext())) {
                            Toast.makeText(RegistrationActivity.this, "Registracija nije uspjela. Problem sa konekcijom.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(RegistrationActivity.this, "Registracija nije uspjela, pokusajte ponovo", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

    }
}