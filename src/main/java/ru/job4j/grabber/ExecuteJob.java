package ru.job4j.grabber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

import java.sql.Connection;
import java.time.LocalDateTime;

public class ExecuteJob implements Job {
    private static final Logger LOG = LogManager.getLogger(ExecuteJob.class.getName());

    @Override
    public void execute(JobExecutionContext context) {
        LOG.info("Start program...");

        Config config = new Config();
        Connection connection = ConnectionManager.createWithRollback(config);
        try (SqlStore store = new SqlStore(connection)) {
            HtmlParse parse = new HtmlParse();

            LocalDateTime lastDate = store.getLastDate();
            if (lastDate != null) {
                parse.setLastDate(lastDate);
                LOG.info(String.format("Last launch date: %s", lastDate.toString()));
            } else {
                parse.setLastDate(LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0, 0));
                LOG.info("First launch.");
            }

            new InitGrab().init(parse, store);
            store.saveDate(LocalDateTime.now());
        }

        LOG.info("Programm completed.");
    }
}