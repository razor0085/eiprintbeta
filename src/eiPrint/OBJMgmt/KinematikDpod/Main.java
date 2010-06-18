package eiPrint.OBJMgmt.KinematikDpod;

/**
 *
 * @author Sergio Ugolini <sergio.ugolini@stud.hslu.ch>
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {


        /**
         * Vorläufige Werte unten aus der Main genommen.
         *
         * Kinematik.e =  Float.parseFloat(jTextField13.getText()); //end effector =145
         * Kinematik.f =  Float.parseFloat(jTextField14.getText()); //base =480
         * Kinematik.re =  Float.parseFloat(jTextField15.getText()); //lower arm = 300
         * Kinematik.rf =  Float.parseFloat(jTextField16.getText()); //upper arm = 150
         *
         */
        Kinematik DPOD = new Kinematik();
        DPOD.theta = 0;
        DPOD.theta1 = 30;
        DPOD.theta2 = 30;
        DPOD.theta3 = 30;
        DPOD.x0 = 0;
        DPOD.y0 = 0;
        DPOD.z0 = 0;
        DPOD.xx0 = 0;
        DPOD.yy0 = 0;
        DPOD.zz0 = 0;
        int OK = 0;

//OK = DPOD.delta_calcInverse(xx0,yy0,zz0,ttheta1,ttheta2,ttheta3);
        OK = DPOD.delta_calcForward(DPOD);

//Output
        System.out.println("Koordinates Calc FW");
        System.out.println("x0: " + DPOD.x0);
        System.out.println("y0: " + DPOD.y0);
        System.out.println("z0: " + DPOD.z0);

        DPOD.theta1 = 30.9;
        DPOD.theta2 = 30.9;
        DPOD.theta3 = 30.9;

        OK = DPOD.delta_calcForward(DPOD);

//Output
        System.out.println("Koordinates Calc FW");
        System.out.println("x0: " + DPOD.x0);
        System.out.println("y0: " + DPOD.y0);
        System.out.println("z0: " + DPOD.z0);

        OK = DPOD.delta_calcInverse(DPOD);

//Output
        System.out.println("Koordinates Calc INV");
        System.out.println("theta1: " + DPOD.theta1);
        System.out.println("theta2: " + DPOD.theta2);
        System.out.println("theta3: " + DPOD.theta3);

        DPODCalculations GUI = new DPODCalculations();
        GUI.setVisible(true);



    }
}
