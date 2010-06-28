/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eiPrint.OBJMgmt;

/**
 *
 * @author dave
 */
public class Koordinatentransformation
{

    //Attribute
    private double x;
    private double y;
    private double z;
    private double xrobot;
    private double yrobot;
    private double zrobot;
    private static final double WINKEL = -(45.0D);
    // höhe von Nullpos entspricht tz höhe des Stifts(MaschinenNullpunkt) in mm
    private static final double tz = 555.0D;
    //Stift zu Armachseeffektor 85mm -> 640-85 = 555
    // ursprünglich 640.0D
    //Ansicht von Vorne Laser auf meiner Seite, 70/2
    private static final double ty = 16.0D; //entspricht ty in mm


    public Koordinatentransformation()
    {

    }

    public Koordinatentransformation(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;        
    }

    /**
     * Umrechnung für die drehung des Koordinatensystems um 45°
     * y Koordinate bleibt unverändert da sie in beiden Systemen gleich ist.
     * Dann wird das Koordinatensystem wird um den Vektor p in y und in
     * z-Richtung verschoben.
     */
    public void koordinatensystemDrehung()
    {
        //Zuerst wird das Koordinatensystem um 45° gedreht
         xrobot = ((z * Math.sin(WINKEL)) + (x * Math.cos(WINKEL)) );
         zrobot = (( z * Math.cos(WINKEL)) - ( x *Math.sin(WINKEL)) );
        //Dann wird es um Vektor t(tx,ty,tz) veschoben
        //yrobot = y + ty;
        yrobot = y-ty;
        zrobot = zrobot-tz;
        
    }
    /**
     * @return the x
     */
    public double getX() {
        return x;
    }
    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }
    /**
     * @return the y
     */
    public double getY() {
        return y;
    }
    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }
    /**
     * @return the z
     */
    public double getZ() {
        return z;
    }
    /**
     * @param z the z to set
     */
    public void setZ(double z) {
        this.z = z;
    }
    /**
     * @return the xrobot
     */
    public double getXrobot() {
        return xrobot;
    }
    /**
     * @param xrobot the xrobot to set
     */
    public void setXrobot(double xrobot) {
        this.xrobot = xrobot;
    }
    /**
     * @return the yrobot
     */
    public double getYrobot() {
        return yrobot;
    }
    /**
     * @param yrobot the yrobot to set
     */
    public void setYrobot(double yrobot) {
        this.yrobot = yrobot;
    }
    /**
     * @return the zrobot
     */
    public double getZrobot() {
        return zrobot;
    }
    /**
     * @param zrobot the zrobot to set
     */
    public void setZrobot(double zrobot) {
        this.zrobot = zrobot;
    }
    /**
     * @return the winkel
     */
    public double getWinkel() {
        return WINKEL;
    }
  
    /**
     * @return the höhe
     */
    public double getHöhe() {
        return tz;
    }

}
