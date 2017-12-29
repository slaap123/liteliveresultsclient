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

    public ServerSocket server;
    public BufferedReader in;
    private Socket client;
    private String line;
    private ResultsWindows resultsWindows;
    private Clock clock;

    public ClockServer(ResultsWindows resultsWindows) {
        this.resultsWindows = resultsWindows;
        clock = new Clock();
        
        clock.setVisible(true);
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(5001);
        } catch (IOException e) {
            System.out.println("Could not listen on port 5001");
            //System.exit(-1);
        }

//listenSocketSocketserver.acceptSocket
        try {
            client = server.accept();
        } catch (IOException e) {
            System.out.println("Accept failed: 5001");
            //System.exit(-1);
        }

//listenSocketBufferedReaderclientPrintWriter
        try {
            in = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(),
                    true);
        } catch (IOException e) {
            System.out.println("Read failed");
            //System.exit(-1);
        }
        System.out.println("startReading!!!");
        while (true) {
            try {
                line = in.readLine();
                if (line != null) {
                    String[] split = line.split("");
                        line = "";
                        for (int i = 0; i < split.length; i++) {
                            line += split[i];
                            if (i % 2 == 1 && i + 1 != split.length) {
                                line += ":";
                            }
                        }
                    if (resultsWindows != null) {
                        resultsWindows.clockLabel.setText(line);
                        resultsWindows.repaint();
                    }
                    if(clock != null){
                        clock.clockLabel.setText(line);
                        clock.repaint();
                    }
                }
            } catch (IOException e) {
                System.out.println("Read failed");
                //System.exit(-1);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        (new ClockServer(null)).start();
    }
}
