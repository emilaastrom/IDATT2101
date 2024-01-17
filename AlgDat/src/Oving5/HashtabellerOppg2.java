package Oving5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class HashtabellerOppg2 {
    public static void main(String[] args) {
        // Instansierer liste
        int m = 10000000;
        ArrayList<Integer> tilfeldigeTall = new ArrayList<>(m);

        // Fyller liste med tilfeldig unike tall i stigende rekkefølge
        Random random = new Random();
        tilfeldigeTall.add(random.nextInt(1, 100));
        for (int i = 0; i < m; i++) {
            tilfeldigeTall.add(tilfeldigeTall.get(i) + random.nextInt(1, 100));
        }

        // Stokker om listen
        Collections.shuffle(tilfeldigeTall);

        // Instansierer de to hash-tabellene
        HashTabellLineærProbing lineærProbing = new HashTabellLineærProbing(m);
        HashTabellDobbelHashing dobbelHashing = new HashTabellDobbelHashing(m);

        double[] fyllingsgrad = {0.5, 0.8, 0.9, 0.99, 1};
        Date slutt;
        Date start;
        int tid;

        // Lineærprobing
        for (int i = 0; i < fyllingsgrad.length; i++) {
            lineærProbing = new HashTabellLineærProbing(m);
            start = new Date();
            for (int j = 0; j < m * fyllingsgrad[i]; j++) {
                lineærProbing.leggInn(tilfeldigeTall.get(j));
            }
            slutt = new Date();
            tid = (int)(slutt.getTime()-start.getTime());
            System.out.println("Fyllingsgrad: " + fyllingsgrad[i]);
            System.out.println("Antall kollisjoner med lineær probing: " + lineærProbing.finnKollisjoner());
            System.out.println("Tid for lineær probing: " + tid + "ms\n");
        }

        //Dobbelhashing
        for (int i = 0; i < fyllingsgrad.length; i++) {
            dobbelHashing = new HashTabellDobbelHashing(m);
            start = new Date();
            for (int j = 0; j < m * fyllingsgrad[i]; j++) {
                dobbelHashing.leggInn(tilfeldigeTall.get(j));
            }
            slutt = new Date();
            tid = (int) (slutt.getTime() - start.getTime());
            System.out.println("Fyllingsgrad: " + fyllingsgrad[i]);
            System.out.println("Antall kollisjoner med dobbel hashing: " + dobbelHashing.finnKollisjoner());
            System.out.println("Tid for dobbel hashing: " + tid + "ms\n");
        }
    }
}

abstract class HashTabell {
    protected Integer[] tabell;
    protected long kollisjoner;

    protected HashTabell(int m) {
        tabell = new Integer[m];
        kollisjoner = 0L;
    }

    public int leggInn(int k) {
        int m = tabell.length;
        int[] h = hash(k, m);
        for (int i = 0; i < m; i++) {
            int j = probe(h, i, m);
            if (tabell[j] == null) {
                tabell[j] = Integer.valueOf(k);
                return j;
            } else {
                kollisjoner++;
            }
        }
        return -1;
    }

    public int finnPos(int k) {
        int m = tabell.length;
        int[] h = hash(k, m);
        for (int i = 0; i < m; i++) {
            int j = probe(h, i, m);
            if (tabell[j] == null) return -1;
            if (tabell[j].intValue() == k) return j;
        }
        return -1;
    }

    public long finnKollisjoner() {
        return kollisjoner;
    }

    protected abstract int[] hash(int k, int m);
    protected abstract int probe(int h[], int i, int m);
}

class HashTabellLineærProbing extends HashTabell {
    public HashTabellLineærProbing(int m) {
        super(m);
    }

    @Override
    protected int[] hash(int k, int m) {
        return new int[]{k % m};
    }

    @Override
    protected int probe(int h[], int i, int m) {
        int j = (h[0] + i) % m;
        if (j < 0) {
            j += m; // Legg til m for å få en gyldig indeks
        }
        return j;
    }
}

class HashTabellDobbelHashing extends HashTabell {
    public HashTabellDobbelHashing(int m) {
        super(m);
    }

    @Override
    protected int[] hash(int k, int m) {
        int[] h = new int[2];
        h[0] = k % m;
        h[1] = (k % (m - 1)) + 1;
        return h;
    }

    @Override
    protected int probe(int[] h, int i, int m) {
        int j = (h[0] + h[1] * i) % m;
        if (j < 0) {
            j += m; // Legg til m for å få en gyldig indeks
        }
        return j;
    }
}
