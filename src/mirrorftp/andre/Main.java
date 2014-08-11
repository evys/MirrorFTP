/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirrorftp.andre;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
     * @param resp
     * @return
     */
    public static ArrayList<Main> getDados(ArrayList<String> resp) {
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

    public static void sincroniza(ComandosFTP cl, String dirLocal) throws IOException, ParseException {
        ArrayList<String> resp = new ArrayList<>();
        resp = cl.list(resp, "/");
        ArrayList<Main> dadosArq = getDados(resp);

        File diretorio = new File(dirLocal);
        File fList[] = diretorio.listFiles();
        ArrayList<String> files = new ArrayList<>(); //só pra poder usar metodo contains

        for (File fList1 : fList) {

            files.add(fList1.getName());
        }
//sincroniza local de acordo com remoto
        for (Main dadosArq1 : dadosArq) {
            if (files.contains(dadosArq1.nomeArq)) {
                if (comparaData(dirLocal, dadosArq1.nomeArq, cl) == 2) {
                    cl.receive(dirLocal, dadosArq1.nomeArq);
                    mudaData(dirLocal, dadosArq1.nomeArq, cl);
                }
            } else {
                cl.receive(dirLocal, dadosArq1.nomeArq);
                mudaData(dirLocal, dadosArq1.nomeArq, cl);
            }
        }

        ArrayList<String> auxRemoto = new ArrayList<>();
        for (Main m2 : dadosArq) {
            auxRemoto.add(m2.nomeArq);
        }
        //sincroniza remoto de acordo com local
        for (String nome : files) {
            if (auxRemoto.contains(nome)) {
                if (comparaData(dirLocal, nome, cl) == 1) {
                    cl.send(dirLocal, nome);
                }
            } else {
                cl.send(dirLocal, nome);
            }
        }
    }

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

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
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

        while (true) {
            sincroniza(cl, dirLocal);
            Thread.sleep(intervalo * 1000);
        }
    }

}
