package core;

public class Test {
	public static void main(String[] args) {
		Runner runner = new Runner();
		String[] commands = { "-command=DefaultCommand", "-path=C:\\Users\\Administrator\\Desktop\\RatingMgmt" };
		runner.run(commands);
	}
}
