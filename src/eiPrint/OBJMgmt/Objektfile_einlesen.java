/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eiPrint.OBJMgmt;


import com.db4o.*;
//import com.db4o.f1.*;
//import eiPrint.OBJMgmt.Util;
import com.db4o.query.*;
import com.db4o.ObjectSet;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import eiPrint.OBJMgmt.KinematikDpod.Kinematik;
import eiPrint.OBJMgmt.DateiUmbenennen;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dave
 */
public class Objektfile_einlesen extends Util {

    //Attribute
    private ObjectContainer db;
    //private Koordinatentransformation kt;//nur wenn Vererbung eingesetzt
    private String pfad;
    private String filename;
    private FileReader fileIn;
    private ArrayList<Point> ptmenge; //Hällt alle Punkte des Druck Objekts
    private double xTmp, yTmp, zTmp;
    private Point point;
    private double xGes, yGes;
    private Kinematik DPOD;
    private int OK = 0;
    private ArrayList<Integer> steps;
    //from aufbereitung
    private ArrayList<Double> Xsujet;
    private ArrayList<Double> Ysujet;
    private ArrayList<Integer> colorPrint;
    private ArrayList<Integer> write;
    //private String sujet;
    private FileReader fileInSujet;
    private ArrayList<Double> xTemp ;
    private ArrayList<Double> yTemp ;
    private int oldStepMotor1;
    private int oldStepMotor2;
    private int oldStepMotor3;

