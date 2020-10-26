import java.io.*;
import java.net.*;
import java.util.Date;

class HttpClient implements Runnable {
    
    private Socket clientSkt;
    private File docRoot;
    private String defaultFile;


    public HttpClient(Socket s, String root, String indexFile){
        this.clientSkt = s;
        this.docRoot = new File(root);
        this.defaultFile = indexFile;
    }

    public String returnContentType(String fileName){
        if(fileName.endsWith(".html") || fileName.endsWith(".htm")){
            return "text/html";
        } else if (fileName.endsWith(".txt") || fileName.endsWith(".java")){
            return "text/plain";
        } else if (fileName.endsWith(".git")){
            return "image/gif";
        } else if (fileName.endsWith(".jpg")){
            return "image/jpg";
        } else if (fileName.endsWith(".class")){
            return "application/octec-stream";
        } else{
            return "text/plain unknow";
        }
    }
    public void run(){
        String request;
        String contentType;
        String httpVersion = "";
        File requestedFile;

        try{
            PrintStream printStream = new PrintStream(
                clientSkt.getOutputStream()
            );
            BufferedReader clientReader = new BufferedReader(
                new InputStreamReader(clientSkt.getInputStream())
            );
            String get = clientReader.readLine();

            String[] tokens = get.split("[ \t]");
            request = tokens[0];

            if(request.equals("GET")){
                String file = tokens[1];

                if(file.endsWith("/")){ // GET /
                    file += defaultFile;
                }
                contentType = returnContentType(file);
                if(tokens.length >= 3){
                    httpVersion = tokens[2];
                }
            

                // 不處理客戶端的標頭
                while((get = clientReader.readLine()) != null){
                    if(get.trim().equals("")){
                        break;
                    }
                }

                try {
                    requestedFile = new File(
                        this.docRoot, file.substring(1, file.length())
                    );
                    FileInputStream fileInputStream = new FileInputStream(requestedFile);

                    //讀入請求的檔案
                    int fileLength = (int)requestedFile.length();
                    byte[] requestedData = new byte[fileLength];
                    fileInputStream.read(requestedData);
                    fileInputStream.close();

                    //寫出標頭制客戶端
                    if(httpVersion.startsWith("HTTP/")){
                        printStream.print("HTTP/1.0 200 OK\r\n");
                        Date now = new Date();
                        printStream.print("Date: "+ now + "\r\n");
                        printStream.print("Server: TinyHtpd v0.1\r\n");
                        printStream.print("content-length: " + fileLength + "\r\n");
                        printStream.print("Content-type: " + contentType + "\r\n\r\n");
                    }

                    //將檔案傳給客戶端
                    printStream.write(requestedData);
                    printStream.close();
                } catch (IOException e){
                    if(httpVersion.startsWith("HTTP/")){
                        printStream.print("HTTP/1.0 404 File Not Found !\r\n");
                        Date now = new Date();
                        printStream.print("Date: " + now + "\r\n");
                        printStream.print("Server: TinyHttpd v0.1\r\n");
                        printStream.print("Content-type: text/html\r\n\r\n");
                    }
                }

                //顯示錯誤訊息網頁
                printStream.println(
                    "<HTML><HEAD><TITLE>File Not Found</TITLE></HEAD>" 
                           + "<BODY><H1>HTTP Error 404: File Not Found" + 
                             "</H1></BODY></HTML>"
                );
                printStream.close();
            } else {
                if(httpVersion.startsWith("HTTP/")){
                    printStream.print("HTTP/1.0 501 Not Implemented \r\n");
                    Date now = new Date();
                    printStream.print("Date: "+ now + "\r\n");
                    printStream.print("Server: TinyHttpd v1.0\r\n");
                    printStream.print("Content-type: text/html\r\n\r\n");
                }
            }
            printStream.print(
                "<HTML><HEAD><TITLE>Not Implemented</TITLE></HEAD>" 
                           + "<BODY><H1>HTTP Error 501: Not Implemented" + 
                             "</H1></BODY></HTML>"
            );
            printStream.close();

        } catch (IOException e){
            e.printStackTrace();
        }

        try{
            clientSkt.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
