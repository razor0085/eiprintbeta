
package eiPrint.OBJMgmt;

import eiPrint.OBJMgmt.KinematikDpod.Kinematik;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dave
 */
public class Druckbefehle_aufbereitung {
    private FileReader fileIn;
    private String pfad;
    private ArrayList<Double> Xsujet;
    private ArrayList<Double> Ysujet;
    private ArrayList<Integer> colorPrint;
    private ArrayList<Integer> write;
    private double xGes, yGes;
    private Kinematik DPOD;
    private int OK = 0;
    private ArrayList steps;

    /**
     *
     * @param pfad
     */
    public Druckbefehle_aufbereitung(String pfad)
    {
       // super("C:/Users/david/Desktop/PREN/eiPrint/src/eiPrint/OBJMgmt/data/BallonMaxV1.txt","");
        Xsujet = new ArrayList<Double>();
        Ysujet = new ArrayList<Double>();
        colorPrint = new ArrayList<Integer>();
        write = new ArrayList<Integer>();
        this.pfad = pfad ;
        try {
            fileIn = new FileReader(pfad);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Druckbefehle_aufbereitung.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


  
    /**
     *
     * @throws IOException
     */
    public void sujetEinlesen() throws IOException {
        ArrayList<Double> xTmp = new ArrayList<Double>();
        ArrayList<Double> yTmp = new ArrayList<Double>();
        BufferedReader buff = new BufferedReader(fileIn);
        //Speichert den BufferInhalt temporär
        String zeile = "";
        //int i = 0;
        //Lies bis nix mehr da ist (Rückgabewert = null statt String)
        while ((zeile = buff.readLine()) != null) {
            Scanner scanner = new Scanner(zeile);
            if (!zeile.isEmpty()) {
                //x ,y,z Koordinate isolieren, zaehler richtig nullen!!!
                int count = 0;
                
               while ((scanner.hasNext())) {
                if (scanner.hasNextDouble()) {

                    if (count == 0) {
                        //speichere x-Koord
                        xTmp.add(scanner.nextDouble());
                        count++;
                        //System.out.println(Xsujet.get(i));
                    }
                    if (count == 1) {
                        yTmp.add(scanner.nextDouble());
                        count++;
                        //System.out.println(Ysujet.get(i));
                    }
                    if (count == 2) {
                        colorPrint.add(scanner.nextInt());
                        write.add(scanner.nextInt());
//                        System.out.println(colorPrint.get(i));
//                        System.out.println(colorPrint.get(i+1));
//                        ++i;
                    } else {
                        //scanner.next();
                    }
                } else {
                    zeile += buff.readLine();
                    count = 0;
                }
            }
            } else {
                zeile += buff.readLine();
                //System.out.println("Neue Zeile");
            }
        }
        for(int i=0;i<Xsujet.size();i++){
            Xsujet.add((xTmp.get(i+1)-xTmp.get(i)));
            Ysujet.add((yTmp.get(i+1)-yTmp.get(i)));
        }
    }


    /**
     *  simple Methode um zu überprüfen wie ArrayLists abgefüllt wurden
     */
    public void printArrayLists()
     {

         for(int i=0;i<Xsujet.size();i++){
            System.out.print("X: ");
            System.out.print(Xsujet.get(i));
            System.out.print(" ");
            System.out.print("Y: ");
            System.out.print(Ysujet.get(i));
            System.out.print(" ");
            System.out.print("Color an Write: ");
            System.out.print(colorPrint.get(i));
            System.out.print(" ");
            System.out.print(write.get(i));
            System.out.println();
         }
     }


//    public void generiereDruckdaten()
//    {
//        for(int i=0; i<Xsujet.size();i++)
//        {
//             getSteps(Xsujet.get(i), Ysujet.get(i), (getZkoordinate(Xsujet.get(i),Ysujet.get(i)).get(0).getZ()),1);
//        }
//    }



  
    public static void main(String[] args) {

        Druckbefehle_aufbereitung d = new Druckbefehle_aufbereitung("C:/Users/david/Desktop/PREN/eiPrint/src/eiPrint/OBJMgmt/data/sujet.txt");
        try {
            d.sujetEinlesen();
            System.out.println("success!!!");
            d.printArrayLists();
        } catch (IOException ex) {
            Logger.getLogger(Druckbefehle_aufbereitung.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }



}
