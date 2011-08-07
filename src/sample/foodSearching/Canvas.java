package sample.foodSearching;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Canvas extends JPanel {
	private static final long serialVersionUID = 1L;
	private JFrame frame = null;
	private World world = null;

	public void initFrame() {
		if (frame != null) {
			throw new IllegalStateException("Frame already initialized.");
		}

		frame = new JFrame("Food Searching");
		WindowAdapter wa = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		frame.addWindowListener(wa);
		frame.setPreferredSize(new Dimension(500, 500));
		frame.getContentPane().add(this);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void paint(Graphics g) {
		if (frame == null) {
			throw new IllegalStateException("Frame not initialized.");
		}

		g.clearRect(0, 0, getWidth(), getHeight());

		if (world == null) {
			throw new IllegalStateException("World unknown.");
		}

		double xRate = (double) getWidth() / world.getWidth();
		double yRate = (double) getHeight() / world.getHeight();
		int width = (int) Math.floor(xRate);
		int height = (int) Math.floor(yRate);

		Marker[][] waveField = world.getWaveField();
		for (int x = 0; x < world.getWidth(); x++) {
			for (int y = 0; y < world.getHeight(); y++) {
				int xDraw = (int) Math.floor(xRate * x);
				int yDraw = (int) Math.floor(yRate * y);

				int R = 255;
				int G = 255;
				int B = 255;
				Coords position = new Coords(x, y);
				if (!world.isAccessible(position)) {
					R = 0;
					G = 0;
					B = 0;
				} else {
					Integer resource = world.getResourceAt(position);
					Double mark = waveField[x][y].getMark(Ant.MARK_ID);
					Double wave = waveField[x][y].getMark(Anthill.WAVE_ID);
					if (resource > 0) {
						int depth = 255 - (int) Math.min(resource, 255);
						R = depth;
						G = depth;
						B = 255;
					} else if (mark > 0) {
						int depth = (int) Math.floor(255.0 * (1.0 - mark
								/ World.MAX_MARK));
						R = depth;
						G = 255;
						B = depth;
					} else if (wave > 0) {
						int depth = (int) Math.floor(255.0 * (1.0 - wave
								/ Anthill.MAX_WAVE));
						R = 255;
						G = depth;
						B = depth;
					}
				}
				Color color = new Color(R, G, B);
				g.setColor(color);
				g.fillRect(xDraw - width / 2, yDraw - height / 2, width, height);
			}
		}

		for (Anthill anthill : world.getAnthills()) {
			g.setColor(Color.GRAY);
			Coords position = anthill.getPosition();
			int xDraw = (int) Math.floor(xRate * position.getX());
			int yDraw = (int) Math.floor(yRate * position.getY());
			g.fillOval(xDraw - width, yDraw - height, 2 * width, 2 * height);

			for (Ant ant : anthill.getAnts()) {
				if (ant.isLookingForResource()) {
					g.setColor(Color.GRAY);
				} else {
					g.setColor(Color.BLUE);
				}

				position = ant.getCurrentPosition();
				xDraw = (int) Math.floor(xRate * position.getX());
				yDraw = (int) Math.floor(yRate * position.getY());
				g.fillOval(xDraw - width / 2, yDraw - height / 2, width, height);
			}
		}

	}

	public void setWorld(World world) {
		this.world = world;
	}

	public World getWorld() {
		return world;
	}
}
