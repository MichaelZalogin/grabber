# Агрегатор вакансий
[![Build Status](https://www.travis-ci.com/Mosquitosd3/job4j_grabber.svg?branch=master)](https://www.travis-ci.com/MichaelZalogin/grabber)
[![codecov](https://codecov.io/gh/Mosquitosd3/job4j_grabber/branch/master/graph/badge.svg)](https://codecov.io/gh/MichaelZalogin/grabber)
---
* Система запускается по расписанию.
  Период запуска указывается в настройках - app.properties.
  Первый сайт sql.ru. В нем есть раздел job.
  Программа должна считывать все вакансии и записывать их в базу.
  Доступ к интерфейсу через REST API.

___

### Расширение:
1. В проект можно добавить новые сайты без изменения кода.
2. В проекте можно сделать параллельный парсинг сайтов.
___
### Technologies:
* Java core
* Jsoup
* Maven
* Quartz-Scheduler
* JDBC, PostgreSQL
* Travis CI
