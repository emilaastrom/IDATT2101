package Oving4;

//Oppgave 3
public class ListerTrærOppg3 {
    TreNode rot;

    public ListerTrærOppg3() {
        rot = null;
    }
    
    //rekursiv metode for å traversere tre og regne ut fasit
    private float getResult(TreNode n) {
        if (n != null) {
            if (n.element == '+') {
                return getResult(n.venstre) + getResult(n.høyre);
            }
            else if (n.element == '-') {
                return getResult(n.venstre) - getResult(n.høyre);
            }
            else if (n.element == '*') {
                return getResult(n.venstre) * getResult(n.høyre);
            }
            else if (n.element == '/') {
                return getResult(n.venstre) / getResult(n.høyre);
            }
            else {
                return Character.getNumericValue(n.element);
            }
        }
        else {
            return 0;
        }
    }

    //rekursiv metode for å traversere tre og sette den sammen til en String
    private String getResultString(TreNode n) {
        if (n != null) {
            if (n.element == '+') {
                return "("+getResultString(n.venstre) +"+"+ getResultString(n.høyre)+")";
            }
            else if (n.element == '-') {
                return "("+getResultString(n.venstre) +"-"+ getResultString(n.høyre)+")";
            }
            else if (n.element == '*') {
                return "("+getResultString(n.venstre) +"*"+ getResultString(n.høyre)+")";
            }
            else if (n.element == '/') {
                return "("+getResultString(n.venstre) +"/"+ getResultString(n.høyre)+")";
            }
            else {
                return String.valueOf(n.element);
            }
        }
        else {
            return "";
        }
    }

    //generell getResult metode for treroten
    public float getResult() {
        return getResult(rot);
    }

    
    //generell getResultString metode for treroten
    public String getResultString() {
        String result = getResultString(rot);
        return result.substring(1,result.length()-1);
    }

    public boolean tomt() {
        return rot == null;
    }


    //main metode
    public static void main(String[] args) {
        //initialiserer tre
        ListerTrærOppg3 tre = new ListerTrærOppg3();
        TreNode treNode1 = new TreNode('/', null, null);
        TreNode treNode2 = new TreNode('*', null, null);
        TreNode treNode3 = new TreNode('3', null, null);
        TreNode treNode4 = new TreNode('+', null, null);
        TreNode treNode5 = new TreNode('2', null, null);
        TreNode treNode6 = new TreNode('4', null, null);
        TreNode treNode7 = new TreNode('-', null, null);
        TreNode treNode8 = new TreNode('7', null, null);
        TreNode treNode9 = new TreNode('*', null, null);
        TreNode treNode10 = new TreNode('2', null, null);
        TreNode treNode11 = new TreNode('2', null, null);

        //Kobler sammen nodene
        tre.rot = treNode1;
        treNode1.venstre = treNode2;
        treNode2.venstre = treNode3;
        treNode2.høyre = treNode4;
        treNode4.venstre = treNode5;
        treNode4.høyre = treNode6;
        treNode1.høyre = treNode7;
        treNode7.venstre = treNode8;
        treNode7.høyre = treNode9;
        treNode9.venstre = treNode10;
        treNode9.høyre = treNode11;

        //skriver ut String og fasit
        System.out.println(tre.getResultString() +" = "+ tre.getResult());
    }
}

//Tre node klassen
class TreNode {
    char element;
    TreNode venstre;
    TreNode høyre;

    public TreNode(char e, TreNode v, TreNode h) {
        element = e;
        venstre = v;
        høyre = h;
    }
}

