import model.Cockroach;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class Part1Test {

    private static void randomSleep(long i) {
        if (i == 0) {
            Random r = new Random();
            try {
                Thread.sleep(r.nextInt(4000));
            } catch (InterruptedException ingore) {
            }
        } else {
            try {
                Thread.sleep(i);
            } catch (InterruptedException ignore) {}
        }
    }

    @Test
    public void createCF() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        //completableFuture.get(); // show infinity

        completableFuture.complete("All ok");
        System.out.println(completableFuture.get());
    }

    @Test
    public void createCF2() {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            randomSleep(0);
            return "Privetik";
        });

        System.out.println(completableFuture.getNow("nothing"));

        CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().isDaemon());
            randomSleep(100);
            System.out.println("Privetik");
        });
        randomSleep(200);
    }

    @Test
    public void runnableExample() {
        Runnable r = () -> {
            System.out.println(Thread.currentThread().isDaemon());
            randomSleep(300);
            System.out.println("hi");};
        new Thread(r).start();
        randomSleep(500);
    }

    @Test
    public void cockroachRunners() throws ExecutionException, InterruptedException, TimeoutException {
        Cockroach first = new Cockroach(20, "Patrick");
        Cockroach second = new Cockroach(19, "Cross");
        Cockroach third = new Cockroach(21, "Devil");

        CountDownLatch cdl = new CountDownLatch(1);
        Random r = new Random();

        CompletableFuture<Cockroach> c1 = CompletableFuture.supplyAsync(() -> {
            try {
                cdl.await();
            } catch (InterruptedException ignore) {}
            randomSleep(100 - r.nextInt(first.getSpeed()));
            randomSleep(100 - r.nextInt(first.getSpeed()));
            randomSleep( 100 - r.nextInt(first.getSpeed()));
            return first;
        });

        CompletableFuture<Cockroach> c2 = CompletableFuture.supplyAsync(() -> {
            try {
                cdl.await();
            } catch (InterruptedException ignore) {}
            randomSleep(100 - r.nextInt(second.getSpeed()));
            randomSleep( 100 - r.nextInt(second.getSpeed()));
            randomSleep( 100 - r.nextInt(second.getSpeed()));
            return second;
        });

        CompletableFuture<Cockroach> c3 = CompletableFuture.supplyAsync(() -> {
            try {
                cdl.await();
            } catch (InterruptedException ignore) {}
            randomSleep(100 - r.nextInt(third.getSpeed()));
            randomSleep(100 - r.nextInt(third.getSpeed()));
            randomSleep(100 - r.nextInt(third.getSpeed()));
            return third;
        });

        cdl.countDown();

        CompletableFuture<Cockroach> winner = CompletableFuture.anyOf(c1, c2, c3).thenApply(Cockroach.class::cast);

        System.out.println(winner.get());
    }

    private static void downloadFromSite(String URL){
        // TODO: 12/14/2018 Create later
    }

    @Test
    public void allOfCF() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> cf1 = CompletableFuture.runAsync(() -> downloadFromSite("google.com"));
        CompletableFuture<Void> cf2 = CompletableFuture.runAsync(() -> downloadFromSite("mail.ru"));
        CompletableFuture<Void> cf3 = CompletableFuture.runAsync(() -> downloadFromSite("yandex.ru"));

        CompletableFuture<Void> result = CompletableFuture.allOf(cf1, cf2, cf3)
                .thenRun(() -> System.out.println("All data download"));

        result.get();
    }

    @Test
    public void withExecutor() throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            System.out.println(Thread.currentThread().isDaemon());
        }, es);

        es.shutdown();
        future.get();
    } // show ForkJoinPool:1533

    @Test
    public void thenCompose() {
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> "ABC");
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> "DEF");

        CompletableFuture<String> finalFuture = cf1.thenCompose(s -> cf2.thenApply(s1 -> s + s1));
        //finalFuture.thenComposeAsync(s -> )

        System.out.println(finalFuture.getNow("error"));
    }

    @Test
    public void thenAccept() throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();

        CompletableFuture<Void> result = CompletableFuture.completedFuture("TMNT")
                .thenAccept(s -> {
                    System.out.println(Thread.currentThread().getName());
                    System.out.println(Thread.currentThread().isDaemon());
                })
                .thenAcceptAsync(v -> {
                    System.out.println(Thread.currentThread().getName());
                    System.out.println(Thread.currentThread().isDaemon());
                })
                .thenAcceptAsync(v -> {
                    System.out.println(Thread.currentThread().getName());
                    System.out.println(Thread.currentThread().isDaemon());
                }, es)
                .thenAcceptBoth(CompletableFuture.completedFuture("Another CF"),
                        (aVoid, s) -> {
                            System.out.println(Thread.currentThread().getName());
                            System.out.println(Thread.currentThread().isDaemon());
                        });

        result.get();
    }

    @Test
    public void thenApply() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> tmnt = CompletableFuture.completedFuture("TMNT")
                .thenApplyAsync(s -> {
                    System.out.println(Thread.currentThread().getName());
                    System.out.println(Thread.currentThread().isDaemon());
                    return s.length();
                })
                .thenApply(i -> {
                    System.out.println(Thread.currentThread().getName());
                    System.out.println(Thread.currentThread().isDaemon());
                    return i * i;
                });

        System.out.println(tmnt.get());
    }

    @Test
    public void thenCombine() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cf1 = CompletableFuture.completedFuture("123");
        CompletableFuture<String> cf2 = CompletableFuture.completedFuture("456");

        CompletableFuture<String> result = cf1.thenCombine(cf2, (s, s2) -> s + s2);

        System.out.println(result.get());
    }

    @Test
    public void thenRun() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> "Hi");
        cf.thenRun(() -> System.out.println(Thread.currentThread().getName()))
                .thenRunAsync(() -> System.out.println(Thread.currentThread().getName()));
        randomSleep(1000);
        System.out.println(cf.get());
    }

    @Test
    public void runAfterBoth() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cf1 = CompletableFuture.completedFuture("one");
        CompletableFuture<String> cf2 = CompletableFuture.completedFuture("two");

        CompletableFuture<Void> result = cf1.runAfterBoth(cf2, () -> System.out.println("Completed!"));
        result.get();
    }

    @Test
    public void runAfterEither() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            randomSleep(20000);
            return "cf1";
        });
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
            randomSleep(1000);
            return "cf2";
        });

        CompletableFuture<Void> result = cf1.runAfterEither(cf2, () -> System.out.println("Completed"));

        result.get();
        if (cf1.isDone()) {
            System.out.println(cf1.get());
        }
        if (cf2.isDone()){
            System.out.println(cf2.get());
        }
    }

    @Test
    public void exceptionHandler() throws ExecutionException, InterruptedException {
        boolean bad = true;
        CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> {
            if (bad) {
                throw new IllegalArgumentException("=(");
            }
            return "All fine";
        }).handle((s, throwable) -> {
            if (throwable != null) {
                System.out.println("Sad =(");
                return "error";
            }
            return s;
        });

        System.out.println(result.get());
    }

    @Test
    public void exceptionallyCF() throws ExecutionException, InterruptedException {
        boolean bad = true;
        CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> {
            if (bad) {
                throw new IllegalArgumentException("=(");
            }
            return "All fine";
        }).exceptionally(t -> {
            System.out.println(t.toString());
            return "error";
        });

        System.out.println(result.get());
    }

    @Test
    public void whenComplete() throws ExecutionException, InterruptedException {
        boolean bad = false;
        CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> {
            if (bad) {
                throw new IllegalArgumentException("=(");
            }
            return "All fine";
        }).whenComplete((s, throwable) -> {
            if (throwable != null){
                System.out.println("Bad =(");
            } else {
                System.out.println(s);
            }
        });

        result.get();
    }
}
