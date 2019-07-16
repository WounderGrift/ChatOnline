package edu.server;

import edu.client.ClientReg;
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
    public String URL;
    public String owner;
    public String password;
    public Connection con;
    public Statement st;
    public ResultSet rs;
    public PreparedStatement select;

    public static void main(String[] args) {
        new ConnectBD();
    }

    ConnectBD() {
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

    }

    public void CreateTable() {
         try{

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

                   // st.close();
                   // con.close();

            } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    public void authorisUsers() {
        boolean gonnaToWin = false;

      try{
            PreparedStatement select = con.prepareStatement("SELECT * FROM TESTDB.PUBLIC.USER WHERE TESTDB.PUBLIC.USER.NAME = ? AND TESTDB.PUBLIC.USER.PASS = ?");
            select.setString(1, ClientReg.getName());
            select.setString(2, encodedPassword());
            rs = select.executeQuery();

            while (rs.next()) {
                    System.out.println("Log in " + rs.getInt("id") + " " + rs.getString("name") + " " + rs.getString("pass"));
                    gonnaToWin = !gonnaToWin;
            }
            select.close();

            if(gonnaToWin){
                ClientReg.openWindowClient();
            } else {
                System.out.println("Неправильное имя или пароль");
                return;
            }

        } catch (SQLException e) {
          System.out.println("Error: " + e);
      }
    }


    public void registrUsers()  {

      try{
          int id = 0;
          ResultSet rs;

          select = con.prepareStatement("SELECT * FROM TESTDB.PUBLIC.USER WHERE TESTDB.PUBLIC.USER.NAME = ?");
          select.setString(1, ClientReg.getName());

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
            select.setString(2, ClientReg.getName());
            select.setString(3, encodedPassword());

            select.executeUpdate();
            select.close();
//=================================Записать пользователя
            select = con.prepareStatement("SELECT * FROM TESTDB.PUBLIC.USER WHERE ID = " + id +" ");
            rs = select.executeQuery();

            while (rs.next()) {
                System.out.println("Sign in " + rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3));
            }

            select.close();
//=================================Показать на консоль кто зарегистрировался
            ClientReg.openWindowClient();

        } catch (SQLException e) {
          System.out.println("Error: " + e);
      }
    }

    public String encodedPassword(){
        BASE64Encoder enc = new BASE64Encoder();
        String encodedPass = enc.encode(ClientReg.getPass().getBytes());
        enc = null;
        return encodedPass;
    }

    public String decodedPassword(String encodedPass){
        String decodedPass = null;
        try{
            BASE64Decoder dec = new BASE64Decoder();
            decodedPass = new String(dec.decodeBuffer(encodedPass));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return decodedPass;
    }

}//end