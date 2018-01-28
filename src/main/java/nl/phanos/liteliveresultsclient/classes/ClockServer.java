/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.phanos.liteliveresultsclient.classes;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import nl.phanos.liteliveresultsclient.gui.Clock;
import nl.phanos.liteliveresultsclient.gui.ResultsWindows;

/**
 *
 * @author woutermkievit
 */
public class ClockServer extends Thread {

    private ServerSocket server;
    private BufferedReader in;
    private DataOutputStream out;
    private Socket client;
    private Socket ouputClient;
    private String line;
    private ResultsWindows resultsWindows;
    public Clock clock;
    private boolean working = true;
    private boolean restart = false;
    private String ip="192.168.1.121";

    public ClockServer(ResultsWindows resultsWindows) {
        this.resultsWindows = resultsWindows;
        clock = new Clock();

        clock.setVisible(true);
    }

    public String getIp() {
        return ip;
    }
    
    
    public void changeIp(String newip){
        restart=true;
        ip=newip;
        working=false;
        if(!this.isAlive()){
            this.start();
            System.out.println("restart");
        }
    }
    
    @Override
    public void run() {
        restart=false;
        working = true;
        try {
            //server = new ServerSocket(5002);
            client = new Socket(ip,1202);
        } catch (IOException e) {
            System.out.println("Could not listen on port 5002");
            //System.exit(-1);
            working = false;
        }

//listenSocketSocketserver.acceptSocket
        

//listenSocketBufferedReaderclientPrintWriter
        try {
            //client = server.accept();
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            //out = new DataOutputStream(ouputClient.getOutputStream());
        } catch (IOException e) {
            System.out.println("Read failed");
            //System.exit(-1);
            working = false;
        }
        System.out.println("startReading!!!");
        while (working) {
            try {
                line = in.readLine();
                if (line != null&&line.length()>1) {
                    //out.writeBytes(line);
                    String[] split = line.split("");
                    line = "";
                    for (int i = 1; i < Math.min(split.length, 7); i++) {
                        line += split[i];
                        if (i % 2 == 0 && i + 1 != 7) {
                            line += ":";
                        }
                    }
                    if (resultsWindows != null) {
                        resultsWindows.clockLabel.setText(line);
                        resultsWindows.repaint();
                    }
                    if (clock != null) {
                        clock.clockLabel.setText(line);
                        clock.repaint();
                    }
                }
            } catch (IOException e) {
                System.out.println("Read failed");
                //System.exit(-1);
                working = false;
            }
        }
        if(restart){
        System.out.println("restart!!!");
            this.start();
        }else{
            clock.setVisible(false);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        (new ClockServer(null)).start();
    }
}
