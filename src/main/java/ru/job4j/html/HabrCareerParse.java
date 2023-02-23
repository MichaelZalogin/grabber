package ru.job4j.html;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static int amountPage;

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=%d", SOURCE_LINK, amountPage);


    public static void main(String[] args) throws IOException {
        amountPage = 5;
        for (int i = 0; i <= amountPage; i++) {
            Connection connection = Jsoup.connect(PAGE_LINK);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                Element dateTimeElement = row.select(".vacancy-card__date").first();
                Element dataTime = dateTimeElement.child(0);
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                System.out.printf("%s, дата публикации: %s, %s%n", vacancyName, dataTime.attr("datetime"), link);
            });
        }
    }
}
