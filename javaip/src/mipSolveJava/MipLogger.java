package mipSolveJava;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class MipLogger {

	private MipTimer branchingTimer;
	private MipTimer lpTimer;

	public static class MipTimer {

		private boolean isRunning;
		private long eventStartTime;

		private long cumulativeTime;
		private long mostRecentEventTime;
		private long sinceLastQueriedTime;

		private SummaryStatistics summaryStatistics;

		public MipTimer() {
			this.cumulativeTime = 0;
			this.mostRecentEventTime = 0;
			this.sinceLastQueriedTime = 0;
			this.isRunning = false;

		}

		public void tic() {
			if (isRunning) {
				throw new RuntimeException(
						"Timer was already running and then was restarted");
			}
			eventStartTime = System.currentTimeMillis();
			mostRecentEventTime = 0;
			isRunning = true;
			summaryStatistics = new SummaryStatistics();
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

}
