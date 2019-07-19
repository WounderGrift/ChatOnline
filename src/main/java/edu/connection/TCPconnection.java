package edu.connection;

import edu.client.User;
import edu.server.ChatServer;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.rmi.registry.Registry;

import static com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken.Name;

/**
 * Created by Dima on 03.07.2019.
 */

public class TCPconnection {
    //сокет-сввязь, через который сервер общается с клиентом
    private final Socket socket;
    //поток
    private final Thread rxThread;
    //Интерфейс слушатель-подключения
    private final TCPconnectionListener eventListener;
    //поток чтения из сокета
    private final BufferedReader in;
    //поток записи в сокет
    private final BufferedWriter out;

    //конструктор для инит объект подключение (с параметором адреса и порта для сокета и слушателя для действия)
    public TCPconnection(TCPconnectionListener eventListener, String ipAddr, int port) throws IOException{
        this(eventListener, new Socket(ipAddr, port));
    }

    //еще один метод конструктор
    public TCPconnection(final TCPconnectionListener eventListener, Socket socket) throws IOException {
        this.socket = socket;
        this.eventListener = eventListener;
        //если у потоков возникнут исключение, то оно пробросится дальше
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),Charset.forName("UTF-8")));

        //создаем поток в параметре интерфейс запуска
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //слушатель - подключение готово
                    eventListener.onConnectionReady(TCPconnection.this);
                    //до тех пор пока подклюбчение не разрушенно, списываем строку
                    while (!rxThread.isInterrupted()){
                        eventListener.onReceiveString(TCPconnection.this, in.readLine());
                    }
                    String mes = in.readLine();
                } catch (IOException e) {
                    eventListener.onException(TCPconnection.this, e);
                } finally{
                    eventListener.onDisconnect(TCPconnection.this);
                }
            }
        });
        rxThread.start();
    }

    public synchronized void nameUserToBD(User user){
        try {
            out.write(user.getName() + " " + user.getPass() + "\r\n");
            out.flush();
            ChatServer.RegistrUsers(user);
        } catch (IOException e) {
            eventListener.onException(TCPconnection.this, e);
            disconnect();
        }
    }

    //синхронизируем методы, чтобы он выполнялся только одним потоком
    public synchronized void sendString(String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPconnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPconnection.this, e);
        }
    }

    @Override
    public String toString(){
        return "TCPConnection: " + socket.getInetAddress()+ " : " + socket.getPort();
    }
}