    /**
     * Konstruktor
     *
     * @param filename
     * @param pfad
     */
    @SuppressWarnings("empty-statement")
    public Objektfile_einlesen(String pfad,String sujet) {
        db = Db4oEmbedded.openFile(createConfiguration(), "C:/PREN/DB.yap");

// Pfad von David        db = Db4oEmbedded.openFile(createConfiguration(), "C:/Users/david/Documents/NetBeansProjects/PRENbeta/DB.yap");
        DPOD = new Kinematik();
        point = new Point();
        ptmenge = new ArrayList<Point>();
        this.pfad = pfad;
        System.out.println("" + pfad);
//        DPOD.xx0 = 0;
//        DPOD.yy0 = 0;
//        DPOD.zz0 = 0;
        // int OK = 0;
        DPOD.theta1 = DPOD.theta2 = DPOD.theta3 = 0.0;
        steps = new ArrayList<Integer>();

        Xsujet = new ArrayList<Double>();
        Ysujet = new ArrayList<Double>();
        colorPrint = new ArrayList<Integer>();
        write = new ArrayList<Integer>();
        xTemp = new ArrayList<Double>();
        yTemp = new ArrayList<Double>();
        oldStepMotor1 = 0;
        oldStepMotor2 = 0;
        oldStepMotor3 = 0;

        try {
            fileIn = new FileReader(pfad); //.obj file
            fileInSujet = new FileReader(sujet);// sujet.txt file
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Objektfile_einlesen.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    /**
     * Das .obj File wird eingelesen und in einen Arraylist abgespeichert.
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("empty-statement")
    public void fileEinlesen() throws ClassNotFoundException, IOException {
        BufferedReader buff = new BufferedReader(fileIn);
        //Speichert den BufferInhalt temporär
        String zeile = "";
        //Lies bis nichts mehr da ist (Rückgabewert = null statt String)
        while ((zeile = buff.readLine()) != null) {
            Scanner scanner = new Scanner(zeile);
            if (zeile.startsWith("v ")) {
                //x ,y,z Koordinate isolieren, zaehler richtig nullen!!!
                int zaehler = 0;
                while ((scanner.hasNext())) {
                    if (scanner.hasNextDouble()) {
                        if (zaehler == 0) {
                            xTmp = scanner.nextDouble();
                            //System.out.println(x);
                            zaehler++;
                        } else if (zaehler == 1) {
                            yTmp = scanner.nextDouble();
                            zaehler++;
                            // System.out.println(y);
                        } else if (zaehler == 2) {
                            zTmp = scanner.nextDouble();
                            zaehler = 0;
                            //System.out.println(z);
                        }
                    } else {
                        scanner.next();
                    }
                }
                //x,y,z transformieren
                Koordinatentransformation kt = new Koordinatentransformation(xTmp, yTmp, zTmp);
                kt.koordinatensystemDrehung();
                //koord in point schreiben, wenn kt.getX() übergeben wird werden die
                //Werte unverändert in Db geschrieben, d.h. ohne Koordinatesystemdrehg.
                ptmenge.add(new Point(kt.getXrobot(), kt.getYrobot(), kt.getZrobot()));
                //System.out.println("ptmenge"+point);
            } else {
                zeile += buff.readLine();
                System.out.println("Kein v!!");
            }
        }
        //InputStream schliessen
        buff.close();
        fileIn.close();
        saveToDb();
    }

    public void saveToDb() {
        for(Point p : ptmenge)
        {
            db.store(p);
            System.out.println("Point stored: " +p);
        }
    }

    private EmbeddedConfiguration createConfiguration() {
        EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
        config.common().objectClass(Point.class).indexed(true);
        config.common().objectClass(Point.class).objectField("x").indexed(true);
        config.common().objectClass(Point.class).objectField("y").indexed(true);
        config.common().objectClass(Point.class).objectField("z").indexed(true);
        return config;
    }

    /**
     * Sucht in der Db nach einem Punkt welcher die x,y-Koordinate möglichst
     * nahe bei null ist.
     * @return
     */
    public List<Point> getOrigin() {

        List<Point> result = db.query(new Predicate<Point>() {
        double tol = 15.0;
            public boolean match(Point point) {
                return (point.getX() < (0 + tol) &&
                        point.getX() > (0 - tol) &&
                        point.getY() < (0 + tol) &&
                        point.getY() > (0 - tol));
            }
        });
        return result;
    }

    /**
     * Druckt alle punkte aus
     *
     */
    public static void retrievePoints(ObjectContainer db) {
        ObjectSet result = db.get(Point.class);
        listResult(result);
    }

    /**
     * Sucht in der DB nach Punkten welche mit den übergebenen x und y
     * Koordinate übereinstimmen
     * @param xGes
     * @param yGes
     * @return Liste aller übereinstimmenden Punkte mit 0.2mm Tolleranz
     */
    public List<Point> getZkoordinate(double xGes, double yGes) {
        this.xGes = xGes;
        this.yGes = yGes;
        List<Point> result = db.query(new Predicate<Point>() {

            public boolean match(Point point) {
                return (point.getX() < (Objektfile_einlesen.this.xGes + 0.2) &&
                        point.getX() > (Objektfile_einlesen.this.xGes - 0.2) &&
                        point.getY() < (Objektfile_einlesen.this.yGes + 0.2) &&
                        point.getY() > (Objektfile_einlesen.this.yGes - 0.2));
            }
        });
        if(result.size() <= 0){
            result = new ArrayList<Point>();
            result.add(new Point(0, 0, 0));
            //result.add(new Point(0.0, 0.0, 0.0));
        }

        return result;
    }

   /**
     * Diese Methode erwartet die Koordinaten eines Punktes im Raum und
     * berechnet wieviele Steps welcher Motor drehen muss und speichert die
     * Steps in eine ArrayList namens steps
     * @param x
     * @param y
     * @param z
     */
    public void getStepsInitailposition(double x, double y, double z,int color)
    {
        int stepsMotor1, stepsMotor2, stepsMotor3;
        int write = 1 ;
       // double ubersetzVerh = (22.0/150.0);
        double ubersVerhaltTot = (0.9/(5.4545));
        //Dpod erwarted floats
        DPOD.x0 = x;
        DPOD.y0 = y;
        DPOD.z0 = z;
        OK = DPOD.delta_calcInverse(DPOD);
        //Schrittberechnung ausgehend von Nullposition
        stepsMotor1 =  (int) (((DPOD.theta1) / ubersVerhaltTot) );
        stepsMotor2 =  (int) (((DPOD.theta2) / ubersVerhaltTot) );
        stepsMotor3 =  (int) (((DPOD.theta3) / ubersVerhaltTot) );
        oldStepMotor1 = stepsMotor1;
        oldStepMotor2 = stepsMotor2;
        oldStepMotor3 = stepsMotor3;
        steps.add(stepsMotor1);
        steps.add(stepsMotor2);
        steps.add(stepsMotor3);
        steps.add(color);
        steps.add(write);
        System.out.println(stepsMotor1);
        System.out.println(stepsMotor2);
        System.out.println(stepsMotor3);
    }
    /**
     * Diese Methode erwartet die Koordinaten eines Punktes im Raum und
     * berechnet wieviele Steps welcher Motor drehen muss und speichert die
     * Steps in eine ArrayList namens steps
     * @param x
     * @param y
     * @param z
     */
    public void getSteps(double x, double y, double z,int color) {
        int stepsMotor1, stepsMotor2, stepsMotor3;
        int write = 1 ;
        //double ubersetzVerh = (22.0/150.0);
        double ubersVerhaltTot = (0.9/(5.4545));
        //Dpod erwarted floats
        DPOD.x0 = x;
        DPOD.y0 = y;
        DPOD.z0 = z;
        OK = DPOD.delta_calcInverse(DPOD);
        //Schrittberechnung ausgehend von Nullposition
        stepsMotor1 =  (int) (((DPOD.theta1) / ubersVerhaltTot) );
        stepsMotor2 =  (int) (((DPOD.theta2) / ubersVerhaltTot) );
        stepsMotor3 =  (int) (((DPOD.theta3) / ubersVerhaltTot) );
        oldStepMotor1 = oldStepMotor1- stepsMotor1;
        oldStepMotor2 = oldStepMotor2- stepsMotor2;
        oldStepMotor3 = oldStepMotor3-stepsMotor3;
        steps.add(oldStepMotor1);
        steps.add(oldStepMotor2);
        steps.add(oldStepMotor3);
        steps.add(color);
        steps.add(write);
        System.out.println(oldStepMotor1);
        System.out.println(oldStepMotor2);
        System.out.println(oldStepMotor3);
        oldStepMotor1 = stepsMotor1;
        oldStepMotor2 = stepsMotor2;
        oldStepMotor3 = stepsMotor3;

    }


    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    protected void finalize() {
        db.close();
    }
        /**
     *
     * @throws IOException
     */
    public void sujetEinlesen() throws IOException {
//        ArrayList<Double> xTemp = new ArrayList<Double>();
//        ArrayList<Double> yTemp = new ArrayList<Double>();
        BufferedReader buff = new BufferedReader(fileInSujet);
        //Speichert den BufferInhalt temporär
        String zeile = "";
//        int i = 0;
        //Lies bis nix mehr da ist (Rückgabewert = null statt String)
        while ((zeile = buff.readLine()) != null) {
            Scanner scanner = new Scanner(zeile);
            if (!zeile.isEmpty()) {
                int count = 0;
               while ((scanner.hasNext())) {
                if (scanner.hasNextDouble()) {

                    if (count == 0) {
                        //speichere x-Koord
                        xTemp.add(scanner.nextDouble());
                        count++;
//                        System.out.println(xTemp.get(i));
                    }
                    if (count == 1) {
                        yTemp.add(scanner.nextDouble());
                        count++;
//                        System.out.println(yTemp.get(i));
                    }
                    if (count == 2) {
                        colorPrint.add(scanner.nextInt());
                        write.add(scanner.nextInt());
//                        System.out.println(colorPrint.get(i));
//                        System.out.println(colorPrint.get(i+1));
//                       ++i;
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
        for(int j=0;j<xTemp.size()-1;j++){
            Xsujet.add((xTemp.get(j+1)-xTemp.get(j)));
            Ysujet.add((yTemp.get(j+1)-yTemp.get(j)));
            System.out.print(Xsujet.get(j));
            System.out.print(Ysujet.get(j));
            System.out.println();
        }
    }



     public void generiereDruckdaten(double xStart,double yStart, double zStart)
    {
//        double xStart = 0.0;
//        double yStart = 0.0;
//        double zStart = 0.0;
        double zTemp = 0.0;
        Point pt = null;
        pt = getZkoordinate(xStart + Xsujet.get(0), yStart + Ysujet.get(0)).get(0);
        zTemp = pt.getZ();
        //für das Anfahren der Startposition
        getStepsInitailposition((xStart+Xsujet.get(0)), (yStart+Ysujet.get(0)),(zStart+zTemp),1);
        xStart = xStart + Xsujet.get(0);
        yStart = yStart + Ysujet.get(0);
        zStart = zStart + (pt.getZ());

        //für die restlichen Punkte des Sujet
        for(int i=1; i<Xsujet.size()-1;i++)
        {
            //zugehörige z-Koordinate auf bedruckbares Objekt finden
             pt = getZkoordinate(xStart+Xsujet.get(i), yStart+Ysujet.get(i)).get(0);
             zTemp = pt.getZ();
             getSteps((xStart+Xsujet.get(i)), (yStart+Ysujet.get(i)),(zStart+zTemp),1);
             xStart = xStart+Xsujet.get(i);
             yStart = yStart+Ysujet.get(i);
             zStart = zStart+(pt.getZ());
        }
    }

     public void verticalDown()
     {
         int oldStepMotor1 = 464;
        steps.add(oldStepMotor1);
        steps.add(oldStepMotor1);
        steps.add(oldStepMotor1);
        steps.add(1);
        steps.add(1);
        System.out.println(oldStepMotor1);
        System.out.println(oldStepMotor2);
        System.out.println(oldStepMotor3);
     }

     public void motor1()
     {
          int oldStepMotor1 = 50;
        steps.add(oldStepMotor1);
        steps.add(0);
        steps.add(0);
        steps.add(1);
        steps.add(1);
        System.out.println(oldStepMotor1);
        System.out.println(oldStepMotor2);
        System.out.println(oldStepMotor3);
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

         }
         for(int i=0; i<colorPrint.size();i++){
            System.out.print("Color an Write: ");
            System.out.print(colorPrint.get(i));
            System.out.print(" ");
            System.out.print(write.get(i));
            System.out.println();
         }
     }
    public ArrayList<Integer> getStepsArray()
    {
        return steps;
    }


    /**
     * @return the actualStepMotor1
     */
    public int getActualStepMotor1() {
        return oldStepMotor1;
    }

    /**
     * @param actualStepMotor1 the actualStepMotor1 to set
     */
    public void setActualStepMotor1(int actualStepMotor1) {
        this.oldStepMotor1 = actualStepMotor1;
    }

    /**
     * @return the actualStepMotor2
     */
    public int getActualStepMotor2() {
        return oldStepMotor2;
    }

    /**
     * @param actualStepMotor2 the actualStepMotor2 to set
     */
    public void setActualStepMotor2(int actualStepMotor2) {
        this.oldStepMotor2 = actualStepMotor2;
    }

    /**
     * @return the actualStepMotor3
     */
    public int getActualStepMotor3() {
        return oldStepMotor3;
    }

    /**
     * @param actualStepMotor3 the actualStepMotor3 to set
     */
    public void setActualStepMotor3(int actualStepMotor3) {
        this.oldStepMotor3 = actualStepMotor3;
    }

    public static void main(String[] args) {
//C:\Users\david\Desktop\PREN\eiPrint\src\eiPrint\OBJMgmt\data
//C:\Users\david\Desktop\PREN\eiPrint\src\eiPrint\OBJMgmt\data
        //C:/Users/david/Desktop/PREN/eiPrint/src/eiPrint/OBJMgmt/data/sujet.txt
      // DateiUmbenennen rename = new DateiUmbenennen();
       //rename.RenameFile("C:/Users/david/Desktop/PREN/eiPrint/src/eiPrint/OBJMgmt/data/");//Pfad ohne dateiname

//      try {
//            o.fileEinlesen();
//            o.saveToDb();
//           // Startpunkt suchen
           //System.out.println( o.getOrigin().get(0));
           //Point: 0.0034228569986112234 0.001378 291.4954994853929

            //Startpunkt übergeben
             //List<Point> lst = o.getZkoordinate(-182.9859, 0.2033183);
            //System.out.println(lst.size());
            //System.out.println(lst.get(0).getZ());
            //Umrechnung in Schritte
            //Kinematik DPOD = new Kinematik();
           //  o.getSteps(0.0034228569986112234, 0.001378, 291.4954994853929,1);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//        }
       //Druckbefehle_aufbereitung d = new Druckbefehle_aufbereitung("C:/Users/david/Desktop/PREN/eiPrint/src/eiPrint/OBJMgmt/data/sujet.txt");
        try {
            //C:/Users/david/Desktop/PREN/eiPrint/src/eiPrint/OBJMgmt/data
// Pfad David            Objektfile_einlesen o = new Objektfile_einlesen("C:/Users/david/Documents/NetBeansProjects/PRENbeta/trunk/src/eiPrint/OBJMgmt/data/BallonMaxV1.txt","C:/Users/david/Documents/NetBeansProjects/PRENbeta/trunk/src/eiPrint/OBJMgmt/data/sujet.txt");
Objektfile_einlesen o = new Objektfile_einlesen("C:/PREN/asdf.txt","C:/PREN/sujet.txt");
            //            System.out.println( o.getOrigin().get(0));
            //o.getOrigin().get(0).getX();
           // o.getSteps(0.0034228569986112234, 0.001378, 291.4954994853929,1);
            //output 213 213 213
            //o.getSteps(-0.014922630119031055 ,0.3060949999999991, -177.97422619470058,1);
            //output was -117 -15 -15

            o.fileEinlesen();
           o.saveToDb();
            o.sujetEinlesen();

           System.out.println(o.getOrigin().size());
           System.out.println( o.getOrigin().get(0));
           System.out.println(o.getOrigin().get(0).getX());
            // o.getSteps(-0.149 ,-9.36, -274.974,1);
            o.generiereDruckdaten(o.getOrigin().get(0).getX(), o.getOrigin().get(0).getY(), o.getOrigin().get(0).getZ());
            o.printArrayLists();
        }catch (Exception e){
          e.printStackTrace();
      }

    }

}
