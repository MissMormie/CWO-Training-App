package nl.multimedia_engineer.cwo_app.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.cwo_app.CursistHeaderFragment;

import static org.junit.Assert.*;

public class CursistTest {

    private Cursist cursist;
    private List<DiplomaEis> diplomaEisen;
    private List<Diploma> diplomas;

    @Before
    public void setUp() throws Exception {
        // Set up standard list of diplomas
        diplomas = new ArrayList<>();
        Diploma d1 = new Diploma();
        d1.setId("diploma1");
        diplomas.add(d1);

        Diploma d2 = new Diploma();
        d2.setId("diploma2");
        diplomas.add(d2);


        // Set up standard list of diplomaEisen
        diplomaEisen = new ArrayList<>();

        DiplomaEis de1 = new DiplomaEis("id1", "titel1", "diploma 1 eis 1");
        de1.setDiploma(d1);
        diplomaEisen.add(de1);

        DiplomaEis de2 = new DiplomaEis("id2", "titel2", "diploma 2 eis 2");
        de2.setDiploma(d2);
        diplomaEisen.add(de2);

        DiplomaEis de3 = new DiplomaEis("id3", "titel3", "diploma 1 eis 3");
        de3.setDiploma(d1);
        diplomaEisen.add(de3);
    }

    @Test
    public void isAlleEisenBehaald_nietsBehaald() {
        Cursist cursist = new Cursist();
        assertFalse("Geen eisen behaald", cursist.isAlleEisenBehaald(diplomaEisen));
    }

    @Test
    public void isAlleEisenBehaald_1EisBehaaldNietAlles() {
        Cursist cursist = new Cursist();
        cursist.addDiplomeEis(diplomaEisen.get(0));
        assertFalse("Niet alle eisen behaald", cursist.isAlleEisenBehaald(diplomaEisen));
    }

    @Test
    public void isAlleEisenBehaald_alleEisenBehaald() {
        Cursist cursist = new Cursist();
        cursist.addDiplomeEis(diplomaEisen.get(0));
        cursist.addDiplomeEis(diplomaEisen.get(1));
        cursist.addDiplomeEis(diplomaEisen.get(2));
        assertTrue("Alle eisen behaald", cursist.isAlleEisenBehaald(diplomaEisen));
    }

    @Test
    public void isAlleEisenBehaald_1DiplomaBehaaldNietAlleEisen() {
        Cursist cursist = new Cursist();
        cursist.addDiploma(diplomas.get(0));
        assertFalse("1 Diploma, niet alle eisen", cursist.isAlleEisenBehaald(diplomaEisen));
    }

    @Test
    public void isAlleEisenBehaald_2DiplomaBehaaldAlleEisenVoldaan() {
        Cursist cursist = new Cursist();
        cursist.addDiploma(diplomas.get(0));
        cursist.addDiploma(diplomas.get(1));
        assertTrue("2 Diploma, wel alle eisen voldaan", cursist.isAlleEisenBehaald(diplomaEisen));
    }

    @Test
    public void isAlleEisenBehaald_1Diploma1EisenAllesBehaald() {
        Cursist cursist = new Cursist();
        cursist.addDiploma(diplomas.get(0));
        cursist.addDiplomeEis(diplomaEisen.get(1));
        assertTrue("1 diploma, 1 eis, alles gehaald", cursist.isAlleEisenBehaald(diplomaEisen));
    }

    @Test
    public void isAlleDiplomasBehaald() {
    }

    @Test
    public void hasDiploma() {
    }
}