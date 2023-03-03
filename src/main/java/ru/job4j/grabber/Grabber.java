package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.dao.*;
import ru.job4j.entity.Post;
import ru.job4j.utils.*;
import ru.job4j.html.HabrCareerParse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.List;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {

    private final Parse parse;
    private final Store store;
    private final Scheduler scheduler;
    private final PropertiesUtil cfg;
    private final Connection cn;

    public Grabber(Parse parse, Store store, Scheduler scheduler, PropertiesUtil cfg, Connection cn) {
        this.parse = parse;
        this.store = store;
        this.scheduler = scheduler;
        this.cfg = cfg;
        this.cn = cn;
    }

    @Override
    public void start() throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        data.put("parseUrl", cfg.get("parseUrl"));
        data.put("amountPage", cfg.get("amountPage"));
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        var time = Integer.parseInt(cfg.get("time"));
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(time)
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public static void main(String[] args) throws Exception {
        var readProperties = new PropertiesUtil("application.properties");
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        var parse = new HabrCareerParse(new HabrCareerDateTimeParser());
        var store = new PsqlStore(readProperties);
        Connection connection = new ConnectionManager(readProperties).open();
        new Grabber(parse, store, scheduler, readProperties, connection).start();
    }

    /**
     * public void web(Store store) {
     * new Thread(() -> {
     * try (ServerSocket server = new ServerSocket(Integer.parseInt(cfg.get("port")))) {
     * while (!server.isClosed()) {
     * Socket socket = server.accept();
     * try (OutputStream out = socket.getOutputStream()) {
     * out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
     * for (Post post : store.getAll()) {
     * out.write(post.toString().getBytes());
     * out.write(System.lineSeparator().getBytes());
     * }
     * } catch (IOException io) {
     * io.printStackTrace();
     * }
     * }
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * }).start();
     * }
     */

    public static class GrabJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            String parseUrl = (String) map.get("parseUrl");
            String amountPage = (String) map.get("amountPage");
            List<Post> postList = parse.list(parseUrl, Integer.parseInt(amountPage));
            for (Post post : postList) {
                store.save(post);
            }
        }
    }
}