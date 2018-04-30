class Telegrafist implements Runnable{
    private Kanal kanal;
    private Monitor monitor;
    private int kanalID;

    public Telegrafist(Kanal kan, Monitor mon) {
        kanal = kan;
        monitor = mon;
        kanalID = kanal.hentId();
    }

    public void run() {
        /*Prover aa hente ut meldinger fra kanalen sin helt til kanalen er tom.
        Naar kanalen er tom, signaliserer telegrafisten til monitoren at den er ferdig med aa lytte.*/
        try {
            int sekvensnummer = 0;
            String kryptertMelding = kanal.lytt();
            while (kryptertMelding != null) {
                Melding melding = new Melding(kryptertMelding, sekvensnummer, kanalID);
                monitor.settInnKryptert(melding);
                sekvensnummer++;
                kryptertMelding = kanal.lytt();
            }
            monitor.settFerdigTelegraf();
        } catch (InterruptedException ex) {

        }
    }
}