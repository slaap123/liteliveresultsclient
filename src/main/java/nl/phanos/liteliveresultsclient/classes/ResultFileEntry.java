/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.phanos.liteliveresultsclient.classes;

/**
 *
 * @author woutermkievit
 */
public class ResultFileEntry implements java.io.Serializable {

    public int plaats;
    public int baan;
    public String tijd;
    public int startnummer;
    public String naam;
    public String info;

    ResultFileEntry(String[] line) {
        plaats = Integer.parseInt(line[0]);
        baan = Integer.parseInt(line[1]);
        tijd = line[2];
        info = "";
        if(line[3].length()==0){
            startnummer = -1;
        }else{
            startnummer = Integer.parseInt(line[3]);
        }
        naam = line[4];
        info = line[5];

    }
}
