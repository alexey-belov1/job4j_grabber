package ru.job4j.grabber;

import java.util.Objects;

public class Post {
    private String name;
    private String text;
    private String link;

    public Post(String name, String text, String link) {
        this.name = name;
        this.text = text;
        this.link = link;
    }

    public String getName() {
        return this.name;
    }

    public String getText() {
        return this.text;
    }

    public String getLink() {
        return this.link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post v = (Post) o;
        return Objects.equals(this.name, v.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
}
