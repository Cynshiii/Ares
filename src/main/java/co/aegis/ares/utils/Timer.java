package co.aegis.ares.utils;

import co.aegis.ares.Ares;
import lombok.Getter;

public class Timer {
	private static final int IGNORE = 1000;

	@Getter
	private final long duration;

	public Timer(String id, Runnable runnable) {
		long startTime = System.currentTimeMillis();

		runnable.run();

		duration = System.currentTimeMillis() - startTime;
		if (Ares.isDebug() || duration > IGNORE)
			Ares.log("[Timer] " + id + " took " + duration + "ms");
	}

}
