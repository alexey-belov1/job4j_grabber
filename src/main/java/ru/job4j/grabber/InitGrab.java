package ru.job4j.grabber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class InitGrab implements Grab {
    private static final Logger LOG = LogManager.getLogger(InitGrab.class.getName());

    private final String link = "https://www.sql.ru/forum/job-offers";

    @Override
    public void init(Parse parse, Store store) {
        List<Post> posts = parse.list(link);
        for (Post p : posts) {
            store.save(p);
        }
    }
}