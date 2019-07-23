package edu.connection;

/**
 * Created by Dima on 18.07.2019.
 */
public class User {
    private String name;
    private String pass;
    private String reg;

    public User(String name, String pass, String reg) {
        this.name = name;
        this.pass = pass;
        this.reg = reg;
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


    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }
}
