package com.example.applicationlogin;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class GestionApiUnitaireTest {

    @Mock
    private MainActivity mockMainActivity;

    @Mock
    private Context mockContext;

    @Mock
    private RequestQueue mockRequestQueue;

    @Mock
    private GestionApiUnitaire.PoubelleCallback mockPoubelleCallback;

    @Mock
    private GestionApiUnitaire.MesureCallback mockMesureCallback;

    private GestionApiUnitaire gestionApi;

    @Before
    public void setUp() {

        MockitoAnnotations.openMocks(this);



        gestionApi = new GestionApiUnitaire(mockRequestQueue);
    }

    @Test
    public void testProcessLogin_AjouteRequeteALaFile() {

        gestionApi.processLogin("Riglet", "truc", mockMainActivity);



        verify(mockRequestQueue, times(1)).add(any(Request.class));
    }

    @Test
    public void testChargerPoubelles_AjouteRequeteALaFile() {

        gestionApi.chargerPoubelles(mockContext, mockPoubelleCallback);


        verify(mockRequestQueue, times(1)).add(any(Request.class));
    }

    @Test
    public void testChargerMesures_AjouteRequeteALaFile() {

        gestionApi.chargerMesures(mockContext, 42, "2026-04-08", mockMesureCallback);


        verify(mockRequestQueue, times(1)).add(any(Request.class));
    }
}