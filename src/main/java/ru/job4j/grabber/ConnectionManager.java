package ru.job4j.grabber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static final Logger LOG = LogManager.getLogger(ConnectionManager.class.getName());

    private Connection init(Config config) {
        Connection connection = null;
        try (InputStream in = ConnectionManager.class.getClassLoader().getResourceAsStream("grabber.properties")) {
            Class.forName(config.get("jdbc.driver"));
            connection = DriverManager.getConnection(
                    config.get("jdbc.url"),
                    config.get("jdbc.username"),
                    config.get("jdbc.password")
            );
            LOG.info("Connection completed successfully.");
        } catch (ClassNotFoundException | SQLException | IOException e) {
            LOG.error(e.getMessage(), e);
        }

        return connection;
    }

    public static Connection createWithoutRollback(Config config) {
        return new ConnectionManager().init(config);
    }

    public static Connection createWithRollback(Config config) {
        Connection connection = new ConnectionManager().init(config);
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }

        return (Connection) Proxy.newProxyInstance(
                ConnectionManager.class.getClassLoader(),
                new Class[] {
                        Connection.class
                },
                (proxy, method, args) -> {
                    Object rsl = null;
                    if ("close".equals(method.getName())) {
                        connection.rollback();
                        connection.close();
                    } else {
                        rsl = method.invoke(connection, args);
                    }
                    return rsl;
                }
        );
    }
}