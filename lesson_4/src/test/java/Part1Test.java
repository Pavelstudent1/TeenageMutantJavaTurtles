
import model.*;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static model.Gender.MALE;

public class Part1Test {


    @Test
    public void collectStream() {
        Collection<Integer> c = Arrays.asList(1,2,3,4,5,6);
        Collection<Integer> c2 = Arrays.asList(1,2,7,8,9,10);

        List<Integer> list = Stream.of(c, c2)
                .flatMap(Collection::stream)
                .collect(toList());

        System.out.println(list);

        Set<Integer> set = Stream.of(c, c2)
                .flatMap(Collection::stream)
                .collect(toSet());

        System.out.println(set);

        LinkedHashSet<Integer> hashSet = Stream.of(c, c2)
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        System.out.println(hashSet);

        Map<Integer, Integer> map = Stream.of(c, c2)
                .flatMap(Collection::stream)
                .collect(toMap(
                        Function.identity(),
                        integer -> 1,
                        //(val, val2) -> val + val2));
                        Integer::sum));

        System.out.println(map);
    }



    @Test
    public void adultOnly() {
        House house = generateHouse();

        List<Tenant> adultMen = house.getFlats().stream()
                .flatMap(f -> f.getTenants().stream())
                .collect(Collectors.collectingAndThen(
                        toList(),
                        tenants -> tenants.stream()
                                .filter(t -> t.getAge() > 18)
                                .collect(collectingAndThen(
                                        toList(),
                                        tenants1 -> tenants1.stream()
                                                .filter(t -> t.getGender() == MALE)
                                                .collect(toList())))
                ));
        System.out.println(adultMen);
    }

    @Test
    public void joinCollectors() {
        String s = generateHouse().getFlats().stream()
                .flatMap(f -> f.getTenants().stream())
                .map(Tenant::getLastName)
                .distinct()
                .collect(Collectors.joining(", ", "Families: ", ". House 66"));
        System.out.println(s);
    }

    private static Stream<Tenant> getAll(Flat f){
        return f.getTenants().stream();
    }

    @Test
    public void countingCollectors() {
        long countElements = generateHouse().getFlats().stream()
                .flatMap(Part1Test::getAll)
                .count();

        System.out.println(countElements);

        long cE = generateHouse().getFlats().stream()
                .flatMap(Part1Test::getAll)
                .collect(Collectors.counting());

        System.out.println(cE);
    }

    @Test
    public void summarizingCollectors() {
        IntSummaryStatistics statistics = generateHouse().getFlats().stream()
                .flatMap(Part1Test::getAll)
                .mapToInt(Tenant::getAge)
                .summaryStatistics();

        System.out.println(statistics);
        IntSummaryStatistics statistics1 = generateHouse().getFlats().stream()
                .flatMap(Part1Test::getAll)
                .collect(Collectors.summarizingInt(Tenant::getAge));
        System.out.println(statistics1);

        statistics.accept(22);
        statistics.combine(statistics1);
        System.out.println(statistics.getCount());
    }

    @Test
    public void groupingByCollectors() {
        String families = generateHouse().getFlats().stream()
                .flatMap(f -> f.getTenants().stream())
                .collect(collectingAndThen(
                        groupingBy(Tenant::getLastName),
                        f -> {
                            StringBuilder sb = new StringBuilder();
                            f.forEach((s, tenants) -> {
                                tenants.forEach(t -> sb.append(t.getFirstName()).append(", "));
                                sb.append(s).append("<f> ");
                            });
                            return sb.toString();
                        }
                ));
        System.out.println(families);
    }

    @Test
    public void groupingByWithMapping() {
        Map<String, Set<String>> families = generateHouse().getFlats().stream()
                .flatMap(Part1Test::getAll)
                .collect(groupingBy(
                        Tenant::getLastName,
                        mapping(Tenant::getFirstName, toSet())
                ));
        families.forEach((s, fNames) -> System.out.println(s + ": " + fNames));
    }

    @Test
    public void partitionByCollectors() {
        Map<Boolean, List<Tenant>> map = generateHouse().getFlats().stream()
                .flatMap(Part1Test::getAll)
                .collect(Collectors.partitioningBy(t -> t.getAge() > 18));

        map.forEach((b, tenants) -> System.out.println((b ? "Adults " : "Young ") + tenants.toString()));
    }

