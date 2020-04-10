package ru.job4j.grabber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.regex.Pattern;

public class HtmlParse implements Parse {

    private static final Logger LOG = LogManager.getLogger(HtmlParse.class.getName());

    private LocalDateTime lastDate = null;

    private DateTimeFormatter fmt = new DateTimeFormatterBuilder()
            .appendPattern("d ")
            .appendText(ChronoField.MONTH_OF_YEAR, new HashMap<>() { {
                put(1L, "янв"); put(2L,  "фев"); put(3L,  "мар"); put(4L,  "апр");
                put(5L, "май"); put(6L,  "июн"); put(7L,  "июл"); put(8L,  "авг");
                put(9L, "сен"); put(10L, "окт"); put(11L, "ноя"); put(12L, "дек");
            } })
            .appendPattern(" yy, HH:mm")
            .toFormatter(new Locale("ru"));

    private LocalDateTime parseDate(String dateIn) {
        LocalDateTime dateOut;
        if (dateIn.contains("сегодня")) {
            dateOut = LocalDateTime.of(
                    LocalDate.now(),
                    LocalTime.parse(dateIn.split(" ")[1])
            );
        } else if (dateIn.contains("вчера")) {
            dateOut = LocalDateTime.of(
                    LocalDate.now().minusDays(1L),
                    LocalTime.parse(dateIn.split(" ")[1])
            );
        } else {
            dateOut = LocalDateTime.parse(dateIn, fmt);
        }

        return dateOut;
    }

    private boolean filter(String str) {
        return Pattern.compile("(java)(?!\\s?script)", Pattern.CASE_INSENSITIVE).matcher(str).find();
    }

    private String nextPage(Document doc) {
        int current = Integer.parseInt(doc.select("table.sort_options td b").text());
        return doc.select(String.format("table.sort_options td a:contains(%s)", current + 1)).attr("href");
    }

    @Override
    public List<Post> list(String link) {
        LOG.info("Start parsing...");

        Set<Post> posts = new LinkedHashSet<>();
        String nextPage = link;
        try {
            exitlabel: do {
                Document doc = Jsoup.connect(nextPage).get();
                Elements table = doc.select("table.forumtable tr:has(td)");
                for (Element tr : table) {
                    Elements td = tr.select("td");
                    String text = td.get(1).text();
                    if (filter(text)) {
                        LocalDateTime date = parseDate(td.get(5).text());
                        if (lastDate != null && lastDate.isAfter(date)) {
                            break exitlabel;
                        }
                        String href = td.get(1).select("a").attr("href");
                        posts.add(detail(href));
                    }
                }
                nextPage = nextPage(doc);
            } while (!nextPage.isEmpty());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        LOG.info(String.format("%s posts found.", posts.size()));
        return new ArrayList<>(posts);
    }

    @Override
    public Post detail(String link) {
        Post post = null;
        try {
            Document doc = Jsoup.connect(link).get();
            String name = doc.title().split(" / Вакансии")[0];
            String text = doc.select("td.msgBody").get(1).text();
            post = new Post(name, text, link);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return post;
    }

    public void setLastDate(LocalDateTime lastDate) {
        this.lastDate = lastDate;
    }
}
