/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirrorftp.andre;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Andre
 */
public class Main {
public String tipo;
public int naosei;
public String server;
public int tamanho;
public String mes;
public int dia;
public String hora;
public String nomeArq;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        File f = new File("entradas.txt");
        InputStream is = new FileInputStream(f);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String host = br.readLine();
        int porta = new Integer(br.readLine());
        int intervalo = new Integer(br.readLine());
        String usuario = br.readLine();
        String senha = br.readLine();
        String dirLocal = br.readLine();
        String dirRemoto = br.readLine();

        ComandosFTP cl = new ComandosFTP();
        cl.connect(host, porta);
        cl.login(usuario, senha);
        
        //cl.send (dirLocal+"tst.txt");
        //cl.receive(dirLocal,"tst.txt");
    
        ArrayList <String> arqui  = new ArrayList<String> ();
        
        //arqui.add = 
        arqui=cl.list(arqui,"/");
        StringTokenizer parser = new StringTokenizer(astring);
    while (parser.hasMoreTokens()) {€
        processWord(parser.nextToken());
    }


        
    }
}
