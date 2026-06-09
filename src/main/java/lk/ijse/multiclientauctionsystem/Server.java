package lk.ijse.multiclientauctionsystem;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static List<Socket> clients = new ArrayList<>();
    private static final int PORT = 6000;

    public static void main(String[] args) {
        System.out.println("[Auction Server Started - Port 6000]");
        System.out.println("[Item: Vintage Watch | Starting Price: LKR 5,000]");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket socket = serverSocket.accept();
                clients.add(socket);

                new Thread(() -> handleClient(socket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        String clientName = "Unknown";
        try (DataInputStream in = new DataInputStream(socket.getInputStream())) {

            clientName = in.readUTF();
            broadcast("Client Connected: " + clientName,null);

            String message;
            while (true) {
                message = in.readUTF();
                broadcast(clientName + ": " + message, socket);
            }

        } catch (IOException e) {
            System.out.println(clientName + " disconnected.");
        } finally {
            removeClient(socket);
        }
    }

    private static void broadcast(String message, Socket sender) {
        System.out.println(message);

        List<Socket> toRemove = new ArrayList<>();

        for (Socket client : clients) {
            if (client != sender && client.isConnected()) {
                try {
                    DataOutputStream out = new DataOutputStream(client.getOutputStream());
                    out.writeUTF(message);
                    out.flush();
                } catch (IOException e) {
                    toRemove.add(client);
                }
            }
        }

        for (Socket dead : toRemove) {
            removeClient(dead);
        }
    }

    private static void removeClient(Socket socket) {
        clients.remove(socket);
        System.out.println("Client disconnected...");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}