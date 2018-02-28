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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
    private DatagramSocket client;
    private String line;
    private ResultsWindows resultsWindows;
    public Clock clock;
    private boolean working = true;
    private boolean restart = false;
    private String ip = "192.168.1.115";

    public ClockServer(ResultsWindows resultsWindows) {
        this.resultsWindows = resultsWindows;
        clock = new Clock();

        clock.setVisible(true);
    }

    public String getIp() {
        return ip;
    }

    public void changeIp(String newip) {
        restart = true;
        ip = newip;
        working = false;
        if (!this.isAlive()) {
            this.start();
            System.out.println("restart");
        }
    }

    @Override
    public void run() {
        restart = false;
        working = true;
//        try {
//            //server = new ServerSocket(5002);
//            client = new DatagramSocket(1201);
//        } catch (IOException e) {
//            System.out.println("Could not listen on port 1201");
//            //System.exit(-1);
//            working = false;
//        }
        try {
            DatagramSocket serverSocket = new DatagramSocket(1201);
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
            while (working) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData());
                String[] data = sentence.split("\n");
                String line = "  :  :  ";
                if (data.length > 3) {
                    Long millis = Long.parseLong(data[3].split("\t")[1]);
                    Long millisFnish = Long.parseLong(data[4].split("\t")[1]);

                    if (millisFnish > 0) {
                        millis = millisFnish;
                    }
                    if (millis > 0) {
                        long hs = (millis / 10000) % 100;
                        if (millisFnish == 0) {
                            hs = hs / 10 % 10;
                        }
                        long second = (millis / 1000000) % 60;
                        long minute = (millis / (1000000 * 60)) % 60;
                        long hour = (millis / (1000000 * 60 * 60)) % 24;
                        if (hour > 0) {
                            line = String.format("%02d:%02d:%02d", hour, minute, second);
                        } else {
                            if (millisFnish > 0) {
                                line = String.format("%02d:%02d:%02d", minute, second, hs);
                            } else {
                                line = String.format("%02d:%02d:%01d ", minute, second, hs);
                            }
                        }
                    }else{
                        line = "  :  :  ";
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
                //System.out.println("RECEIVED: " + sentence);
            }
        } catch (SocketException ex) {
            Logger.getLogger(ClockServer.class.getName()).log(Level.SEVERE, null, ex);

            working = false;
        } catch (IOException ex) {
            Logger.getLogger(ClockServer.class.getName()).log(Level.SEVERE, null, ex);

            working = false;
        }
//listenSocketSocketserver.acceptSocket

//listenSocketBufferedReaderclientPrintWriter
//        try {
//            //client = server.accept();
//            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
//            //out = new DataOutputStream(ouputClient.getOutputStream());
//        } catch (IOException e) {
//            System.out.println("Read failed");
//            //System.exit(-1);
//            working = false;
//        }
//        System.out.println("startReading!!!");
//        while (working) {
//            try {
//                line = in.readLine();
//                if (line != null&&line.length()>1) {
//                    //out.writeBytes(line);
//                    String[] split = line.split("");
//                    line = "";
//                    for (int i = 1; i < Math.min(split.length, 7); i++) {
//                        line += split[i];
//                        if (i % 2 == 0 && i + 1 != 7) {
//                            line += ":";
//                        }
//                    }
//                    if (resultsWindows != null) {
//                        resultsWindows.clockLabel.setText(line);
//                        resultsWindows.repaint();
//                    }
//                    if (clock != null) {
//                        clock.clockLabel.setText(line);
//                        clock.repaint();
//                    }
//                }
//            } catch (IOException e) {
//                System.out.println("Read failed");
//                //System.exit(-1);
//                working = false;
//            }
        if (restart) {
            System.out.println("restart!!!");
            this.start();
        } else {
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
