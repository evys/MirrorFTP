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
import java.util.Date;

/**
 *
 * @author Andre
 */
public class Local {

    public static void mudaData(String pasta, String arq, ComandosFTP cl) throws ParseException, IOException, InterruptedException {
        String aux = Main.dirLocal+ pasta + "/" + arq;
        File fi = new File(aux.replace("///", "/").replace("//", "/"));
        //System.out.println ("pastaaa: "+ pasta);
        // Thread.sleep(10000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
      //  cl.changeDir(pasta);
        // System.out.println ("Arq mudadata: "+arq);
        //cl.actDir();
        //System.out.println ("DAAT: "+cl.modificationTime(arq).replace("213 ", ""));
        String dat = cl.modificationTime(arq).replace("213 ", "");
        java.util.Date data = sdf.parse(dat);

        long x = data.getTime();
        fi.setLastModified(x);
        
    }
public static int comparaData(String dirLocal, String pasta, String arq, ComandosFTP cl) throws IOException {
        File f = new File(dirLocal + pasta + "/" + arq);
        System.out.println("testando compara data"+dirLocal+pasta +"/"+ arq);
        cl.changeDir(pasta);
        
        DateFormat formatData = new SimpleDateFormat("yyyyMMddHHmmss");
        
        String aux = cl.modificationTime(arq).replace("213 ", "");
        long remoto = Long.parseLong(aux);
        //aux= formatData.format(new Date (remoto));
        remoto = Long.parseLong(aux);
        cl.changeDir(Main.dirRemoto);
        
        
        String data = formatData.format(new Date(f.lastModified()));
        long local = Long.parseLong(data);
        
        
        System.out.println ("Long local "+local+"  Long remoto "+remoto);
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


//nao preciso fazer as mudanças de pastas quando recebo os arquivos
    public static int comparaDataReceive(String dirLocal, String pasta, String arq, ComandosFTP cl) throws IOException {
        File f = new File(dirLocal + pasta + "/" + arq);
        System.out.println("testando compara data"+dirLocal+pasta +"/"+ arq);
        //cl.changeDir(pasta);
        
        DateFormat formatData = new SimpleDateFormat("yyyyMMddHHmmss");
        
        String aux = cl.modificationTime(arq).replace("213 ", "");
        long remoto = Long.parseLong(aux);
       
        remoto = Long.parseLong(aux);
              
        
        String data = formatData.format(new Date(f.lastModified()));
        long local = Long.parseLong(data);
        
        
        System.out.println ("Long local "+local+"  Long remoto "+remoto);
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
