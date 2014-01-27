package mipSolveBase;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

public class CutCallbackLogger {

	private Map<CutCallback, CutCallbackLog> logs;
	private int countCallbackStarted;

	public String getNodeLogString() {
		String ans = "";
		for (Entry<CutCallback, CutCallbackLog> entry : logs.entrySet()) {
			if (entry.getValue().isCurrentNodeCutsAttempted()) {
				if (!ans.isEmpty()) {
					ans += ", ";
				}
				ans += entry.getKey().toString() + ": "
						+ entry.getValue().getCurrentNodeNumCutsAdded();
			}
		}
		return ans;
	}

	public CutCallbackLogger() {
		this.logs = Maps.newHashMap();
		this.countCallbackStarted = 0;
	}

	public void onAddCallback(CutCallback cutCallback) {
		logs.put(cutCallback, new CutCallbackLog());
	}

	public CutCallbackLog getLog(CutCallback cutCallback) {
		return logs.get(cutCallback);
	}

	public void onStartCallback() {
		this.countCallbackStarted++;
		for (CutCallbackLog cutCallbackLog : logs.values()) {
			cutCallbackLog.onNewNode();
		}
	}

	public int getCountCallbackStarted() {
		return this.countCallbackStarted;
	}

	public static class CutCallbackLog {

		private int totalNumCutsAdded;
		private int currentNodeNumCutsAdded;
		private boolean currentNodeCutsAttempted;

		private CutCallbackLog() {
			this.totalNumCutsAdded = 0;
			this.currentNodeCutsAttempted = false;
			this.currentNodeNumCutsAdded = 0;
		}

		public void onNewNode() {
			this.currentNodeCutsAttempted = false;
			this.currentNodeNumCutsAdded = 0;
		}

		public void onAttempt() {
			this.currentNodeCutsAttempted = true;
		}

		public void onCut() {
			this.currentNodeNumCutsAdded++;
			this.totalNumCutsAdded++;
		}

		public int getTotalNumCutsAdded() {
			return totalNumCutsAdded;
		}

		public int getCurrentNodeNumCutsAdded() {
			return currentNodeNumCutsAdded;
		}

		public boolean isCurrentNodeCutsAttempted() {
			return currentNodeCutsAttempted;
		}

	}

}
