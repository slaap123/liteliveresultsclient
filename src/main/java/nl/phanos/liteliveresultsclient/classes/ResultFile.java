/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.phanos.liteliveresultsclient.classes;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.phanos.liteliveresultsclient.gui.Main;

/**
 *
 * @author woutermkievit
 */
public class ResultFile implements java.io.Serializable {

    public String wind;
    public HashMap<Integer, ResultFileEntry> atleten = new HashMap();
    public ParFile BelongsTo;

    ResultFile(File file, ParFile BelongsTo) {
        this.BelongsTo = BelongsTo;
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
        String[] lines = content.split("\n");
        wind = lines[0].split("\t")[1];
        if (wind.startsWith("N/A")) {
            wind = "";
        } else {
            wind = " W :" + wind.split(":")[1];
        }
        int lI = 1;
        while (!lines[lI].startsWith("Place")) {
            lI++;
        }
        lI++;
        atleten.clear();
        while (lines.length > lI) {
            String[] line = lines[lI].split("\t");
            if (line.length > 1) {
                ResultFileEntry entry = new ResultFileEntry(line);
                atleten.put(entry.plaats, entry);
            }
            lI++;
        }
        if (Main.getWindow().resultsWindow != null) {
            Main.getWindow().resultsWindow.setSerieResults(this);
        }
    }
}
