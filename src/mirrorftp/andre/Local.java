/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirrorftp.andre;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Andre
 */
public class Local {

    public String nomeArq;
    public String tipo;

    public static ArrayList<Local> getDadosLocal(String dirLocal) {
        File diretorio = new File(dirLocal);
        File fList[] = diretorio.listFiles();
        ArrayList<Local> files = new ArrayList<>(); //só pra poder usar metodo contains

        for (File fList1 : fList) {
            Local l1 = new Local();
            l1.nomeArq = fList1.getName();
            if (fList1.isDirectory()) {
                l1.tipo = "pasta";
            } else {
                l1.tipo = "arquivo";
            }
            
            files.add(l1);
        }
    return files;}
    
     public static void mudaData(String pasta, String arq, ComandosFTP cl) throws ParseException, IOException {
        File fi = new File(pasta + arq);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
        String dat = cl.modificationTime(arq).replace("213 ", "");
        java.util.Date data = sdf.parse(dat);

        long x = data.getTime();
        fi.setLastModified(x);
    }

    public static int comparaData(String pasta, String arq, ComandosFTP cl) throws IOException {
        File f = new File(pasta + arq);
        String aux = cl.modificationTime(arq).replace("213 ", "");
        long remoto = Long.parseLong(aux);

        DateFormat formatData = new SimpleDateFormat("yyyyMMddHHmmSS");
        String data = formatData.format(new Date(f.lastModified()));
        long local = Long.parseLong(data);
        int resp = -1;
        if (local == remoto) {
            resp = 0;
        }
        if (local > remoto) {
            resp = 1;
        }
        if (local < remoto) {
            resp = 2;
        }
        return resp;
    }


}
