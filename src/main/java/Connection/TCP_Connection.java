package Connection;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by Dima on 03.07.2019.
 */

public class TCP_Connection {
    //сокет-сввязь, через который сервер общается с клиентом
    private final Socket socket;
    //поток
    private final Thread rxThread;
    //Интерфейс слушатель-подключения
    private final TCP_ConnectionListener eventListener;
    //поток чтения из сокета
    private final BufferedReader in;
    //поток записи в сокет
    private final BufferedWriter out;

    //конструктор для инит объект подключение (с параметором адреса и порта для сокета и слушателя для действия)
    public TCP_Connection(TCP_ConnectionListener eventListener, String ipAddr, int port) throws IOException{
        this(eventListener, new Socket(ipAddr, port));
    }

    //еще один метод конструктор
    public TCP_Connection(final TCP_ConnectionListener eventListener, Socket socket) throws IOException {
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
                    eventListener.onConnectionReady(TCP_Connection.this);
                    //до тех пор пока подклюбчение не разрушенно, списываем строку
                    while (!rxThread.isInterrupted()){
                        eventListener.onReceiveString(TCP_Connection.this, in.readLine());
                    }
                    String mes = in.readLine();

                } catch (IOException e) {
                    eventListener.onException(TCP_Connection.this, e);
                } finally {
                    eventListener.onDisconnect(TCP_Connection.this);
                }
            }
        });
        rxThread.start();
    }

    //синхронизируем методы, чтобы он выполнялся только одним потоком
    public synchronized void SendString(String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCP_Connection.this, e);
            Disconnect();
        }
    }

    public synchronized void Disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCP_Connection.this, e);
        }
    }

    @Override
    public String toString(){
        return "TCPConnection: " + socket.getInetAddress()+ " : " + socket.getPort();
    }
}
