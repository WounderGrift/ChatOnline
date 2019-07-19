package edu.client;

import edu.connection.TCPconnection;
import edu.connection.TCPconnectionListener;
import edu.server.ConnectBD;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by Dima on 03.07.2019.
 * Сначала запустить сервер потом ClientReg
 */

public class ClientWin extends JFrame implements ActionListener, TCPconnectionListener {
    private static final String IP_ADDR = "127.0.0.1";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private String name;
    private String msg;

    private TCPconnection connection;

    //Немножко формочек
    private final JTextArea log = new JTextArea();
    private final JTextField fieldinput = new JTextField();


    ClientWin(User user) {
        name = user.getName();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        JTextField fieldNickName = new JTextField("Вы вошли как " + name);
        fieldNickName.setEditable(false);
        fieldinput.addActionListener(this);
        add(fieldNickName, BorderLayout.NORTH);
        add(fieldinput, BorderLayout.SOUTH);

        setVisible(true);

        //  Здесь намертво вставет интерфейс
        //  Oшибка была в разных портах сервера и клиента
        try {
            connection = new TCPconnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection exception: " + e);
        }
    }

    //Написать сообщение на экран
    @Override
    public void actionPerformed(ActionEvent e) {
        this.msg = fieldinput.getText();
        if (msg.equals("")) {
            return;
        } else {
            fieldinput.setText(null);
            connection.sendString(name + ": " + msg);
        }
    }

    //Метод интерфейса приконнектится - написать - дисконнектится - поймать ошибку

    @Override
    public synchronized void onConnectionReady(TCPconnection tcp_connection) {
        printMessage("Connection ready...");
    }

    @Override
    public synchronized void onReceiveString(TCPconnection tcp_connection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPconnection tcp_connection) {
        printMessage("Connection close");
    }

    @Override
    public synchronized void onException(TCPconnection tcp_connection, Exception e) {
        JOptionPane.showMessageDialog(null, "Connection exception: " + e);
    }

    //сдвинуть его выше
    private synchronized void printMessage(final String str) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(str + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }

}