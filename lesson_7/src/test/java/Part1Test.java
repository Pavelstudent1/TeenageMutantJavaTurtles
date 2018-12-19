import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalQuery;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Part1Test {

    @Test
    public void time_time_time() {
        //show old Date
        Date date = new Date(2001, 1, 1);
        System.out.println(date);

        Instant i = Instant.now();
        System.out.println(i);

        Set<String> set = ZoneId.getAvailableZoneIds();
        //set.forEach(System.out::println);

        ZoneId zone = ZoneId.of("Asia/Singapore");
        System.out.println(i.atZone(zone)); //show toLocalTime/Date

        LocalDate ld = LocalDate.of(2001, 1, 1);
        System.out.println(ld);

        LocalTime lt = LocalTime.of(12, 12, 12);
        System.out.println(lt);

        LocalDateTime ldt = LocalDateTime.ofInstant(i, ZoneId.of("Europe/Moscow"));
        System.out.println(ldt);
    }

    @Test
    public void downloadFromSiteWithDates() throws IOException {
        //http://www.calend.ru/day/12-17-2018/

        LocalDateTime ldt = LocalDateTime.now();

        String date = ldt
                .format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));

        Document document = Jsoup.connect("http://www.calend.ru/day/" + date).get();

        Elements elements = document.body().getElementsByClass("famous-date plusyear");
        Element root = elements.get(0);

        Elements when = root.getElementsByTag("h2");
        Element elementWhen = when.get(0);
        String text = elementWhen.text();
        String d = text.substring(0, text.length() - 9);
        //System.out.println(elementWhen.text());

        Elements history = root.getElementsByTag("div");
        history.remove(0);
        List<HistoryInfo> historyList = history.stream()
                .map(e -> new HistoryInfoDTO(
                        d,
                        e.child(0).text(),
                        e.child(1).text()))
                .sorted(Comparator.comparingInt(v -> Integer.valueOf(v.year)))
                .map(h -> new HistoryInfo(
                        LocalDate.of(Integer.parseInt(h.year), ldt.getMonth(), ldt.getDayOfMonth()),
                        h.info))
                .collect(Collectors.toList());

        historyList.forEach(System.out::println);
    }

    @Test
    public void pacOfHI() {
        final String URL = "http://www.calend.ru/day/";

        List<HistoryInfoDTO> dtos = Stream.iterate(1, i -> ++i)
                .limit(90)
                .parallel() //37s915
                .map(i -> LocalDate.now().plusDays(i))
                .map(ld -> ld.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")))
                .map(path -> {
                    try {
                        return Jsoup.connect(URL + path).get();
                    } catch (IOException ignore) {
                    }
                    throw new IllegalArgumentException();
                })
                .map(d -> d.body().getElementsByClass("famous-date plusyear"))
                .map(e -> e.get(0))
                .flatMap(root -> {
                    String date = root
                            .getElementsByTag("h2")
                            .get(0)
                            .text();

                    String d = date.substring(0, date.length() - 9);

                    Elements h = root.getElementsByTag("div");
                    h.remove(0);

                    return h.stream()
                            .map(e -> new HistoryInfoDTO(d, e.child(0).text(), e.child(1).text()));
                })
                .collect(Collectors.toList());

        dtos.forEach(System.out::println);
    }

    class HistoryInfoDTO {

        private final String dayAndMonth;

        private final String year;

        private final String info;

        public HistoryInfoDTO(String dayAndMonth, String year, String info) {
            this.dayAndMonth = dayAndMonth;
            this.year = year;
            this.info = info;
        }

        public String getYear() {
            return year;
        }

        public String getData() {
            return year + " " + dayAndMonth;
        }

        public String getInfo() {
            return info;
        }

        @Override
        public String toString() {
            return "В " + getData() + "\n" + getInfo();
        }
    }

    class HistoryInfo {
        private LocalDate date;
        private String text;

        public HistoryInfo(LocalDate date, String text) {
            this.date = date;
            this.text = text;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return "HistoryInfo{" +
                    "date=" + date +
                    ", text='" + text + '\'' +
                    '}';
        }
    }

    private static List<HistoryInfo> giveHistory() throws IOException {
        LocalDateTime ldt = LocalDateTime.now();

        String date = ldt
                .format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));

        Document document = Jsoup.connect("http://www.calend.ru/day/" + date).get();

        Elements elements = document.body().getElementsByClass("famous-date plusyear");
        Element root = elements.get(0);

        Elements when = root.getElementsByTag("h2");
        Element elementWhen = when.get(0);
        String text = elementWhen.text();
        String d = text.substring(0, text.length() - 9);
        //System.out.println(elementWhen.text());

        Elements history = root.getElementsByTag("div");
        history.remove(0);

        return history.stream()
                .map(e -> new Part1Test().new HistoryInfoDTO(
                        d,
                        e.child(0).text(),
                        e.child(1).text()))
                //.sorted(Comparator.comparingInt(v -> Integer.valueOf(v.year)))
                .map(h -> new Part1Test().new HistoryInfo(
                        LocalDate.of(Integer.parseInt(h.year), ldt.getMonth(), ldt.getDayOfMonth()),
                        h.info))
                .collect(Collectors.toList());
    }

    @Test
    public void temporalQuery() throws IOException {

        final LocalDate ldt = LocalDate.of(1870, 1, 1);

        TemporalQuery<List<HistoryInfo>> untilSelectedYear = temporal -> {
            int year = temporal.get(ChronoField.YEAR);
            try {
                return giveHistory().stream()
                        .filter(h -> h.getDate().getYear() < year)
                        .collect(Collectors.toList());
            } catch (IOException ignore) {
            }
            throw new IllegalArgumentException();

        };

        List<HistoryInfo> historyInfo = untilSelectedYear.queryFrom(ldt);
        historyInfo.forEach(System.out::println);
    }

    @Test
    public void durationTest() throws IOException {
        HistoryInfo historyInfo = giveHistory().get(0);

        /*long l = Duration.between(historyInfo.date, LocalDate.now())
                .toHours();
        System.out.println(l);*/

        long diff = ChronoUnit.YEARS.between(historyInfo.date, LocalDate.now());
        System.out.println(diff);
    }

    @Test
    public void dateTimeFormatter() {
        LocalDateTime ldt = LocalDateTime.now();
        System.out.println(DateTimeFormatter.ISO_DATE.format(ldt));

        System.out.println(DateTimeFormatter
                .ISO_LOCAL_DATE_TIME.format(ldt));

        System.out.println(DateTimeFormatter
                .ofLocalizedDate(FormatStyle.FULL)
                .format(ldt));


        //new Locale.Builder().

        System.out.println(
                DateTimeFormatter
                .ofLocalizedDate(FormatStyle.FULL)
                .withLocale(Locale.ITALY)
                .format(ldt)
        );

    }
}
