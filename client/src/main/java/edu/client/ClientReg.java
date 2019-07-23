package edu.client;

import edu.connection.User;
//import edu.server.ChatServer;
import java.util.Scanner;

/**
 * Created by Dima on 09.07.2019.
 */

public class ClientReg {
    private static String name, pass, reg;

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
                    reg = "Y";
                }
                break;
                case "N": {
                    reg = "N";
                }
                break;
                default: {
                    System.out.println("Я не понимаю что вы хотите, давайте еще раз");
                    loop = true;
                }
                break;
            }   //end switch
        }

        System.out.print("Введите ваше имя: ");
        name = come.nextLine();

        System.out.print("Введите ваш пароль: ");
        pass = come.nextLine();


        User user = new User(name, pass, reg);

        ClientWin cw = new ClientWin(user);

        come.close();


    }

}
