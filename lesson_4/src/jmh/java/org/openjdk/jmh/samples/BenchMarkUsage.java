package org.openjdk.jmh.samples;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3)
@Measurement(iterations = 100, batchSize = 10)
public class BenchMarkUsage {


  @Benchmark
  public void testSimpleStream1000000(){
    Stream.iterate(0, integer -> ++integer)
        .limit(1000000)
        .map(integer -> ++integer)
        .map(integer -> --integer)
        .filter(integer -> integer > 500000)
        .map(integer -> integer * 2)
        .findFirst();
  }

  @Benchmark
  public void testParallelStream1000000(){
    Stream.iterate(0, integer -> ++integer)
        .limit(1000000).parallel()
        .map(integer -> ++integer)
        .map(integer -> --integer)
        .filter(integer -> integer > 500000)
        .map(integer -> integer * 2)
        .findFirst();
  }

  @Benchmark
  public void testSimpleStream100(){
    Stream.iterate(0, integer -> ++integer)
        .limit(100)
        .map(integer -> ++integer)
        .map(integer -> --integer)
        .filter(integer -> integer > 50)
        .map(integer -> integer * 2)
        .findFirst();
  }

  @Benchmark
  public void testParallelStream100(){
    Stream.iterate(0, integer -> ++integer)
        .limit(100).parallel()
        .map(integer -> ++integer)
        .map(integer -> --integer)
        .filter(integer -> integer > 50)
        .map(integer -> integer * 2)
        .findFirst();
  }

}
