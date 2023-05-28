package com.ll.myMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionPool {
    private Map<Long, Connection> connections;
    private String host;
    private int port;
    private String dbName;
    private String username;
    private String password;

    public ConnectionPool(String host, int port, String username, String password, String dbName) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.dbName = dbName;

        connections = new HashMap<>();
    }

    public ConnectionPool(String host, String username, String password, String dbName) {
        this(host, 3306, username, password, dbName);
    }

    public Connection getConnection() {
        long currentThreadId = Thread.currentThread().getId();

        if (connections.containsKey(currentThreadId) == false) {
            createConnection(currentThreadId);
        }

        return connections.get(currentThreadId);
    }

    private void createConnection(long currentThreadId) {
        loadDriver();

        Connection connection = null;

        String url = "jdbc:mariadb://" + host + ":" + port + "/" + dbName
                + "?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul&useOldAliasMetadataBehavior=true&zeroDateTimeBehavior=convertToNull";
        try {
            connection = DriverManager.getConnection(url, username, password);
            connections.put(currentThreadId, connection);
        } catch (SQLException e) {
            closeConnection();
            throw new MyMapException(e);
        }
    }

    private void loadDriver() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new MyMapException(e);
        }
    }

    public void closeConnection() {
        long currentThreadId = Thread.currentThread().getId();

        if (connections.containsKey(currentThreadId) == false) {
            return;
        }

        Connection connection = connections.get(currentThreadId);

        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new MyMapException(e);
        }

        connections.remove(currentThreadId);
    }
}
