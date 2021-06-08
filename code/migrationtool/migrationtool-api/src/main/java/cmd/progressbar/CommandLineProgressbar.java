package cmd.progressbar;

public class CommandLineProgressbar {

	private static final int PERCENT_OF_SIGN = 5;
	private static final String SIGN = "=";

	private boolean done;
	private boolean carriageReturn;

	public CommandLineProgressbar() {

	}

	public CommandLineProgressbar(boolean carriageReturn) {
		this.carriageReturn = carriageReturn;
	}

	public void setProgressbar(int current) {
		int z = Math.min(100, current);
		if (z <= 100) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i <= (z / PERCENT_OF_SIGN); i++) {
				sb.append(SIGN);
			}

			System.out.println("|" + String.format("%-" + (100 / PERCENT_OF_SIGN) + "s", sb.toString()) + "|"
					+ (this.carriageReturn ? "\r" : ""));
		} else {
			this.done = true;
			System.out.println("Done");
		}
	}

	public boolean isDone() {
		return this.done;
	}
}
