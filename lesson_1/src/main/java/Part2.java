import java.util.stream.Stream;

public class Part2 {

    public static void main(String[] args) {
        TestInterface<Integer> interface1 = (f, s) -> f + s;
        int result = interface1.doSomething1(1, 2);
        System.out.println(result);


        /*
        We will give them this Hell at the end of a course https://www.youtube.com/watch?v=6bN1HcRhse4 >:)
        Math::max
        (a, b) -> comparator.compare(a, b) >= 0 ? a : b;

        Math.max(-1, 0) = 0  ----> 0 >= 0 --> -1
        Math.max(-1, 1) = 1  ----> 1 >= 0 --> -1
         */
        System.out.println(Stream.of(-1, 0, 1).max(Math::max).get());
    }
}
