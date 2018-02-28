/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.phanos.liteliveresultsclient.classes;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.phanos.liteliveresultsclient.AtletiekNuPanel;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.stack.AddressGenerator;

/**
 *
 * @author woutermkievit
 */
public class ResultScreenClient extends ReceiverAdapter {

    public JChannel channel;

    public ResultScreenClient() {
        try {

            String prop = null;
            channel = new JChannel().addAddressGenerator(null).setName(null);
            
            channel.setReceiver(this); // use the default config, udp.xml
            //System.out.println(channel.printProtocolSpec(true));
            channel.connect("Scoreboards");
//                ByteArrayInputStream bis = new ByteArrayInputStream(datagramPacket.getData());
//                ObjectInput in = null;
//                try {
//                    in = new ObjectInputStream(bis);
//                    AtletiekNuPanel.GetAtletiekNuPanel().parFiles = (HashMap<String, ParFile>) in.readObject();
//                } finally {
//                    try {
//                        if (in != null) {
//                            in.close();
//                        }
//                    } catch (IOException ex) {
//                        // ignore close exception
//                    }
//                }
        } catch (Exception ex) {
            Logger.getLogger(ResultScreenClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void stop() {
        channel.disconnect();
    }

    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public void receive(Message msg) {
        try {
            if (channel.getAddress() != msg.getSrc()) {
                System.out.println("go message");
                Object object = msg.getObject();
                AtletiekNuPanel panel = AtletiekNuPanel.GetAtletiekNuPanel();
                HashMap<String, ParFile> parfiles = (HashMap<String, ParFile>) object;
                panel.setParFiles(parfiles);
                panel.UpdateView();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
