package siteParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class SiteParser extends RecursiveAction {
    private static Connection connection;
    public final static String USER_NAME = "root";
    public final static String PASSWORD = "16Sokol90@";
    public final static String Url = "jdbc:mysql://localhost:3306/search_engine";
    public final static String PATH = "http://www.playback.ru/";
    private final static String regex = "[^А-Яа-яA-Za-z<>]+";

    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(new SiteParser());
    }

    private static void pageParser() throws IOException, SQLException, InterruptedException {

        org.jsoup.Connection.Response d = Jsoup.connect(PATH).execute();
        Document document = Jsoup.connect(SiteParser.PATH)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com")
                .maxBodySize(0)
                .get();


        Elements elements = document.getElementsByAttribute("href");
        for (Element element : elements) {
            String link = element.absUrl("href");

            if (link.startsWith(PATH) && link.endsWith(".html")) {

                String sql = "INSERT INTO page(path, code, content) " +
                        "VALUES('" + link.substring(22) + "', '" + d.statusCode() + "', '" + document.toString().replaceAll(regex, "") + "')";

                connection.createStatement().executeUpdate(sql);
         //  connection.close();
            }
        }
    }

    private static void connectToDateBase() {
        if (connection == null) {
            try {

                connection = DriverManager.getConnection(Url, USER_NAME, PASSWORD);

                connection.createStatement().executeUpdate("DROP TABLE IF EXISTS page");
                connection.createStatement().executeUpdate("CREATE TABLE page (" +
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "path TEXT NOT NULL," +
                        "code INT NOT NULL," +
                        "content MEDIUMTEXT NOT NULL)");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void compute() {
        connectToDateBase();
        try {
            pageParser();
        } catch (IOException | SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        invokeAll();
    }

}
