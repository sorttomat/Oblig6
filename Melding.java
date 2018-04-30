class Melding implements Comparable<Melding> {
    private String melding;
    private int kanalID;
    private int sekvensNummer;

    public Melding(String meld, int sekv, int ID) {
        melding = meld;
        kanalID = ID;
        sekvensNummer = sekv;
    }

    public String hentMelding() {
        return melding;
    }

    public void settMelding(String dekryptert) {
        melding = dekryptert;
    }

    public int hentKanalID() {
        return kanalID;
    }

    public int hentSekvensNummer() {
        return sekvensNummer;
    }

    @Override
    public int compareTo(Melding other) {
        /*For at meldingene skal kunne sorteres, var det noedvendig aa implementere dette. */
        Integer sekv = (Integer) sekvensNummer;
        Integer otherSekv = (Integer) other.sekvensNummer;
        return sekv.compareTo(otherSekv);
    }
}