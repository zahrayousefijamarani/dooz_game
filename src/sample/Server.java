package sample;

import dooz.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static ArrayList<Game> games = new ArrayList<>();
    public static ArrayList<Player> players = new ArrayList<>();

    public static void main(String args[]) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);

        while (true) {//when a client connected it will be run
            Socket socket = serverSocket.accept();
            System.out.println("client connected");
            new ThreadForClient(socket, socket.getInputStream(), socket.getOutputStream()).start();
        }
    }
}



