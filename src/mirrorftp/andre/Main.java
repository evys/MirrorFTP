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
    public String naosei;
    public String server;
    public String ftp;
    public String tamanho;
    public String mes;
    public String dia;
    public String hora;
    public String nomeArq;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static ArrayList<Main> getData(ArrayList<String> resp) {
        ArrayList<Main> dadosArq = new ArrayList<>();

        for (String linha : resp) {
            ArrayList<String> aux = new ArrayList<>();
            StringTokenizer parser = new StringTokenizer(linha, "   ");
            while (parser.hasMoreTokens()) {
                aux.add(parser.nextToken());
            }
            Main m1 = new Main();
            m1.tipo = aux.get(0);
            m1.naosei = aux.get(1);
            m1.server = aux.get(2);
            m1.ftp = aux.get(3);
            m1.tamanho = aux.get(4);
            m1.mes = aux.get(5);
            m1.dia = aux.get(6);
            m1.hora = aux.get(7);
            m1.nomeArq = aux.get(8);
            // System.out.println (aux);
            dadosArq.add(m1);
        }
        return dadosArq;
    }

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
        //cl.delete("E:\\Aula 09-07\\teste.txt");
        //cl.send (dirLocal, "tst.txt");
        //cl.receive(dirLocal,"tst.txt");

        ArrayList<String> resp = new ArrayList<>();

        resp = cl.list(resp, "/");
ArrayList<Main> dadosArq = getData(resp);
        for (Main dadosArq1 : dadosArq) {
            System.out.println(dadosArq1.nomeArq);
        }

    }
}
