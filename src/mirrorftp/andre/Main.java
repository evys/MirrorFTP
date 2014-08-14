/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirrorftp.andre;

import java.io.*;
import java.text.ParseException;
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
                if (Local.comparaData(dirLocal, aux, cl) == 1) {
                    cl.send(dirLocal, aux);
                }
            } else {
                cl.send(dirLocal, aux);
            }
        }

//sincroniza local de acordo com remoto
        for (String dadosArq1 : auxRemoto) {
            if (auxLocal.contains(dadosArq1)) {
                if (Local.comparaData(dirLocal, dadosArq1, cl) == 2) {
                    cl.receive(dirLocal, dadosArq1);
                    Local.mudaData(dirLocal, dadosArq1, cl);  //tentar unir os 2 laços
                }
            } else {
                cl.receive(dirLocal, dadosArq1);
                Local.mudaData(dirLocal, dadosArq1, cl);
            }
        }

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
