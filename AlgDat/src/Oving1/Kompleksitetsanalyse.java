package Oving1;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Kompleksitetsanalyse {
  /**
   * Genererer fiktive akjseendringer og finner beste kjøps- og salgsdag.
   * Kalkulerer gjennomsnittlig tid brukt på å finne beste kjøps- og salgsdag.
   */
  public static void main(String[] args) {
    ArrayList<Integer> aksjeEndringer = genererTilfeldigListe(10000);
    ArrayList<Integer> prisPerDag = finnPrisPerDag(aksjeEndringer);
    Date start = new Date();
    Date slutt;
    int runder = 0;
    double snittTid;

    do {
      kalkulerKjop(prisPerDag);
      ++runder;
      slutt = new Date();
    } while (slutt.getTime() - start.getTime() < 1000);

    snittTid = ((double) (slutt.getTime() - start.getTime()) / runder);

    System.out.printf("Tid brukt i millisekunder per runde: %f", snittTid);
  }

  /**
   * Kalkulerer beste kjøps- og salgsdager
   */
  private static void kalkulerKjop(ArrayList<Integer> prisPerDag) {
    double salgVerdi = 0;
    double kjopVerdi = 0;
    int besteKjopsDag = 0;
    int besteSalgsDag = 0;

    double bestRatio = -100;

    for (int i = 0; i < prisPerDag.size(); i++) {
      for (int j = i + 1; j < prisPerDag.size(); j++) {
        double midlertidigRate = 0;
        kjopVerdi = prisPerDag.get(j);
        salgVerdi = prisPerDag.get(i);
        if (salgVerdi == 0) {
          kjopVerdi += 10;
          salgVerdi += 10;
        }
        midlertidigRate = kjopVerdi / salgVerdi;

        if (midlertidigRate > bestRatio) bestRatio = midlertidigRate;
      }
    }
  }

  /*
   * Finner pris per dag
   */
  private static ArrayList<Integer> finnPrisPerDag(ArrayList<Integer> aksjeEndringerStatisk) {
    int startPunkt = 10;
    ArrayList<Integer> prisPerDag = new ArrayList<>(aksjeEndringerStatisk.size());
    for (int i = 0; i < aksjeEndringerStatisk.size(); i++) {
      prisPerDag.add(i, (startPunkt + aksjeEndringerStatisk.get(i)));
      startPunkt = prisPerDag.get(i);
    }
    return prisPerDag;
  }

  /*
   * Genererer statisk liste med aksjeendringer
   */
  private static ArrayList<Integer> genererStatiskListe() {
    ArrayList<Integer> aksjeEndringerStatisk = new ArrayList<>();
    aksjeEndringerStatisk.add(-1);
    aksjeEndringerStatisk.add(3);
    aksjeEndringerStatisk.add(-9);
    aksjeEndringerStatisk.add(2);
    aksjeEndringerStatisk.add(2);
    aksjeEndringerStatisk.add(-1);
    aksjeEndringerStatisk.add(2);
    aksjeEndringerStatisk.add(-1);
    aksjeEndringerStatisk.add(-5);
    return aksjeEndringerStatisk;
  }

  /*
   * Genererer tilfeldig liste med aksjeendringer
   */
  private static ArrayList<Integer> genererTilfeldigListe(int storrelsePaaListe) {
    Random tilfeldigTall = new Random();
    ArrayList<Integer> aksjeEndringerTilfeldig = new ArrayList<>();
    boolean alternererFortegn = true;
    for (int i = 0; i < storrelsePaaListe; i++) {
      aksjeEndringerTilfeldig.add(tilfeldigTall.nextInt(10));
      if (alternererFortegn) aksjeEndringerTilfeldig.set(i, i * (-1));
      alternererFortegn = !alternererFortegn;
    }
    return aksjeEndringerTilfeldig;
  }

}

