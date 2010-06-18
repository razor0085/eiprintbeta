
package GUI;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author Ruedi Odermatt
 * @version 0.0.2
 *
 */
public class StatusMonitor {

    private HashMap<Integer,String> errorMSGMap;


    public StatusMonitor()
    {
        errorMSGMap = new HashMap<Integer,String>();
        this.setMessageList();
    }



    /**
     *
     * Ruedi: Liest eine Auflistung zu erwartender Fehlermeldungen
     * aus der Konfigurationsdate eiPrintFMs.ini und speichert
     * diese in die Map errorMSGMap
     *
     */
    private void setMessageList()
    {
        int max = 3000;
        int i;
        String stringMax="";
        String errorMessage="";
        
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream("eiPrintFMs.ini"));

            stringMax =  prop.getProperty("maxErrorMessageCode");
            if(stringMax!=null)
            {
                max=Integer.valueOf(stringMax).intValue();
            }

            for (i=0; i<=max; i++)
            {
                errorMessage=prop.getProperty(""+i+"");
                if(errorMessage != null)
                {
                    this.errorMSGMap.put(i, errorMessage);
                }
            }

            this.printAllMSGNumbers();
        }
        
        catch (IOException ex)
        {
            ex.printStackTrace();
        }


    }



    /**
     *
     * Ruedi: Ruft die Methode getState() von MCCmdRsp auf
     * und verbindet den erhaltenen int-Wert mit einer Liste,
     * die innerhalb der Klasse StatusMonitor gehalten wird
     *
     */
    public String showState()
    {
        

        return ("Muss noch programmiert werden");
    }




    /**
     *
     * Soll alle Vorgänge der Maschine loggen
     * Wird durch Gion gemacht.
     *
     */
    public void dataLogger()
    {

    }


    /**
     *
     * Ruedi: Die drei folgenden Funktionen
     * - insertValueErrorMSGMap
     * - printValueErrorMSGMap
     * - printAllMSGNumbers
     * sind für die Verwaltung der errorMSGMap notwendig
     *
     */

    private void insertValueErrorMSGMap(Integer key, String value)
    {
        errorMSGMap.put(key, value);
    }

    private void printValueErrorMSGMap(Integer key)
    {
        System.out.println(errorMSGMap.get(key));
    }


    // Dient für Wartungsarbeiten.
    private void printAllMSGNumbers()
    {
        Set<Integer> setMap = errorMSGMap.keySet();
        for (Integer key : setMap) {
            System.out.println(key);
        }
    }





}
