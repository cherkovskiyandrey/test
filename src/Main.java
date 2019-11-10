import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss.SSS");

    public static void main(String[] args) throws IOException {

        List<Driver> drivers = Stream.concat(
                toStream("start.log"),
                Stream.concat(
                        toStream("end.log"),
                        toStream("abbreviations.txt")
                ))
                .map(Main::toDriver)
                .collect(Collectors.groupingBy(Driver::getId, Collectors.reducing(Main::mergeDriverInfo)))
                .entrySet().stream()
                .sorted(Comparator.comparingLong(entry -> entry.getValue().map(Driver::getTimeInMs).orElse(0L)))
                .filter(entry -> entry.getValue().isPresent())
                .map(entry -> entry.getValue().get())
                .collect(Collectors.toList());

        IntStream.range(0, drivers.size())
                .limit(15)
                .forEach(i -> System.out.printf("%d. %s%n", i + 1, drivers.get(i)));
        System.out.println("-----------------");
        IntStream.range(0, drivers.size())
                .skip(15)
                .forEach(i -> System.out.printf("%d. %s%n", i + 1, drivers.get(i)));

    }

    private static Stream<String> toStream(String fileName) throws IOException {
        return Files.readAllLines(Paths.get(fileName)).stream();
    }

    private static Driver toDriver(String s) {
        if (s.contains("|")) {
            int i = s.indexOf("|");
            String id = s.substring(0, i).trim();
            String desc = s.substring(i + 1).trim();
            return new Driver(id, null, null, desc);
        }

        String[] split = s.split("2018");
        String id = split[0];
        LocalDateTime time = LocalDateTime.parse("2018" + split[1], DATE_TIME_FORMAT);
        return new Driver(id, time, time, null);
    }

    private static Driver mergeDriverInfo(Driver left, Driver right) {
        return new Driver(
                left.getId(),
                Optional.ofNullable(left.getStartTime())
                        .map(lTime -> Optional.ofNullable(right.getStartTime())
                                .filter(rTime -> lTime.compareTo(rTime) > 0)
                                .orElse(lTime))
                        .orElse(right.getStartTime()),
                Optional.ofNullable(left.getEndTime())
                        .map(lTime -> Optional.ofNullable(right.getEndTime())
                                .filter(rTime -> lTime.compareTo(rTime) <= 0)
                                .orElse(lTime))
                        .orElse(right.getEndTime()),
                Optional.ofNullable(left.getDescr()).orElse(right.getDescr()));
    }
}
