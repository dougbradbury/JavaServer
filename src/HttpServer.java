import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    public static class ServerThread extends Thread {
        Socket connectedClient = null;
        HttpRequest request;
        HttpResponse response;

        public ServerThread(Socket newConnection) {
            connectedClient = newConnection;
        }

        public void run() {
            try {
              //This could be extracted so that it is more testable
              //HandleConnection(in, out) or something like that
                request = new HttpRequest(connectedClient.getInputStream());
                response = new HttpResponse(request);
                response.createResponse();
                response.sendNewResponse(connectedClient.getOutputStream());
                //One connection, One request, the the socket is close
                //This is a good place to start, but it might
                //be fun to look at the Connection header
                //if it is 'Connection: Keep-Alive'
                //then the server should keep reading from this socket
                //looking for a new request after processing the first one
            } catch (IOException e) {
                System.out.println(e);
            }
            finally {
                try {
                    System.out.println("A client is going down, closing it's socket");
                    connectedClient.close();
                } catch (IOException e) {
                    System.out.println(e);
                    System.out.println("this is from the thread");
                }
            }
        }
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
