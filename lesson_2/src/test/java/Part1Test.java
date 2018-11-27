
import model.*;
import org.junit.Test;

import java.util.*;
import java.util.function.*;

public class Part1Test {

    private static Function<List<Flat>, Integer> getPeopleCount = flats -> {
        int count = 0;
        for (Flat flat : flats) {
            count += flat.getTenants().size();
        }
        return count;
    };

    private static Function<House, Double> averageAge = house -> {
        int peopleCount = getPeopleCount.apply(house.getFlats());

        int ageSum = 0;
        for (Flat flat : house.getFlats()) {
            for (Tenant tenant : flat.getTenants()) {
                ageSum += tenant.getAge();
            }
        }
        return ageSum * 1.0 / peopleCount;
    };

    @Test
    public void getAverageAge() {
        House h = generateHouse();
        double age = averageAge.apply(h);
        System.out.println(age);
    }

    private static Function<List<Flat>, List<Tenant>> getAllTenants = flats -> {
        List<Tenant> tenants = new ArrayList<>();
        for (Flat flat : flats) {
            tenants.addAll(flat.getTenants());
        }
        return tenants;
    };

    private static BiFunction<House, House, Set<Tenant>> tenantsInBothHouses = (house, house2) -> {
        List<Tenant> tenants = getAllTenants.apply(house.getFlats());
        List<Tenant> tenants2 = getAllTenants.apply(house2.getFlats());
        tenants.retainAll(tenants2);
        return new HashSet<>(tenants);
    };

    @Test
    public void getTenantsThatHaveBothFlats() {
        House h = generateHouse();
        House h2 = new House(Arrays.asList(
                new Flat(2, 90_000, false, Arrays.asList(
                        new Tenant(Gender.MALE, "Gomez", "Addams", 42)))),
                HouseType.WOODEN, "str 22");
        Set<Tenant> tenants = tenantsInBothHouses.apply(h, h2);
        System.out.println(tenants);
    }

    private static Predicate<House> maleMoreThanFemale = house -> {
        List<Tenant> tenants = getAllTenants.apply(house.getFlats());
        int maleCount = 0;
        int femaleCount = 0;

        for (Tenant tenant : tenants) {
            if (tenant.getGender() == Gender.MALE) {
                maleCount++;
            } else {
                femaleCount++;
            }
        }

        return maleCount > femaleCount;
    };

    @Test
    public void boysGirls() {
        House h = generateHouse();
        boolean test = maleMoreThanFemale.test(h);
        System.out.println(test);
    }

    private static BiPredicate<Predicate<House>, House> targetOnBeer = (maleMore, house) -> {
        if (maleMore.test(house)) {
            int adult = 0;
            int young = 0;
            for (Tenant tenant : getAllTenants.apply(house.getFlats())) {
                if (tenant.getAge() > 18) {
                    adult++;
                } else {
                    young++;
                }
            }
            return adult > young;
        }
        return false;
    };

    @Test
    public void targetAd() {
        House h = generateHouse();
        boolean isItOurTarget = targetOnBeer.test(maleMoreThanFemale, h);
        System.out.println(isItOurTarget);
    }

    private static class MailSender {
        private Consumer<Tenant> sendEmail;

        private List<Tenant> lastSend; // Write it later (a n t)

        public MailSender(Consumer<Tenant> sendEmail) {
            this.sendEmail = sendEmail;
        }

        public void sendToTenants(List<Tenant> list) {
            for (Tenant tenant : list) {
                sendEmail.accept(tenant);
            }
            lastSend = list;
        }

        // Write it later (after next test)
        public void sendAgentInfo(BiConsumer<List<Tenant>, String> agent) {
            agent.accept(lastSend, "We have send ad to this people:");
        }
    }

    private static Predicate<Tenant> isMale = tenant -> tenant.getGender() == Gender.MALE;

    private static Consumer<Tenant> sendMail = tenant -> System.out.println("Hi " + tenant.getFirstName()
            + "! We have great bear, click this link to get more info #########");

    //Write it a n t
    private static BiConsumer<List<Tenant>, String> sendMailtoAgent = (list, s) -> {
        StringBuilder sb = new StringBuilder(s);
        for (Tenant tenant : list) {
            sb.append("\n")
                    .append(tenant.getFirstName())
                    .append("  ")
                    .append(tenant.getLastName());
        }
        System.out.println(sb.toString());
    };

