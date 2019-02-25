package com.example.meetup.Adapteri;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.meetup.Fragmenti.DogadjajiFragment;
import com.example.meetup.Helper.MyUtils;
import com.example.meetup.Model.Kategorija;
import com.example.meetup.R;
import com.example.meetup.Settings;

import java.util.List;

class KategorijaView extends RecyclerView.ViewHolder  {
    private View mView;
    public ImageView slika;
    public TextView TV_Title;
    public RelativeLayout parentLayout;

    public KategorijaView(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        slika = mView.findViewById(R.id.IV_KategorijaImage);
        TV_Title = mView.findViewById(R.id.TV_KategorijaTitle);
        parentLayout = itemView.findViewById(R.id.PL_KategorijaItem);
    }



}

public class RCVKategorija extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Kategorija> data;
    private Activity ctx;
    private String id;

    public RCVKategorija(List<Kategorija> kategorije, Activity _ctx) {
        data=kategorije;
        ctx= _ctx;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(viewGroup.getContext())
                                 .inflate(R.layout.kategorija_item,viewGroup,false);




        final KategorijaView kategorijaView=new KategorijaView(view);


        return kategorijaView;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder,final  int i) {

         KategorijaView kategorijaView=(KategorijaView) viewHolder;

        Glide.with(ctx)
                .load(data.get(i).Slika)
                .into(kategorijaView.slika);

        String KategorijaTitle=data.get(i).Title;
        kategorijaView.TV_Title.setText(KategorijaTitle);

            kategorijaView.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!MyUtils.pristupInternetu(ctx))
                        Toast.makeText(ctx, "Došlo je do greške. Problem sa konekcijom.", Toast.LENGTH_SHORT).show();
                    else {
                        Bundle bundle = new Bundle();
                        bundle.putString("kategorijaID", data.get(i).KategorijaID);
                        bundle.putString("kategorijaTitle", data.get(i).Title);
                        DogadjajiFragment dogadjajiFragment = new DogadjajiFragment();
                        dogadjajiFragment.setArguments(bundle);

                        MyUtils.zamjeniFragment(ctx, R.id.FL_MijestoZaFragmente, dogadjajiFragment);
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
