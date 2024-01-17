package Oving5;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HashtabellerOppg1 {
  public static void main(String[] args) throws FileNotFoundException {

    int m = 150;
    int kollisjoner = 0;

    LinkedList[] arrayListe = new LinkedList[m];


    try (Scanner scanner = new Scanner(new FileInputStream("src/Oving5/navn.txt"), StandardCharsets.UTF_8)) {
      while (scanner.hasNextLine()) {
        String linje = scanner.nextLine();
        Integer hashNr = Hash.stringHashDefault(linje, m);
        // Sjekker om indeksen i listen er tom eller ikke
        if (arrayListe[hashNr] == null) {
          arrayListe[hashNr] = new LinkedList<>();
          arrayListe[hashNr].add(linje);
        } else {
          kollisjoner++;
          System.out.println(linje + " kolliderte med: " + arrayListe[hashNr] + "\n");
          arrayListe[hashNr].add(linje);
        }
      }
    }

    int brukteIndekserIListen = 0;
    for (LinkedList element : arrayListe) {
      if (element != null) {
        brukteIndekserIListen++;
      }
    }

    System.out.println("Listen: " + Arrays.toString(arrayListe));

    System.out.println("\nAntall kollisjoner: " + kollisjoner
            + "\nAntall indekser brukt: " + brukteIndekserIListen
            + "\nKapasitet: " + m
            + "\nLastfaktor: " + (double)brukteIndekserIListen/m
            + "\nKollisjoner per person: " + (double)kollisjoner/135);

    String finnDetteNavnet = "Emil Leonard Aastrøm";
    int hashNr = Hash.stringHashDefault(finnDetteNavnet, m);
    System.out.println("\nFinnes '" + finnDetteNavnet + "' i listen? " + arrayListe[hashNr].contains(finnDetteNavnet));
  }
}

class Hash {
  public static int stringHashDefault(String s, int m){
    int hash = 0;
    for (int i = s.length(); i-- > 0;){
      hash = ((7*hash + s.charAt(i)) % m);
    }
    return hash;
  }

  public static int stringHashVariant(String s, int m){
    int hash = 0;
    for (int i = 1; i <= s.length(); i++){
      hash += ((i*5 + s.charAt(i-1)) % m);
    }
    hash = hash % m;
    return hash;
  }
}