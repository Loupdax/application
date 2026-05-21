package com.example.applicationlogin;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GestionApi {
    private final String API_URL = "http://172.20.10.2/applicationmobil/";

    public void processLogin(String user, String pass, MainActivity ma) {
        StringRequest loginRequest = new StringRequest(Request.Method.POST, API_URL + "login.php",
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            ma.navigateToHome(user);
                        } else {
                            ma.showToast(json.optString("message", "Échec de connexion"));
                        }
                    } catch (JSONException e) {
                        ma.showToast("Données corrompues");
                    }
                },
                error -> ma.showToast("Erreur réseau")) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", user);
                params.put("password", pass);
                return params;
            }
        };
        Volley.newRequestQueue(ma).add(loginRequest);
    }

    public void chargerPoubelles(Context context, final PoubelleCallback callback) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API_URL + "get_poubelles.php", null,
                response -> callback.onSuccess(response),
                error -> Log.e("API_ERROR", "Erreur : " + error.getMessage()));
        Volley.newRequestQueue(context).add(request);
    }

    public void chargerMesures(Context context, int idPoubelle, String dateMesure, final MesureCallback callback) {
        JSONArray body = new JSONArray();

        body.put(dateMesure + " 00:00:00");
        body.put(dateMesure + " 23:59:59");

        body.put(idPoubelle);
        System.out.println(body.toString());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, API_URL + "get_mesures.php", body,
                response -> callback.onSuccess(response),
                error -> Log.e("API_ERROR", "Erreur : " + error.getMessage()));
        Volley.newRequestQueue(context).add(request);
    }

    public void chargerMesuresMois(Context context, int idPoubelle, String dateMesure, final MesureCallback callback) {

        JSONArray body = new JSONArray();
        String[] tab = dateMesure.split("-");
        int anneeChoisie = Integer.parseInt(tab[0]);
        int moisChoisi = Integer.parseInt(tab[1]);


        Calendar calendar = Calendar.getInstance();
        calendar.set(anneeChoisie, moisChoisi - 1, 1);
        int jourChoisi = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        body.put(dateMesure + "-01 00:00:00");
        body.put(dateMesure + "-" + String.format("%02d", jourChoisi) + " 23:59:59");

        body.put(idPoubelle);
        System.out.println(body.toString());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, API_URL + "get_mesures_mois.php", body,
                response -> callback.onSuccess(response),
                error -> Log.e("API_ERROR", "Erreur : " + error.getMessage()));
        Volley.newRequestQueue(context).add(request);
    }

    public interface MesureCallback {
        void onSuccess(JSONArray result);
    }

    public interface PoubelleCallback {
        void onSuccess(JSONArray result);
    }
}