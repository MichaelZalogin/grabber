package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.utils.ConnectionManager;
import ru.job4j.utils.PropertiesUtil;

import java.sql.*;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        try (var connection = new ConnectionManager(new PropertiesUtil("database.properties")).open()) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", connection);
            JobDetail job = newJob(Rabbit.class).usingJobData(data).build();
            int secondInterval = Integer.parseInt(new PropertiesUtil("rabbit.properties").get("rabbit.interval"));
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(secondInterval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    static private void insert(Connection connection, long currentTimeMillis) {
        try (var statement = connection.prepareStatement("""
                INSERT INTO rabbit_schema.rabbit (created_date)
                VALUES (?);
                        """)) {
            statement.setLong(1, System.currentTimeMillis());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection connection = (Connection) context.getJobDetail()
                    .getJobDataMap().get("connection");
            insert(connection, System.currentTimeMillis());
        }
    }
}