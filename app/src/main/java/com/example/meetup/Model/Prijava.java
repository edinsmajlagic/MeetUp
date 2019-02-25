package com.example.meetup.Model;

public class Prijava {
    public String PrijavaID;
    public String KategorijaID;
    public Object Ocjena;


    public Prijava(String prijavaID, String kategorijaID, Object ocjena) {
        PrijavaID = prijavaID;
        KategorijaID = kategorijaID;
        if (ocjena == null)
            Ocjena = 0;
        else
            Ocjena = ocjena;
    }
}
