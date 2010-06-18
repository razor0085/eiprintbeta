/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Communication;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.lang.Math;

/**
 * Verteilt Schritte anhand aList gleichmÃ¤ssig auf erzeugtes Array
 * @author sugolini
 */
public class Parsedata {

    public byte[][] printArray;

    public Parsedata() {
    }

    public void parsedata(ArrayList<Integer> alist) {

        int m1 = 0, m2 = 0, m3 = 0, col = 0, write = 0;
        int size = 0;

        for (int x = 0; x < alist.size(); x += 5) {

            m1 = Math.abs(alist.get(x));
            m2 = Math.abs(alist.get(x + 1));
            m3 = Math.abs(alist.get(x + 2));
            col = alist.get(x + 3);
            write = alist.get(x + 4);

            size += Math.max(m3, Math.max(m1, m2));
        }
        printArray = new byte[size][5];
        int offset = 0;

        for (int x = 0; x < alist.size(); x += 5) {

            m1 = alist.get(x);
            m2 = alist.get(x + 1);
            m3 = alist.get(x + 2);
            col = alist.get(x + 3);
            write = alist.get(x + 4);

            int linesize = Math.max(Math.abs(m3), Math.max(Math.abs(m1), Math.abs(m2)));
            boolean Flag = false;

            // Fall 1 Wert M1 am grÃ¶ssten
            if (linesize == Math.abs(m1)) {
                Flag = true;
                for (int y = offset; y < offset + linesize; y++) {
                    printArray[y][3] = (byte) col;
                    printArray[y][4] = (byte) write;
                    if (m1 > 0) {
                        printArray[y][0] = 1;
                    } else {
                        printArray[y][0] = -1;
                    }
                    if (m2 != 0) {
                        if ((int) ((y + 1 - offset) % ((float) m1 / (float) m2)) == 0) {
                            if (m2 > 0) {
                                printArray[y][1] = 1;
                            } else {
                                printArray[y][1] = -1;
                            }
                        }
                    } else {
                        printArray[y][1] = 0;
                    }
                    if (m3 != 0) {
                        if ((int) ((y + 1 - offset) % ((float) m1 / (float) m3)) == 0) {

                            if (m3 > 0) {
                                printArray[y][2] = 1;
                            } else {
                                printArray[y][2] = -1;
                            }
                        }
                    } else {
                        printArray[y][2] = 0;
                    }

                }
            }
            // Fall 2 Wert M2 am grÃ¶ssten
            if ((linesize == Math.abs(m2)) && (Flag == false)) {
                Flag = true;
                for (int yy = offset; yy < offset + linesize; yy++) {
                    printArray[yy][3] = (byte) col;
                    printArray[yy][4] = (byte) write;
                    if (m2 > 0) {
                        printArray[yy][1] = 1;
                    } else {
                        printArray[yy][1] = -1;
                    }
                    if (m1 != 0) {
                        if ((int) ((yy + 1 - offset) % ((float) m2 / (float) m1)) == 0) {
                            if (m1 > 0) {
                                printArray[yy][0] = 1;
                            } else {
                                printArray[yy][0] = -1;
                            }
                        }
                    } else {
                        printArray[yy][0] = 0;
                    }
                    if (m3 != 0) {
                        if ((int) ((yy + 1 - offset) % ((float) m2 / (float) m3)) == 0) {
                            if (m3 > 0) {
                                printArray[yy][2] = 1;
                            } else {
                                printArray[yy][2] = -1;
                            }
                        }
                    } else {
                        printArray[yy][2] = 0;
                    }
                }

            }
            // Fall 3 Wert M3 am grÃ¶ssten
            if ((linesize == Math.abs(m3)) && (Flag == false)) {
                Flag = true;
                for (int yyy = offset; yyy < offset + linesize; yyy++) {
                    printArray[yyy][3] = (byte) col;
                    printArray[yyy][4] = (byte) write;
                    if (m3 > 0) {
                        printArray[yyy][2] = 1;
                    } else {
                        printArray[yyy][2] = -1;
                    }
                    if (m1 != 0) {
                        if ((int) ((yyy + 1 - offset) % ((float) m3 / (float) m1)) == 0) {
                            if (m1 > 0) {
                                printArray[yyy][0] = 1;
                            } else {
                                printArray[yyy][0] = -1;
                            }
                        }
                    } else {
                        printArray[yyy][0] = 0;
                    }
                    if (m2 != 0) {
                        if ((int) ((yyy + 1 - offset) % ((float) m3 / (float) m2)) == 0) {
                            if (m2 > 0) {
                                printArray[yyy][1] = 1;
                            } else {
                                printArray[yyy][1] = -1;
                            }
                        }

                    } else {
                        printArray[yyy][1] = 0;
                    }
                }
            }


            //Fehlerkompensation fÃ¼llt durch die Rundung verlorengegangene Schritte auf
            int countm1, countm2, countm3;
            countm1 = countm2 = countm3 = 0;

            for (int y = offset; y < offset + linesize; y++) {
                if (printArray[y][0] != 0) {
                    countm1++;
                }
                if (printArray[y][1] != 0) {
                    countm2++;
                }
                if (printArray[y][2] != 0) {
                    countm3++;
                }
            }
//                System.out.println("M1: " + m1 + " Count: " + countm1);
//                System.out.println("M2: " + m2 + " Count: " + countm2);
//                System.out.println("M3: " + m3 + " Count: " + countm3);

            while (Math.abs(m1) != countm1) {
                for (int y = offset; y < offset + linesize; y++) {
                    if (printArray[y][0] == 0) {
                        if (m1 > 0) {
                            printArray[y][0] = 1;
                        } else {
                            printArray[y][0] = -1;
                        }
                        countm1++;

                    }
                    break;
                }
            }
            while (Math.abs(m2) != countm2) {
                for (int y = offset; y < offset + linesize; y++) {
                    if (printArray[y][1] == 0) {
                        if (m2 > 0) {
                            printArray[y][1] = 1;
                        } else {
                            printArray[y][1] = -1;
                        }
                        countm2++;

                    }
                    break;
                }
            }
            while (Math.abs(m3) != countm3) {
                for (int y = offset; y < offset + linesize; y++) {
                    if (printArray[y][2] == 0) {
                        if (m3 > 0) {
                            printArray[y][2] = 1;
                        } else {
                            printArray[y][2] = -1;
                        }
                        countm3++;

                    }
                    break;
                }
            }

            //nur ausgabe
//                countm1 = countm2 = countm3 = 0;
//
//                for (int y = offset; y < offset + linesize; y++) {
//                    if (printArray[y][0] != 0) {
//                        countm1++;
//                    }
//                    if (printArray[y][1] != 0) {
//                        countm2++;
//                    }
//                    if (printArray[y][2] != 0) {
//                        countm3++;
//                    }
//                }
//                System.out.println("*M1: " + m1 + " Count: " + countm1);
//                System.out.println("*M2: " + m2 + " Count: " + countm2);
//                System.out.println("*M3: " + m3 + " Count: " + countm3);



            offset += linesize;
        }
//        System.out.println("ArrayFertig ");
    }

    public static void main(String[] args) {
        ArrayList alist = new ArrayList();
        Parsedata PData = new Parsedata();
        alist.add(1);
        alist.add(-1);
        alist.add(1);
        alist.add(0);
        alist.add(0);

        alist.add(3);
        alist.add(-4);
        alist.add(2);
        alist.add(1);
        alist.add(0);

        alist.add(2);
        alist.add(3);
        alist.add(-4);
        alist.add(2);
        alist.add(0);

//    alist.add(5);
//    alist.add(66);
//    alist.add(44);
//    alist.add(1);
//    alist.add(1);
//
//    alist.add(44);
//    alist.add(20);
//    alist.add(9);
//    alist.add(2);
//    alist.add(1);

        PData.parsedata(alist);
        //Ausgabe Array
//        for (int i = 0; i < PData.printArray.length; i++) {
//            for (int j = 0; j < PData.printArray[i].length; j++) {
//                System.out.print(PData.printArray[i][j] + " ");
//            }
//            System.out.println();
//        }



        MCCmdRSP mccom = new MCCmdRSP();
        try {
            mccom.writetoport(PData.printArray);
        } catch (IOException ex) {
            Logger.getLogger(Parsedata.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
