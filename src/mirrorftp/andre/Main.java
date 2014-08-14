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

    public static void sincroniza(ComandosFTP cl, String dirLocal, String dirRemoto) throws IOException, ParseException {
        ArrayList<String> resp = new ArrayList<>();
        resp = cl.list(resp, "/");
        ArrayList<Remoto> dadosArqRemoto = Remoto.getDadosRemoto(resp);

        ArrayList<Local> filesLocal = new ArrayList<>(); 
        filesLocal = Local.getDadosLocal(dirLocal);

        ArrayList<String> auxRemoto = new ArrayList<>();
        for (Remoto m1 : dadosArqRemoto) {
            auxRemoto.add(m1.nomeArq);
        }
        
        ArrayList<String> auxLocal = new ArrayList<>();
        for (Local m2 : filesLocal) {
            auxLocal.add(m2.nomeArq);
        }
        //sincroniza remoto de acordo com local
        for (String aux : auxLocal) {
            if (auxRemoto.contains(aux)) {
                if (comparaData(dirLocal, aux, cl) == 1) {
                    cl.send(dirLocal, aux);
                }
            } else {
                cl.send(dirLocal, aux);
            }
        }

//sincroniza local de acordo com remoto
        for (String dadosArq1 : auxRemoto) {
            if (auxLocal.contains(dadosArq1)) {
                if (comparaData(dirLocal, dadosArq1, cl) == 2) {
                    cl.receive(dirLocal, dadosArq1);
                    mudaData(dirLocal, dadosArq1, cl);  //tentar unir os 2 laços
                }
            } else {
                cl.receive(dirLocal, dadosArq1);
                mudaData(dirLocal, dadosArq1, cl);
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

    static int indentLevel = -1;

    public static void listPath(File path) {
        File files[];
        indentLevel++;
        files = path.listFiles();
        Arrays.sort(files);
        for (int i = 0, n = files.length; i < n; i++) {
            for (int indent = 0; indent < indentLevel; indent++) {
                System.out.print("  ");
            }
            if (files[i].isDirectory()) {
                listPath(files[i]);
                System.out.println("Pasta: " + files[i].getName());
            }
            if (files[i].isFile()) {
                System.out.println("Arquivo: " + files[i].getName());
            }
        }
        indentLevel--;
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

        File teste = new File(dirLocal);

        listPath(teste);

        while (true) {
            sincroniza(cl, dirLocal, dirRemoto);
            Thread.sleep(intervalo * 1000);
        }
    }

}
