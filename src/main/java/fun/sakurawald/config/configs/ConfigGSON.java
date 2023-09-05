package fun.sakurawald.config.configs;


@SuppressWarnings("ALL")
public class ConfigGSON {

    public Modules modules = new Modules();

    public class Modules {

        public ResourceWorld resource_world = new ResourceWorld();

        public class ResourceWorld {
            public long seed = 0L;
        }

    }

}
