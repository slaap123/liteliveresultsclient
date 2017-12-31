/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.phanos.liteliveresultsclient.classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

/**
 *
 * @author woutermkievit
 */
public class ParFile implements java.io.Serializable {

    public int startlijst_onderdeel_id;
    public String versie;
    public String begin_tijd;
    public String startgroep;
    public String onderdeel;
    public String serie;
    public String fileName;
    public HashMap<Integer,ParFileEntry> atleten = new HashMap();
    public boolean gotResults=false;
    public ResultFile results=null;
    public boolean done=false;
    public boolean forceUpload=false;
    public boolean foundResult=false;
    public String UploadedAtleten;
    public boolean keepLocal=false;
    public long uploadDate=0;
    
    public Long resultSize=-1L;
    public File resultFile=null;

    public ParFile() {

    }
    public ParFile(File file) {
        getValuesFromFile(file);
    }
    public void getValuesFromFile(File file) {
        fileName=file.getName();
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
        String[] lines=content.split("\n");
        startlijst_onderdeel_id=Integer.parseInt(lines[0].split(":")[1].replaceAll("[ \t\n\r]*", ""));
        versie=lines[1].split(":",2)[1].replaceAll("[ \t\r\n]*", "");
        begin_tijd=lines[2].split(":",2)[1].replaceAll("[ \t\r\n]*", "");
        String temp_startgroep=lines[3].split(":",2)[1].replaceAll("[ \t\r\n]*", "");
        String temp_onderdeel=lines[4].split(":",2)[1].replaceAll("[ \t\r\n]*", "");
        String temp_serie=lines[5].split(":",2)[1].replaceAll("[ \t\r\n]*", "");
        if(temp_startgroep!=startgroep&&temp_onderdeel!=onderdeel&&temp_serie!=serie){
            atleten=new HashMap();
        }
        startgroep=temp_startgroep;
        onderdeel=temp_onderdeel;
        serie=temp_serie;
        for (int i = 6; i < lines.length; i++) {
            if(lines[i].split("\t").length>2){
                int baan=Integer.parseInt(lines[i].split("\t")[1]);
                if(!atleten.containsKey(baan)){
                    atleten.put(baan,new ParFileEntry(lines[i].split("\t")));
                }else{
                    atleten.get(baan).Update(lines[i].split("\t"));
                }
            }
        }
    }
    
    public void writeValuesToFile(){
        List<String> lines=new ArrayList<String>();
        lines.addAll(getHeaderInfo());
        for(ParFileEntry entry : atleten.values()){
            lines.add(entry.startnummer+"\t"+entry.baan+"\t"+entry.naam+"\t"+entry.info);
        }
        try {
            Files.write(Paths.get(fileName), lines, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(ParFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public ArrayList<String> getHeaderInfo(){
        ArrayList<String> lines=new ArrayList<String>();
        lines.add("# startlijst_onderdeel_id:"+startlijst_onderdeel_id);
        lines.add("# versie:\t\t"+versie);
        lines.add("# begin_tijd:\t"+begin_tijd);
        lines.add("# startgroep:\t"+startgroep);
        lines.add("# onderdeel:\t"+onderdeel);
        lines.add("# serie:\t\t"+serie);
        return lines;
    }

    public void setResults(File file) {
        
        resultSize = file.length();
        resultFile=file;
        results=new ResultFile(file,this);
    }

    
}
