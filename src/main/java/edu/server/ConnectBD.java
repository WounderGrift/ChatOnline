package edu.server;

import edu.client.ClientReg;
import edu.client.ClientWin;
import edu.client.User;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.*;
import java.util.Properties;


/**
 * Created by Dima on 09.07.2019.
 */

public class ConnectBD {
    private String URL;
    private String owner;
    private String password;
    private Connection con;
    private Statement st;
    private ResultSet rs;
    private PreparedStatement select;

    private String msg;

    private static ConnectBD instanse;

    public static ConnectBD getInstanse() {
        if (instanse == null) {
            instanse = new ConnectBD();
        }
        return instanse;
    }

    private ConnectBD() {

        Properties prop = new Properties();

        try (InputStream in = Files.newInputStream(Paths.get("src/main/resources/database.properties"))) {
            prop.load(in);
            URL = prop.getProperty("URL");
            owner = prop.getProperty("owner");
            password = prop.getProperty("password");

            con = DriverManager.getConnection(URL, owner, password);
            st = con.createStatement();

            System.out.println(URL + " " + owner + " " + password);
        } catch (IOException e) {
            System.out.println("Error of read properties: " + e);
        } catch (SQLException e) {
            System.out.println("Error of connection: " + e);
        }

        try {
            st.execute("CREATE TABLE IF NOT EXISTS TESTDB.PUBLIC.USER(\n" +
                    "  id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "  name VARCHAR(50),\n" +
                    "  pass VARCHAR(50));");

            st.execute("CREATE TABLE IF NOT EXISTS TESTDB.PUBLIC.MESSAGE(\n" +
                    "id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "msg VARCHAR(255));");

            st.execute("CREATE TABLE IF NOT EXISTS TESTDB.PUBLIC.USERS_MESSAGE (\n" +
                    "  USER_ID INT NOT NULL,\n" +
                    "  MSG_ID INT NOT NULL,\n" +
                    "  CONSTRAINT USERS_MESSAGE_USER_ID_fkey FOREIGN KEY (USER_ID)\n" +
                    "  REFERENCES public.USER (id),\n" +
                    "  CONSTRAINT USERS_MESSAGE_MSG_ID_fkey FOREIGN KEY (MSG_ID)\n" +
                    "  REFERENCES public.MESSAGE (ID)\n" +
                    ");");

            System.out.println("Tables is created");

             st.close();
             //con.close();

        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }


    public void authorisUsers(User user) {
        boolean gonnaToWin = false;

        try {
            PreparedStatement select = con.prepareStatement("SELECT * FROM TESTDB.PUBLIC.USER WHERE TESTDB.PUBLIC.USER.NAME = ? AND TESTDB.PUBLIC.USER.PASS = ?");
            select.setString(1, user.getName());
            select.setString(2, encodedPassword(user));
            rs = select.executeQuery();

            while (rs.next()) {
                System.out.println("Log in " + rs.getInt("id") + " " + rs.getString("name") + " " + rs.getString("pass"));
                gonnaToWin = !gonnaToWin;
            }
            select.close();

            if (gonnaToWin) {
                ClientReg.openWindowClient(user);
            } else {
                System.out.println("Неправильное имя или пароль");
                return;
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }


    public void registrUsers(User user) {
        System.out.println(user);
        try {
            int id = 0;
            ResultSet rs;

            select = con.prepareStatement("SELECT * FROM TESTDB.PUBLIC.USER WHERE TESTDB.PUBLIC.USER.NAME = ?");
            select.setString(1, user.getName());

            rs = select.executeQuery();

            while (rs.next()) {
                System.out.println("Этот пользователь уже есть");
                select.close();

                return;
            }

            select.close();
//===========================Узнать, есть ли этот пользователь

            select = con.prepareStatement("SELECT * FROM TESTDB.PUBLIC.USER");
            rs = select.executeQuery();

            while (rs.next()) {
                System.out.println("Exist user: " + rs.getInt("ID") + " " + rs.getString("NAME") + " " + rs.getString("PASS"));
                id = rs.getInt("ID") + 1;
            }
            select.close();
//============================Узнать последний ID
            select = con.prepareStatement("INSERT INTO TESTDB.PUBLIC.USER (id, name, pass) VALUES (?, ?, ?)");
            select.setInt(1, id);
            select.setString(2, user.getName());
            select.setString(3, encodedPassword(user));

            select.executeUpdate();
            select.close();
//=================================Записать пользователя
            select = con.prepareStatement("SELECT * FROM TESTDB.PUBLIC.USER WHERE ID = " + id);
            rs = select.executeQuery();

            while (rs.next()) {
                System.out.println("Sign in " + rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3));
            }
            select.close();

            ClientReg.openWindowClient(user);

        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }

    public void messagePutToBD(String msg) {
        try {

            ResultSet rs; int id = 1;

            select = con.prepareStatement("SELECT * FROM TESTDB.PUBLIC.MESSAGE");
            rs = select.executeQuery();

            while (rs.next()) {
                System.out.println("Message From ID " + rs.getInt("ID") + " And Name " + rs.getString("MSG"));
                id = rs.getInt("ID") + 1;
            }
            select.close();
//=========================================Узнать последний ID
            select = con.prepareStatement("INSERT INTO TESTDB.PUBLIC.MESSAGE (ID, MSG) VALUES (?, ?)");
            select.setInt(1, id);
            select.setString(2, msg);

            select.executeUpdate();
            select.close();
//=================================Записать сообщение
            select = con.prepareStatement("SELECT * FROM TESTDB.PUBLIC.MESSAGE WHERE ID = " + id);
            rs = select.executeQuery();

            while (rs.next()) {
                System.out.println("New Message: " + rs.getInt(1) + " " + rs.getString(2));
                System.out.println();
            }
            select.close();


        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }

    public String encodedPassword(User user) {
        BASE64Encoder enc = new BASE64Encoder();
        String encodedPass = enc.encode(user.getPass().getBytes());
        enc = null;
        return encodedPass;
    }

    public String decodedPassword(String encodedPass) {
        String decodedPass = null;
        try {
            BASE64Decoder dec = new BASE64Decoder();
            decodedPass = new String(dec.decodeBuffer(encodedPass));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return decodedPass;
    }

}//end