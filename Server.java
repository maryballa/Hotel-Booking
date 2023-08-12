
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import java.util.Scanner;
public class Server{

    private ServerSocket serverSocket;
    public Semaphore sem;
    
    public Server(ServerSocket serverSocket, Semaphore sem) {
        this.serverSocket = serverSocket;
        this.sem=sem;
    }

    public static void main(String[] args) throws IOException {
    	File file=new File("file.txt");
        ServerSocket serverSocket = new ServerSocket(1234);
        Semaphore sem=new Semaphore(1);
        Server server = new Server(serverSocket,sem);
        server.startServer();
    }
    
    public void startServer(){
        try{
        	
            while(!serverSocket.isClosed()){
            	
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket,sem);
                System.out.println(clientHandler.getName()+" client has connected.");
                clientHandler.start();
            }
        } catch (IOException e){
        	
        }
        System.out.println("Server terminated :))");
    }

    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
