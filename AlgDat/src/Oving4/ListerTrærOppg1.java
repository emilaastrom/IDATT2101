class Node {
  int data;
  Node neste;
  //Node forrige;
  public Node(int data) {
    this.data = data;
    this.neste = null;
    //this.forrige = null;
  }
}

class SirkulaerLenketListe {
  private Node hodeNode;

  public SirkulaerLenketListe() {
    //Starter opp med en tom liste
    hodeNode = null;
  }

  public void leggInnFremst(int data) {
    Node nyHodeNode = new Node(data);

    // Sjekker at listen ikke er tom før vi legger inn den nye noden
    if (hodeNode != null) {

      // Finner halen ved å lete etter en referanse til hodet (noden som peker til hode)
      Node haleNode = hodeNode;
      while (haleNode.neste != hodeNode) {
        haleNode = haleNode.neste;
      }

      // Legger inn den nye noden foran det forrige hodet
      nyHodeNode.neste = hodeNode;
      hodeNode = nyHodeNode;

      // Oppdaterer halen så neste peker til det nye hodet
      haleNode.neste = nyHodeNode;
    } else {
      //Dersom listen er tom settes hodet til å være den nye noden
      hodeNode = nyHodeNode;
      hodeNode.neste = nyHodeNode; // Sirkulær referanse til samme node
    }
  }

  public void printListe() {
    // Sjekker at listen ikke er tom før vi skriver ut
    if (hodeNode == null) {
      System.out.println("\nDen sirkulære listen er tom.");
      return;
    }

    // Skriver ut alle nodene i listen
    Node naavaerende = hodeNode;
    System.out.print("\nDen sirkulære listen: ");
    do {
      System.out.print(naavaerende.data + " ");
      naavaerende = naavaerende.neste;
    } while (naavaerende != hodeNode);
    System.out.println();
  }

  public static void main(String[] args) {
    SirkulaerLenketListe sirkulaerListe = new SirkulaerLenketListe();

    // Valgte parametere for oppgaven
    int antallSoldater = 40;
    int intervallMellomSoldater = 3;

    // Legger til soldater fremst i listen
    for (int i = 1; i <= antallSoldater; i++) {
      sirkulaerListe.leggInnFremst(i);
    }

    // Printer ut listen
    sirkulaerListe.printListe();

    // Finner ut hvor Josephus bør stille seg
    System.out.println("\nHvor bør Josephus stille seg for å leve lengst?");
    System.out.println("Josephus bør stille seg på plassen hvor det står " +
            josephus(sirkulaerListe.hodeNode, intervallMellomSoldater) +
            " for å ende opp som siste overlevende soldat.");
  }

  private static String josephus(Node head, int intervallMellomSoldater) {
    // Starter i begynnelsen av listen, hvor nåværende node er hodet og forrige ikke er definert
    Node naavaerende = head;
    Node forrige = null;
    // Teller som holder styr på intervallet mellom soldatene
    int teller = 0;
    // Teller som hjelper til å formatere utskriften på en lesbar måte
    int tellerForNyLinje = 0;

    System.out.println("\nSoldatene som faller...");

    //Traverserer helt til det kun er en gjenstående node
    while (naavaerende.neste != naavaerende) {
      teller++;
      if (teller == intervallMellomSoldater) {
        //Formaterer utskriften så ikke alle soldatene skrives ut på en linje
        if (tellerForNyLinje % 10 == 0 && tellerForNyLinje != 0) System.out.print("\n");
        // Soldaten som faller skrives ut før den fjernes
        System.out.print(naavaerende.data + " ");
        // Fjerner en node ved å hoppe over den i neste-pekeren til den forrige noden
        assert forrige != null;
        forrige.neste = naavaerende.neste;
        teller = 0;
        tellerForNyLinje++;
      } else {
        forrige = naavaerende;
      }
      naavaerende = naavaerende.neste;
    }
    System.out.println("\n");
    return String.valueOf(naavaerende.data);
  }
}