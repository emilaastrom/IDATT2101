package Oving6;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

class NodeO5 {
    int id;
    List<NodeO5> naboer;
    TopoListe liste;
    Kant førsteKant;

    public NodeO5(int id) {
        this.id = id;
        this.naboer = new ArrayList<>();
    }

    public void leggTilNabo(NodeO5 nabo) {
        naboer.add(nabo);
    }

    public void leggTilKant(NodeO5 til) {
        if (førsteKant == null) { førsteKant = new Kant(null, til);
        } else { førsteKant = new Kant(førsteKant, til);
        }
    }

    public Kant getFørsteKant() {
        return førsteKant;
    }

    public TopoListe getListe() {
        return liste;
    }
}

class Kant {
    private Kant neste;
    NodeO5 tilNode;

    public Kant(Kant neste, NodeO5 tilNode) {
        this.neste = neste;
        this.tilNode = tilNode;
    }

    public Kant neste() {
        return neste;
    }

}

class TopoListe {
    public boolean funnet;
    public NodeO5 neste;

    public boolean erFunnet() {
        return funnet;
    }

    public void setFunnet() {
        funnet = true;
    }
}


class Graf {
    private String navn;
    private int nodeAntall;
    private int kantAntall;
    NodeO5[] noder;

    public Graf(String navn, int nodeAntall, int kantAntall, NodeO5[] noder) {
        this.navn = navn;
        this.nodeAntall = nodeAntall;
        this.kantAntall = kantAntall;
        this.noder = noder;
    }

    public String getNavn() {
        return navn;
    }

    public int getNodeAntall() {
        return nodeAntall;
    }

    public NodeO5 getNode(int id) {
        return noder[id];
    }

    public void leggTilkant(int fraNode, int tilNode) {
        noder[fraNode].leggTilKant(noder[tilNode]);
    }
}

