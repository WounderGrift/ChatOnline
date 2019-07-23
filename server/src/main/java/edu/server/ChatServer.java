package edu.server;

//import edu.client.User;
import edu.connection.TCPconnection;
import edu.connection.TCPconnectionListener;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Dima on 03.07.2019.
 * Сначала запустить сервер потом клиент
 */

public class ChatServer implements TCPconnectionListener {

    private static ConnectBD connectBD;
    private int IndexOfName, IndexOfPass, IndexOfReg;
    private String name, pass, reg;
    private boolean init;

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

    public synchronized static void registrUsers(String name, String pass) {
        connectBD = ConnectBD.getInstanse();
        connectBD.registrUsers(name, pass);


    }

    public synchronized static void authorisUsers(String name, String pass) {
        connectBD = ConnectBD.getInstanse();
        connectBD.authorisUsers(name, pass);
    }

    @Override
    public synchronized void onConnectionReady(TCPconnection TCPconnection) {
        init = true;
        connections.add(TCPconnection);
    }

    @Override
    public synchronized void onReceiveString(TCPconnection TCPconnection, String value) {
        if(init){
            IndexOfName = value.indexOf(" ");
            name = value.substring( 0, IndexOfName);

            IndexOfPass = value.indexOf(" ", IndexOfName+1);
            pass = value.substring(IndexOfName+1, IndexOfPass);

            IndexOfReg = value.indexOf(" ");
            reg = value.substring(IndexOfPass+1);

           // sendToAllConnections(name + "~" + pass + "~" + reg);

            switch (reg){
                case "Y":{
                    authorisUsers(name,pass);
                }break;
                case "N":{
                    registrUsers(name, pass);
                }break;
            }
            TCPconnection.sendString(String.valueOf(connectBD.isOpenform()));

            if(connectBD.isOpenform()){
                sendToAllConnections("client connected " + TCPconnection);
            }

            init = !init;
        } else {
            System.out.println();
            connectBD = ConnectBD.getInstanse();
            connectBD.messagePutToBD(value);

            sendToAllConnections(value);

        }

    }

    @Override
    public synchronized void onDisconnect(TCPconnection TCPconnection) {
        connections.remove(TCPconnection);
        if(connectBD.isOpenform()) {
            sendToAllConnections("client disconnected: " + TCPconnection);
        }
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
