package io.github.sakurawald.module.initializer.functional.config.model;

public class FunctionalConfigModel {

    public Enchantment enchantment = new Enchantment();

    public static class Enchantment {

        public OverridePower override_power = new OverridePower();

        public static class OverridePower {

            public boolean enable = false;
            public int power_provider_amount = 15;
        }
    }
}
