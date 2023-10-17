package io.github.sakurawald.config;

public class OptimizationGSON {

    public Optimization optimization = new Optimization();

    public static class Optimization {
        public Spawn spawn = new Spawn();
        public Chunk chunk = new Chunk();

        public static class Spawn {
            public boolean fastBiomeLookup = true;
        }

        public static class Chunk {
            public boolean fastTickChunk = true;
        }
    }
}

