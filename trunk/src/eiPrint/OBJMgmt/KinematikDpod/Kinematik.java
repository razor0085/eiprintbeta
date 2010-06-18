
package eiPrint.OBJMgmt.KinematikDpod;

/**
 *Deltapod Kinematik
 *
 */
public class Kinematik {

 //Constants
 // robot geometry
 public static double e = 145.0;     // end effector
 public static double f = 480.0;     // base
 public static double re = 300.0;    //lower arm
 public static double rf = 150.0;    //upper arm
 // trigonometric constants
 private static final double sqrt3 = Math.sqrt(3.0);
 private static final double pi = 3.141592653;    // PI
 private static final double sin120 = sqrt3/2.0;
 private static final double cos120 = -0.5;
 private static final double tan60 = sqrt3;
 private static final double sin30 = 0.5;
 private static final double tan30 = 1/sqrt3;

 
 //object variables
 public double theta = 0;
 public double theta1 = 0;
 public double theta2 = 0;
 public double theta3 = 0;
 public double x0;
 public double y0;
 public double z0;
 public double xx0; //helper variables only used in delta_calcAngleYZ
 public double yy0; // "
 public double zz0; // "



 // forward kinematics: (theta1, theta2, theta3) -> (x0, y0, z0)
 // returned status: 0=OK, -1=non-existing position
 public int delta_calcForward(Kinematik DPOD)
 //public int delta_calcForward(Double ttheta1, Double ttheta2, Double ttheta3, Double xx0, Double yy0, Double zz0)
    {
     double t = (f-e)*tan30/2;
     double dtr = pi/(float)180.0;

     DPOD.theta1 *= dtr;
     DPOD.theta2 *= dtr;
     DPOD.theta3 *= dtr;

     double y1 = -(t + rf*Math.cos(DPOD.theta1));
     double z1 = -rf*Math.sin(DPOD.theta1);

     double y2 = (t + rf*Math.cos(DPOD.theta2))*sin30;
     double x2 = y2*tan60;
     double z2 = -rf*Math.sin(DPOD.theta2);

     double y3 = (t + rf*Math.cos(DPOD.theta3))*sin30;
     double x3 = -y3*tan60;
     double z3 = -rf*Math.sin(DPOD.theta3);

     double dnm = (y2-y1)*x3-(y3-y1)*x2;

     double w1 = y1*y1 + z1*z1;
     double w2 = x2*x2 + y2*y2 + z2*z2;
     double w3 = x3*x3 + y3*y3 + z3*z3;

     // x = (a1*z + b1)/dnm
     double a1 = (z2-z1)*(y3-y1)-(z3-z1)*(y2-y1);
     double b1 = -((w2-w1)*(y3-y1)-(w3-w1)*(y2-y1))/2.0;

     // y = (a2*z + b2)/dnm;
     double a2 = -(z2-z1)*x3+(z3-z1)*x2;
     double b2 = ((w2-w1)*x3 - (w3-w1)*x2)/2.0;

     // a*z^2 + b*z + c = 0
     double a = a1*a1 + a2*a2 + dnm*dnm;
     double b = 2*(a1*b1 + a2*(b2-y1*dnm) - z1*dnm*dnm);
     double c = (b2-y1*dnm)*(b2-y1*dnm) + b1*b1 + dnm*dnm*(z1*z1 - re*re);

     // discriminant
     double d = b*b - (float)4.0*a*c;
     if (d < 0) return -1; // non-existing point

     DPOD.z0 = -(double)0.5*(b+Math.sqrt(d))/a;
     DPOD.x0 = (a1*DPOD.z0 + b1)/dnm;
     DPOD.y0 = (a2*DPOD.z0 + b2)/dnm;

    
     return 0;
 }

 // inverse kinematics
 // helper functions, calculates angle theta1 (for YZ-pane)
public int delta_calcAngleYZ(Kinematik DPOD)
    {
     double y1 = -0.5 * 0.57735 * f; // f/2 * tg 30
     DPOD.yy0 -= 0.5 * 0.57735    * e;    // shift center to edge
     // z = a + b*y
     double a = (DPOD.xx0*DPOD.xx0 + DPOD.yy0*DPOD.yy0 + DPOD.zz0*DPOD.zz0 +rf*rf - re*re - y1*y1)/(2*DPOD.zz0);
     double b = (y1-DPOD.yy0)/DPOD.zz0;
     // discriminant
     double d = -(a+b*y1)*(a+b*y1)+rf*(b*b*rf+rf);
     if (d < 0) return -1; // non-existing point
     double yj = (y1 - a*b - Math.sqrt(d))/(b*b + 1); // choosing outer point
     double zj = a + b*yj;
     DPOD.theta = 180.0*Math.atan(-zj/(y1 - yj))/pi + ((yj>y1)?180.0:0.0);

     return 0;
    }

// inverse kinematics: (x0, y0, z0) -> (theta1, theta2, theta3)
 // returned status: 0=OK, -1=non-existing position
 public int delta_calcInverse(Kinematik DPOD)
 //public int delta_calcInverse(Double xx0, Double yy0, Double zz0, Double ttheta1, Double ttheta2, Double ttheta3) {
 {
     DPOD.theta1 = DPOD.theta2 = DPOD.theta3 = 0.0;

     //theta1 = theta2 = theta3 = 0;
     //fill helper variables 4 funct call
     DPOD.xx0 = DPOD.x0;
     DPOD.yy0 = DPOD.y0;
     DPOD.zz0 = DPOD.z0;
     DPOD.theta = DPOD.theta1;
     int status = delta_calcAngleYZ(DPOD);
     DPOD.theta1 = DPOD.theta;
     if (status == 0){
     //fill helper variables 4 funct call // rotate coords to +120 deg
     DPOD.xx0 = DPOD.x0*cos120 + DPOD.y0*sin120;
     DPOD.yy0 = DPOD.y0*cos120 - DPOD.x0*sin120;
     DPOD.zz0 = DPOD.z0;
     DPOD.theta = DPOD.theta2;
     status = delta_calcAngleYZ(DPOD);
     DPOD.theta2 = DPOD.theta;
     }
     if (status == 0){
     //fill helper variables 4 funct call  // rotate coords to -120 deg
     DPOD.xx0 = DPOD.x0*cos120 - DPOD.y0*sin120;
     DPOD.yy0 = DPOD.y0*cos120 + DPOD.x0*sin120;
     DPOD.zz0 = DPOD.z0;
     DPOD.theta = DPOD.theta3;
     status = delta_calcAngleYZ(DPOD);
     DPOD.theta3 = DPOD.theta;
     }

     return status;
 }

}
