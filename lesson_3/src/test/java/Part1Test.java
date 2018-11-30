
import model.*;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static model.Gender.MALE;

public class Part1Test {

    @Test
    public void createStreamWithBuilder(){
        House h = generateHouse();

        Stream.Builder<Flat> builder = Stream.builder();
        for (Flat flat : h.getFlats()) {
            builder.add(flat);
        }
        Stream<Flat> flatStream = builder.build();

        flatStream.forEach(System.out::println);
    }

    @Test
    public void createFromCollection(){
        List<Flat> flats = generateHouse().getFlats();

        Stream<Flat> flatStream = flats.stream();

        // After show this
        flats.add(new Flat(2, 1_000_000, true, null));

        System.out.println(flatStream.count());
    }

    @Test
    public void createFromArray(){
        int[] array = {1,2,3,4,5};

        int sum = Arrays.stream(array).sum();

        System.out.println(sum);
    }


    //after next test
    private static Integer incrementAndReturn(Integer integer) {
        try
        {
            //after show try-finally
            return integer++;
        } finally
        {
            return integer;
        }

    }

    @Test
    public void iterateStream(){
        List<Integer> list = Stream.iterate(0, integer -> integer + 1)
                .limit(20)
                .collect(toList());
        // after test show i++ and what happens

        System.out.println(list);
    }

    //Go to module 2 and copy from there
    private static Supplier<Tenant> randomTenant = () -> {
        String[] fNames = {"Jenia", "Sasha"};
        String[] lNames = {"Vim", "Truman", "Leonhard"};
        Random r = new Random();
        return new Tenant(Gender.values()[r.nextInt(2)],
                fNames[r.nextInt(2)], lNames[r.nextInt(3)], 10 + r.nextInt(30));
    };

    @Test
    public void generateStream() {
        Stream.generate(randomTenant)
                .limit(10)
                .forEach(System.out::println);
    }

    @Test
    public void emptyStream() {
        Stream<Tenant> tenantStream = tenantWithAgeStream(new ArrayList<>(), 22);
        tenantStream.forEach(System.out::println);

        House house = generateHouse();

        Stream<Tenant> tenantStream1 = tenantWithAgeStream(house.getFlats()
                        .stream()
                        .flatMap(flat -> flat.getTenants().stream())
                        .collect(toList()),
                40);
        tenantStream1.forEach(System.out::println);
    }

    private static Stream<Tenant> tenantWithAgeStream(List<Tenant> tenants, int age){
        if (tenants.size() == 0){
            return Stream.empty();
        }
        return tenants.stream().filter(tenant -> tenant.getAge() == age);
    }

    @Test
    public void streamOf() {
        Stream.of("odin", 2, "tri", 4).map(o -> {
            if (o instanceof String){
                return ((String) o);
            } else if (o instanceof Integer){
                return  String.valueOf(o);
            } else {
                throw new IllegalArgumentException();
            }
        }).map(String::length)
                .forEach(System.out::println);
    }

    @Test
    public void streamConcat() {
        Stream<String> stream = Stream.of("odin", "dva", "tri");
        Stream<String> stream2 = Stream.of("chetire", "pati", "6ix");

        Stream<String> bigStream = Stream.concat(stream, stream2);

        bigStream.forEach(System.out::println);
    }

    @Test
    public void mapExample() {
        List<Integer> list = Stream.of("Odin", "raz", "ne", "ananas")
                .map(s -> s + "bum")
                .map(String::length)
                .map(AtomicInteger::new)
                .map(AtomicInteger::incrementAndGet)
                .map(integer -> integer * 2)
                .collect(toList());
        
        System.out.println(list);
    }

    @Test
    public void filter() {
        List<Integer> list = Stream.of(1, 4, 12, 33, 11, 10, 13)
                .filter(i -> i > 10)
                .map(i -> i * 2)
                .filter(i -> i < 40)
                .collect(toList());

        System.out.println(list);
    }

    @Test
    public void flatMap() {
        House h = generateHouse();
        List<String> list = h.getFlats().stream()
                //.map(flat -> flat.getTenants())
                .flatMap(flat -> flat.getTenants().stream())
                .map(Tenant::getFirstName)
                .collect(toList());

        System.out.println(list);

        List<String> fNames = new ArrayList();
        for (Flat flat : h.getFlats()) {
            for (Tenant tenant : flat.getTenants()) {
                fNames.add(tenant.getFirstName());
            }
        }

        System.out.println(fNames);
    }

    @Test
    public void distinctStream() {
        List<Integer> list = Stream.of(1, 1, 2, 2, 3, 6, 5, 8, 12, 3, 6, 6, 6, 6, 6, 6, 6)
                .distinct()
                .collect(toList());
        System.out.println(list);
    }

    @Test
    public void sortedStream() {
        List<Integer> list = Stream.of(4, 2, 6, 8, 12, 44, 32, 77, 53, 42, 38)
                .sorted()
                .collect(toList());
        System.out.println(list);

        List<Integer> list2 = list.stream()
                //.sorted(Comparator.reverseOrder())
                .sorted((o1, o2) -> o2.compareTo(o1))
                .collect(toList());
        System.out.println(list2);

        House house = generateHouse();
        List<Tenant> tenantList = house.getFlats().stream()
                .flatMap(flat -> flat.getTenants().stream())
                //.sorted((o1, o2) -> Integer.compare(o1.getAge(), o2.getAge()))
                .sorted(Comparator.comparingInt(Tenant::getAge).reversed())
                .collect(toList());
        System.out.println(tenantList);
    }

