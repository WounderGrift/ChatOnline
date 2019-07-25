package edu.server;

import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.io.InputStream;
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
    private boolean openform;

    private String msg;

    private static ConnectBD instanse;

    public static ConnectBD getInstanse() {
        if (instanse == null) {
            instanse = new ConnectBD();
        }
        return instanse;
    }

    public boolean isOpenform() {
        return openform;
    }

    public void setOpenform(boolean openform) {
        this.openform = openform;
    }

    private ConnectBD() {
        System.out.println(Thread.currentThread().getContextClassLoader());
        String file = "database.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties prop = new Properties();
        System.out.println(loader.getResourceAsStream(file));
        try (InputStream in = loader.getResourceAsStream(file)) {
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


    public void authorisUsers(String name, String pass) {
        boolean gonnaToWin = false;
        setOpenform(false);
        try {
            PreparedStatement select = con.prepareStatement("SELECT * FROM TESTDB.PUBLIC.USER WHERE TESTDB.PUBLIC.USER.NAME = ? AND TESTDB.PUBLIC.USER.PASS = ?");
            select.setString(1, name);
            select.setString(2, pass);
            rs = select.executeQuery();

            while (rs.next()) {
                System.out.println("Log in " + rs.getInt("id") + " " + rs.getString("name") + " " + rs.getString("pass"));
                gonnaToWin = !gonnaToWin;
            }
            select.close();

            if (gonnaToWin) {
                setOpenform(true);
            } else {
                System.out.println("Неправильное имя или пароль");
                return;
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }


    public void registrUsers(String name, String pass) {
        System.out.println(name + " " + pass);
        setOpenform(false);
        try {
            int id = 0;
            ResultSet rs;

            select = con.prepareStatement("SELECT * FROM TESTDB.PUBLIC.USER WHERE TESTDB.PUBLIC.USER.NAME = ?");
            select.setString(1, name);

            rs = select.executeQuery();

            while (rs.next()) {
                System.out.println("Этот пользователь уже есть");
                select.close();
                setOpenform(false);
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
            select.setString(2, name);
            select.setString(3, pass);

            select.executeUpdate();
            select.close();
//=================================Записать пользователя
            select = con.prepareStatement("SELECT * FROM TESTDB.PUBLIC.USER WHERE ID = " + id);
            rs = select.executeQuery();

            while (rs.next()) {
                System.out.println("Sign in " + rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3));
                setOpenform(true);
            }
            select.close();

        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }

    public void messagePutToBD(String msg) {
        try {

            ResultSet rs;
            int id = 1;

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

    public String decodedPassword(String encodedPass) {
        String decodedPass = null;
        try {
            BASE64Decoder dec = new BASE64Decoder();
            decodedPass = new String(dec.decodeBuffer(encodedPass));
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        return decodedPass;
    }

}//end