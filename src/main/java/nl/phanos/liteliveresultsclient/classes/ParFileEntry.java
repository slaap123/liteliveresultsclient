/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.phanos.liteliveresultsclient.classes;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author woutermkievit
 */
public class ParFileEntry implements java.io.Serializable, Comparable<ParFileEntry> {

    public String baan;
    public Atleet atleet;

    ParFileEntry(String[] params) {
        try {
            baan = params[1];
            this.atleet=Atleet.getAtleet(params[0], params[2], (params.length > 3)?params[3]:"");
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println(Arrays.toString(params));
            Logger.getLogger(ParFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Update(String[] params) {
        try {
            baan = params[1];
            this.atleet=Atleet.getAtleet(params[0], params[2], (params.length > 3)?params[3]:"");
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println(Arrays.toString(params));
            Logger.getLogger(ParFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int compareTo(ParFileEntry o) {
        return baan.compareTo(o.baan);
    }

}
