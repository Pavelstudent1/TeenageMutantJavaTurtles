import org.junit.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Part2Test {

    static class MyCollector implements Collector<Integer, List<Integer>, List<Integer>> {

        @Override
        public Supplier<List<Integer>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<Integer>, Integer> accumulator() {
            return List::add;
        }

        @Override
        public BinaryOperator<List<Integer>> combiner() {
            return (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            };
        }

        @Override
        public Function<List<Integer>, List<Integer>> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return new HashSet<>();
        }
    }

    @Test
    public void interfaceCollector() {
        List<Integer> result = Stream.of(1, 2, 3, 4, 5).collect(new MyCollector());
        System.out.println(result);
    }

    static class Summator implements Collector<Integer, Helper, Long> {

        private final List<Characteristics> list;

        Summator(List<Characteristics> list) {
            this.list = list == null || list.isEmpty() ? Collections.emptyList() : list;
        }

        @Override
        public Supplier<Helper> supplier() {
            return Helper::new;
        }

        @Override
        public BiConsumer<Helper, Integer> accumulator() {
            return Helper::addSum;
        }

        @Override
        public BinaryOperator<Helper> combiner() {
            return (h1, h2) -> {
                h1.addSum(h2);
                return h1;
            };
        }

        @Override
        public Function<Helper, Long> finisher() {
            return Helper::getSum;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return new HashSet<>(list);
        }

    }

    static class Helper {
        private long sum;

        public long getSum() {
            return sum;
        }

        public void addSum(long value) {
            this.sum += value;
        }

        public void addSum(Helper another) {
            this.sum += another.getSum();
        }
    }

    @Test
    public void sequentialAndParallelWorking() {
        List<Integer> list = Stream
                .generate(() -> ThreadLocalRandom.current().nextInt(1000))
                .parallel()
                .limit(1000)
                .collect(Collectors.toList());

        System.out.println("Reference sum: " + list.stream().collect(Collectors.summarizingInt(e -> e)));
        System.out.println("My summator sequential: " + list.stream().collect(new Summator(null)));

        /*
        Ok, at this moment we know, that our collector works correctly. Let's break it ))
        Any collector can return a set of so called Characteristics.
        What do they do?
        They tell to underlying code that uses passed collector, how working data can be treated.
        And a combination of Characteristics defines this treating.
        Before invoking collecting process, underlying code will ask "itself" (for more accuracy, previous steps)
        and our collector smth like the following:
        "If I'm parallel, AND your collector is CONCURRENT, AND previous data can be processed UNORDERED
        (or your collector tells that), thus I really can work in parallel" (see ReferencePipeline.java line 489)
        */
        System.out.println("No parallel, both characteristics: " +
                list.stream().collect(new Summator(
                        Arrays.asList(
                                Collector.Characteristics.CONCURRENT, Collector.Characteristics.UNORDERED))));
        System.out.println("Parallel, no characteristics: " +
                list.stream().parallel().collect(new Summator(null)));
        System.out.println("Parallel and unordered, no characteristics: " +
                list.stream().parallel().unordered().collect(new Summator(null)));
        /*
        Last two examples returns correct variants. Why? Because the collector doesn't tell them that he supports
        parallel work
         */
        System.out.println("Parallel, CONCURRENT characteristics: " +
                list.stream().parallel().collect(new Summator(Arrays.asList(Collector.Characteristics.CONCURRENT))));
        System.out.println("Parallel, CONCURRENT AND UNORDERED characteristics: " +
                list.stream()
                        .parallel()
                        .collect(new Summator(Arrays.asList(
                                Collector.Characteristics.CONCURRENT,
                                Collector.Characteristics.UNORDERED)))); //bad result

        /*
        Whoa, the last example returned smth different. And restart of the test didn't fix it.
        Let's answer on this question a bit later. Continue testing
         */
        System.out.println("Parallel, CONCURRENT characteristics: " +
                list.stream()
                        .parallel()
                        .collect(new Summator(Arrays.asList(
                                Collector.Characteristics.CONCURRENT))));
        System.out.println("Parallel and unordered, CONCURRENT characteristics: " +
                list.stream()
                        .parallel()
                        .unordered()
                        .collect(new Summator(Arrays.asList(
                                Collector.Characteristics.CONCURRENT)))); //bad result

        /*
        As I told before, we need getting some condition in order to be able running collector in true parallel mode
        We provided underlying code with parallel mark, unordered mark and our collector can work CONCURRENTLY.
        Let's see this example
         */
        System.out.println("Parallel and unordered, CONCURRENT and sorted(): " +
                list.stream()
                        .parallel()
                        .unordered()
                        .sorted()
                        .collect(new Summator(Arrays.asList(
                                Collector.Characteristics.CONCURRENT)))); //bad result

        /*
        Stop, stop, stop... The result is proper. How?
        Because we added sorted() function. It updates underlying code with new characteristic - SORTED -
        which erases previous, i.e. UNORDERED mark. And our collector again works in sequential mode.
         */
        System.out.println("Parallel and unordered, CONCURRENT + UNORDERED, sorted(): " +
                list.stream()
                        .parallel()
                        .unordered()
                        .sorted()
                        .collect(new Summator(Arrays.asList(
                                Collector.Characteristics.CONCURRENT, Collector.Characteristics.UNORDERED)))); //bad result
        /*
        Here we have a bit different case: our data is SORTED, but collector has UNORDERED.
        In order to run in parallel, underlying code need to know that data is UNORDERED _OR_ collector
        can work with UNORDERED data. If one of this questions returns true -> parallel mode on.
         */

        /*
        Ok, ok. Why do we have this problem at all?
        Because our Helper has one tiny and meaningless detail: he isn't threadsafe )))
        How we can fix it? Easy, we need to make it threadsafe.
        Ways - synchronization, immutability, using atomics.
        As an example, let's make all Helper's methods synchronized and rerun test - it'll fix all problems

        The moral is the following: in most cases, by default, most of collectors are sequential.
        If you create your own collector, think twice before making it CONCURRENT.
        Concurrent version could dramatically increase collecting performance, and equally become
        a painful source of unforeseen bugs.
        Dive into Java's sources in order to find truly concurrent collectors, if it's really needed.
         */
    }
}
