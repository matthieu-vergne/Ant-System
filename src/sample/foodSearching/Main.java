package sample.foodSearching;

public class Main {
	public static void main(String[] args) {
		World world = new World();
		world.addResources(20, new Coords(7, 15));
		world.addResources(20, new Coords(40, 40));
		world.addResources(20, new Coords(5, 40));
		world.addResources(50, new Coords(30, 5));
		world.addResources(50, new Coords(30, 25));
		world.createAnthill(new Coords(40, 5));
		Canvas canvas = new Canvas();
		canvas.setWorld(world);
		canvas.initFrame();

		while (true) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

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
		}
	}
}
