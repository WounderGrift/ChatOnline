package Server;
import Client.ClientWin;
import Connection.TCP_Connection;
import Connection.TCP_ConnectionListener;
import org.hsqldb.Server;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Created by Dima on 03.07.2019.
 * Сначала запустить сервер потом клиент
 */

public class ChatServer implements TCP_ConnectionListener{

    public static void main(String[] args) {
        new ChatServer();    //есть объект сервера
    }

    //в нем есть список всех подключившихся устройств
    private final ArrayList<TCP_Connection> connections = new ArrayList<>();

    //еще один конструктор
    private ChatServer(){
        System.out.println("Server running...");

        try(ServerSocket serverSocket = new ServerSocket(8189)){
            while(true){
                try {
                    new TCP_Connection(this, serverSocket.accept());
                }   catch (IOException e){
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCP_Connection tcp_connection) {
        connections.add(tcp_connection);
        sendToAllConnections("Client connected: " + tcp_connection);
    }

    @Override
    public synchronized void onReceiveString(TCP_Connection tcp_connection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCP_Connection tcp_connection) {
        connections.remove(tcp_connection);
        sendToAllConnections("Client disconnected: " + tcp_connection);
    }

    @Override
    public synchronized void onException(TCP_Connection tcp_connection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    //Отправить всем присоединившимся
    private void sendToAllConnections(String value){
        System.out.println(value);
        final int cnt = connections.size();
        for(int i = 0; i<cnt; i++)
            connections.get(i).SendString(value);

    }
}
