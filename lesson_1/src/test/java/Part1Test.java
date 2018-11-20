import java.util.Arrays;
import java.util.Comparator;

import org.junit.Test;

public class Part1Test
{
    @Test
    public void oldNewWayCreateRunnable()
    {
        Runnable anonymousClassVersion = new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println("Hi, I have been created with anonymous class!");
            }
        };
        Runnable lambdaVersion = () -> System.out.println("Hi, I have been created with anonymous class!");

        anonymousClassVersion.run();
        lambdaVersion.run();
    }

    @Test
    public void integerIsFunction()
    {
        // Integer result = (String x, Integer y) -> Integer.parseInt(x) + y;

        //Show how we can create resolve that create interface

        Convertible c = numberValue -> Integer.parseInt(numberValue);

        int result = c.convertToInteger("32");
        System.out.println(result);
    }

    @FunctionalInterface
    public interface Convertible {
        int convertToInteger(String numberValue);
    }

    @Test
    public void howToWriteLambda(){
        Convertible c = (String number) -> {
            if (number == null){
                return 0;
            }
            return Integer.parseInt(number);
        };

        Convertible c2 = number -> Integer.parseInt(number);

        Convertible c3 = Integer::parseInt;

        System.out.println(c.convertToInteger(null));

        System.out.println(c2.convertToInteger("33"));

        System.out.println(c3.convertToInteger("-5"));
    }

    @Test
    public void sortArray(){
        Integer[] unsortedArray = {8, 7 ,3, 9, 12, 0};

        Arrays.sort(unsortedArray, new Comparator<Integer>(){
            @Override
            public int compare(Integer o1, Integer o2)
            {
                return o1 - o2;
            }
        });

        System.out.println(Arrays.toString(unsortedArray));

        Integer[] unsortedArray2 = {8, 7 ,3, 9, 12, 0};

        //Arrays.sort(unsortedArray2, (o1, o2) -> o1 - o2);
        Arrays.sort(unsortedArray2, Integer::compareTo);

        //Look into comparator interface (equals)

        System.out.println(Arrays.toString(unsortedArray2));
    }

    //Create 3 main classes in main-java-show Main_1, Main_2, Main_3 Comparator<String> cs =  ...
}