    @Test
    public void peelStream() {
        /*List<Integer> list = Stream.of("a", "b", "c", "d", "e")
                .peek(System.out::println)
                .map(s -> s + s)
                .peek(System.out::println)
                .map(String::length)
                .peek(System.out::println)
                .collect(toList());*/

        List<Integer> list2 = Stream.of("privet", "kak", "dela")
                .sequential()
                .peek(System.out::println)
                .map(String::length)
                .peek(System.out::println)
                .collect(toList());
    }

    @Test
    public void skipLimitStream() {
        Stream.iterate(0, i -> i + 1)
                .skip(10)
                .limit(25)
                .skip(5)
                .limit(5)
                .forEach(System.out::println); //15 - 19
    }

    @Test
    public void forEachStream() {
        Stream.of(1,2,3,4,5)
                .forEach(i -> System.out.println(i * i));
    }

    @Test
    public void toArrayStream() {
        Object[] objects = Stream.of("a", "b", "c", "d")
                .toArray();

        System.out.println(Arrays.toString(objects));

        String[] array = Stream.of("a", "b", "c", "d")
                .toArray(String[]::new);
        System.out.println(Arrays.toString(array));
    }

    @Test
    public void reduceStream() {
        House h = generateHouse();

        Tenant tenant = h.getFlats().stream()
                .flatMap(flat -> flat.getTenants().stream())
                .reduce(new Tenant(MALE, "Andrey", "Dvinov", 25),
                        (t, t2) -> {
                            Tenant res;
                            if (t.getGender() == MALE && t2.getGender() == MALE) {
                                res = t.getLastName().length() > t2.getLastName().length() ? t : t2;
                            } else {
                                res = t.getAge() > t2.getAge() ? t2 : t;
                            }
                            return res;
                        });
        System.out.println(tenant);

        Integer num = Stream.of(1, 2, 3, 4, 5)
                .filter(integer -> integer > 3)
                //.filter(integer -> integer > 10)
                .reduce((integer, integer2) -> integer + integer2)
                .map(integer -> integer * 2)
                .orElse(-1);
        System.out.println(num);
    }

    @Test
    public void collectStream() {
        Collection<Integer> c = Arrays.asList(1,2,3,4,5,6);

        List<Integer> list = Stream.of(c)
                .flatMap(Collection::stream)
                .collect(toList());

        System.out.println(list);

        Set<Integer> set = Stream.of(c)
                .flatMap(Collection::stream)
                .collect(toSet());

        System.out.println(set);

        LinkedHashSet<Integer> hashSet = Stream.of(c)
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        System.out.println(hashSet);
    }

    @Test
    public void getAverageAge() {
        House h = generateHouse();
        double average = h.getFlats().stream()
                .flatMap(flat -> flat.getTenants().stream())
                .mapToInt(Tenant::getAge)
                .average()
                .orElse(0.0);

        System.out.println(average);

    }

    @Test
    public void getTenantsThatHaveBothFlats() {
        House h = generateHouse();
        House h2 = new House(Arrays.asList(
                new Flat(2, 90_000, false, Arrays.asList(
                        new Tenant(MALE, "Gomez", "Addams", 42)))),
                HouseType.WOODEN, "str 22");

        List<Tenant> tenants = getAllTenants(h);
        List<Tenant> tenants2 = getAllTenants(h2);

        tenants.retainAll(tenants2);

        System.out.println(tenants);
    }

    private static List<Tenant> getAllTenants(House h){
        return h.getFlats().stream().flatMap(flat -> flat.getTenants().stream()).collect(toList());
    }


    @Test
    public void boysGirls() {
        House h = generateHouse();

        List<Tenant> allTenants = getAllTenants(h);

        long male = allTenants.stream().filter(tenant -> tenant.getGender() == MALE).count();

        System.out.println((allTenants.size() - male) < male);
    }


    @Test
    public void targetAd() {
        House h = generateHouse();
        List<Tenant> allTenants = getAllTenants(h);
        long male = allTenants.stream()
                .filter(isMale).count();
        if ((allTenants.size() - male) < male){
            long adultMen = allTenants.stream()
                    .filter(isMale)
                    .filter(tenant -> tenant.getAge() > 18)
                    .count();
            System.out.println(adultMen > (male - adultMen));
        } else {
            System.out.println(false);
        }
    }

    private static Predicate<Tenant> isMale = tenant -> tenant.getGender() == MALE;

    @Test
    public void spamSend() {
        House h = generateHouse();

        List<Tenant> men = getAllTenants(h).stream()
                .filter(isMale)
                .filter(t -> t.getAge() > 18)
                .collect(toList());

        men.forEach(tenant ->
                System.out.println("Hi " + tenant.getFirstName()
                        + "! We have great bear, click this link to get more info #########"));

        System.out.println("We have send ad to this people:");
        men.forEach(t -> System.out.println(t.getFirstName() +" " + t.getLastName()));
    }

    @Test
    public void wtUnaryOperator() {
        House h = generateHouse();

        getAllTenants(h).stream()
                .map(tenant -> tenant)
                .map(Function.identity())
                .forEach(tenant -> System.out.println(tenant));
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
