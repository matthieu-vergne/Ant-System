package sample.foodSearching;

public class Main {
	public static void main(String[] args) {
		World world = new World();
		world.addResources(300, new Coords(5, 15));
		world.addResources(300, new Coords(40, 40));
		world.createAnthill(new Coords(40, 5));
		Canvas canvas = new Canvas();
		canvas.setWorld(world);
		canvas.initFrame();

		while (true) {
			try {
				Thread.sleep(10);
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
			System.out.println(world.getWaveField()[5][15]);
		}
	}
}
