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
    public static void sincroniza1(File path, ComandosFTP cl, String dirLocal, String dirRemoto) throws IOException, ParseException {
        ArrayList<Remoto> dadosArqRemoto = Remoto.listRemoto(acumulaDir1, cl);
        ArrayList<String> auxRemoto = new ArrayList<>();
        for (Remoto r1 : dadosArqRemoto) {
            auxRemoto.add(r1.nomeArq);
        }

        File files[];
        files = path.listFiles();
        Arrays.sort(files);

        for (int i = 0, n = files.length; i < n; i++) {

            if (files[i].isDirectory()) {

                acumulaDir1 = acumulaDir1 + "/" + files[i].getName();

                if (!auxRemoto.contains(files[i].getName())) {
                    cl.createDir(files[i].getParent().replace("\\", "/").replace("/" + Main.dirLocal.replace("/", ""), "") + "/" + files[i].getName());
                }
                sincroniza1(files[i], cl, dirLocal, dirRemoto);
            }
            if (files[i].isFile()) { //enviar
                String caminho;
                caminho = files[i].getParent().replace("C:", "").replace("\\", "/");
                caminho = caminho.replace("/" + Main.dirLocal.replace("/", ""), "");
                caminho = caminho.replace(files[i].getName(), "");
                System.out.println("Caminho " + caminho);

                if (auxRemoto.contains(files[i].getName())) {
                    //System.out.println("VALOR: " + Local.comparaData(dirLocal, caminho, files[i].getName(), cl));
                    if (Local.comparaData(dirLocal, caminho, files[i].getName(), cl) == 1) {

                        cl.send(caminho, files[i].getName());
                    }
                } else {
                    cl.send(caminho, files[i].getName());
                }
            }
        }
        acumulaDir1 = "";
    }

    public static String dirAnt = "";

    public static void sincroniza2(ComandosFTP cl, String dirLocal, String dirRemoto) throws IOException, ParseException, InterruptedException {

        ArrayList<Remoto> files = Remoto.listRemoto(dirAnt, cl);

        Remoto r1;

        for (int i = 0, n = files.size(); i < n; i++) {

            r1 = files.get(i);
            if (r1.tipo.equals("drwxr-xr-x")) {

                cl.changeDir(r1.nomeArq);
                dirAnt = cl.actDir().replace("257 \"", "").replace("\" is the current directory", "");

                criaDiretorio(dirLocal, dirAnt);
                sincroniza2(cl, dirLocal, dirAnt);

                cl.back();
            }
            if (r1.tipo.equals("-rw-r--r--")) {
                //  cl.receive(dirLocal, dirAnt, r1.nomeArq);
                File fileAux = new File(dirLocal + dirAnt + "/" + r1.nomeArq);
                if (!fileAux.exists()) {
                    cl.receive(dirLocal, dirAnt, r1.nomeArq);
                    Local.mudaData(dirAnt, r1.nomeArq, cl);
                } else {
                    if (Local.comparaDataReceive(dirLocal, dirAnt, r1.nomeArq, cl) == 2) {
                        System.out.println("Recebendoooo " + Local.comparaDataReceive(dirLocal, dirAnt, r1.nomeArq, cl));
                        cl.receive(dirLocal, dirAnt, r1.nomeArq);
                        Local.mudaData(dirAnt, r1.nomeArq, cl);
                    }
                }
            }
        }
        dirAnt = "";
    }

    public static void sincroniza(File path, ComandosFTP cl, String dirLocal, String dirRemoto) throws IOException, ParseException, InterruptedException {

        //sincroniza1(path, cl, dirLocal, dirRemoto);
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
       
        /*while (true) {
         sincroniza(y, cl, dirLocal, dirRemoto);
         Thread.sleep(intervalo * 1000);
         }*/
    }

}
