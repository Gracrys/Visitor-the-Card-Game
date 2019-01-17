/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import client.Client;
import static java.awt.EventQueue.invokeLater;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import server.Server;
import server.ServerGameReceiver;
import server.ServerReceiver;

/**
 *
 * @author pseudo
 */
public class Main {

    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        if(args.length > 0 && args[0].equals("--server")){
            System.out.println("Server starting...");
            Server server = new Server();
            server.port = 8_080;
            server.gamePort = 8_081;
            try {
                    server.serverSocket = new ServerSocket(server.port);
                    server.serverGameSocket = new ServerSocket(server.gamePort);
            } catch (IOException e) {
                throw new RuntimeException("Error accepting client connection", e);
            }

            new Thread(() -> {
            while(true){
                try {
                    Socket clientSocket = server.serverSocket.accept();
                    new Thread(new ServerReceiver(clientSocket, server)).start();
                } catch (IOException e) {
                    throw new RuntimeException("Error accepting client connection", e);
                }
            }
            }).start();

            new Thread(() -> {
            while(true){
                try {
                    Socket clientSocket = server.serverGameSocket.accept();
                    new Thread(new ServerGameReceiver(clientSocket, server)).start();
                } catch (IOException e) {
                    throw new RuntimeException("Error accepting client connection", e);
                }
            }
            }).start();
            System.out.println("Server started.");
        } else {
            Client client = new Client();
            invokeLater(() -> client.main.setVisible(true));
        }
    }
}