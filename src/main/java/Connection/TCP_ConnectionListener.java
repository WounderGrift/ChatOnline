package Connection;

public interface TCP_ConnectionListener {
    void onConnectionReady(TCP_Connection tcp_connection);

    void onReceiveString(TCP_Connection tcp_connection, String value);

    void onDisconnect(TCP_Connection tcp_connection);

    void onException(TCP_Connection tcp_connection, Exception e);
}