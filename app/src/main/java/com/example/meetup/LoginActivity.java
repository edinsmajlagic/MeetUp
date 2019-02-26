package com.example.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.meetup.Fragmenti.LoginFragment;
import com.example.meetup.Helper.MyUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if(MyUtils.pristupInternetu(this)) {
                Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(i);
                this.finish();
            }
            else
            {
                Intent i = new Intent(getApplicationContext(), NoInternetActivity.class);
                startActivity(i);
                this.finish();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(MyUtils.pristupInternetu(this))
        MyUtils.zamjeniFragment(this, R.id.MjestoZaFragment, new LoginFragment());
        else{
            Intent i = new Intent(getApplicationContext(), NoInternetActivity.class);
            startActivity(i);
            this.finish();
        }


    }



}
