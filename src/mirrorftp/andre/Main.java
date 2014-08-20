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

    static String usuario;
    static String host;
    static String senha;
    static int porta;

    static String dirLocal;
    static String dirRemoto;

    static int indentLevel = -1;
    static String acumulaDir1 = "";

    static int indentLevel1 = -1;
    static String acumulaDir2 = "";

    public static void criaDiretorio(String dirLocal, String novoDiretorio) {
        File x = new File(dirLocal + novoDiretorio);
        x.mkdir();
    }

    //reveer esse metodo
    public static void sincroniza1(ArrayList<String> auxRemoto, File path, ComandosFTP cl, String dirLocal, String dirRemoto) throws IOException, ParseException {
        /* ArrayList<Remoto> dadosArqRemoto = Remoto.listTest("/", cl);
         ArrayList<String> auxRemoto = new ArrayList<>();
         for (Remoto m1 : dadosArqRemoto) {
         auxRemoto.add(m1.nomeArq);
         }*/
        File files[];
        indentLevel++;
        files = path.listFiles();
        Arrays.sort(files);
        String dirAnt = acumulaDir1;
        for (int i = 0, n = files.length; i < n; i++) {
            for (int indent = 0; indent < indentLevel; indent++) {
                //System.out.print("  ");
            }
            if (files[i].isDirectory()) {
                dirAnt = acumulaDir1;
                acumulaDir1 = acumulaDir1 + "/" + files[i].getName();
                if (!auxRemoto.contains(files[i].getName())) {
                    //System.out.println(acumulaDir);
                    cl.createDir(acumulaDir1);
                }
                sincroniza1(auxRemoto, files[i], cl, dirLocal, dirRemoto);
                // System.out.println("Pasta: " + files[i].getName());
            }
            if (files[i].isFile()) { //enviar
                // System.out.println("DIR ANT:  " + dirAnt);
                if (auxRemoto.contains(files[i].getName())) {
                    if (Local.comparaData(dirLocal, acumulaDir1, files[i].getName(), cl) == 1) {
                        System.out.println("ENVIANDO " + Local.comparaData(dirLocal, dirAnt, files[i].getName(), cl));
                        cl.send(dirAnt, dirLocal + dirAnt, files[i].getName());
                    }
                } else {
                    cl.send(dirAnt, dirLocal + dirAnt, files[i].getName());
                }
            }
        }
        acumulaDir1 = "";
        indentLevel--;

    }

    public static void sincroniza2(ComandosFTP cl, String dirLocal, String dirRemoto) throws IOException, ParseException, InterruptedException {

        ArrayList<String> aux = cl.list(dirRemoto);
        ArrayList<Remoto> files = Remoto.getDadosRemoto(aux);

        indentLevel1++;
        Remoto r1;
        String dirAnt = acumulaDir2;
        for (int i = 0, n = files.size(); i < n; i++) {
            for (int indent = 0; indent < indentLevel1; indent++) {
                System.out.print("  ");
            }
            r1 = files.get(i);
            if (r1.tipo.equals("drwxr-xr-x")) {
                dirAnt = acumulaDir2;
                // System.out.println("DIR ANTEEEEEE: " + dirAnt);
                acumulaDir2 = acumulaDir2 + "/" + r1.nomeArq;
                criaDiretorio(dirLocal, acumulaDir2);
                sincroniza2(cl, dirLocal, acumulaDir2);
                System.out.println("Pasta: " + r1.nomeArq);
            }
            if (r1.tipo.equals("-rw-r--r--")) {
                // System.out.println("Arquivo: " + r1.nomeArq);
                File fileAux = new File(dirLocal + dirAnt + "/" + r1.nomeArq);
                if (!fileAux.exists()) {

                    cl.receive(dirLocal, dirAnt, r1.nomeArq);

                    Local.mudaData(dirAnt, r1.nomeArq, cl);
                } else {
                    if (Local.comparaData(dirLocal, dirAnt, r1.nomeArq, cl) == 2) {
                        //  System.out.println ("Recebendoooo "+ Local.comparaData(dirLocal, dirAnt, r1.nomeArq, cl));
                        cl.receive(dirLocal, dirAnt, r1.nomeArq);

                        Local.mudaData(dirAnt, r1.nomeArq, cl);
                    }
                }

            }
        }
        acumulaDir2 = "";
        indentLevel1--;

    }

    public static void sincroniza(File path, ComandosFTP cl, String dirLocal, String dirRemoto) throws IOException, ParseException, InterruptedException {
        ArrayList<Remoto> dadosArqRemoto = Remoto.listRemoto("/", cl);
        ArrayList<String> auxRemoto = new ArrayList<>();
        for (Remoto m1 : dadosArqRemoto) {
            auxRemoto.add(m1.nomeArq);
        }
        sincroniza1(auxRemoto, path, cl, dirLocal, dirRemoto);
        sincroniza2(cl, dirLocal, dirRemoto);
    }

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        File f = new File("entradas.txt");
        InputStream is = new FileInputStream(f);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        host = br.readLine();
        porta = new Integer(br.readLine());
        int intervalo = new Integer(br.readLine());
        usuario = br.readLine();
        senha = br.readLine();
        dirLocal = br.readLine();
        dirRemoto = br.readLine();

        ComandosFTP cl = new ComandosFTP();
        cl.connect(host, porta);
        cl.login(usuario, senha);

        File y = new File(dirLocal);

        sincroniza(y, cl, dirLocal, dirRemoto);

       // while (true) {
        //    sincroniza(y, cl, dirLocal, dirRemoto);
        //   Thread.sleep(intervalo * 1000);
        // }
    }

}
