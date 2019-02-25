package com.example.meetup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.meetup.Fragmenti.DogadjajiFragment;
import com.example.meetup.Fragmenti.HomeFragment;
import com.example.meetup.Helper.MyUtils;
import com.google.firebase.auth.FirebaseAuth;

public class Main2Activity extends AppCompatActivity {

    private static final int MY_PERMISSIONS = 20;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = firebaseAuth.getInstance();
        if(MyUtils.pristupInternetu(this))
            MyUtils.zamjeniFragment(this, R.id.FL_MijestoZaFragmente, new HomeFragment());
        else
        {
            Intent i = new Intent(getApplicationContext(), NoInternetActivity.class);
            startActivity(i);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        NavigationView navigationView = findViewById(R.id.NV_Menu);
        final DrawerLayout drawerLayout = findViewById(R.id.DL_drawer);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Main2Activity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS);
        }
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        if(!MyUtils.pristupInternetu(getApplicationContext()))
                        {
                            Toast.makeText(Main2Activity.this, "Došlo je do greške. Problem sa konekcijom.", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                MyUtils.zamjeniFragment(Main2Activity.this, R.id.FL_MijestoZaFragmente, new HomeFragment());

                                break;
                            case R.id.nav_profil:
                                Intent i2 = new Intent(getApplicationContext(), ProfileActivity.class);
                                startActivity(i2);

                                break;
                            case R.id.nav_event:
                                MyUtils.zamjeniFragment(Main2Activity.this, R.id.FL_MijestoZaFragmente, new DogadjajiFragment());
                                break;
                            case R.id.nav_odjava:
                                Log.e("Position", "3");
                                firebaseAuth.signOut();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finish();
                                break;

                        }
                        drawerLayout.closeDrawers();

                        return true;
                    }
                });
    }
}
