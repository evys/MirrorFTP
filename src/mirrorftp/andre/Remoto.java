/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirrorftp.andre;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 *
 * @author Andre
 */
public class Remoto {

    public String tipo;
    public String links;
    public String server;
    public String ftp;
    public String tamanho;
    public String mes;
    public String dia;
    public String hora;
    public String nomeArq;

    public static ArrayList<Remoto> getDadosRemoto(ArrayList<String> resp) {
        ArrayList<Remoto> dadosArq = new ArrayList<>();
        for (String linha : resp) {
            ArrayList<String> aux = new ArrayList<>();
            StringTokenizer parser = new StringTokenizer(linha, "   ");
            while (parser.hasMoreTokens()) {
                aux.add(parser.nextToken());
            }
            Remoto m1 = new Remoto();
            m1.tipo = aux.get(0);
            m1.links = aux.get(1);
            m1.server = aux.get(2);
            m1.ftp = aux.get(3);
            m1.tamanho = aux.get(4);
            m1.mes = aux.get(5);
            m1.dia = aux.get(6);
            m1.hora = aux.get(7);
            m1.nomeArq = aux.get(8);
            dadosArq.add(m1);
        }
        return dadosArq;
    }
    
     
    static int indentLevel = -1;

    public static ArrayList<Remoto> listTest(String dirRemoto, ComandosFTP cl) throws IOException {
        cl.list (dirRemoto);
    
        ArrayList <String> aux = cl.list(dirRemoto);
        ArrayList <Remoto> files = Remoto.getDadosRemoto(aux);
        ArrayList <Remoto> result = new ArrayList<>();
        indentLevel++;
       Remoto r1 ;
        for (int i = 0, n = files.size(); i < n; i++) {
            for (int indent = 0; indent < indentLevel; indent++) {
                System.out.print("  ");
            }
            r1 = files.get(i);
            if (r1.tipo.equals("drwxr-xr-x")) {
                listTest(r1.nomeArq, cl);
                //System.out.println("Pasta: " + r1.nomeArq);
                result.add(r1);
            }
            if (r1.tipo.equals("-rw-r--r--")) {
               // System.out.println("Arquivo: " + r1.nomeArq);
                result.add(r1);
            }
        }
        indentLevel--;
    return result;}
}



