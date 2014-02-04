package mipSolveJava.logging;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import mipSolveJava.logging.NodeLog.NewSolutionStatus;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

public class NodeFormatter {

	public static enum Mode {
		FIXED_WIDTH, CSV;
	}

	public static enum Field {

		SOLUTION_FOUND("sol", 4), NODES_CREATED("nCreated", 9), NODE_STACK_SIZE(
				"nStack", 9), CURRENT_NODE("nCurrent", 9), CURRENT_LP(
				"lp objective", 14), BEST_BOUND("best bound", 14), INCUMBENT(
				"incumbent", 14), USER_CUTS("user cuts", 18), LAZY_CUTS(
				"lazy cuts", 18);

		private Field(String fullName, int defaultWidth) {
			this.fullName = fullName;
			this.defaultWidth = defaultWidth;
		}

		private String fullName;
		private int defaultWidth;

		public String getFullName() {
			return this.fullName;
		}

		public int getDefaultWidth() {
			return this.defaultWidth;
		}
	}

	private List<Field> fieldsToPrint;
	private EnumMap<Field, FieldFormatter> alternateFieldFormatters;
	private EnumMap<Field, Integer> alternateFieldWidths;
	private Mode mode;

	public NodeFormatter(Mode mode) {
		this.mode = mode;
		this.fieldsToPrint = Arrays.asList(Field.values());
		this.alternateFieldFormatters = new EnumMap<Field, FieldFormatter>(
				Field.class);
		this.alternateFieldWidths = new EnumMap<Field, Integer>(Field.class);
	}

	public String header() {
		StringBuilder ans = new StringBuilder();
		for (int i = 0; i < fieldsToPrint.size(); i++) {
			Field field = fieldsToPrint.get(i);
			String head = field.getFullName();
			if (mode == Mode.FIXED_WIDTH) {
				ans.append(Strings.padStart(head, getColumnWidth(field), ' '));
			} else if (mode == Mode.CSV) {
				ans.append(head);
				if (i < fieldsToPrint.size() - 1) {
					ans.append(',');
				}
			} else {
				throw new RuntimeException("Unrecognized mode: " + mode);
			}
		}
		return ans.toString();
	}

	private FieldFormatter getFormatter(Field field) {
		return alternateFieldFormatters.containsKey(field) ? alternateFieldFormatters
				.get(field) : defaultFormatters.get(field);
	}

	private int getColumnWidth(Field field) {
		return alternateFieldWidths.containsKey(field) ? alternateFieldWidths
				.get(field) : field.getDefaultWidth();
	}

	public String format(MipLog mipLog) {
		StringBuilder ans = new StringBuilder();
		for (int i = 0; i < fieldsToPrint.size(); i++) {
			Field field = fieldsToPrint.get(i);
			String value = getFormatter(field).format(mipLog);
			if (mode == Mode.FIXED_WIDTH) {
				ans.append(Strings.padStart(value, getColumnWidth(field), ' '));
			} else if (mode == Mode.CSV) {
				ans.append(value);
				if (i < fieldsToPrint.size() - 1) {
					ans.append(',');
				}
			} else {
				throw new RuntimeException("Unrecognized mode: " + mode);
			}
		}
		return ans.toString();
	}

	public static interface FieldFormatter {
		public String format(MipLog mipLog);
	}

	private static DecimalFormat logsFormat = new DecimalFormat("#0.00");

	private static ImmutableMap<Field, FieldFormatter> defaultFormatters;
	static {
		ImmutableMap.Builder<Field, FieldFormatter> builder = ImmutableMap
				.builder();
		builder.put(Field.BEST_BOUND, new FieldFormatter() {
			@Override
			public String format(MipLog mipLog) {
				NodeLog nodeLog = mipLog.getLastNodeLog();
				if (nodeLog.getBestBound().isPresent()) {
					return logsFormat.format(nodeLog.getBestBound().get());
				} else {
					return "?";
				}
			}
		});
		builder.put(Field.CURRENT_LP, new FieldFormatter() {
			@Override
			public String format(MipLog mipLog) {
				NodeLog nodeLog = mipLog.getLastNodeLog();
				if (nodeLog.getCurrentLp().isPresent()) {
					return logsFormat.format(nodeLog.getCurrentLp().get());
				} else {
					return "?";
				}

			}
		});
		builder.put(Field.CURRENT_NODE, new FieldFormatter() {

			@Override
			public String format(MipLog mipLog) {
				NodeLog nodeLog = mipLog.getLastNodeLog();
				return Long.toString(nodeLog.getCurrentNode());
			}
		});
		builder.put(Field.USER_CUTS, new FieldFormatter() {

			@Override
			public String format(MipLog mipLog) {
				return mipLog.getUserCutCallbackLog().getNodeLogString();
			}
		});
		builder.put(Field.LAZY_CUTS, new FieldFormatter() {

			@Override
			public String format(MipLog mipLog) {
				return mipLog.getLazyConstraintCallbackLog().getNodeLogString();
			}
		});
		builder.put(Field.INCUMBENT, new FieldFormatter() {

			@Override
			public String format(MipLog mipLog) {
				NodeLog nodeLog = mipLog.getLastNodeLog();
				if (nodeLog.getIncumbent().isPresent()) {
					return logsFormat.format(nodeLog.getIncumbent().get());
				} else {
					return "?";
				}
			}
		});
		builder.put(Field.NODE_STACK_SIZE, new FieldFormatter() {

			@Override
			public String format(MipLog mipLog) {
				NodeLog nodeLog = mipLog.getLastNodeLog();
				return Long.toString(nodeLog.getNodeStackSize());
			}

		});
		builder.put(Field.NODES_CREATED, new FieldFormatter() {

			@Override
			public String format(MipLog mipLog) {
				NodeLog nodeLog = mipLog.getLastNodeLog();
				return Long.toString(nodeLog.getNodesCreated());
			}
		});
		builder.put(Field.SOLUTION_FOUND, new FieldFormatter() {

			@Override
			public String format(MipLog mipLog) {
				NodeLog nodeLog = mipLog.getLastNodeLog();
				if (nodeLog.getNewSolutionStatus() == NewSolutionStatus.NONE) {
					return "-";
				} else if (nodeLog.getNewSolutionStatus() == NewSolutionStatus.INTEGRAL) {
					return "Int";
				} else if (nodeLog.getNewSolutionStatus() == NewSolutionStatus.HEURISTIC) {
					return nodeLog.getNewSolutionHeuristicShortName().get();
				} else {
					throw new RuntimeException("Solution status missing");
				}
			}
		});
		defaultFormatters = builder.build();
	}

}
