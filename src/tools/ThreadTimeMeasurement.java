package tools;

import gui.GUICallback;
import sortAlgorithms.SortAlgorithm;

/**
 * Class for measuring execution time for sorting algorithms.
 * @author Franz-Josef
 *
 */
public class ThreadTimeMeasurement {

	private Object mutex = new Object();

	private long endTime;

	/**
	 * Measure execution time for given sorting algorithm.
	 * @param sortAlg
	 * @return Thread execution time in milliseconds
	 */
	public static float measureExecutionTime(SortAlgorithm sortAlg) {
		return (new ThreadTimeMeasurement())
				.measureExecutionTimeInternal(sortAlg);
	}

	private float measureExecutionTimeInternal(SortAlgorithm sortAlg) {
		sortAlg.addCallback(new GUICallback() {
			@Override
			public void callback(State state) {
				if (state == GUICallback.State.FINISHED) {
					endTime = System.nanoTime();
					synchronized (mutex) {
						mutex.notify();
					}
				}
			}
		});

		long startTime;
		synchronized (mutex) {
			startTime = System.nanoTime();
			sortAlg.start();
			try {
				mutex.wait();
			} catch (InterruptedException exc) {
			}
		}

		return (float)((double)(endTime - startTime) / 1e6d);
	}
}
