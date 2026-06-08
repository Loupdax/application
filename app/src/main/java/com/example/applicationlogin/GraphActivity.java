package com.example.applicationlogin;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {
    private LineChart chartTemp, chartmes;
    private TextView dateChoisie;
    private String dateMesure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        chartTemp = findViewById(R.id.chartTemp);
        chartmes = findViewById(R.id.chartmes);
        dateChoisie = findViewById(R.id.dateChoisie);
        setupChartStyle(chartTemp);
        setupChartStyle(chartmes);

        int idPoubelle = getIntent().getIntExtra("ID_POUBELLE", -1);
        dateMesure = getIntent().getStringExtra("DATE_MESURE");

        String dateAffichee = dateMesure;
        String[] tab = dateMesure.split("-");

        if (tab.length == 3) {
            dateAffichee = tab[2] + "-" + tab[1] + "-" + tab[0];
        } else if (tab.length == 2) {
            dateAffichee = tab[1] + "-" + tab[0];
        }

        dateChoisie.setText(dateAffichee);

        GestionApi gapi = new GestionApi();

        if (dateMesure.length() == 7) {
            if (idPoubelle != -1) {
                gapi.chargerMesuresMois(this, idPoubelle, dateMesure, this::displayData);
            } else {
                Toast.makeText(this, "Erreur : Aucune poubelle sélectionnée", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (idPoubelle != -1) {
                gapi.chargerMesures(this, idPoubelle, dateMesure, this::displayData);
            } else {
                Toast.makeText(this, "Erreur : Aucune poubelle sélectionnée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupChartStyle(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
    }

    private void displayData(JSONArray data) {
        if (data == null || data.length() == 0) {
            chartTemp.clear();
            chartmes.clear();
            Toast.makeText(this, "Aucune donnée pour cette période", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Entry> tempEntries = new ArrayList<>();
        List<Entry> profEntries = new ArrayList<>();
        List<String> labelsX = new ArrayList<>();

        boolean isMois = (dateMesure != null && dateMesure.length() == 7);
        int maxDays = 31;

        if (isMois) {
            try {
                String[] tab = dateMesure.split("-");
                int annee = Integer.parseInt(tab[0]);
                int mois = Integer.parseInt(tab[1]);

                if (mois == 4 || mois == 6 || mois == 9 || mois == 11) {
                    maxDays = 30;
                } else if (mois == 2) {
                    boolean estBissextile = (annee % 4 == 0 && annee % 100 != 0) || (annee % 400 == 0);
                    maxDays = estBissextile ? 29 : 28;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = data.getJSONObject(i);
                String heureStr = obj.getString("heure");

                float xValue = i;

                if (isMois) {
                    try {

                        String[] dateParts = heureStr.split("-");
                        int jour = Integer.parseInt(dateParts[2]);


                        int annee = Integer.parseInt(dateParts[0]);
                        int mois = Integer.parseInt(dateParts[1]);


                        xValue=jour;
                    } catch (Exception e) {
                        xValue = 7;
                    }
                } else {
                    labelsX.add(heureStr.substring(11, 16));
                }

                tempEntries.add(new Entry(xValue, (float) obj.getDouble("Temperature")));
                profEntries.add(new Entry(xValue, (float) obj.getDouble("Profondeur")));
            }

            ValueFormatter monthFormatter = new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return "Jour " + (int) value; // Ajout du mot "Jour" pour plus de clarté
                }
            };


            LineDataSet tempSet = new LineDataSet(tempEntries, "Température (°C)");
            styleLine(tempSet, Color.parseColor("#F44336"), isMois);

            tempSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format("%.1f°C", value);
                }
            });
            chartTemp.setData(new LineData(tempSet));

            YAxis yAxisTemp = chartTemp.getAxisLeft();
            yAxisTemp.setLabelCount(8, false);
            yAxisTemp.setDrawGridLines(true);
            yAxisTemp.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format("%.1f °C", value);
                }
            });

            // Personnalisation de l'abscisse (Axe X)
            XAxis xAxisTemp = chartTemp.getXAxis();
            xAxisTemp.setLabelRotationAngle(-45f);
            xAxisTemp.setLabelCount(15, false);

            if (isMois) {
                xAxisTemp.setValueFormatter(monthFormatter);
                xAxisTemp.setAxisMinimum(1f);
                xAxisTemp.setAxisMaximum(maxDays);
                xAxisTemp.setGranularity(1f);
            } else {
                xAxisTemp.setValueFormatter(new IndexAxisValueFormatter(labelsX));
                xAxisTemp.setAxisMinimum(0f);
                xAxisTemp.setAxisMaximum(data.length() - 1);
                xAxisTemp.setGranularity(1f);
            }

            chartTemp.setExtraBottomOffset(15f);
            chartTemp.invalidate();
            chartTemp.animateX(800);



            LineDataSet profSet = new LineDataSet(profEntries, "Profondeur (cm)");
            styleLine(profSet, Color.parseColor("#2196F3"), isMois);

            profSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format("%.0f cm", value);
                }
            });
            chartmes.setData(new LineData(profSet));

            YAxis yAxisProf = chartmes.getAxisLeft();
            yAxisProf.setLabelCount(8, false);
            yAxisProf.setDrawGridLines(true);
            yAxisProf.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format("%.0f cm", value);
                }
            });

            // Personnalisation de l'abscisse (Axe X)
            XAxis xAxisProf = chartmes.getXAxis();
            xAxisProf.setLabelRotationAngle(-45f); // INCLINAISON POUR ÉVITER LE CHEVAUCHEMENT
            xAxisProf.setLabelCount(15, false);    // FORCE L'AFFICHAGE DE PLUS DE VALEURS

            if (isMois) {
                xAxisProf.setValueFormatter(monthFormatter);
                xAxisProf.setAxisMinimum(1f);
                xAxisProf.setAxisMaximum(maxDays);
                xAxisProf.setGranularity(1f);
            } else {
                xAxisProf.setValueFormatter(new IndexAxisValueFormatter(labelsX));
                xAxisProf.setAxisMinimum(0f);
                xAxisProf.setAxisMaximum(data.length() - 1);
                xAxisProf.setGranularity(1f);
            }
            // Marge en bas pour ne pas couper les textes inclinés
            chartmes.setExtraBottomOffset(15f);
            chartmes.invalidate();
            chartmes.animateX(800);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void styleLine(LineDataSet set, int color, boolean isMois) {
        set.setColor(color);
        set.setLineWidth(3f);
        set.setDrawCircles(true);
        set.setCircleColor(color);
        set.setCircleRadius(4f);

        set.setDrawValues(true);
        set.setValueTextSize(9f);
        set.setValueTextColor(Color.DKGRAY);

        if (isMois) {
            set.setMode(LineDataSet.Mode.LINEAR);
        } else {
            set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        }
    }
}