    @Test
    public void spamSend() {
        House h = generateHouse();

        List<Tenant> tenants = getAllTenants.apply(h.getFlats());
        List<Tenant> forAdSend = new ArrayList<>();

        for (Tenant tenant : tenants) {
            if (isMale.test(tenant)) {
                forAdSend.add(tenant);
            }
        }

        MailSender mailSender = new MailSender(sendMail);
        mailSender.sendToTenants(forAdSend);

        //add after test
        System.out.println();
        mailSender.sendAgentInfo(sendMailtoAgent);
    }

    private static Supplier<Tenant> randomTenant = () -> {
        String[] fNames = {"Jenia", "Sasha"};
        String[] lNames = {"Vim", "Truman", "Leonhard"};
        Random r = new Random();
        return new Tenant(Gender.values()[r.nextInt(2)],
                fNames[r.nextInt(2)], lNames[r.nextInt(3)], 10 + r.nextInt(30));
    };

    @Test
    public void getRandomTenant() {
        System.out.println(randomTenant.get());
    }

    @Test
    public void wtUnaryOperator() {
        UnaryOperator<Integer> returnWhatWeGive = UnaryOperator.identity();

        Integer result = returnWhatWeGive.apply(133);
        System.out.println(result);
    }

    @Test
    public void worWithPrimitiveTypes() {
        IntFunction<String> romeNumberProvider = value -> {
            switch (value) {
                case 1:
                    return "I";
                case 2:
                    return "II";
                case 3:
                    return "III";
                default:
                    return "=(";
            }
        };

        String s = romeNumberProvider.apply(2);
        System.out.println(s);
    }


    enum Environment {
        MOCK,
        DEV,
        PROD,
        UAT
    }

    static Map<String, String> props = new HashMap<>();

    @Test
    public void showFunctionPowerTest() {

        props.put("id", "super-test-id");

        Function<Environment, Map<String, String>> envPropsProvider = environment -> {
            Map<String, String> map = null;
            switch (environment) {
                case MOCK:
                    map = props;
                    break;
                case DEV:
                    map = props;
                    break;
                case UAT:
                    break;
                case PROD:
                    map = props;
                    break;
            }

            return map;
        };


        Function<Function<Integer, String>, String> provideTestContext = fun -> fun.apply(1);

    }

    @Test
    public void chainOfPTextProcessors() {

       // Function<String, String> capitalize = str -> new StringBuilder(str).replace(0, 1, str.substring(0, 1).toUpperCase()).toString();

        Function<String, String> capitalize = str -> str.substring(0,1).toUpperCase() + str.substring(1);
        Function<String, String> changeLettertoDigit = str -> str.replace('a', '1');
        Function<String, String> addTail = str -> str + "Tail";

        List<Function<String, String>> functions = Arrays.asList(capitalize, changeLettertoDigit, addTail);

        Function<String, String> inputFunction = chainProcessors(functions);
        System.out.println(inputFunction.apply("sample"));
    }

    private Function<String, String> chainProcessors(List<Function<String, String>> functions) {

        Function<String, String> head = functions.get(0);
        for (int i = 1; i < functions.size(); i++) {
            head = head.andThen(functions.get(i));
        }

        return head;

    }

    private House generateHouse() {
        // f1
        Tenant tenant = new Tenant(Gender.MALE, "David", "Smith", 37);
        Tenant tenant2 = new Tenant(Gender.FEMALE, "Susan", "Smith", 33);
        // f2
        Tenant tenant3 = new Tenant(Gender.MALE, "Jim", "Bush", 53);
        // f3
        Tenant[] tArr = new Tenant[]{
                new Tenant(Gender.MALE, "Gomez", "Addams", 42),
                new Tenant(Gender.FEMALE, "Morticia", "Addams", 40),
                new Tenant(Gender.MALE, "Pugsley", "Addams", 15),
                new Tenant(Gender.FEMALE, "Wednesday", "Addams", 14)
        };

        Flat flat = new Flat(2, 100_000, false, Arrays.asList(tenant, tenant2));
        Flat flat2 = new Flat(1, 74_999, false, Arrays.asList(tenant3));
        Flat flat3 = new Flat(5, 666_666, true, Arrays.asList(tArr));

        return new House(Arrays.asList(flat, flat2, flat3), HouseType.BRICK, "someWhere square 57");
    }
}
