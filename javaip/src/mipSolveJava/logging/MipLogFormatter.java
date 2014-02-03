package mipSolveJava.logging;

import java.text.DecimalFormat;
import java.util.Map.Entry;

import mipSolveBase.CutCallback;
import mipSolveBase.logging.CutCallbackLogger;
import mipSolveBase.logging.CutCallbackLogger.CutCallbackLog;
import mipSolveBase.logging.TimedEventLogger;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class MipLogFormatter {

	private static String newLine = System.getProperty("line.separator");

	public String format(MipLog mipLog) {
		StringBuilder ans = new StringBuilder();
		ans.append("Total solve time: ");
		ans.append(formatTime(mipLog.getSolveTimeMs()));
		ans.append(newLine);
		ans.append("Total user cut opportunities: ");
		ans.append(mipLog.getUserCutCallbackLog().getCountCallbackStarted());
		ans.append(newLine);
		appendCutCallbackLogger(ans, mipLog.getUserCutCallbackLog());
		ans.append("Total lazy constraint opportunities: ");
		ans.append(mipLog.getUserCutCallbackLog().getCountCallbackStarted());
		ans.append(newLine);
		appendCutCallbackLogger(ans, mipLog.getLazyConstraintCallbackLog());

		ans.append("Branching time: ");
		ans.append(formatTimedEventLogger(mipLog.getBranchingTimer()));
		ans.append(newLine);
		ans.append("Lp Time: ");
		ans.append(formatTimedEventLogger(mipLog.getLpTimer()));
		ans.append(newLine);
		return ans.toString();
	}

	private void appendCutCallbackLogger(StringBuilder log,
			CutCallbackLogger cutCallbackLogger) {
		for (Entry<CutCallback, CutCallbackLog> entry : cutCallbackLogger
				.getLogs().entrySet()) {
			log.append("Cut name: ");
			log.append(entry.getKey().toString());
			log.append(newLine);
			if (entry.getValue().getCutTime().getSummaryStatistics().getN() > 0) {
				log.append("    Cut Time: ");
				log.append(formatTimedEventLogger(entry.getValue().getCutTime()));
				log.append(newLine);
				log.append("    Cuts: ");
				log.append(formatIntegerSummaryStatistics(entry.getValue()
						.getCutsAdded()));
				log.append(newLine);
			}
		}
	}

	private String formatTimedEventLogger(TimedEventLogger timedEventLogger) {
		SummaryStatistics summary = timedEventLogger.getSummaryStatistics();
		StringBuilder ans = new StringBuilder();
		ans.append("Total time: ");
		ans.append(formatTime(summary.getSum()));
		ans.append(", total events: ");
		ans.append(summary.getN());
		ans.append(", average time: ");
		ans.append(formatTime(summary.getMean()));
		ans.append(", stdev time: ");
		ans.append(formatTime(summary.getStandardDeviation()));
		ans.append(", max time: ");
		ans.append(formatTime(summary.getMax()));
		ans.append(", min time: ");
		ans.append(formatTime(summary.getMin()));
		return ans.toString();
	}

	private String formatIntegerSummaryStatistics(
			SummaryStatistics summaryStatistics) {
		StringBuilder ans = new StringBuilder();
		ans.append("Sum Total: ");
		ans.append((long) summaryStatistics.getSum());
		ans.append(", event count: ");
		ans.append(summaryStatistics.getN());
		ans.append(", average: ");
		ans.append(oneDecFormat.format(summaryStatistics.getMean()));
		ans.append(", stdev: ");
		ans.append(oneDecFormat.format(summaryStatistics.getStandardDeviation()));
		ans.append(", max: ");
		ans.append((long) summaryStatistics.getMax());
		ans.append(", min: ");
		ans.append((long) summaryStatistics.getMin());
		return ans.toString();
	}

	private static DecimalFormat oneDecFormat = new DecimalFormat("#.0");

	public static String formatTime(double timeMs) {
		if (timeMs < 1000) {
			return (int) Math.round(timeMs) + "ms";
		}
		double timeSeconds = timeMs / 1000;
		if (timeSeconds < 60) {
			return oneDecFormat.format(timeSeconds) + "s";
		}
		double timeMinutes = timeSeconds / 60;
		if (timeMinutes < 60) {
			return oneDecFormat.format(timeMinutes) + "m";
		}
		double timeHours = timeMinutes / 24;
		return oneDecFormat.format(timeHours) + "h";
	}

}
