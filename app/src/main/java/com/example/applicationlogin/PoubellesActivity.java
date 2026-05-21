package com.example.applicationlogin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PoubellesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PoubelleAdapter adapter;
    private List<Poubelle> toutesLesPoubelles;
    private List<Poubelle> poubelleList;
    private Button bChoixVille, bChoixDate, bChoixJour;
    private String villeSelectionnee = "Toutes";
    private String dateSelectionnee = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poubelles);

        bChoixVille = findViewById(R.id.btn_choixVille);
        bChoixDate = findViewById(R.id.btn_choixDate);
        bChoixJour = findViewById(R.id.btn_choixJour);
        recyclerView = findViewById(R.id.recyclerViewPoubelles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toutesLesPoubelles = new ArrayList<>();
        poubelleList = new ArrayList<>();
        adapter = new PoubelleAdapter(poubelleList);
        recyclerView.setAdapter(adapter);

        Calendar c = Calendar.getInstance();
        dateSelectionnee = String.format("%04d-%02d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
        bChoixJour.setText("Jour : " + dateSelectionnee);
        bChoixDate.setText("Mois : Aucun");

        bChoixVille.setOnClickListener(v -> afficherDialogVille());
        bChoixJour.setOnClickListener(v -> afficherDialogDate());
        bChoixDate.setOnClickListener(v -> afficherDialogMoisAnnee());

        GestionApi gapi = new GestionApi();
        gapi.chargerPoubelles(this, result -> traiterDonnees(result));
    }

    private void afficherDialogMoisAnnee() {
        Calendar cal = Calendar.getInstance();
        int anneeActuelle = cal.get(Calendar.YEAR);
        int moisActuel = cal.get(Calendar.MONTH);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setPadding(50, 50, 50, 50);

        final NumberPicker monthPicker = new NumberPicker(this);
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        String[] moisNoms = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        monthPicker.setDisplayedValues(moisNoms);
        monthPicker.setValue(moisActuel);

        final NumberPicker yearPicker = new NumberPicker(this);
        yearPicker.setMinValue(2020);
        yearPicker.setMaxValue(2030);
        yearPicker.setValue(anneeActuelle);

        layout.addView(monthPicker);
        layout.addView(yearPicker);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choisissez le mois et l'année");
        builder.setView(layout);

        builder.setPositiveButton("Valider", (dialog, which) -> {
            int moisChoisi = monthPicker.getValue() + 1;
            int anneeChoisie = yearPicker.getValue();

            dateSelectionnee = String.format("%04d-%02d", anneeChoisie, moisChoisi);
            String texteAffiche = moisNoms[monthPicker.getValue()] + " " + anneeChoisie;
            bChoixDate.setText("Mois : " + texteAffiche);
            bChoixJour.setText("Jour : --");

            appliquerFiltres();
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void traiterDonnees(JSONArray data) {
        try {
            toutesLesPoubelles.clear();
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = data.getJSONObject(i);
                int id = obj.getInt("idpoubelle");
                String adresse = obj.getString("adresse");
                String ville = obj.optString("ville", "Inconnue");

                toutesLesPoubelles.add(new Poubelle(id, adresse, ville));
            }
            appliquerFiltres();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur de lecture des données", Toast.LENGTH_SHORT).show();
        }
    }

    private void appliquerFiltres() {
        poubelleList.clear();
        for (Poubelle p : toutesLesPoubelles) {

            p.setDateVisu(dateSelectionnee);
            Log.i("DATE_SELECTIONNEE", dateSelectionnee);


            if (villeSelectionnee.equals("Toutes") || p.getVille().equalsIgnoreCase(villeSelectionnee)) {
                poubelleList.add(p);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void afficherDialogVille() {
        List<String> villesUniques = new ArrayList<>();
        villesUniques.add("Toutes");
        for (Poubelle p : toutesLesPoubelles) {
            if (!villesUniques.contains(p.getVille())) {
                villesUniques.add(p.getVille());
            }
        }

        String[] villes = villesUniques.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choisissez une ville");
        builder.setItems(villes, (dialog, which) -> {
            villeSelectionnee = villes[which];
            bChoixVille.setText("Ville : " + villeSelectionnee);
            appliquerFiltres();
        });
        builder.show();
    }

    private void afficherDialogDate() {
        final Calendar c = Calendar.getInstance();
        int annee = c.get(Calendar.YEAR);
        int mois = c.get(Calendar.MONTH);
        int jour = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    dateSelectionnee = String.format("%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
                    String dateAffichee = String.format("%02d-%02d-%04d", dayOfMonth, (monthOfYear + 1), year);
                    bChoixJour.setText("Jour : " + dateAffichee);
                    bChoixDate.setText("Mois : --");
                    appliquerFiltres();
                }, annee, mois, jour);
        datePickerDialog.show();
    }
}