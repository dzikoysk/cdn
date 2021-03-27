package net.dzikoysk.cdn.benchmarks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import net.dzikoysk.cdn.Cdn;
import net.dzikoysk.cdn.CdnFactory;
import net.dzikoysk.cdn.entity.SectionLink;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.yaml.snakeyaml.Yaml;

import java.util.concurrent.TimeUnit;

@Fork(value = 1)
@Warmup(iterations = 10, time = 2)
@Measurement(iterations = 10, time = 2)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class DumpBenchmark extends BenchmarkSpec {

    public static class Entity {

        @SectionLink
        public Entry entry = new Entry();

    }

    public static class Entry {

        public String key = "value";

    }

    private static final Cdn CDN = CdnFactory.createStandard();
    private static final Gson GSON = new Gson();
    private static final Yaml YAML = new Yaml();
    private static final ObjectMapper JACKSON = new ObjectMapper();

    @Benchmark
    public String cdn() {
        return CdnFactory.createStandard().render(new Entity());
    }

    @Benchmark
    public String cdnCache() {
        return CDN.render(new Entity());
    }

    @Benchmark
    public String gson() {
        return new Gson().toJson(new Entity());
    }

    @Benchmark
    public String gsonCache() {
        return GSON.toJson(new Entity());
    }

    @Benchmark
    public String jackson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(new Entity());
    }

    @Benchmark
    public String jacksonCache() throws JsonProcessingException {
        return JACKSON.writeValueAsString(new Entity());
    }

    @Benchmark
    public String yaml() {
        return new Yaml().dumpAsMap(new Entity());
    }

    @Benchmark
    public String yamlCache() {
        return YAML.dumpAsMap(new Entity());
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new DumpBenchmark().cdn());
        System.out.println(new DumpBenchmark().gson());
        System.out.println(new DumpBenchmark().jackson());
        System.out.println(new DumpBenchmark().yaml());

        run(DumpBenchmark.class);
    }

}
