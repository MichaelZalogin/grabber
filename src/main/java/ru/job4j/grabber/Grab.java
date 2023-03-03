package ru.job4j.grabber;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import ru.job4j.dao.Store;
import ru.job4j.utils.Parse;

public interface Grab {

    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;

}