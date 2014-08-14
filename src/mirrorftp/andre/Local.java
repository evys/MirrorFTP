/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirrorftp.andre;

import java.io.File;
import java.util.ArrayList;

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

}
