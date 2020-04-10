package ru.job4j.grabber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SqlStore implements Store, AutoCloseable {

    private static final Logger LOG = LogManager.getLogger(SqlStore.class.getName());

    private Connection connection;

    public SqlStore(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement st = connection
                .prepareStatement("insert into post(name, text, link) values(?,?,?)")) {
            st.setString(1, post.getName());
            st.setString(2, post.getText());
            st.setString(3, post.getLink());
            st.executeUpdate();
            LOG.info("New post added to the database.");
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public List<Post> get(Predicate<Post> filter) {
        List<Post> list = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("select * from post;")) {
            while (rs.next()) {
                Post post = new Post(rs.getString("name"), rs.getString("text"), rs.getString("link"));
                if (filter.test(post)) {
                    list.add(post);
                }
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                LOG.info("Close database connection.");
            } catch (SQLException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public LocalDateTime getLastDate() {
        LocalDateTime lastDate = null;
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("select date from log")) {
            while (rs.next()) {
                lastDate = rs.getTimestamp("date").toLocalDateTime();
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return lastDate;
    }

    public void saveDate(LocalDateTime date) {
        try (PreparedStatement st = connection
                .prepareStatement("insert into log(date) values(?)")) {
            st.setTimestamp(1, Timestamp.valueOf(date));
            st.execute();
            LOG.info(String.format("Launch date saved: %s", date.toString()));
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
