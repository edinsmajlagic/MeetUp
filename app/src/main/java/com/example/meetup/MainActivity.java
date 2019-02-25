package com.example.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.meetup.Helper.MyUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public TextView TV_NemaNeta;
    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent i = new Intent(getApplicationContext(), Main2Activity.class);
            startActivity(i);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TV_NemaNeta=findViewById(R.id.TV_NemaNeta);
        if(MyUtils.pristupInternetu(this)) {
            Intent view = new Intent(this, LoginActivity.class);
            startActivity(view);
        }
        else
        {
            Intent view = new Intent(this, NoInternetActivity.class);
            startActivity(view);
            this.finish();
                
        }
    }


}
