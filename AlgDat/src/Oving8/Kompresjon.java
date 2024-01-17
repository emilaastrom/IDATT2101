import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Kompresjon {

    static final int SØKEBUFFER = 2048*64;

    public static byte[] lesFil(String filnavn) throws IOException {
        try {
            File fil = new File(filnavn);
            byte[] data = new byte[(int) fil.length()];
            FileInputStream fis = new FileInputStream(fil);
            fis.read(data);
            fis.close();
            return data;
        }
        catch (IOException e) {
            throw new IOException("Finner ikke en fil med navnet \" " + filnavn + "\".");
        }
    }
    
    public static void opprettFil(String filnavn, byte[] data) throws IOException {
        FileOutputStream fos = new FileOutputStream(filnavn);
        fos.write(data);
        fos.close();
    }
    
    public static byte[] oversettBytes(ArrayList<Byte> bytes) {
        byte[] resultat = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            resultat[i] = bytes.get(i);
        }
        return resultat;
    }
    
    public static ArrayList<Byte> komprimer(byte[] inputBytes) {
        ArrayList<Byte> komprimert = new ArrayList<>();
    
        int posisjon = 0;
        while (posisjon < inputBytes.length) {
            int søkePosisjon = Math.max(0, posisjon - SØKEBUFFER);
            int matchLengde = 0;
            int funnetMatchAvstand = -1;
        
            for (int i = søkePosisjon; i < posisjon; i++) {
                int lengde = 0;
                while (i + lengde < posisjon && posisjon + lengde < inputBytes.length && inputBytes[i + lengde] == inputBytes[posisjon + lengde]) {
                    lengde++;
                    if (lengde == 255) {
                        break; // Forhindre at lengden overskrider én byte
                    }
                }
        
                if (lengde > matchLengde) {
                    matchLengde = lengde;
                    funnetMatchAvstand = posisjon - i;
                }
            }
        
            if (matchLengde >= 3 && funnetMatchAvstand >= 0) {
                komprimert.add((byte) (funnetMatchAvstand >> 16));
                komprimert.add((byte) (funnetMatchAvstand >> 8));
                komprimert.add((byte) funnetMatchAvstand);
                komprimert.add((byte) matchLengde);
                posisjon += matchLengde;
            } else {
                komprimert.add((byte) 0);
                komprimert.add((byte) 0);
                komprimert.add((byte) 0);
                komprimert.add((byte) 1);
                komprimert.add(inputBytes[posisjon]);
                posisjon++;
            }
        }
        return komprimert;
    }
    
    public static byte[] dekomprimer(byte[] komprimerteBytes) throws IllegalArgumentException {
        ArrayList<Byte> dekomprimert = new ArrayList<>();
    
        int posisjon = 0;
        while (posisjon < komprimerteBytes.length) {
            if (posisjon + 2 >= komprimerteBytes.length) {
                throw new IllegalArgumentException("Feil i formatet til den komprimerte filen.");
            }
        
            int avstand = ((komprimerteBytes[posisjon] & 0xFF) << 16) |
                    ((komprimerteBytes[posisjon + 1] & 0xFF) << 8) |
                    (komprimerteBytes[posisjon + 2] & 0xFF);
            int lengde = komprimerteBytes[posisjon + 3] & 0xFF;
        
            if (avstand == 0 && lengde == 1) {
                if (posisjon + 4 >= komprimerteBytes.length) {
                    throw new IllegalArgumentException("Feil i formatet til den komprimerte filen.");
                }
                dekomprimert.add(komprimerteBytes[posisjon + 4]);
                posisjon += 5;
            } else {
                int start = dekomprimert.size() - avstand;
                if (start < 0) {
                    throw new IllegalArgumentException("Feil 'avstand' angitt i den komprimerte filen.");
                }
                for (int i = 0; i < lengde; i++) {
                    if (start + i < dekomprimert.size()) {
                        dekomprimert.add(dekomprimert.get(start + i));
                    } else {
                        throw new IllegalArgumentException("Ugyldig match-lengde angitt i den komprimerte filen.");
                    }
                }
                posisjon += 4;
            }
        }
        return oversettBytes(dekomprimert);
    }

    public static void meny(Scanner scanner) {
        System.out.println("Velg et alternativ:");
        System.out.println("0:\tAvslutt");
        System.out.println("1:\tKomprimer fil");
        System.out.println("2:\tDekomprimer fil");
        
        int valg = getInput(scanner, 0, 2);
        
        switch (valg) {
            case 0:
                System.out.println("Avslutter program.");
                System.exit(0);
                break;
            case 1:
                meny1(scanner);
                break;
            case 2:
                meny2(scanner);
                break;
            default:
                break;
        }
    }

    public static void meny1(Scanner scanner) {
        try {
            System.out.print("Skriv filnavn: ");
            String filNavn = scanner.nextLine();
            if (filNavn.isBlank()) return;

            byte[] filInnhold = lesFil(filNavn);
            ArrayList<Byte> komprimert = komprimer(filInnhold);

            String komprimertFilNavn = filNavn + ".komprimert";
            opprettFil(komprimertFilNavn, oversettBytes(komprimert));
            System.out.println("Den komprimerte filen er lagret som \"" + komprimertFilNavn + "\".");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void meny2(Scanner scanner) {
        try {
            System.out.print("Skriv filnavn: ");
            String filNavn = scanner.nextLine();
            if (filNavn.isBlank()) return;

            byte[] komprimertData = lesFil(filNavn);
            byte[] dekomprimertData = dekomprimer(komprimertData);
    
            String dekomprimertFilNavn = filNavn.replace(".komprimert", ".dekomprimert");
            opprettFil(dekomprimertFilNavn, dekomprimertData);
            System.out.println("Den dekomprimerte filen er lagret som \"" + dekomprimertFilNavn + "\".");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
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

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) meny(scanner);
    }
}
