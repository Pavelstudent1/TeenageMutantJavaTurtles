
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Part1Test {

    enum Environment{
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
            switch (environment){
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
}