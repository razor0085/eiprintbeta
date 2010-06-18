/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eiPrint.OBJMgmt;

/**
 *
 * @author dave
 */
public class Point {

    private double x;
    private double y;
    private double z;
    private Point point;


    public Point()
    {

    }

    public Point(Point point)
    {
        this.point = point;
    }
    public Point(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;

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
 * Überschreibt die Methode toString damit ein verständlicher String zurück-
 * gegeben wird,
 * @return Punkt mit x,y,z
 */

    @Override
    public String toString()
    {
        return "Point: "+x+ " " + y+ " " + z;
    }

    public static void main(String[] args)
    {
    }
}
