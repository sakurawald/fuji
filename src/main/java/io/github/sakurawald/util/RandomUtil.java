package io.github.sakurawald.util;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

import java.util.List;

@UtilityClass
public class RandomUtil {

    @Getter
    private static final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();

    public static <T> T drawList(List<T> list) {
        return list.get(rng.nextInt(list.size()));
    }
}
