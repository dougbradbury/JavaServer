import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//This must be an earlier version?
public class Server {
    public static class ServerThread extends Thread {
        Socket connectedClient = null;
        BufferedReader inputFromClient;
        PrintWriter outputToClient;
        String httpVersion;
        String requestMethod;
        String requestPath;

        public ServerThread(Socket newConnection) {
            connectedClient = newConnection;
        }

        public void run() {
            try {
                inputFromClient = new BufferedReader(new InputStreamReader(connectedClient.getInputStream()));
                outputToClient = new PrintWriter(connectedClient.getOutputStream(), true);//needs the true to pass the test - autoFlush
                String request = inputFromClient.readLine();

                httpVersion = getHTTPVersion(request);
                requestMethod = getMethod(request);
                requestPath = getPath(request);

                if (requestPath.equals("/")){
                        sendOkResponseWithoutBody();
                 }else if(new File("../cob_spec/public" + requestPath).exists()){
                    if (requestMethod.equals("GET")){
                        sendResponse("200 OK", readFile());
                    }else if (requestMethod.equals("PUT")){
                        send405();
                    }else if (requestMethod.equals("POST")){
                        send405();
                    }
                }else if(requestPath.equals("/method_options")){
                        sendResponse("200 OK\n Allow: GET,HEAD,POST,OPTIONS,PUT", "");
                }else if(requestPath.equals("/form")) {
                    sendOkResponseWithoutBody();
                }else{
                    sendResponse("404 Not Found", "404 File Not Found");
                }

            } catch (IOException e) {
                System.out.println(e);
            }
            //***do i need this in order for the test suite to work? otherwise it doesn't seem to close the sockets properly
            finally {
                try {
                    System.out.println("A client is going down, closing it's socket");
                    connectedClient.close();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }

        private String readFile() throws IOException {
            BufferedReader fileBR = new BufferedReader(new FileReader("../cob_spec/public" + requestPath));
            String currentLine;
            StringBuffer fileContent = new StringBuffer();
            while ((currentLine = fileBR.readLine()) != null) {
                fileContent.append(currentLine + '\n');
            }
            return fileContent.toString();
        }

        private void sendOkResponseWithoutBody() {
            sendResponse("200 OK", "");
        }

        private void sendResponse(String status, String body) {
            outputToClient.println(httpVersion + " " + status + "\r\n");
            outputToClient.println(body + "\r\n");


            outputToClient.close();
        }

        public void send405() {
            sendResponse("405 Method Not Allowed", "");
        }
    }

    public static String getHTTPVersion(String request) {
        return request.split(" ")[2];
    }

    public static String getMethod(String request) {
        return request.split(" ")[0];
    }

    public static String getPath(String request) {
        return request.split(" ")[1];
    }

    public static void main(String[] args) throws IOException {
        int portNumber = 5000;
        System.out.println("Server started!");
        ServerSocket serverSocket = new ServerSocket(portNumber);
        try {
            while (true) {
                Socket newConnection = serverSocket.accept();
                ServerThread newThread = new ServerThread(newConnection);
                newThread.start();
            }
        } finally {
            serverSocket.close();
        }
    }
}
