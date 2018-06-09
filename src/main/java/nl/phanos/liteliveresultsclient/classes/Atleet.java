/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.phanos.liteliveresultsclient.classes;

import java.util.Dictionary;
import java.util.HashMap;

/**
 *
 * @author woutermkievit
 */
public class Atleet implements java.io.Serializable, Comparable<Atleet> {
    
    private static HashMap<Integer,Atleet> Atleten=new HashMap<Integer,Atleet>();
    public int startnummer;
    public String naam;
    public String info;
    
    public static Atleet getAtleet(int startnummer){
        if(Atleten.containsKey(startnummer)){
            return Atleten.get(startnummer);
        }
        return new Atleet(-1,"","");
    }
    public static Atleet getAtleet(String startnummerS, String naam, String info){
        int startnummer=-1;
        if(startnummerS.length()>0){
            startnummer = Integer.parseInt(startnummerS);
        }
        if(!Atleten.containsKey(startnummer)){
            return new Atleet(startnummer, naam, info);
        }else{
            Atleet atleet=Atleten.get(startnummer);
            if(atleet.naam.equals("")&&!naam.equals("")){
                atleet.naam=naam;
            }
            if(atleet.info.equals("")&&!info.equals("")){
                atleet.info=info;
            }
            return atleet;
        }
    }
    
    private Atleet(int startnummer, String naam, String info) {
        this.startnummer=startnummer;
        this.naam = naam;
        this.info = info;
        Atleten.put(this.startnummer, this);
    }
    
    @Override
    public int compareTo(Atleet o) {
        return this.startnummer-o.startnummer;
    }
    
}
