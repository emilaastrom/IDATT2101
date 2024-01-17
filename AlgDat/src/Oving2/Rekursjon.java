package Oving2;

import java.util.Date;

public class Rekursjon {
  public static void main(String[] args) {
    Date start = new Date();
    Date slutt;
    long runder = 0;
    double tid;

    /*Uncomment to test logic
    double intOne = runMethodTwo(14, 10.1);
    System.out.println(intOne);*/

    do {
      //runMethodOne(10, 13.5);
      double result = runMethodTwo(1000000, 5);
      runder++;
      slutt = new Date();
    } while(slutt.getTime()-start.getTime() < 10000);
    System.out.println("Total tid:" + (slutt.getTime() - start.getTime()) + " ms.");
    tid = ((double) (slutt.getTime() - start.getTime()) /runder);
    System.out.println("Runder: " + runder);
    System.out.println("Tid per runde: " + tid);
  }

    /*Both functions are imited to positive integers of n,
    otherwise n will never reach base statement of n=1 when reducing by 1 */
  private static double runMethodOne(double n, double x){
    if (n == 1){
      return x;
    } else {
      return (x + runMethodOne((n-1),x));
    }
  }

  private static double runMethodTwo(double n, double x){
    if (n == 1){
      return x;
    } else if (n%2==0){
      //Even (partall)
      return (runMethodTwo((n)/2, x+x));
    } else {
      //Odd (oddetall)
      return (x+runMethodTwo((n-1)/2, x+x));
    }
  }

}