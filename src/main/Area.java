package main;

import java.util.Random;

public class Area {

	private static Area instance;
	private Point[][] area;
	private int areaSize;

	private Area(int size) {
		super();

		// Retain the size
		areaSize = size;

		// Create new area
		Random rand = new Random();
		this.area = new Point[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (j > 3) {
					area[i][j] = new Point(new Location(i, j), rand.nextBoolean(),
							rand.nextBoolean(), false);
				} else { //Remove the else later (this is for testing)
					area[i][j] = new Point(new Location(i, j), false, false, false);
				}
			}
		}
	}

	public static Area getInstance() {
		if (instance == null) {
			// Create a 20 by 20 area
			instance = new Area(20);
		}
		return instance;
	}

	public Point[][] getArea() {
		return area;
	}

	public int getAreaSize() {
		return areaSize;
	}
}
