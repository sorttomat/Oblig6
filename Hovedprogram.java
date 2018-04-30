class Hovedprogram {
    public static void main(String[] args) {
        /*Programmet kjoerer bra, dersom det blir for mange kryptografer hender det at det oppstaar en vranglaas. Opp til 10 gaar fint (tar ca 20 sekunder), 20 gaar som regel ogsaa fint (tar ca 13 sekunder), men flere enn det gaar ikke. */
        Operasjonssentral ops = new Operasjonssentral(3);
        Kanal[] kanaler = ops.hentKanalArray();
        int antallTelegrafisterViTrenger = kanaler.length;

        int antallKryptografer = 20;
        Monitor monitor = new Monitor(antallTelegrafisterViTrenger, antallKryptografer);

        for (int i = 0; i < antallTelegrafisterViTrenger; i++) {
            Telegrafist telegrafist = new Telegrafist(kanaler[i], monitor);
            new Thread(telegrafist).start();
        }
        for (int i = 0; i < antallKryptografer; i++) {
            Kryptograf kryptograf = new Kryptograf(monitor);
            new Thread(kryptograf).start(); 
        }

        Operasjonsleder operasjonsleder = new Operasjonsleder(monitor, kanaler.length, "tilfil.txt");
        new Thread(operasjonsleder).start();
    }
}