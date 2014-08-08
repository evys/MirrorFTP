/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mirrorftp.andre;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author proimp
 */
public class ComandosFTP {

    private Socket contrS;
    private InputStream isContr;   //vinculado a conexão de controle
    private OutputStream osContr;

    private Socket dados;
    private InputStream isDados;   //vinculado a conexão de dados
    private OutputStream osDados;

    private String getCntrlResp() throws IOException {  //metodo de captura de resposta
        BufferedReader br = new BufferedReader(new InputStreamReader(this.isContr));//ajuda a encontrar o /n
        String resp = br.readLine(); //devolve os bytes de string ate achar /n
        System.out.println("Resposta: " + resp);
        return resp;
    }

    public void connect(String host, int port) throws IOException {
        this.contrS = new Socket(host, port);
        this.isContr = contrS.getInputStream(); //pegando meus canais de entrada e saída
        this.osContr = contrS.getOutputStream();

        this.getCntrlResp(); //obrigatorio! capturar resposta
    }

    public void login(String user, String pass) throws IOException {
        String msg = "USER " + user + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();

        msg = "PASS " + pass + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();
    }

    public void createDir(String nome) throws IOException {  //criar pasta
        String msg = "MKD " + nome + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();
    }

    public void changeDir(String pasta) throws IOException {  //mudar diretorio
        String msg = "CWD " + pasta + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();
    }

    public void actDir() throws IOException {  //diretorio atual
        String msg = "PWD \r \n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();
    }

    private void pasv() throws IOException {  //começar a criar conexao de dados
        String msg = "PASV \r\n"; //manda servidor escutar numa porta
        this.osContr.write(msg.getBytes());
        String resp = getCntrlResp(); //pegar resposta com a porta do servidor
        //Entering Passive Mode (187,17,122,141,198,107). as 4 primeiras são o IP, as 2 ultimas calculamos a porta (198*256+107)
        StringTokenizer st = new StringTokenizer(resp);
        st.nextToken("(");
        String ip = st.nextToken(",").substring(1) + "."
                + st.nextToken(",") + "."
                + st.nextToken(",") + "."
                + st.nextToken(",");
        int value1 = Integer.parseInt(st.nextToken(","));
        int value2 = Integer.parseInt(st.nextToken(")").substring(1));
        int port = value1 * 256 + value2;

        this.dados = new Socket(ip, port);
        this.isDados = dados.getInputStream(); //pegando meus canais de entrada e saída
        this.osDados = dados.getOutputStream();
    }

    public ArrayList<String> list(ArrayList<String> lista, String pasta) throws IOException {  //mandar arquivo
        this.pasv();
        String msg = "LIST " + pasta + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();

        BufferedReader br = new BufferedReader(new InputStreamReader(isDados));
        String resp = "";
        String line;
        while ((line = br.readLine()) != null) {
            lista.add(line);
            //resp = resp + "\n" + line;
            System.out.println(line);
        }
        osDados.flush();
        osDados.close();
        isDados.close();
        this.getCntrlResp();
        return lista;
    }

    public void send(String dir, String arq) throws IOException {  //mandar arquivo
        this.pasv();
        String msg = "STOR " + arq + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();

        FileInputStream fos = new FileInputStream(dir + arq);
        int umByte = 0;
        while ((umByte = fos.read()) != -1) {
            osDados.write(umByte);
        }
        osDados.flush();
        osDados.close();
        isDados.close();
        this.getCntrlResp();
    }

    public void receive(String pasta, String arq) throws IOException {  //mandar arquivo
        this.pasv();
        String msg = "RETR " + arq + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();

        FileOutputStream fos = new FileOutputStream(pasta + arq);
        int umByte = 0;
        while ((umByte = isDados.read()) != -1) {
            fos.write(umByte);
        }
        osDados.flush();
        osDados.close();
        isDados.close();
        this.getCntrlResp();
    }

    public void delete(String arq) throws IOException {  //mandar arquivo
        String msg = "DELE " + arq + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();

    }

    public String modificationTime(String arq) throws IOException {  //mandar arquivo
        this.pasv();
        String msg = "MDTM " + arq + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        String resp = this.getCntrlResp();
        return resp;
    }
}
