import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntBinaryOperator;

public class Part1 {

    /*
    Old way: passing input args into run() method
     */
    static Runnable createOldRunnable(String part1, String part2, String part3) {
        return new Runnable() {

            @Override
            public void run() {
                System.out.println(part1 + part2 + part3);
            }
        };
    }

    /*
    New way: passing input args into... what? Into the same 'run()' method in 'background'
    Java knows that 'Runnable' is a functional interface and knows who is an acceptor of args.
     */
    static Runnable createLambdaRunnable(String part1, String part2, String part3) {
        return () -> System.out.println(part1 + part2 + part3);
    }


    public static void main(String[] args) {
        Part1.createOldRunnable("1", "2", "3").run();
        Part1.createLambdaRunnable("1", "2", "3").run();

        /*
        What the f*ck?! Why this code doesn't want to compile?!?!?!
        IDE hints you that 'target type of a lambda conversion must be an interface'
        So you can't just come up with smth without backing of an interface!
         */
//        Integer result = (String x, Integer y) -> {
//            return Integer.parseInt(x) + y;
//        };


        //=========================================================================

        /*
        Full definition of a lambda.
         */
        IntBinaryOperator comparator1 = (int first, int second) -> {
            return Integer.compare(first, second);
        };

        //'return and {}' can be kicked off
        IntBinaryOperator comparator2 = (int first, int second) -> Integer.compare(first, second);

        //Types can be kicked off, because we can infer info about them
        IntBinaryOperator comparator3 = (first, second) -> Integer.compare(first, second);

        //Uber-feature: args can be kicked off with '::', so called 'method reference',
        //it knows, in accordance with the interface, that 2 args will be accepted ans smth will be returned
        IntBinaryOperator comparator4 = Integer::compare;

        /*
        As you can see, all comparators have the same method
         */
        int result1 = comparator1.applyAsInt(1, 2);
        int result2 = comparator2.applyAsInt(1, 2);
        int result3 = comparator3.applyAsInt(1, 2);
        int result4 = comparator4.applyAsInt(1, 2);


        //=========================================================================

        Integer[] array = {10, 7, 5, 4, 2, 1, 9};
        Arrays.sort(array, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });

        /*
        How many of you know that Arrays.toString() properly prints out an array? :)
         */
        System.out.println(Arrays.toString(array));


        Integer[] array2 = {10, 7, 5, 4, 2, 1, 9};
        Arrays.sort(array2, (first, second) -> first - second);
        System.out.println(Arrays.toString(array2));



        /*
        We can use method reference
        But look a bit closer here: method 'compare' here isn't a method of an interface,
        it's a static method, BUT-BUT-BUT... how?
        Because it fits to expecting input and output types.
        Here, we expect (int, int) -> return int
         */
        Integer[] array3 = {10, 7, 5, 4, 2, 1, 9};
        Arrays.sort(array3, Integer::compare);
        System.out.println(Arrays.toString(array3));



        /*
        Finally, Java 8 provides us with huge amount of predefined functional interfaces and utilities
         */
        Integer[] array4 = {10, 7, 5, 4, 2, 1, 9};
        Arrays.sort(array4, Comparator.comparingInt(integer -> integer));
        /*
        How many of you know that Arrays.toString() properly prints out an array? :)
         */
        System.out.println(Arrays.toString(array4));


        //=========================================================================


    }

}
