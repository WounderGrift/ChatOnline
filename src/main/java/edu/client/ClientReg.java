package edu.client;

import edu.server.ChatServer;
import edu.server.ConnectBD;

import javax.swing.*;
import java.sql.*;
import java.util.Scanner;

/**
 * Created by Dima on 09.07.2019.
 */

public class ClientReg {

    public static void main(String[] args) throws SQLException {
        Scanner come = new Scanner(System.in);
        boolean loop = true;
        String select;

        while (loop) {
            System.out.println("Войти/Регистрация Y/N");
            select = come.nextLine();
            loop = !loop;
            String name, pass;

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
