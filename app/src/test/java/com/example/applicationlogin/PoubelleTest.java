package com.example.applicationlogin;

import org.junit.Test;
import static org.junit.Assert.*;

public class PoubelleTest {

    @Test
    public void testCreationPoubelle() {

        Poubelle p = new Poubelle(10, "78 Rue de la Paix", "Troyes");


        assertEquals(10, p.getId());
        assertEquals("78 Rue de la Paix", p.getAdresse());
        assertEquals("Troyes", p.getVille());
    }

    @Test
    public void testAjoutDate() {
        Poubelle p = new Poubelle(5, "Place Stanislas", "Nancy");


        assertNull(p.getDateVisu());


        p.setDateVisu("2026-03-31");


        assertEquals("2026-03-31", p.getDateVisu());
    }
}