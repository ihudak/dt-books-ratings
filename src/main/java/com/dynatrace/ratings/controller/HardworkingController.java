package com.dynatrace.ratings.controller;

import com.dynatrace.ratings.exception.CrashException;
import com.dynatrace.ratings.model.Config;
import com.dynatrace.ratings.repository.ConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.lang.Math.sqrt;

public abstract class HardworkingController {
    public abstract ConfigRepository getConfigRepository();
    private Logger logger = LoggerFactory.getLogger(HardworkingController.class);

    private boolean shouldWorkHard() {
        Optional<Config> config = getConfigRepository().findById("dt.work.hard");
        return config.isPresent() && config.get().isTurnedOn() && 10 < getMemPressureMb() && 100 < getCPUPressure();
    }

    private boolean shouldSimulateCrash() {
        Optional<Config> config = getConfigRepository().findById("dt.simulate.crash");
        return config.isPresent() && config.get().isTurnedOn();
    }

    private long getMemPressureMb() {
        Optional<Config> config = getConfigRepository().findById("dt.work.hard");
        return config.map(Config::getLoadRAM).orElseGet(() -> 0L);
    }

    private long getCPUPressure() {
        Optional<Config> config = getConfigRepository().findById("dt.work.hard");
        return config.map(Config::getLoadCPU).orElseGet(() -> 0L);
    }

    protected void simulateCrash() {
        if (!shouldSimulateCrash()) {
            return;
        }
        throw new CrashException("Service is not available");
    }

    protected void simulateHardWork() {
        if (!shouldWorkHard()) {
            return;
        }
        int arraySize = (int)(getMemPressureMb() * 1024L * 1024L / 8L);
        if (arraySize < 0) {
            arraySize = Integer.MAX_VALUE;
        }
        long[] longs = new long[arraySize];
        int j = 0;
        long cpuPressure = getCPUPressure();
        for(long i = 0; i < cpuPressure; i++, j++) {
            j++;
            if (j >= arraySize) {
                j = 0;
            }
            try {
                if (longs[j] > Integer.MAX_VALUE) {
                    longs[j] = (long) Integer.MIN_VALUE;
                    if (longs[j] < 0) {
                        longs[j] *= -1;
                    }
                    logger.debug(String.valueOf(sqrt(sqrt((double) longs[j]))));
                }
            } catch (Exception ignored) {};
        }
    }
}
