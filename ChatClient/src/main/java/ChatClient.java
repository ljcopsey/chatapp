import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;

import org.apache.commons.cli.*;

public class ChatClient {

    private Socket clientSocket;
    String user;

    private boolean isConnected() {
        return false;
    }

    public ChatClient(String host, int port, String user) {
        try {
            clientSocket = new Socket(host, port);
            this.user = user;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {

        new Thread(() -> {
            ObjectMapper mapMsg = new ObjectMapper();
            try {
                BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter serverOut = new PrintWriter(clientSocket.getOutputStream(), true);
                String data = userIn.readLine();
                while (!data.equals("quit")) {
                    data = userIn.readLine() ;
                    Message msg;
                    if (data.startsWith("@")) {
                        //get the following username
                        //Add to the message recipient field
                        //set message type to private

                        msg = new Message(Message.Type.PRIVATE, user, data);
                    }
                    else if (data.equals("quit")) {
                        msg = new Message(Message.Type.QUIT, user, data);
                    }
                    else {
                        msg = new Message(Message.Type.BROADCAST, user, data);
                    }
                    String json = mapMsg.writeValueAsString(msg);
                    serverOut.println(json);
                }

                // Add functionality to wait for server to close socket and permit exit
                System.out.println("Program Terminated");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                BufferedReader serverIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                while (true) {
                    String line = serverIn.readLine();
                    System.out.println(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

    }


    public static void main(String[] args) throws Exception {
        Options options = new Options();

        Option host = new Option("h", "host", true, "IP address of server - if not entered, default of localhost will be assigned");
        host.setRequired(false);

        options.addOption(host);

        Option port = new Option("p", "port", true, "connection port - if not entered, default of 14001 will be assigned");
        port.setRequired(false);
        options.addOption(port);

        Option user = new Option("u", "user", true, "username selection - this is required to make a connection. usernames must be unique");
        user.setRequired(true);
        options.addOption(user);


        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            String hostArg = cmd.getOptionValue("host", "localhost");
            int portArg = Integer.parseInt(cmd.getOptionValue("port","14001"));
            String userArg = cmd.getOptionValue("user");

            new ChatClient(hostArg, portArg, userArg).connect();

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("ChatClient", options);
            // Exit the program if cmd line arguments
            System.exit(1);
        }

    }

}



// closing the client cleanly = terminal input "quit"
// need to tell server so server gets rid of the connection to close socket
