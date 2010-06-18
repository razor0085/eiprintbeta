
package eiPrint.OBJMgmt;

import java.io.File;
import java.util.StringTokenizer;


/**
 * Veränder die extension eines Files rekursiv, kann auch für mehrere Dateien
 * im gleichen Verzeichnis angewendet werden.
 * @author dave
 */
public class DateiUmbenennen {

    static String reName = "";
    static StringTokenizer str = null;
    static File files[] = null;
     //Extension verändern von .obj zu .txt
    static final String ext = ".txt"; 
   
/**
 * Methode welche rekursiv alle Dateien im übergebenen Pfad umbennent inklusive
 * Datei-extension.
 * @param path
 */
    public void RenameFile(String path) {
        String t;
        try {
            File file = new File(path);
            files = file.listFiles();
            for (File fl : files) {
                if (fl.isDirectory()) {
                    System.out.println("Directory name:" + fl.toString());
                    RenameFile(fl.toString());
                } else {
                    System.out.println("File name:" + fl.toString());
                    if (fl.isFile()) {
                        t = fl.getName();
                        str = new StringTokenizer(t, ".");
                        if (str.hasMoreTokens()) {
                            t = str.nextToken();
                            t = fl.getParent() + "\\" + t + ext;
                            System.out.println("Changed Name " + t);
                            fl.renameTo(new File(t));
                        }
                    } else {
                        System.out.println(fl.getName() + " Not A File! ");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String path = "C:/Dokumente und Einstellungen/dave/Eigene Dateien/My Dropbox/scoula/PREN2/Pojekt/eiPrint/src/eiPrint/OBJMgmt/data";
        DateiUmbenennen rename = new DateiUmbenennen();
        rename.RenameFile(path);
    }
}
