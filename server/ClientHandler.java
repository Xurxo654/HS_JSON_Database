package server;

import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread{

    final Socket socket;
    final Database db;
    final Command command;

    public ClientHandler(Socket socket, Database db, Command command) {

        this.socket = socket;
        this.db = db;
        this.command = command;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {

        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            Gson gson = new Gson();
            System.out.println("Received: " + gson.toJson(command));
            DbResponse response;

            switch (command.getType()) {
                case "get":
                    response = db.get(command.getKey());
                    break;
                case "set":
                    response = db.set(command.getKey(), command.getValue());
                    break;
                case "delete":
                    response = db.delete(command.getKey());
                    break;
                case "exit":
                    response = new DbResponse("OK");
                    System.out.println("Sent: " + gson.toJson(response));
                    dos.writeUTF(gson.toJson(response));
                    System.out.println("Severing closing");
                    break;
                default:
                    response = new DbResponse("ERROR");
                    response.setReason("Invalid task");
                    break;
            }

            System.out.println("Sent: " + gson.toJson(response));
            dos.writeUTF(gson.toJson(response));
        } catch (IOException e) {
            System.err.println("Error in ClientHandler Run");
            e.printStackTrace();
        }
    }
}
