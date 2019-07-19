package edu.client;

import edu.connection.TCPconnection;
import edu.connection.TCPconnectionListener;

/**
 * Created by Dima on 18.07.2019.
 */
public class User {
    private String name;
    private String pass;

    public User(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public String getPass() {
        return pass;
    }

    void setName(String name) {
        this.name = name;
    }

    void setPass(String pass) {
        this.pass = pass;
    }

}
