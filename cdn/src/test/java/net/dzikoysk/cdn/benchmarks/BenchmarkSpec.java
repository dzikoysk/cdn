package net.dzikoysk.cdn.benchmarks;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

class BenchmarkSpec {

    static void run(Class<?> clazz) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(clazz.getName())
                .build();

        Runner runner = new Runner(options);
        runner.run();
    }

}
