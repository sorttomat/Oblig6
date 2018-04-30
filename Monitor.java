import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Monitor {
    /*Denne monitoren inneholder baade en beholder for dekrypterte meldinger, 
	og en beholder for krypterte meldinger. */
    private LinkedList<Melding> beholderKryptert;
    private LinkedList<Melding> beholderDekryptert;
    private Lock laasKryptert;
	private Lock laasDekryptert;
	private Lock laasTelegrafTeller;
	private Lock laasKryptografTeller;
    private Condition ikkeTomKryptert;
    private Condition ikkeTomDekryptert;
    private int antallTelegrafister;
    private int antallKryptografer;
    private int telegrafisterFerdig; //Antall telegrafister som er ferdige
    private int kryptograferFerdig; //Antall kryptografer som er ferdige
    private boolean alleTelegraferFerdige = false;
    private boolean alleKryptograferFerdige = false;

    public Monitor(int antallTelegraf, int antallKrypt) {
		/*Alle delte ressurser (ressurser som mer enn én traad oensker tilgang til) faar en Lock. */
        beholderKryptert = new LinkedList<Melding>();
        beholderDekryptert = new LinkedList<Melding>();
        laasKryptert = new ReentrantLock();
		laasDekryptert = new ReentrantLock();
		laasTelegrafTeller = new ReentrantLock();
		laasKryptografTeller = new ReentrantLock();
        ikkeTomKryptert = laasKryptert.newCondition();
        ikkeTomDekryptert = laasDekryptert.newCondition();
        antallTelegrafister = antallTelegraf;
        antallKryptografer = antallKrypt;
        telegrafisterFerdig = 0;
        kryptograferFerdig = 0;
    }

    public void settInnKryptert(Melding melding) throws InterruptedException {
        try {
			laasKryptert.lock();
            beholderKryptert.add(melding);
            ikkeTomKryptert.signal();
		
		} finally {
			laasKryptert.unlock();
        }
	}
	
    public Melding taUtKryptert() throws InterruptedException {
        /*Dersom beholderen med krypterte meldinger er tom, OG alle telegrafister er ferdige, returneres null.
        Da vil det ikke havne flere meldigner i beholderen. */
        try {
			laasKryptert.lock();
            if (beholderKryptert.isEmpty()) {
				if (skalReturnereNullTilKryprograf()) {
					return null;
				}
                ikkeTomKryptert.await();
            }
            return beholderKryptert.remove(); //Returnerer og fjerner det foerste elementet i beholderen med krypterte meldinger.

        } finally {
			laasKryptert.unlock();
        }
    }

    public void settInnDekryptert(Melding melding) throws InterruptedException {
        try {
			laasDekryptert.lock();
            beholderDekryptert.add(melding);
            ikkeTomDekryptert.signal();
		
		} finally {
            laasDekryptert.unlock();
        }
	}
	
    public Melding taUtDekryptert() throws InterruptedException {
        /*Dersom beholderen med dekrypterte meldinger er tom, OG alle telegrafister og kryptografer er ferdige, returneres null.
        Da vil det ikke havne flere meldinger i beholderen. */
        try {
			laasDekryptert.lock();
			if (beholderDekryptert.isEmpty()) {			
				if (skalReturnereNulltilOperasjonsleder()) {
					return null;
				}
				ikkeTomDekryptert.await();
			}
            return beholderDekryptert.remove(); //Returnerer og fjerner det foerste elementet i beholderen med dekrypterte meldinger.
		
		} finally {
			laasDekryptert.unlock();
        }
	}

    public void settFerdigTelegraf() {
		try {
			laasTelegrafTeller.lock();
			telegrafisterFerdig++;
			if (telegrafisterFerdig == antallTelegrafister) {
				alleTelegraferFerdige = true;
			}	
		
		} finally {
			laasTelegrafTeller.unlock();
		}
    }

    public void settFerdigKryptograf() {
		try {
			laasKryptografTeller.lock();
			kryptograferFerdig++;
			if (kryptograferFerdig == antallKryptografer) {
				alleKryptograferFerdige = true;
			}	
		
		} finally {
			laasKryptografTeller.unlock();
		}
	}
	
	private boolean skalReturnereNullTilKryprograf() {
		try {
			laasTelegrafTeller.lock();
			if (alleTelegraferFerdige) {
				return true;
			}
			return false;
		
		} finally {
			laasTelegrafTeller.unlock();
		}
	}

	private boolean skalReturnereNulltilOperasjonsleder() {
		try {
			laasKryptografTeller.lock();
			laasTelegrafTeller.lock();
			if (alleKryptograferFerdige && alleTelegraferFerdige) {
				return true;
			}
			return false;
		
		} finally {
			laasKryptografTeller.unlock();
			laasTelegrafTeller.unlock();
		}
	}
}