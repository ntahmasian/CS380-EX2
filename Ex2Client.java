/**
 * Created by Narvik on 4/14/17.
 */

 git remote add origin https://github.com/ntahmasian/CS380-EX2.git

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.CRC32;

public class Ex2Client {

    public static void main ( String [] args ){

        try (Socket socket = new Socket("codebank.xyz", 38102)) {

            int ServerFirstHalfAByte = 0, serverSecondHalfAByte = 0, checkCRC = 0;
            byte serverMassageArray[] = new byte[100];
            InputStream getIS = socket.getInputStream();
            OutputStream outStream = socket.getOutputStream();
            CRC32 crc = new CRC32();
            long generateCRC;


            // showes we are connected to the server
            System.out.println("Connected to server.\nReceived bytes:");

            for(int i=0; i < 100; i++) {

                ServerFirstHalfAByte = getIS.read();    //  0x05 = 5  0101
                serverSecondHalfAByte = getIS.read();   //  0X0A = 10 1010    ----> 0x5A = 0101 1010 = 90
                ServerFirstHalfAByte = ServerFirstHalfAByte << 4;

                serverMassageArray[i] = (byte)(ServerFirstHalfAByte+serverSecondHalfAByte);

                if (i == 0){
                    System.out.print("  ");
                }
                if (i == 10 || i == 20 || i == 30 || i == 40 || i == 50 || i == 60 || i == 70 || i == 80 || i == 90 ){
                    System.out.println();
                    System.out.print("  ");
                }

                System.out.printf("%02X",serverMassageArray[i]);

            }
            crc.update(serverMassageArray);
            generateCRC = crc.getValue();
            System.out.print("\nGenerated CRC32: ");
            System.out.printf("%02X.\n", generateCRC);

            for(int i=3; i >= 0; i--) {
                outStream.write((int)generateCRC >> (8*i));
            }

            //Check the CRC32
            checkCRC = getIS.read();
            if(checkCRC == 1) {
                System.out.println("Response good.");
            } else {
                System.out.println("Response bad.");
            }
            System.out.println("Disconnected from server.");

        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}