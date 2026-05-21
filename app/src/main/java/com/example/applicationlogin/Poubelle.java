package com.example.applicationlogin;

public class Poubelle {
    private int id;
    private String adresse;
    private String dateVisu;
    private String ville;

    public Poubelle(int id, String adresse, String ville) {
        this.id = id;
        this.adresse = adresse;
        this.ville = ville;
    }

    public void setDateVisu(String dateVisu){
        this.dateVisu = dateVisu;
    }

    public int getId() { return id; }
    public String getAdresse() { return adresse; }
    public String getVille() { return ville; }
    public String getDateVisu(){ return dateVisu; }
}