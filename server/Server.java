package server;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Server waits for clients to connects and then assigns separate thread to each of them. Server contains the list of all the active threads.
 */

public class Server implements Runnable{

    private ServerSocket serverSocket = null;
    private List<ServerHandler> activeUsers = new ArrayList<>();

    //constructor
    public Server(){

        try {
            serverSocket = new ServerSocket(5056);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //start the server
        Thread runningServer = new Thread(this);
        runningServer.start();
    }

    @Override
    public void run() {

        while(true){

            //create an empty socket
            Socket socket = null;

            try
            {
                //wait for the client
                socket = serverSocket.accept();

                //create streams
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                //create and start a new serverHandler
                ServerHandler serverHandler = new ServerHandler(socket, inputStream, outputStream);
                Thread t = new Thread(serverHandler);
                t.start();

                //add to active users
                activeUsers.add(serverHandler);
            }
            catch (Exception e){
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }

        }

    }

    public List<ServerHandler> getActiveUsers() {
        return activeUsers;
    }
}
