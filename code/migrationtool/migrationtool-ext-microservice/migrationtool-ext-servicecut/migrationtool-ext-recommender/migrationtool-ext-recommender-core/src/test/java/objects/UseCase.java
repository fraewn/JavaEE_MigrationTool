package objects;

public class UseCase {

	public void test1() {
		String x = "String";
		int y = 0;
		while (y < 10) {
			y++;
		}
		int a = 0;
		int b = 1;
		double z = a * b;
		Object o = new Object();
		o.toString();
		Entity entity = new Entity();
		entity.setId("" + x + z);
		if (entity.getId().equals("" + x + z)) {
			entity.setRequired(true);
		}
	}

	public void test2() {
		Object o = new Object();
		o.toString();
		Entity entity = new Entity();
		entity.setId("Hello");
	}
}
