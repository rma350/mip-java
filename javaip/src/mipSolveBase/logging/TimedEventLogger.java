package mipSolveBase.logging;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class TimedEventLogger {

	private boolean isRunning;
	private long eventStartTime;

	private long cumulativeTime;
	private long mostRecentEventTime;
	private long sinceLastQueriedTime;

	private SummaryStatistics summaryStatistics;

	public TimedEventLogger() {
		this.cumulativeTime = 0;
		this.mostRecentEventTime = 0;
		this.sinceLastQueriedTime = 0;
		this.isRunning = false;
		this.summaryStatistics = new SummaryStatistics();

	}

	public void tic() {
		if (isRunning) {
			throw new RuntimeException(
					"Timer was already running and then was restarted");
		}
		eventStartTime = System.currentTimeMillis();
		mostRecentEventTime = 0;
		isRunning = true;
	}

	public long toc() {
		if (!isRunning) {
			throw new RuntimeException(
					"Timer was already stopped when stop was called");
		}
		long elapsedTime = System.currentTimeMillis() - eventStartTime;
		this.cumulativeTime += elapsedTime;
		this.mostRecentEventTime = elapsedTime;
		this.sinceLastQueriedTime += elapsedTime;
		summaryStatistics.addValue(elapsedTime);
		isRunning = false;
		return elapsedTime;
	}

	public long getAndResetSinceLastQueriedTime() {
		long ans = this.sinceLastQueriedTime;
		this.sinceLastQueriedTime = 0;
		return ans;
	}

	public long getCumulativeTime() {
		return cumulativeTime;
	}

	public long getMostRecentEventTime() {
		return mostRecentEventTime;
	}

	public SummaryStatistics getSummaryStatistics() {
		return this.summaryStatistics;
	}

}