package edu.client;

import edu.server.ChatServer;
import edu.server.ConnectBD;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

/**
 * Created by Dima on 09.07.2019.
 */

public class ClientReg{
    public static String name, pass;

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

                    ChatServer.AuthorisUsers(name, pass);
                }
                break;
                case "N": {
                    System.out.print("Введите ваше имя: ");
                    name = come.nextLine();

                    System.out.print("Введите ваш пароль: ");
                    pass = come.nextLine();

                    BASE64Encoder enc = new BASE64Encoder();
                    String encodedPass = enc.encode(pass.getBytes());
                    enc = null;

                    ChatServer.RegistrUsers(name, pass);

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

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWin();

            }
        });

    }

}
