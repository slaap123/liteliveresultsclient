package nl.phanos.liteliveresultsclient;

import java.io.BufferedReader;
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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.phanos.liteliveresultsclient.classes.Atleet;
import nl.phanos.liteliveresultsclient.classes.ResultFile;

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
    
    int downladerCounter=0;
    
    @Override
    public void run() {
        
        while (true) {
            if (LoginHandler.isReachable()) {
                if(AtletiekNuPanel.panel!=null){
                    AtletiekNuPanel.panel.removeLine(NO_INTERNET_CONNECTION_WAITING_10_SECONDS);
                }
                //System.out.println("start reading");
                for (File fileEntry : dir.listFiles()) {
                    
                    if (fileEntry.getName().endsWith("png")||fileEntry.getName().endsWith("jpg")) {
                        if(AtletiekNuPanel.panel.phototFiles.get(fileEntry.getName())==null){
                            AtletiekNuPanel.panel.phototFiles.put(fileEntry.getName(), fileEntry);
                        }
                        ResultFile resultFile = AtletiekNuPanel.panel.ResultFiles.get(fileEntry.getName().replace("jpg", "txt").replace("png", "txt"));
                        if(resultFile!=null&&resultFile.Photo==null){
                            resultFile.Photo=fileEntry.getAbsolutePath();
                            System.out.println("found foto");
                            if (Main.getWindow().resultsWindow != null) {
                                Main.getWindow().resultsWindow.setSerieResults(resultFile);
                            }
                        }
                    }else
                    if (fileEntry.getName().endsWith("txt")) {
                        ParFile parFile = AtletiekNuPanel.panel.parFiles.get(fileEntry.getName().replace("txt", "par"));
                        if(parFile==null){
                            int id;
                            String serie;
                            try{
                             String[] listID=fileEntry.getName().split(",",2)[0].split("-", 4);
                             id=Integer.parseInt(listID[0]);
                             serie=listID[2];
                            }catch(Exception e){
                                continue;
                            }
                            for (Entry<String,ParFile> file : AtletiekNuPanel.panel.parFiles.entrySet()) {
                                if(file.getValue().startlijst_onderdeel_id==id&&file.getValue().serie.trim().equals(serie.trim())){
                                    parFile=file.getValue();
                                    break;
                                }
                            }
                        }
                        if (parFile != null) {
                            parFile.foundResult = true;
                            if ((parFile.done && parFile.resultSize != fileEntry.length()) || parFile.forceUpload) {
                                checkHeaderInfo(fileEntry, parFile);
                                parFile.forceUpload = false;
                                parFile.setResults(fileEntry);
                                parFile.gotResults = true;
                                files.add(parFile);
                                AtletiekNuPanel.panel.ResultFiles.put(fileEntry.getName(), parFile.results);
                                System.out.println("addPar:" + parFile.fileName);
                                if(!AtletiekNuPanel.panel.live){
                                    parFile.uploadDate=Calendar.getInstance().getTimeInMillis();
                                }
                            }
                            File File = AtletiekNuPanel.panel.phototFiles.get(fileEntry.getName().replace("txt", "jpg"));
                            if(File!=null&&parFile.results!=null&&parFile.results.Photo==null){
                                parFile.results.Photo=File.getAbsolutePath();
                                if (Main.getWindow().resultsWindow != null) {
                                    Main.getWindow().resultsWindow.setSerieResults(parFile.results);
                                }
                            }
                        }
                    }
                }
                try {
                    if (AtletiekNuPanel.panel.live && files.size() > 0) {
                        System.out.println("files:" + files.size());
                        ((AtletiekNuPanel) AtletiekNuPanel.panel).loginHandler.submitResults(files);
                        System.out.println("upload");
                    }
                    files = new ArrayList<ParFile>();
                } catch (Exception ex) {
                    System.out.println("failed to upload");
                    Logger.getLogger(ResultsHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                downladerCounter++;
                if(downladerCounter%10==0){
                    AtletiekNuPanel.panel.UpdateList();
                    downladerCounter=0;
                }else{
                    AtletiekNuPanel.panel.UpdateView();
                }
                //System.out.println("end reading");
                //if(!ResultsPanel.panel.test){
                //}
            } else {
                AtletiekNuPanel.panel.addText(NO_INTERNET_CONNECTION_WAITING_10_SECONDS);
            }
            AtletiekNuPanel.panel.savePrefResults();
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ResultsHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    private static final String NO_INTERNET_CONNECTION_WAITING_10_SECONDS = "No internet connection!!!";

    private void checkHeaderInfo(File file, ParFile parFile) {
        String content = null;
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(ParFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(ParFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        ArrayList<String> lines = new ArrayList<String>(Arrays.asList(content.split("\n")));
        lines = CheckResultsFile(file, parFile, lines);

    }

    private ArrayList<String> CheckResultsFile(File file, ParFile parFile, ArrayList<String> lines) {
        for(int i=2;i<lines.size();i++){
            //if(lines.get(i).startsWith("#")) continue;
            String[] row=lines.get(i).split("\t");
            if(row.length<=3) continue;
            if(row[3].trim().equals("")){
                try{
                int baan=Integer.parseInt(row[1]);
                    if(parFile.atleten.containsKey(baan)){
                        row[3]=parFile.atleten.get(baan).atleet.startnummer+"";
                        row[4]=parFile.atleten.get(baan).atleet.naam;
                        row[5]=parFile.atleten.get(baan).atleet.info;
                    }
                    lines.set(i, String.join("\t",row));
                }catch(Exception e){
                    
                }
            }else if(row.length>4&&row[4].trim().equals("")){
                Atleet atleet=Atleet.getAtleet(Integer.parseInt(row[3]));
                row[4]=atleet.naam;
                row[5]=atleet.info;
                lines.set(i, String.join("\t",row));
            }
        }
        if (!lines.get(1).startsWith("#")) {
            System.out.println("addHeaderINFO!!!!");
            ArrayList<String> newLines = new ArrayList<String>();
            newLines.addAll(lines);
            newLines.addAll(1, parFile.getHeaderInfo());
            FileWriter writer = null;

            try {
                writer = new FileWriter(file.getAbsolutePath());
                for (int i = 0; i < newLines.size(); i++) {
                    if (i > 0) {
                        writer.append(System.getProperty("line.separator"));
                    }
                    writer.append(newLines.get(i));
                }
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(ResultsHandler.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(ResultsHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            file = new File(file.getAbsolutePath());
            parFile.resultSize = file.length();
            lines = newLines;
        }
        return lines;
    }
}
