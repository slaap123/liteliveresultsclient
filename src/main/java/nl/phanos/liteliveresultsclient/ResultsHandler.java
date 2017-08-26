package nl.phanos.liteliveresultsclient;

import nl.phanos.liteliveresultsclient.classes.ParFile;
import nl.phanos.liteliveresultsclient.gui.Main;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author woutermkievit
 */
public class ResultsHandler extends Thread {

    private File dir;
    private ArrayList<ParFile> files = new ArrayList<ParFile>();

    public ResultsHandler(File dir) {
        this.dir = dir;
    }

    @Override
    public void run() {
        while (true) {
            if (LoginHandler.isReachable()) {
                AtletiekNuPanel.panel.removeLine(NO_INTERNET_CONNECTION_WAITING_10_SECONDS);
                System.out.println("start reading");
                for (File fileEntry : dir.listFiles()) {
                    if (fileEntry.getName().endsWith("txt")) {
                        ParFile parFile = AtletiekNuPanel.panel.parFiles.get(fileEntry.getName().replace("txt", "par"));
                        if (parFile != null) {
                            parFile.foundResult = true;
                            if ((parFile.done && parFile.resultSize != fileEntry.length())||parFile.forceUpload) {
                                parFile.forceUpload=false;
                                parFile.resultSize = fileEntry.length();
                                parFile.resultFile = fileEntry;
                                parFile.gotResults = true;
                                files.add(parFile);
                                System.out.println("addPar:"+parFile.fileName);
                            }
                        }
                    }
                }
                try {
                    System.out.println("files:"+ files.size());
                    if (AtletiekNuPanel.panel.live && files.size() > 0) {
                        ((AtletiekNuPanel) AtletiekNuPanel.panel).loginHandler.submitResults(files);
                        System.out.println("upload");
                    }
                    files = new ArrayList<ParFile>();
                } catch (Exception ex) {
                    System.out.println("failed to upload");
                    Logger.getLogger(ResultsHandler.class.getName()).log(Level.SEVERE, null, ex);
                }

                AtletiekNuPanel.panel.UpdateList();
                System.out.println("end reading");
                //if(!ResultsPanel.panel.test){
                //}
            }else{
                AtletiekNuPanel.panel.addText(NO_INTERNET_CONNECTION_WAITING_10_SECONDS);
            }
            AtletiekNuPanel.panel.savePrefResults();
            try {
                sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ResultsHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    private static final String NO_INTERNET_CONNECTION_WAITING_10_SECONDS = "No internet connection!!!";
}
