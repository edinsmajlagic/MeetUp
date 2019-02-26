package com.example.meetup.Fragmenti;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetup.Helper.MyUtils;
import com.example.meetup.Main2Activity;
import com.example.meetup.R;
import com.example.meetup.RegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;


public class LoginFragment extends Fragment {
    private EditText ET_Email;
    private EditText ET_Password;
    private TextView TV_Register;
    private Button B_Login;
    private String email;
    private String password;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;


    public LoginFragment() {
    }


    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUtils.HideBar(this.getActivity());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ET_Email = view.findViewById(R.id.ET_Email);
        ET_Password = view.findViewById(R.id.ET_Password);
        TV_Register = view.findViewById(R.id.TV_RegistrujSe);
        B_Login = view.findViewById(R.id.B_Login);
        progressBar = view.findViewById(R.id.PB_Login);
        mAuth = FirebaseAuth.getInstance();

        B_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = ET_Email.getText().toString();
                password = ET_Password.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                B_Login.setVisibility(View.GONE);
                if (email.isEmpty()) {
                    ET_Email.setError("Niste unijeli email");
                    ET_Email.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    B_Login.setVisibility(View.VISIBLE);
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    ET_Email.setError("Email nije validan");
                    ET_Email.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    B_Login.setVisibility(View.VISIBLE);
                    return;
                }
                if (password.isEmpty()) {
                    ET_Password.setError("Nije unijeli lozinku");
                    ET_Password.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    B_Login.setVisibility(View.VISIBLE);
                    return;
                }
                if (MyUtils.pristupInternetu(getActivity()))
                    SignIn(email, password);
                else {
                    progressBar.setVisibility(View.GONE);
                    B_Login.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Prijava nije uspjela. Problem sa konekcijom.", Toast.LENGTH_SHORT).show();

                }
            }
        });

        TV_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
                if (MyUtils.pristupInternetu(getActivity())) {
                    Intent intent = new Intent(getActivity(), RegistrationActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(getActivity(), "Problem sa konekcijom, pokušajte ponovo.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void SignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Log.d("df", "signInWithEmail:success");
                            Intent i = new Intent(getActivity(), Main2Activity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } else {
                            B_Login.setVisibility(View.VISIBLE);

                            if (MyUtils.pristupInternetu(getActivity())) {
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(getActivity(), "Logiranje nije uspjelo. ", Toast.LENGTH_SHORT).show();
                                    ET_Email.setError("Pogresan email ili lozinka");
                                    ET_Email.requestFocus();
                                    ET_Password.requestFocus();
                                }
                                Log.w("df", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getActivity(), "Logiranje nije uspjelo.",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getActivity(), "Logiranje nije uspjelo. Problem sa konekcijom.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }
}
