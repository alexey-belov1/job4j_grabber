package ru.job4j.grabber;

import java.util.List;
import java.util.function.Predicate;

public interface Store {
    void save(Post post);

    List<Post> get(Predicate<Post> filter);
}