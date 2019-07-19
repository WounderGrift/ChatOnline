package edu.connection;

import edu.client.User;

public interface TCPconnectionListener {

    void onConnectionReady(TCPconnection TCPconnection);

    void onReceiveString(TCPconnection TCPconnection, String value);

    void onDisconnect(TCPconnection TCPconnection);

    void onException(TCPconnection TCPconnection, Exception e);
}