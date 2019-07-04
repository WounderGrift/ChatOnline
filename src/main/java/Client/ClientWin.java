package Client;

import Connection.TCP_Connection;
import Connection.TCP_ConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by Dima on 03.07.2019.
 * Сначала запустить сервер потом клиент
 */

public class ClientWin extends JFrame implements ActionListener, TCP_ConnectionListener{
    private static final String IP_ADDR = "127.0.0.1";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private String msg;

    private TCP_Connection connection;


    public static void main(String[] args) {
        //Эта штука нужна для запуска ассинхронной операци
        //Сохраняет дейсвтие runnable и запускает его на одном из следующих итераций цикла сообщений
        //При помощи нее можно отложить какую-лтбо операцию на потом.
            SwingUtilities.invokeLater((new Runnable() {
                @Override
                public void run() {
                    new ClientWin();
                }
            }));

    }

    //Немножко формочек
    private final JTextArea log = new JTextArea();
    //Делать ли регистрацию пользователя
    private final JTextField fieldNickName = new JTextField("User");
    private final JTextField fieldinput = new JTextField();

    private ClientWin(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        fieldinput.addActionListener(this);
        add(fieldNickName, BorderLayout.NORTH);
        add(fieldinput, BorderLayout.SOUTH);

        setVisible(true);

        //  Здесь намертво вставет интерфейс, возможно нужно подключить локальный сервер
        //  Нет, ошибка была в разных портах сервера и клиента
        try {
            connection = new TCP_Connection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection exception: " + e);
        }
    }

    //Написать сообщение на экран
    @Override
    public void actionPerformed(ActionEvent e) {
        this.msg = fieldinput.getText();
        if(msg.equals("")){
            return;
        } else{
            fieldinput.setText(null);
            connection.SendString(fieldNickName.getText() + ": " + msg);
        }
    }

    //Метод интерфейса приконнектится - написать - дисконнектится - поймать ошибку

    @Override
    public synchronized void onConnectionReady(TCP_Connection tcp_connection) {
        printMessage("Connection ready...");
    }

    @Override
    public synchronized void onReceiveString(TCP_Connection tcp_connection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCP_Connection tcp_connection) {
        printMessage("Connection close");
    }

    @Override
    public synchronized void onException(TCP_Connection tcp_connection, Exception e) {
        JOptionPane.showMessageDialog(null, "Connection exception: " + e);
    }

    //сдвинуть его выше
    private synchronized void printMessage(final String str){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(str + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
