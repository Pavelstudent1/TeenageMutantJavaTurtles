import model.*;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static model.Gender.MALE;

public class Part1Test {

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

    @Test
    public void ofOptional() {
        //show Optional<String> s = new Optional<String>();
        Optional<String> helloWorld = Optional.of("Hello World");

        if (helloWorld.isPresent()) {
            System.out.println(helloWorld.get());
        }

        helloWorld.ifPresent(System.out::println);

    }

    @Test
    public void ofNullable() {
        int guess = new Random().nextInt(2);

        Optional<String> optional = Optional.ofNullable(guess == 0 ? "Data" : null);

        String s = optional.orElse("MOCK_Data");
        System.out.println(s);

        Integer result = optional.map(String::length)
                .map(i -> i * 2)
                .filter(i -> i > 7)
                //.orElseThrow(() -> new IllegalArgumentException("Data not found"));
                .orElse(-1);

        System.out.println(result);
    }

    @Test
    public void findAny() {
        Optional<Tenant> tenant = generateHouse().getFlats().stream()
                .flatMap(flat -> flat.getTenants().stream())
                .findAny();

        System.out.println(tenant);
    }

    @Test
    public void findFirst(){
        Optional<Tenant> tenant = generateHouse().getFlats().stream()
                .flatMap(flat -> flat.getTenants().stream())
                .sorted((o1, o2) -> o2.getLastName().length() - o1.getLastName().length())
                .findFirst();

        System.out.println(tenant);
    }

    @Test
    public void intDoubleLongOptional() {

        OptionalLong aLong = OptionalLong.of(33L);
        OptionalDouble optionalDouble = OptionalDouble.of(22.0);
        OptionalInt optionalInt = OptionalInt.of(11);


        boolean match = Stream.of(1.0, 2.0, 3.0, 4.0, 5.5)
                .mapToDouble(v -> v)
                //.allMatch(d -> d > 0);
                //.noneMatch(d -> d < 0);
                .anyMatch(d -> d > 0);

        System.out.println(match);

        long[] longs = generateHouse().getFlats().stream()
                //.mapToDouble(Flat::getPrice)
                //.mapToInt(l -> (int) l.getPrice());
                .mapToLong(Flat::getPrice)
                //.mapToObj(value -> new Object())
                //.boxed() from long -> Long
                .toArray();
                //.max()
    }

    @Test
    public void spliteratorExample() {
        //show them where using spliterator throw documentation (arrayList)
        Spliterator<Tenant> spliterator =
                generateHouse().getFlats().stream()
                .flatMap(flat -> flat.getTenants().stream())
                .collect(Collectors.toList()).spliterator();

        System.out.println(spliterator.getExactSizeIfKnown());
        System.out.println("FS size: " + spliterator.estimateSize());

        System.out.println("Then split");
        Spliterator<Tenant> spliterator2 = spliterator.trySplit();

        System.out.println("FS size: " + spliterator.estimateSize());
        System.out.println("SS size: " + spliterator2.estimateSize());
        System.out.println("Then split 2nd");

        Spliterator<Tenant> spliterator3 = spliterator2.trySplit();

        System.out.println("FS size: " + spliterator.estimateSize());
        System.out.println("SS size: " + spliterator2.estimateSize());
        System.out.println("TS size: " + spliterator3.estimateSize());

        Spliterator<Tenant> nullSpliterator = spliterator3.trySplit();
        if (nullSpliterator == null)
            System.out.println("Null");


        spliterator.forEachRemaining(System.out::println);

        spliterator2.forEachRemaining(System.out::println);

        spliterator3.tryAdvance(System.out::println);
        spliterator3.tryAdvance(System.out::println);
    }

    @Test
    public void ordersSpliterator() {
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5, 6, 7, 8);

        Spliterator<Integer> spliterator = integerStream.spliterator();

        Spliterator<Integer> spliterator2 = spliterator.trySplit();

        spliterator.forEachRemaining(System.out::println);

        System.out.println("****");
        spliterator2.forEachRemaining(System.out::println);

        integerStream.forEach(integer -> System.out.println(integer));
    }

    //Show sources: Collections, Spliterator, ArrayList, structure


}
