/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.phanos.liteliveresultsclient.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JLabel;
import nl.phanos.liteliveresultsclient.gui.Clock;
import nl.phanos.liteliveresultsclient.gui.ResultsWindows;

/**
 *
 * @author woutermkievit
 */
public class ClockServer extends Thread {

    public Socket server;
    public BufferedReader in;
    private Socket client;
    private Socket ouputClient;
    private String line;
    private ResultsWindows resultsWindows;
    private Clock clock;
    private boolean working = true;

    public ClockServer(ResultsWindows resultsWindows) {
        this.resultsWindows = resultsWindows;
        clock = new Clock();

        clock.setVisible(true);
    }

    @Override
    public void run() {
        try {
            server = new Socket("192.168.1.103",5001);
        } catch (IOException e) {
            System.out.println("Could not listen on port 5001");
            //System.exit(-1);
            working = false;
        }

//listenSocketSocketserver.acceptSocket
        

//listenSocketBufferedReaderclientPrintWriter
        try {
            in = new BufferedReader(new InputStreamReader(
                    server.getInputStream()));
        } catch (IOException e) {
            System.out.println("Read failed");
            //System.exit(-1);
            working = false;
        }
        System.out.println("startReading!!!");
        while (working) {
            try {
                line = in.readLine();
                if (line != null) {
                    String[] split = line.split("");
                    line = "";
                    for (int i = 1; i < 7; i++) {
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
        clock.setVisible(false);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        (new ClockServer(null)).start();
    }
}
