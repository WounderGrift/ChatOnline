package edu.client;

import edu.server.ChatServer;
import javax.swing.*;
import java.sql.*;
import java.util.Scanner;

/**
 * Created by Dima on 09.07.2019.
 */

public class ClientReg{
    public static String name;
    public static String pass;

    public static String getName() {
        return name;
    }

    public static String getPass() {
        return pass;
    }

    public static void main(String[] args) throws SQLException {
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

                    ChatServer.AuthorisUsers();
                }
                break;
                case "N": {
                    System.out.print("Введите ваше имя: ");
                    name = come.nextLine();

                    System.out.print("Введите ваш пароль: ");
                    pass = come.nextLine();

                    ChatServer.RegistrUsers();

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

    public static void openWindowClient(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWin();

            }
        });
    }


}
