package io.github.sakurawald.util;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

@UtilityClass
public class NumberUtil {

    @Getter
    private static final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();

}
