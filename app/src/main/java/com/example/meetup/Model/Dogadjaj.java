package com.example.meetup.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Dogadjaj {
    public String DogadjajID;
    public String KategorijaID;
    public String Tema;
    public String KategorijaTitle;
    public String Predavac;
    public String Slika;
    public GeoPoint Lokacija;
    public String Longitude;
    public String Latitude;
    public String LokacijaNaziv;
    public Timestamp VrijemeDogadjaja;
    public int BrMijesta;
    public Object Ocjena;
    public String Opis;

    public Dogadjaj(String dogadjajID, String tema, String predavac, String slika, GeoPoint lokacija, String lokacijaNaziv, Timestamp vrijemeDogadjaja,String kategorijaTitle,String kategorijaID, String brM, String opis) {
        DogadjajID = dogadjajID;
        Tema = tema;
        Predavac = predavac;
        Slika = slika;
        Lokacija = lokacija;
        LokacijaNaziv = lokacijaNaziv;
        VrijemeDogadjaja = vrijemeDogadjaja;
        KategorijaTitle=kategorijaTitle;
        KategorijaID = kategorijaID;
        BrMijesta = Integer.parseInt(brM);
        Opis=opis;
    }
    public Dogadjaj(String dogadjajID, String tema, String predavac, String slika, GeoPoint lokacija, String lokacijaNaziv, Timestamp vrijemeDogadjaja,String kategorijaTitle,String kategorijaID, String brM,String opis,Object ocjena) {
        DogadjajID = dogadjajID;
        Tema = tema;
        Predavac = predavac;
        Slika = slika;
        Lokacija = lokacija;
        LokacijaNaziv = lokacijaNaziv;
        VrijemeDogadjaja = vrijemeDogadjaja;
        KategorijaTitle=kategorijaTitle;
        KategorijaID = kategorijaID;
        BrMijesta = Integer.parseInt(brM);
        Ocjena = ocjena;
        Opis=opis;

    }
}
