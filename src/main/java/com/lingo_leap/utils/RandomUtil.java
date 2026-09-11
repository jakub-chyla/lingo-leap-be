package com.lingo_leap.utils;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class RandomUtil {

    public Integer getRandomFromRange(Integer start, Integer end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end cannot be null");
        }
        if (start > end) {
            throw new IllegalArgumentException("Start must be less than or equal to end");
        }
        if (start.equals(end)) {
            return start;
        }

        return ThreadLocalRandom.current().nextInt(start, end);
    }

    public boolean percentChance(int chance) {
        int randomValue = ThreadLocalRandom.current().nextInt(100);
        boolean changeFinal = randomValue < chance;
        return changeFinal;
    }
}
