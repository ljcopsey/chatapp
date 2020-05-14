import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class ChatServer extends Thread implements MessageProcessor {

    private ServerSocket in;
    private int i;
    private List<ServerClientHandler> clients = new ArrayList<>();
    private Map<Integer, Queue<Message>> messageQueueMap = new HashMap<>();

    private Boolean exit;


    public ChatServer(int port) {
        try {
            in = new ServerSocket(port);
            i = 0;
            exit = false;

            //creating new thread to listen for "exit" on server
            new Thread(() -> {
                try {
                    BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
                    while (true) {
                        String in = userIn.readLine();
                        if (in.equals("EXIT")) {
                            //or can add synchronized block
                            System.out.println(("Server commencing exit"));
                            synchronized (exit) {
                                exit = true;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve message from ServerClientHandler and add to message store
    public synchronized void processMessage(Message message) {
        messageQueueMap.getOrDefault(message.id, new LinkedList<>()).add(message);
        System.out.println(message.arrivalTime.toString() + " " + message.data);
    }

    //thread safe: blocks multiple threads accessing it at same time
    public synchronized boolean getExit() {
        return exit;
    }

    //accepting clients
    public void run() {
        try {
            while (!getExit()) {
                Socket s = in.accept();
                ServerClientHandler c = new ServerClientHandler(s, "Client " + i, this);
                c.start();
                clients.add(c);
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new ChatServer(14001).start();
    }
}


// need to enter exit on server side to shut down all client connections cleanly
// exit cmd will be entered here
// inform clients that server is shutting down + close all sockets
// clients will need to listen for this message, so can close socket from client side as well
// use try and catch first to ensure clean exit
// could use System.exit as final termination

// ** also need to remove clients from broadcast lists if their programs crash ans client leaves without netering "quit"
