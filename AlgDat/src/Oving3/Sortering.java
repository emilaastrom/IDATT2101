import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class Sortering {

  public static void main(String[] args) {
	System.out.println("Øving 3: Quicksort + Innsettingssortering");

	Date start;
	Date slutt = new Date();
  Random tallGenerator = new Random();
  int runder = 0;
  double standardUtenHjelpalgoritme;
  double standardTid;
  double duplikatTid;
  double stigendeTid;

  //Lager lister
	int lengdePaaListe = 1000000;

  int[] standardListe = new int[lengdePaaListe];
    for (int i = 0; i < lengdePaaListe; i++)
      standardListe[i] = tallGenerator.nextInt(-1000000, 1000000);

  int[] duplikatListe = new int[lengdePaaListe];
    for (int i = 0; i < lengdePaaListe; i++) {
      if (i%2==0){
        duplikatListe[i] = tallGenerator.nextInt(-1000000, 1000000);
      } else {
        duplikatListe[i] = 42;
      }
    }

  int[] stigendeListe= new int[lengdePaaListe];
    for (int i = 0; i < lengdePaaListe; i++) stigendeListe[i] = i;

  //Standard liste, uten hjelpalgoritmer
  start = new Date();
  do {
    int[] standardListeKopi = Arrays.copyOf(standardListe, standardListe.length);

    int sumFoerSortering = sjekkSum(standardListeKopi);

    quicksortUtenHjelpealgoritme(standardListeKopi, 0, standardListeKopi.length - 1);

    if (!korrektSortering(standardListeKopi)) break;

    int sumEtterSortering = sjekkSum(standardListeKopi);

    if (sumFoerSortering != sumEtterSortering) break;

    slutt = new Date();
    ++runder;
  } while (slutt.getTime() - start.getTime() < 10000);

  standardUtenHjelpalgoritme = ((double) (slutt.getTime() - start.getTime())) / runder;
  System.out.println("Antall runder med standard liste uten hjelpalgoritme: " + runder);

  //Standard liste, med hjelpealgoritme
  runder = 0;
  start = new Date();
    do {
      int[] standardListeKopi = Arrays.copyOf(standardListe, standardListe.length);

      int sumFoerSortering = sjekkSum(standardListeKopi);

      quicksort(standardListeKopi, 0, standardListeKopi.length - 1);

      if (!korrektSortering(standardListeKopi)) break;

      int sumEtterSortering = sjekkSum(standardListeKopi);

      if (sumFoerSortering != sumEtterSortering) break;

      slutt = new Date();
      ++runder;
    } while (slutt.getTime() - start.getTime() < 10000);

    standardTid = ((double) (slutt.getTime() - start.getTime())) / runder;
    System.out.println("Antall runder med standard liste: " + runder);

    runder = 0;
    start = new Date();
    do {
      int[] duplikatListeKopi = Arrays.copyOf(duplikatListe, duplikatListe.length);

      int sumFoerSortering = sjekkSum(duplikatListeKopi);

      quicksort(duplikatListeKopi, 0, duplikatListe.length - 1);

      if (!korrektSortering(duplikatListeKopi)) break;

      int sumEtterSortering = sjekkSum(duplikatListeKopi);

      if (sumFoerSortering != sumEtterSortering) break;

      slutt = new Date();
      ++runder;
    } while (slutt.getTime() - start.getTime() < 10000);

    duplikatTid = ((double) (slutt.getTime() - start.getTime())) / runder;
    System.out.println("Antall runder med duplikat liste: " + runder);

    runder = 0;
    start = new Date();
    do {
      int[] stigendeListeKopi = Arrays.copyOf(stigendeListe, stigendeListe.length);

      int sumFoerSortering = sjekkSum(stigendeListeKopi);

      quicksort(stigendeListeKopi, 0, stigendeListeKopi.length - 1);

      if (!korrektSortering(stigendeListeKopi)) break;

      int sumEtterSortering = sjekkSum(stigendeListeKopi);

      if (sumFoerSortering != sumEtterSortering) break;

      slutt = new Date();
      ++runder;
    } while (slutt.getTime() - start.getTime() < 10000);

    stigendeTid = ((double) (slutt.getTime() - start.getTime())) / runder;
    System.out.println("Antall runder med stigende liste: " + runder);

    System.out.println("Standard u/ helpealgoritme, tid per runde: " + standardUtenHjelpalgoritme);
    System.out.println("Standard, tid per runde: " + standardTid);
    System.out.println("Duplikat, tid per runde: " + duplikatTid);
    System.out.println("Stigende, tid per runde: " + stigendeTid);
  }

  public static void quicksort(int[] innsendtListe, int v, int h) {
    if (h - v > 250) {
      int delepos = splitt(innsendtListe, v, h);
      quicksort(innsendtListe, v, delepos - 1);
      quicksort(innsendtListe, delepos + 1, h);
	//} else median3sort(innsendtListe, v, h);
    } else innsettingssortering(innsendtListe, v, h);
  }

  public static void quicksortUtenHjelpealgoritme(int[] innsendtListe, int v, int h) {
    int delepos = splitt(innsendtListe, v, h);
    quicksort(innsendtListe, v, delepos - 1);
    quicksort(innsendtListe, delepos + 1, h);
  }

    public static void innsettingssortering(int[] innsendtListe, int v, int h) {
    for (int j = v + 1; j <= h; ++j) {
      int bytt = innsendtListe[j];

      int i = j - 1;
      while(i >= 0 && innsendtListe[i] > bytt) {
        innsendtListe[i + 1] = innsendtListe[i];
        --i;
      }
      innsendtListe[i + 1] = bytt;
    }
  }

  public static void bytt(int[] t, int i, int j) {
    int k = (int) t[j];
    t[j] = t[i];
    t[i] = k;
  }

  public static void bubbleSortering(int[] t){
    for (int i = t.length-1; i>0; --i){
      for (int j = 0; j<i; ++j){
        if (t[j] > t[j+1])
          bytt(t, j, j+1);
      }
    }
  }

  private static int median3sort(int[] t, int v, int h) {
    int m = (v + h) / 2;
    if (t[v] > t[m]) bytt(t, v, m);
    if (t[m] > t[h]) {
      bytt(t, m, h);
      if (t[v] > t[m]) bytt(t, v, m);
    }
    return m;
  }

  private static int splitt(int[] t, int v, int h) {
    int iv, ih;
    int m = median3sort(t, v, h);
    int dv = t[m];
    bytt(t, m, h - 1);
    for (iv = v, ih = h - 1;;) {
      while(t[++iv] < dv);
      while(t[--ih] > dv);
      if (iv >= ih) break;
      bytt(t, iv, ih);
    }
    bytt(t, iv, h - 1);
    return iv;
  }

  public static boolean korrektSortering(int[] innsendtListe){
    for (int i = 0; i < innsendtListe.length-1; i++)
      if (innsendtListe[i] > innsendtListe[i+1])
        return false;
    return true;
  }

  public static int sjekkSum(int[] innsendtListe){
    int sum = 0;
    for (int i = 0; i<innsendtListe.length; ++i){
      sum += innsendtListe[i];
    }
    return sum;
  }
}
