package edu.client;

import edu.connection.TCPconnection;
import edu.connection.TCPconnectionListener;
import edu.server.ChatServer;
import edu.server.ConnectBD;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

/**
 * Created by Dima on 09.07.2019.
 */

public class ClientReg {
    private static final String IP_ADDR = "127.0.0.1";
    private static final int PORT = 8189;
    private TCPconnection connection;
    private static String name, pass;
    //mTex = 1111 Пароль

    public static void main(String[] args) {
        Scanner come = new Scanner(System.in);
        boolean loop = true;
        String select;

        while (loop) {
            System.out.println("Войти/Регистрация Y/N");
            select = come.nextLine();
            loop = !loop;

            switch (select.toUpperCase()) {
                case "Y": {
                    System.out.print("Введите ваше имя: ");
                    name = come.nextLine();

                    System.out.print("Введите ваш пароль: ");
                    pass = come.nextLine();

                    User user = new User(name, pass);

                   ChatServer.AuthorisUsers(user);
                }
                break;
                case "N": {
                    System.out.print("Введите ваше имя: ");
                    name = come.nextLine();

                    System.out.print("Введите ваш пароль: ");
                    pass = come.nextLine();

                    User user = new User(name, pass);

                   ChatServer.RegistrUsers(user);

                }
                break;
                default: {
                    System.out.println("Я не понимаю что вы хотите, давайте еще раз");
                    loop = true;
                }
                break;
            }   //end switch


        }
        come.close();


    }

    public static void openWindowClient(User user) {
        new ClientWin(user);
    }

}
