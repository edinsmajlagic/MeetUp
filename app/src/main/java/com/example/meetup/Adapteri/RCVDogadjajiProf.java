package com.example.meetup.Adapteri;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.meetup.Event;
import com.example.meetup.Helper.MyUtils;
import com.example.meetup.Model.Dogadjaj;
import com.example.meetup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class DogadjajViewProf extends RecyclerView.ViewHolder{

    private View mView;
    public ImageView IV_PredavacSlika;
    public TextView TV_PredavacIme;
    public TextView TV_NazivTema;
    public TextView TV_KategorijaTitle;
    public TextView TV_Lokacija;
    public TextView TV_Datum;
    public RelativeLayout parentLayout;
    public RatingBar ratingBar;


    public DogadjajViewProf(@NonNull View itemView) {
        super(itemView);
        mView=itemView;
        IV_PredavacSlika=mView.findViewById(R.id.IV_PredavacSlika);
        TV_PredavacIme=mView.findViewById(R.id.TV_Predavac);
        TV_NazivTema=mView.findViewById(R.id.TV_NazivTema);
        TV_KategorijaTitle=mView.findViewById(R.id.TV_KategorijaTitle);
        TV_Lokacija=mView.findViewById(R.id.TV_Lokacija);
        TV_Datum=mView.findViewById(R.id.TV_Datum);
        parentLayout=mView.findViewById(R.id.PL_DogadjajItem);
        ratingBar = mView.findViewById(R.id.RB_zvijezdice);

    }
}


public class RCVDogadjajiProf extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Dogadjaj> data;
    private Activity ctx;

    public RCVDogadjajiProf(List<Dogadjaj> data, Activity ctx) {
        this.data = data;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext())
                                 .inflate(R.layout.one_event_profile,viewGroup,false);
        return new DogadjajViewProf(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {

        final DogadjajViewProf dogadjajView=(DogadjajViewProf) viewHolder;
        Glide.with(ctx)
             .load(data.get(i).Slika)
             .into(dogadjajView.IV_PredavacSlika);
        Boolean zavrsilo=false;
        String Predavac=data.get(i).Predavac.toString();
        String Tema=data.get(i).Tema.toString();
        String Lokacija=data.get(i).LokacijaNaziv.toString();
        String KategorijaTitle=data.get(i).KategorijaTitle.toString();

        Timestamp datumdata=data.get(i).VrijemeDogadjaja;
        if (datumdata.compareTo(Timestamp.now())== -1) {
            dogadjajView.ratingBar.setVisibility(View.VISIBLE);
            zavrsilo=true;
        }
        else
            dogadjajView.ratingBar.setVisibility(View.GONE);

        final Date date=new Date(datumdata.toDate().getTime());
        Time time=new Time(datumdata.toDate().getTime());
        SimpleDateFormat sdf=new SimpleDateFormat("dd.MM.yyyy");

        String Datum=sdf.format(date).toString()+" "+time.toString().substring(0,5);

        dogadjajView.TV_PredavacIme.setText(Predavac);
        dogadjajView.TV_NazivTema.setText(Tema);
        dogadjajView.TV_KategorijaTitle.setText(KategorijaTitle);
        dogadjajView.TV_Lokacija.setText(Lokacija);
        dogadjajView.TV_Datum.setText(Datum);
        dogadjajView.ratingBar.setNumStars(5);
        dogadjajView.ratingBar.setStepSize(0.5f);


        dogadjajView.ratingBar.setRating(Float.valueOf(data.get(i).Ocjena.toString()));

        if (dogadjajView.ratingBar.getRating() > 0)
            dogadjajView.ratingBar.setIsIndicator(true);
        else
            dogadjajView.ratingBar.setIsIndicator(false);
        dogadjajView.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    if (!MyUtils.pristupInternetu(ctx))
                        Toast.makeText(ctx, "Došlo je do greške. Problem sa konekcijom.", Toast.LENGTH_SHORT).show();
                    else {
                        final Map<String, Object> a = new HashMap<>();
                        a.put("Ocjena", rating);
                        FirebaseFirestore.getInstance().collection("Kategorija").document(data.get(i).KategorijaID).collection("Dogadjaji").document(data.get(i).DogadjajID)
                                .collection("Ocjene").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(a).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Prijave").document(data.get(i).DogadjajID).update(a).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                                    dogadjajView.ratingBar.setIsIndicator(true);
                                }
                            }
                        });
                    }
                }
            }
        });

        final Boolean finalZavrsilo = zavrsilo;
        dogadjajView.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MyUtils.pristupInternetu(ctx))
                    Toast.makeText(ctx, "Došlo je do greške. Problem sa konekcijom.", Toast.LENGTH_SHORT).show();
                else {
                    Dogadjaj d = data.get(i);
                    Timestamp datumdata = d.VrijemeDogadjaja;
                    final Date date = new Date(datumdata.toDate().getTime());
                    Time time = new Time(datumdata.toDate().getTime());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    GeoPoint g = d.Lokacija;
                    String Datum = sdf.format(date).toString() + " " + time.toString().substring(0, 5);
                    Intent i = new Intent(ctx, Event.class);
                    i.putExtra("DogadjajID", d.DogadjajID);
                    i.putExtra("KategorijaTitle", d.KategorijaTitle);
                    i.putExtra("LokacijaLatitude", d.Lokacija.getLatitude());
                    i.putExtra("LokacijaLongitude", d.Lokacija.getLongitude());
                    i.putExtra("LokacijaNaziv", d.LokacijaNaziv);
                    i.putExtra("Predavac", d.Predavac);
                    i.putExtra("Slika", d.Slika);
                    i.putExtra("Tema", d.Tema);
                    i.putExtra("VrijemeDogadjaja", Datum);
                    i.putExtra("KategorijaID", d.KategorijaID);
                    i.putExtra("Zavrsilo", finalZavrsilo);
                    i.putExtra("BrMijesta", d.BrMijesta);
                    i.putExtra("Opis", d.Opis);
                    ctx.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
