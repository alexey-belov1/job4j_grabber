package ru.job4j.grabber;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class HtmlParseTest {

    @Test
    public void whenGetDetail() {
        String link = "https://www.sql.ru/forum/1318912/java-developer";
        Post post = new HtmlParse().detail(link);
        assertThat(post.getName(), is("Java developer"));
        assertThat(post.getText().startsWith("Компания BizApps") && post.getText().endsWith("Ваших откликов!"), is(true));
        assertThat(post.getLink(), is(link));
    }

    @Test
    public void whenGetListWithDefaultDate() {
        String link = "https://alexey-belov1.github.io/sqlgrabber.github.io/";
        HtmlParse parse = new HtmlParse();
        List<Post> list = parse.list(link);
        assertThat(list.size(), is(22));
    }

    @Test
    public void whenGetListWithSetDate() {
        String link = "https://alexey-belov1.github.io/sqlgrabber.github.io/";
        HtmlParse parse = new HtmlParse();
        parse.setLastDate(LocalDateTime.of(2020, 1, 1, 0, 0));
        List<Post> list = parse.list(link);
        assertThat(list.size(), is(19));
    }
}