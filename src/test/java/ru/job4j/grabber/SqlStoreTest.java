package ru.job4j.grabber;

import org.junit.Test;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class SqlStoreTest {

    @Test
    public void whenAddAndGet() {
        Config config = new Config();
        Connection connection = ConnectionManager.createWithRollback(config);
        try (SqlStore store = new SqlStore(connection)) {
            Post postIn = new Post("Java", "text", "link");
            store.save(postIn);

            Post postOut = store.get(x -> x.getName().equals("Java")).get(0);
            assertThat(postOut.getName(), is("Java"));
            assertThat(postOut.getText(), is("text"));
            assertThat(postOut.getLink(), is("link"));

            List<Post> list2 = store.get(x -> x.getName().equals("Python"));
            assertThat(list2, is(Collections.EMPTY_LIST));
        }
    }

    @Test
    public void whenSaveDateAndGetLast() {
        Config config = new Config();
        Connection connection = ConnectionManager.createWithRollback(config);
        try (SqlStore store = new SqlStore(connection)) {
            LocalDateTime date1 = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
            store.saveDate(date1);
            LocalDateTime date2 = LocalDateTime.of(2020, 1, 2, 0, 0, 0);
            store.saveDate(date2);
            assertThat(store.getLastDate(), is(date2));
        }
    }
}