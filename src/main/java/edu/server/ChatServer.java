package edu.server;

import edu.client.User;
import edu.connection.TCPconnection;
import edu.connection.TCPconnectionListener;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by Dima on 03.07.2019.
 * Сначала запустить сервер потом клиент
 */

public class ChatServer implements TCPconnectionListener {

    private static ConnectBD connectBD;

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        connectBD = ConnectBD.getInstanse();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                new ChatServer();    //есть объект сервера
            }
        });

    }

    //в нем есть список всех подключившихся устройств
    private final ArrayList<TCPconnection> connections = new ArrayList<>();

    //еще один конструктор
    private ChatServer() {
        System.out.println("server running...");

        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                try {
                    new TCPconnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    public static void RegistrUsers(User user) {
        connectBD = ConnectBD.getInstanse();
        System.out.println(connectBD);
        connectBD.registrUsers(user);
    }

    public static void AuthorisUsers(User user) {
        connectBD = ConnectBD.getInstanse();
        System.out.println(connectBD);
        connectBD.authorisUsers(user);
    }

    @Override
    public synchronized void onConnectionReady(TCPconnection TCPconnection) {
        connections.add(TCPconnection);
        sendToAllConnections("client connected " + TCPconnection);
    }

    @Override
    public synchronized void onReceiveString(TCPconnection TCPconnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPconnection TCPconnection) {
        connections.remove(TCPconnection);
        sendToAllConnections("client disconnected: " + TCPconnection);
    }

    @Override
    public synchronized void onException(TCPconnection TCPconnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    //Отправить всем присоединившимся
    private void sendToAllConnections(String value) {
        System.out.println(value);
        final int cnt = connections.size();
        for (int i = 0; i < cnt; i++)
            connections.get(i).sendString(value);

    }
}