public class Oving6 {
    public static Graf lesGrafFil(String sti) {
        ArrayList<Integer> fraListe = new ArrayList<>();
        ArrayList<Integer> tilListe = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(sti))) {
            int nodeAntall = scanner.nextInt();
            int kantAntall = scanner.nextInt();

            NodeO5[] noder = new NodeO5[nodeAntall];
            for (int i = 0; i < nodeAntall; i++) {
                noder[i] = new NodeO5(i);
            }

            for (int i = 0; i < kantAntall; i++) {
                int fra = scanner.nextInt();
                int til = scanner.nextInt();
                fraListe.add(fra);
                tilListe.add(til);

                noder[fra].leggTilNabo(noder[til]);
                noder[til].leggTilNabo(noder[fra]);
            }
            Graf graf = new Graf(sti, nodeAntall, kantAntall, noder);
            for (int i = 0; i < fraListe.size(); i++) {
                graf.leggTilkant(fraListe.get(i), tilListe.get(i));
            }

            return graf;
        }
        catch (FileNotFoundException e) {
            System.out.println("Filen " + sti + " finnes ikke.");
            return null;
        }
    }

    public static void breddeFørstSøk(Graf graf, int valgtStartNode) {
        // Velg en tilfeldig node som startnode
        Random random = new Random();
//        int startNodeId = random.nextInt(graf.getNodeAntall());
        int startNodeId = valgtStartNode;
        NodeO5 startNode = graf.getNode(startNodeId);

        // Utfør bredde-først-søk på grafen
        Stack<NodeO5> kø = new Stack<>();
        Integer[] forgjengere = new Integer[graf.getNodeAntall()];
        Integer[] distanse = new Integer[graf.getNodeAntall()];

        kø.push(startNode);
        distanse[startNodeId] = 0;

        while (!kø.isEmpty()) {
            NodeO5 node = kø.pop();
            for (NodeO5 nabo : node.naboer) {
                if (distanse[nabo.id] == null) {
                    distanse[nabo.id] = distanse[node.id] + 1;
                    forgjengere[nabo.id] = node.id;
                    kø.push(nabo);
                }
            }
        }

        // Skriv ut resultatet
        System.out.println("Resultat for graf " + graf.getNavn() + ":");
        System.out.println("Node\tForgj\tDist");
        for (int i = 0; i < graf.getNodeAntall(); i++) {
            System.out.print(i + "\t\t");
            if (forgjengere[i] == null) {
                System.out.print("-\t\t");
            } else {
                System.out.print(forgjengere[i] + "\t\t");
            }
            if (distanse[i] == null) {
                System.out.println("-");
            } else {
                System.out.println(distanse[i]);
            }
        }
    }

    public static NodeO5 topologiskSortering(Graf graf) {
        // Utfør topologisk sortering på grafen
        NodeO5[] node = graf.noder;
        NodeO5 listeHode = null;
        for (int i = node.length; i-- > 0;) {
            node[i].liste = new TopoListe();
        }

        for (int i = node.length; i-- > 0;) {
            listeHode = dfs(node[i], listeHode);
        }
        return listeHode;
    }

    public static NodeO5 dfs(NodeO5 node, NodeO5 listeHode) {
        TopoListe nodeList = node.getListe();
        if (nodeList.erFunnet()) return listeHode;
        nodeList.setFunnet();

        for (Kant e = node.getFørsteKant(); e != null; e = e.neste()) {
            listeHode = dfs(e.tilNode, listeHode);
        }
        nodeList.neste = listeHode;
        return node;
    }

    public static void meny(Scanner scanner, List<Graf> grafer) {
        System.out.println("\nVelg et alternativ:");
        System.out.println("0:\tAvslutt");
        System.out.println("1:\tBredde først søk");
        System.out.println("2:\tTopologisk sortering");
        
        int valg = getInput(scanner, 0, 2);

        switch (valg) {
            case 0:
                System.exit(0);
                break;
            case 1:
                meny_1(scanner, grafer);
                break;
            case 2:
                meny_2(scanner, grafer);
                break;
            default:
                break;
        }
    }

    public static void meny_1(Scanner scanner, List<Graf> grafer) {
        System.out.println("\nVelg graf fil (Bredde først søk):");
        System.out.println("0:\tTilbake");
        for (int i = 1; i < grafer.size() + 1; i++) {
            System.out.println(i + ":\t" + grafer.get(i - 1).getNavn());
        }
        int valg = getInput(scanner, 0, grafer.size());
        System.out.println("\nVelg startnode·");
        int valgtStartNode = getInput(scanner, 0, grafer.size());
        System.out.println();
        if (valg == 0) return;
        breddeFørstSøk(grafer.get(valg - 1), valgtStartNode);
    }

    public static void meny_2(Scanner scanner, List<Graf> grafer) {
        System.out.println("\nVelg graf fil (Topologisk søk):");
        System.out.println("0:\tTilbake");
        for (int i = 1; i < 3; i++) {
            System.out.println(i + ":\t" + grafer.get(i + 2).getNavn());
        }
        int valg = getInput(scanner, 0, 2);
        if (valg == 0) return;
        NodeO5 node = topologiskSortering(grafer.get(valg + 2));
        System.out.println("\nTopologisk sortering:");
        while (node != null) {
            System.out.printf(String.valueOf(node.id) + " ");
            node = node.liste.neste;
        }
        System.out.println("\n");
    }

    public static int getInput(Scanner scanner, int min, int max) {
        int valg = 0;
        try {
            System.out.print("~ ");
            String input = scanner.nextLine();
            if (input.isBlank()) {
                return getInput(scanner, min, max);
            }
            valg = Integer.parseInt(input);
            if (valg < min || valg > max) {
                System.out.println("Ugyldig valg.");
                return getInput(scanner, min, max);
            }
        }
        catch (Exception e) {
            System.out.println("Ugyldig input. Skriv inn et heltall.");
            return getInput(scanner, min, max);
        }
        return valg;
    }
    
    public static void main(String[] args) {
        String[] stier = {"ø6g1", "ø6g2", "ø6g3", "ø6g5", "ø6g7"};
        List<Graf> grafer = new ArrayList<>();
        
        for (String sti : stier) {
            grafer.add(lesGrafFil(sti));
        }
        
        Scanner scanner = new Scanner(System.in);
        while (true) meny(scanner, grafer);
    }
}