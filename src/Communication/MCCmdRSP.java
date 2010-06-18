package Communication;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author padrun
 */
// derived from SUN's examples in the javax.comm package
import GUI.eiPrintManager;
import java.io.*;
import java.util.*;
//import javax.comm.*; // for SUN's serial/parallel port libraries
import gnu.io.*; // for rxtxSerial library
import java.util.logging.Level;
import java.util.logging.Logger;

public class MCCmdRSP implements SerialPortEventListener {

    volatile int result;
    static CommPortIdentifier portId;
    static CommPortIdentifier saveportId;
    static Enumeration portList;
    InputStream inputStream;
    SerialPort serialPort;
    Thread readThread;
    //static String        messageString = "that's a,test!";
    static byte print;
    static byte testByes;
    static OutputStream outputStream;
    static boolean outputBufferEmptyFlag = false;
    // Constants
    static final int CMD_STOP = 90;
    static final int CMD_NEUTRAL = 40;
    static final int CMD_STARTLASER = 30;
    static final int CMD_PRINT = 60;

   public void startLaser() throws IOException  {

      byte[] laser = new byte[2];
      laser[0] = CMD_STARTLASER;

      // init port to write
      this.initwritetoport();

      //write command
      outputStream.write(laser);

      System.out.println("start Laser " + laser[0] + serialPort.getName());

      if (!readACK(CMD_STARTLASER)) {
          throw new IOException("No ACK received for CMD: Start laser");
      }
   }

   public void initwritetoport() {
      // initwritetoport() assumes that the port has already been opened and
      //    initialized by "public nulltest()"

      try {
         // get the outputstream
         outputStream = serialPort.getOutputStream();
      } catch (IOException e) {}

      try {
         // activate the OUTPUT_BUFFER_EMPTY notifier
        // serialPort.notifyOnOutputEmpty(true);
      } catch (Exception e) {
         System.out.println("Error setting event notification");
         System.out.println(e.toString());
         System.exit(-1);
      }

   }

   public void print() throws IOException  {

       byte[] printOb = new byte[1];
       printOb[0] = CMD_PRINT;

       // init port to write
      this.initwritetoport();
      
      outputStream.write(printOb);

      System.out.println("print" + printOb[0]+ serialPort.getName());
   }


   public void neutralPos() throws IOException
   {
       byte[] neutral = new byte[1];
       neutral[0] = CMD_NEUTRAL;

       // init port to write
      this.initwritetoport();

      outputStream.write(neutral);

      System.out.println("neutral Position:" + neutral[0]+ serialPort.getName());

      if (!readACK(CMD_NEUTRAL)) {
          throw new IOException("No ACK received for CMD: Neutral position");
      }
   }

   private boolean readACK(int command) {

       int retries = 0;

       while (result != (command+1) && retries < 30){
            try {
                //timeout
                Thread.sleep(100);
                retries++;
            } catch (InterruptedException ex) {
                Logger.getLogger(MCCmdRSP.class.getName()).log(Level.SEVERE, null, ex);
            }
       }


        System.out.println("Read acknowledge signal: "+ result);

        if ( result == (command+1) )
        {
            return true;
        }
        else
        {
            return false;
        }
   }

   // Method for stop print process
   public void stop() throws IOException
   {
       byte[] stop = new byte[1];
       stop[0] = CMD_STOP;

       // init port to write
      this.initwritetoport();

      outputStream.write(stop);

      System.out.println("Stop:" + stop[0]+ serialPort.getName());


      if (!readACK(CMD_STOP)) {
          throw new IOException("No ACK received for CMD: Neutral position");
      }
   }



   public void writetoport(byte[][] printArray) throws IOException {

       for(int i = 0;i<printArray.length;i++){

           print();

           for(int j = 0;j<5;j++){
                try {
                    outputStream.write(printArray[i][j]);
                    System.out.println("Writing " + printArray[i][j] + " to " + serialPort.getName());
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                    
           }
           System.out.println("i:" + i + "-----------------------------------");
           if ( !readACK(CMD_PRINT) )
           {
                //System.out.println("no ACK(61) from MC");
                //i--;
                break;
           }
       }
   }


   public MCCmdRSP () {
       boolean           portFound = false;
      String             defaultPort = "";

      // determine the name of the serial port on several operating systems
      String osname = System.getProperty("os.name","").toLowerCase();
      if ( osname.startsWith("windows") ) {
         // windows
         defaultPort = "COM1";
      } else if (osname.startsWith("linux")) {
         // linux
        defaultPort = "/dev/ttyS0";
      } else if ( osname.startsWith("mac") ) {
         // mac
         defaultPort = "????";
      } else {
         System.out.println("Sorry, your operating system is not supported");
      }

      System.out.println("Set default port to "+ defaultPort);

		// parse ports and if the default port is found, initialized the reader
      portList = CommPortIdentifier.getPortIdentifiers();
      while (portList.hasMoreElements()) {
         portId = (CommPortIdentifier) portList.nextElement();
         if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
            if (portId.getName().equals(defaultPort)) {
               System.out.println("Found port: "+defaultPort);
               portFound = true;
               // init reader thread
               break;

            }
         }

      }
      if (!portFound) {
         System.out.println("port " + defaultPort + " not found.");
      }

      // initalize serial port
      try {
         serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
      } catch (PortInUseException e) {
        System.out.println(e);
      }

      try {
          inputStream = new InputStream() {

                @Override
                public int read() throws IOException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
          };
         inputStream = serialPort.getInputStream();
      } catch (IOException e) {
        System.out.println(e);
      }

      try {
         serialPort.addEventListener(this);
      } catch (TooManyListenersException e) {}

      // activate the DATA_AVAILABLE notifier
      serialPort.notifyOnDataAvailable(true);

      try {
         // set port parameters

          //serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
          serialPort.setSerialPortParams(62500, SerialPort.DATABITS_8,
          SerialPort.STOPBITS_1,
                     SerialPort.PARITY_NONE);
      } catch (UnsupportedCommOperationException e) {}

      // start the read thread
      //readThread = new Thread(this);
      //readThread.start();

   }

   public void serialEvent(SerialPortEvent event) {
      switch (event.getEventType()) {
      case SerialPortEvent.BI:
      case SerialPortEvent.OE:
      case SerialPortEvent.FE:
      case SerialPortEvent.PE:
      case SerialPortEvent.CD:
      case SerialPortEvent.CTS:
      case SerialPortEvent.DSR:
      case SerialPortEvent.RI:
      case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
         break;
      case SerialPortEvent.DATA_AVAILABLE:
         // we get here if data has been received
         byte[] readBuffer = new byte[2];
         try {
            // read data
            while (inputStream.available() > 0) {
               int numBytes = inputStream.read(readBuffer);
              System.out.println("numBytes: "+ numBytes);
            }
            // read one byte. Max number
            result = (int)readBuffer[0];


           System.out.println("Result : "+ result);
                     

         } catch (IOException e) {
            System.out.println(e);
         }

         break;
      }
   }

}

