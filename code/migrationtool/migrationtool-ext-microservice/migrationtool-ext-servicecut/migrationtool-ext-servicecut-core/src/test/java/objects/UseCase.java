package objects;

public class UseCase implements Service {

	private Entity entity = new Entity();

	@Override
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
		this.entity.setId("" + x + z);
		this.entity = new Entity();
		if (this.entity.getId().equals("" + x + z)) {
			this.entity.setRequired(true);
		}
	}

	@Override
	public String test2() {
		Object o = new Object();
		o.toString();
		Entity entity = new Entity();
		entity.setId("Hello");
		return "2";
	}
}
