package edu.server;
import edu.connection.TCPconnection;
import edu.connection.TCPconnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Dima on 03.07.2019.
 * Сначала запустить сервер потом клиент
 */

public class ChatServer implements TCPconnectionListener {

   public static ConnectBD connectBD = new ConnectBD();

    public static void main(String[] args) throws ClassNotFoundException {

        new ChatServer();    //есть объект сервера

    }
        //в нем есть список всех подключившихся устройств
    private final ArrayList<TCPconnection> connections = new ArrayList<>();

    //еще один конструктор
    public ChatServer(){
        System.out.println("server running...");

        try(ServerSocket serverSocket = new ServerSocket(8189)){
            while(true){
                try {
                    new TCPconnection(this, serverSocket.accept());
                }   catch (IOException e){
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void RegistrUsers(String name, String pass) {
       ConnectBD.RegistrUsers(name, pass);

    }

    public static void AuthorisUsers(String name, String pass){
        ConnectBD.AuthorisUsers(name, pass);
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
    private void sendToAllConnections(String value){
        System.out.println(value);
        final int cnt = connections.size();
        for(int i = 0; i<cnt; i++)
            connections.get(i).sendString(value);

    }
}