/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mirrorftp.andre;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
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
        // System.out.println(resp);
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

    public String actDir() throws IOException {  //diretorio atual
        String msg = "PWD \r \n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        String resp = this.getCntrlResp();
        return resp;
    }

    private void pasv() throws IOException {  //começar a criar conexao de dados
        String msg = "PASV \r\n"; //manda servidor escutar numa porta
        this.osContr.write(msg.getBytes());
        String resp = getCntrlResp(); //pegar resposta com a porta do servidor

        //Entering Passive Mode (187,17,122,141,198,107). as 4 primeiras são o IP, as 2 ultimas calculamos a porta (198*256+107)
        // System.out.println ("Resp: "+ resp);
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

    public ArrayList<String> list(String pasta) throws IOException {  //mandar arquivo
        this.pasv();
        String msg = "LIST " + pasta + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();

        ArrayList<String> lista = new ArrayList<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(isDados));

        String line;
        while ((line = br.readLine()) != null) {
            lista.add(line);

            System.out.println(line);
        }
        osDados.flush();
        osDados.close();
        isDados.close();
        this.getCntrlResp();
        return lista;
    }

    public void send(String dir, String arq) throws IOException {  //mandar arquivo  //dirRemoto só sera usado no caso da pastas
        this.changeDir(dir);
        this.pasv();
        String msg = "STOR " + arq + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();
        // System.out.println("Dir "+dir);
        FileInputStream fos = new FileInputStream(Main.dirLocal + "/" + dir + "/" + arq);
        int umByte = 0;
        while ((umByte = fos.read()) != -1) {
            osDados.write(umByte);
        }
        osDados.flush();
        osDados.close();
        isDados.close();

        this.getCntrlResp();
        this.changeDir(Main.dirRemoto);
    }

    public void receive(String pasta, String dir, String arq) throws IOException {  //mandar arquivo
        this.changeDir(dir);
        this.pasv();
        String msg = "RETR " + arq + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();

        FileOutputStream fos = new FileOutputStream(pasta + dir + "/" + arq);
        int umByte = 0;
        while ((umByte = isDados.read()) != -1) {
            fos.write(umByte);
        }
        this.getCntrlResp();

        osDados.flush();
        osDados.close();
        isDados.close();
        this.changeDir("/");
    }

    public void delete(String arq) throws IOException {
        String msg = "DELE " + arq + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();
    }

    public void deletePath(String arq) throws IOException {
        String msg = "RMD " + arq + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();

    }

    public String modificationTime(String arq) throws IOException {
        String msg = "MDTM " + arq + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        String resp = this.getCntrlResp();
        return resp;
    }

    public String back() throws IOException {
        String msg = "CDUP" + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        String resp = this.getCntrlResp();
        return resp;
    }

    public void rename(String de, String para) throws IOException {
        String msg = "RNFR " + de + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();

        String msg2 = "RNTO " + para + "\r\n";
        this.osContr.write(msg2.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();

    }

    public ArrayList<String> Nlist(String pasta) throws IOException {  //mandar arquivo
        this.pasv();
        String msg = "NLST " + pasta + "\r\n";
        this.osContr.write(msg.getBytes());//mandar pro servidor via canal de saída
        this.getCntrlResp();

        ArrayList<String> lista = new ArrayList<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(isDados));

        String line;
        while ((line = br.readLine()) != null) {
            lista.add(line);

            System.out.println(line);
        }
        osDados.flush();
        osDados.close();
        isDados.close();
        this.getCntrlResp();
        return lista;
    }
}
