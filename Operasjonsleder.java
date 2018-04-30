import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;

class Operasjonsleder implements Runnable {
    private Monitor monitor;
    private int antallKanaler;
    private ArrayList<ArrayList<Melding>> dekrypterteMeldinger;
    private File tilFil;


    public Operasjonsleder(Monitor mon, int antKanaler, String filename) {
        monitor = mon;
        antallKanaler = antKanaler;
        dekrypterteMeldinger = new ArrayList<ArrayList<Melding>>();
        tilFil = new File(filename);
        for (int i = 0; i < antallKanaler; i++) { //Oppretter listen med riktig antall sublister. En subliste for hver kanal.
            ArrayList<Melding> liste = new ArrayList<Melding>();
            dekrypterteMeldinger.add(liste);
        }
    }

    public void run() {
        /*Prover hele tiden aa hente dekrypterte meldinger fra monitoren.
        Naar monitoren er tom og alle telegrafister og alle kryptografer er ferdige, har alle meldingene blitt hentet ut.
        Da kan operasjonslederen avslutte og skrive alle meldingene til en fil. */
        try {
            Melding dekryptertMelding = monitor.taUtDekryptert();
            while (dekryptertMelding != null) {
                int kanalNummer = dekryptertMelding.hentKanalID();
                dekrypterteMeldinger.get(kanalNummer-1).add(dekryptertMelding);
                dekryptertMelding = monitor.taUtDekryptert();
                Thread.sleep(100); //For aa unngaa vranglaas, er det noedvendig med en delay her. Det foeles litt som symptombehandling, men men.
            }
            skrivTilFil();
        } catch (InterruptedException ex) {

        } 
    }

    private void skrivTilFil() {
        /*Sorterer foerst meldingene basert paa sekvensnummer (de ligger allerede sortert paa kanalID i hver sin liste).
        Deretter skrives hver melding til fil. For aa vaere sikker paa at rekkefoelgen er riktig, tar jeg med kanalID og sekvensnummeret i filen. */
        for (ArrayList<Melding> liste : dekrypterteMeldinger) {
            Collections.sort(liste);
        }
        try {
            PrintWriter skriver = new PrintWriter(tilFil, "utf-8");
            for (ArrayList<Melding> kanaler : dekrypterteMeldinger) {
                for (Melding melding : kanaler) {
                    skriver.write(melding.hentKanalID() + " " + melding.hentSekvensNummer() + " " + melding.hentMelding());
                    skriver.write("\n\n");
                }
            }
            skriver.close();
        } catch (FileNotFoundException ex) {
        }
        catch (UnsupportedEncodingException ex) {

        }
    }
}