
import java.io.*;
import java.net.*;

//import jdk.internal.jline.internal.InputStreamReader;

public class SimpleHttpServer {
    public static void main(String args[]){
        String docRoot;
        String indexFile;
        int port;

        ServerSocket serverSkt;
        Socket clientSkt;

        try{
            BufferedReader buf = new BufferedReader(
                new InputStreamReader(System.in)
            );
            System.out.println("TinyHttpd v0.1");
            System.out.println("Server Root Folder: ");
            docRoot = buf.readLine();
            System.out.print("Indexing File: ");
            indexFile = buf.readLine();
            System.out.print("Server port: ");
            port = Integer.parseInt(buf.readLine());

            if(port < 0 || port > 65535){
                port = 80; // reset port as default 80
            }
        } catch(Exception e){
            System.err.println("Error: "+e.toString());
            System.out.print("Using internal options:");
            docRoot = "/var/www/html";
            indexFile = "index.html";
            port = 80;
            System.out.println("Root Folder: " + docRoot +
            "\n Index File: " + indexFile +
            "\n Connecting Port: " + port
            );
        }

        try{
            serverSkt = new ServerSocket(port);
            System.out.println(
                "Listening Client from: " + serverSkt.getLocalPort() + "Connecting..."
            );

            while(true){
                clientSkt = serverSkt.accept();
                System.out.println("Client Connection: " + clientSkt.getInetAddress());

                //啟動一個客戶端執行緒
                Thread clientThread = new Thread(
                    new HttpClient(clientSkt, docRoot, indexFile)
                );
                clientThread.start();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
