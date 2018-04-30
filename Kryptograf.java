class Kryptograf implements Runnable{
    private Monitor monitor;

    public Kryptograf(Monitor mon) {
        monitor = mon;
    }

    public void run() {
        /*Prover hele tiden aa ta ut kryptert melding fra monitoren. Dersom monitoren er tom OG 
        alle telegrafister er ferdige, returneres null. Da vil det ikke komme flere meldinger til monitoren.
        Kryptografen er da ferdig, og signaliserer dette til monitoren. */
        try {
            Melding kryptertMelding = monitor.taUtKryptert();
            int teller = 0;
            while (kryptertMelding != null) {
                String dekryptert = Kryptografi.dekrypter(kryptertMelding.hentMelding());
                kryptertMelding.settMelding(dekryptert); //Setter en ny melding (String) inn i den gamle meldingen, slik at kanalID og sekvensnummeret bevares.
                Melding dekryptertMelding = kryptertMelding; //Kun for aa vise at dette naa er en dekryptert melding.
                monitor.settInnDekryptert(dekryptertMelding);
                kryptertMelding = monitor.taUtKryptert();
                teller++;
                // Thread.sleep(10); //Liten sleep for aa skape delay. Prover aa unngaa vranglaas.
            }
            monitor.settFerdigKryptograf();
        } catch (InterruptedException ex) {

        }
    }
}