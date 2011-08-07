package sample.foodSearching;

public class Main {
	public static void main(String[] args) {
		World world = new World();
		world.addResources(300, new Coords(5, 15));
		world.addResources(300, new Coords(40, 40));
		world.addResources(300, new Coords(35, 40));
		world.addResources(300, new Coords(30, 40));
		world.addResources(300, new Coords(25, 40));
		world.addResources(300, new Coords(20, 40));
		world.addResources(300, new Coords(15, 40));
		world.addResources(300, new Coords(10, 40));
		world.addResources(300, new Coords(5, 40));
		world.addResources(300, new Coords(0, 40));
		world.createAnthill(new Coords(40, 5));
		Canvas canvas = new Canvas();
		canvas.setWorld(world);
		canvas.initFrame();

		while (true) {
//			 try {
//			 Thread.sleep(1000);
//			 } catch (InterruptedException e) {
//			 throw new RuntimeException(e);
//			 }

			for (Anthill anthill : world.getAnthills()) {
				if (anthill.getAntCounter() < 50) {
					anthill.createAnt();
				}
				for (Ant ant : anthill.getAnts()) {
					ant.goToNextPosition();
				}
			}
			world.evolve();
			canvas.repaint();
			// System.out.println(world.getMarkAt(new Coords(5, 15)).getMark(
			// Anthill.WAVE_ID));

			// Coords position = world.getAnthills()[0].getPosition();
			// System.out.println(world.getMarkAt(
			// new Coords(position.getX(), position.getY() + 1)).getMark(
			// Ant.MARK_ID));
		}
	}
}