    @Test
    public void fCollector() {
        Collector<Tenant, List<String>, List<String>> collectFL = new Collector<Tenant, List<String>, List<String>>() {
            @Override
            public Supplier<List<String>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<String>, Tenant> accumulator() {
                return (strings, tenant) -> strings.add(tenant.getFirstName() + " " + tenant.getLastName());
            }

            @Override
            public BinaryOperator<List<String>> combiner() {
                return (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                };
            }

            @Override
            public Function<List<String>, List<String>> finisher() {
                return Function.identity();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Stream.of(Characteristics.IDENTITY_FINISH).collect(Collectors.toSet());
            }
        };

        List<String> collect = generateHouse().getFlats().stream()
                .flatMap(Part1Test::getAll)
                .collect(collectFL);
        collect.forEach(System.out::println);
    }

    @Test
    public void sCollector() {
        Collector<Tenant, List<String>, List<String>> collectFL = Collector.of(
                ArrayList::new,
                (list, t) -> list.add(t.getFirstName() + " " + t.getLastName()),
                (l1, l2) -> {l1.addAll(l2); return l1;}
                //Function.identity(),
                //Collector.Characteristics.IDENTITY_FINISH
        );

        List<String> list = generateHouse().getFlats().stream()
                .flatMap(Part1Test::getAll)
                .collect(collectFL);
        list.forEach(System.out::println);
    }

    ///////////////////////// STREAM TYPES ///////////////////////////////////////

    @Test
    public void sequentialStream() {
        List<Flat> flats = generateHouse().getFlats();

        flats.add(new Flat(3, 100_000, false, null));

        Map<Integer, List<Integer>> pricesOnFlats = flats.stream()
                .sequential()
                .collect(groupingBy(
                        Flat::getPrice,
                        mapping(Flat::getRooms, toList())));
        System.out.println(pricesOnFlats);
    }

    @Test
    public void parallelStream() {

       /* Stream.of(1,2,3,4,5,6,7,8)
                .parallel()
                .forEach(System.out::println);

        System.out.println("****");

        Stream.of(1,2,3,4,5,6,7,8,9,10,11,12,13)
                .parallel() // by 4 elements because 4 CPU
                .forEach(e -> { // show then Ordered
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {}
                    System.out.println(e);
                });*/

        long l = System.currentTimeMillis();
        List<Tenant> tenants = generateHouse().getFlats().stream()
                .flatMap(Part1Test::getAll)
                .filter(t -> t.getAge() > 18)
                .sorted(Comparator.comparingInt(Tenant::getAge))
                .collect(toList());

        long l2 = System.currentTimeMillis();

        List<Tenant> tenants2 = generateHouse().getFlats().parallelStream()
                .flatMap(Part1Test::getAll)
                .filter(t -> t.getAge() > 18)
                .sorted(Comparator.comparingInt(Tenant::getAge))
                .collect(toList());
        long l3 = System.currentTimeMillis();

        System.out.println(l2 - l);
        System.out.println(tenants);
        System.out.println(l3 - l2);
        System.out.println(tenants2);
    }

    //The unordered() operation doesn't do any actions to explicitly unorder the stream.
    // What it does is that it removes the constraint on the stream that it must remain ordered,
    // thereby allowing subsequent operations to use optimizations that don't have to take ordering into consideration.


    @Test
    public void checkWhatInTxt() {
        String fileName = "C:\\Java_8_lab\\Practice\\lesson_4\\src\\tmnt.txt";

        try (Stream<String> stream = Files.lines(Paths.get(fileName))){
            stream.onClose(() -> System.err.println("COMPLETED!!!")).forEach(System.out::println);
        } catch (IOException ignore){}
    }

    private House generateHouse() {
        // f1
        Tenant tenant = new Tenant(MALE, "David", "Smith", 37);
        Tenant tenant2 = new Tenant(Gender.FEMALE, "Susan", "Smith", 33);
        // f2
        Tenant tenant3 = new Tenant(MALE, "Jim", "Bush", 53);
        // f3
        Tenant[] tArr = new Tenant[]{
                new Tenant(MALE, "Gomez", "Addams", 42),
                new Tenant(Gender.FEMALE, "Morticia", "Addams", 40),
                new Tenant(MALE, "Pugsley", "Addams", 15),
                new Tenant(Gender.FEMALE, "Wednesday", "Addams", 14)
        };

        Flat flat = new Flat(2, 100_000, false, Arrays.asList(tenant, tenant2));
        Flat flat2 = new Flat(1, 74_999, false, Arrays.asList(tenant3));
        Flat flat3 = new Flat(5, 666_666, true, Arrays.asList(tArr));

        return new House(new ArrayList<>(Arrays.asList(flat, flat2, flat3)), HouseType.BRICK, "someWhere square 57");
    }
}
