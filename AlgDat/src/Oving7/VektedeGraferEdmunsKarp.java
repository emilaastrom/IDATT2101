package Oving7;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class VektedeGraferEdmunsKarp {
    public static class Kant {
        int fra, til, flyt, kapasitet;
        Kant revers;

        public Kant(int til, int fra, int vekt) {
            this.til = til;
            this.fra = fra;
            this.kapasitet = vekt;
        }

        public int getKapasitet(){
            return kapasitet - flyt;
        }

        public void endreFlyt(int flaskehals){
            flyt += flaskehals;
            revers.flyt -= flaskehals;
        }

    }

    int node, kant;
    List<List<Kant>> grafTabell;

    public VektedeGraferEdmunsKarp(BufferedReader br) throws IOException {
        lesGraf(br);
    }

    public void lesGraf(BufferedReader reader) throws IOException {
        grafTabell = new ArrayList<>(node);
        StringTokenizer nåværendeLinje = new StringTokenizer(reader.readLine());
        node = Integer.parseInt(nåværendeLinje.nextToken());
        kant = Integer.parseInt(nåværendeLinje.nextToken());

        for (int i = 0; i < this.node; i++) {
            grafTabell.add(new ArrayList<>());
        }

        for (int i = 0; i < kant; i++) {
            nåværendeLinje = new StringTokenizer(reader.readLine());
            int fra = Integer.parseInt(nåværendeLinje.nextToken());
            int til = Integer.parseInt(nåværendeLinje.nextToken());
            int kapasitet = Integer.parseInt(nåværendeLinje.nextToken());
            leggTilKant(fra, til, kapasitet);
        }
    }

    public void leggTilKant(int nodeFra, int nodeTil, int vekt) {
        if (nodeFra <= node) {
            Kant kant1 = new Kant(nodeTil, nodeFra, vekt);
            Kant kant2 = new Kant(nodeFra, nodeTil, 0);
            grafTabell.get(nodeFra).add(kant1);
            grafTabell.get(nodeTil).add(kant2);
            kant1.revers = kant2;
            kant2.revers = kant1;
        }
    }

    public int bfs(int kilde, int sluk) {
        Queue<Integer> kø = new ArrayDeque<>(node);
        boolean[] besøkt = new boolean[node];
        Integer[] forrigeNode = new Integer[node];
        besøkt[kilde] = true;
        kø.offer(kilde);

        Kant[] forrigeKant = new Kant[node];
        while (!kø.isEmpty()) {
            int node = kø.poll();
            if (node == sluk) break;

            for (Kant kant : grafTabell.get(node)) {
                int kapasitet = kant.getKapasitet();
                if (kapasitet > 0 && !besøkt[kant.til]) {
                    besøkt[kant.til] = true;
                    forrigeNode[kant.til] = node;
                    forrigeKant[kant.til] = kant;
                    kø.add(kant.til);
                }
            }
        }

        int flyt = Integer.MAX_VALUE;

        if (forrigeKant[sluk] == null) return 0;

        for (Kant kant = forrigeKant[sluk]; kant != null; kant = forrigeKant[kant.fra])
            flyt = Math.min(flyt, kant.getKapasitet());

        for (Kant kant = forrigeKant[sluk]; kant != null; kant = forrigeKant[kant.fra]) kant.endreFlyt(flyt);

        String flytøkendeSti = "";
        for (int i = sluk; i != kilde; i = forrigeNode[i]){
            flytøkendeSti = i + " " + flytøkendeSti + " ";
        }
        System.out.println(flyt + "        " + kilde + " " + flytøkendeSti);

        return flyt;
    }

    public void edmondKarp(int kilde, int sluk){
        int maksFlyt = 0;
        int flyt;
        do {
            flyt = bfs(kilde, sluk);
            maksFlyt += flyt;
        } while (flyt != 0);
        System.out.println("Maksimum flyt " + maksFlyt);
    }

    public static void main(String[] args) throws IOException {
        List<String> stier = Arrays.asList("flytgraf1", "flytgraf2", "flytgraf3", "flytgraf4", "flytgraf5");

        Scanner scanner = new Scanner(System.in);
        while (true) meny(scanner, stier);
    }

    public static void meny(Scanner scanner, List<String> stier) {
        System.out.println("\nVelg flytgraf nummer: ");

        for (int i = 0; i < stier.size(); i++) {
            System.out.print(stier.get(i).charAt(8) + ". ");
        }
        System.out.println();
        int valg = lesInndata(scanner, 0, stier.size());
        if (valg == 0) System.exit(0);

        VektedeGraferEdmunsKarp graf;
        FileReader file;
        try{
            file = new FileReader(stier.get(valg - 1));
            BufferedReader br = new BufferedReader(file);
            graf = new VektedeGraferEdmunsKarp(br);
            System.out.println("Økning  Flytøkende sti");
            graf.edmondKarp(0, (valg == 2 || valg == 3) ? 1 : 7);

        } catch (Exception e){
            System.out.printf("feil i lesing av fil");
        }
    }
    public static int lesInndata(Scanner scanner, int min, int max) {
        int valg = 0;
        try {
            System.out.print("~ ");
            String input = scanner.nextLine();
            System.out.println();
            if (input.isBlank()) {
                return lesInndata(scanner, min, max);
            }
            valg = Integer.parseInt(input);
            if (valg < min || valg > max) {
                System.out.println("Ugyldig valg.");
                return lesInndata(scanner, min, max);
            }
        }
        catch (Exception e) {
            System.out.println("Ugyldig input. Skriv inn et heltall.");
            return lesInndata(scanner, min, max);
        }
        return valg;
    }
}