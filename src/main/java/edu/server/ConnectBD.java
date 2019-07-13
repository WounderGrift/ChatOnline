package edu.server;

import com.sun.xml.internal.bind.v2.model.core.ID;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.*;


/**
 * Created by Dima on 09.07.2019.
 */

public class ConnectBD {
    public static String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    public static String owner = "sa";
    public static String password = "";

    public static void main(String[] args) {
        new ConnectBD();
        
    }

    public ConnectBD() {
        try(Connection con = DriverManager.getConnection(URL, owner, password); Statement st = con.createStatement()) {

                st.execute("CREATE TABLE USER(\n" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "  name VARCHAR(50),\n" +
                        "  pass VARCHAR(50));");

                st.execute("CREATE TABLE MESSAGE(\n" +
                        "id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "msg VARCHAR(255));");

                st.execute("CREATE TABLE \"USERS_MESSAGE\"\n" +
                        "(\n" +
                        "  USER_ID INT NOT NULL,\n" +
                        "  MSG_ID INT NOT NULL,\n" +
                        "  CONSTRAINT USERS_MESSAGE_USER_ID_fkey FOREIGN KEY (USER_ID)\n" +
                        "  REFERENCES public.USER (id),\n" +
                        "  CONSTRAINT USERS_MESSAGE_MSG_ID_fkey FOREIGN KEY (MSG_ID)\n" +
                        "  REFERENCES public.MESSAGE (ID)\n" +
                        ");");

                    st.close();
                    con.close();

                    System.out.println("Tables is created");

            } catch (SQLException e) {
                e.printStackTrace();
            }

    }

    public static void AuthorisUsers(String name, String pass){

        try(Connection con = DriverManager.getConnection(URL, owner, password); Statement st = con.createStatement();
        ResultSet resultSet = st.executeQuery("SELECT * FROM USER")){
            String decodedPass = null;
            System.out.println("Initial user's table content:");
            while(resultSet.next()){
             /*   if(name == resultSet.getString(1)) {
                    String n = resultSet.getString(1);
                }

                if(pass == resultSet.getString(2)) {


                    try{
                        BASE64Decoder dec = new BASE64Decoder();
                        decodedPass = new String(dec.decodeBuffer(pass));
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }

                    String p = resultSet.getString(2);
                }*/
                int t = resultSet.getInt("ID");
                String n = resultSet.getString("NAME");
                String p = resultSet.getString("PASS");

                System.out.println("Login in " + n + p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   public static void RegistrUsers(String name, String pass) {
        int id = 2;

         try (Connection con = DriverManager.getConnection(URL, owner,  password); Statement st = con.createStatement()) {
       try (PreparedStatement ps = con.prepareStatement("INSERT INTO TESTDB.PUBLIC.USER (ID, NAME, PASS) VALUES (?, ?, ?)")) {
           ps.setInt(1, id);
           ps.setString(2, name);
           ps.setString(3, pass);

           ps.execute();
           System.out.println("Sign in " + name + " " + pass);


       } catch (SQLException e) {
           e.printStackTrace();
       }

             st.close();
             con.close();

        } catch (SQLException e) {
             e.printStackTrace();
         }

   }

}//end
