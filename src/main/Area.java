package main;

import java.util.Random;

public class Area {

	private static Area instance;
	private Point[][] a, b, c, d;
	private int areaSize;

	private Area(int size) {
		super();

		areaSize = size / 2;
		a = new Point[areaSize][areaSize];
		b = new Point[areaSize][areaSize];
		c = new Point[areaSize][areaSize];
		d = new Point[areaSize][areaSize];
		
		
		/*	__________
		 * |	|	 |
		 * | C	|  D |
		 * |____|____|
		 * |	|	 |
		 * | A	|  B |
		 * |____|____|
		 */

		// Create 4 areas of equal size
		Random rand = new Random();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (i < areaSize) {
					if (j < areaSize) {
						a[i][j] = new Point(new Location(j, i), rand.nextBoolean(),
								rand.nextBoolean(), false);
					} else {
						b[i][j - areaSize] = new Point(new Location(j, i), rand.nextBoolean(),
								rand.nextBoolean(), false);
					}
				} else {
					if (j < areaSize) {
						c[i - areaSize][j] = new Point(new Location(j, i), rand.nextBoolean(),
								rand.nextBoolean(), false);
					} else {
						d[i - areaSize][j - areaSize] = new Point(new Location(j, i),
								rand.nextBoolean(), rand.nextBoolean(), false);
					}
				}
			}
		}
	}

	public static Area getInstance() {
		if (instance == null) {
			// Create a x by x area
			instance = new Area(10);
		}
		return instance;
	}

	public Point[][] getArea(char areaCode) {
		switch (areaCode) {
		case 'a':
			return a;
		case 'b':
			return b;
		case 'c':
			return c;
		case 'd':
			return d;
		default:
			return a;
		}
	}

	public int getAreaSize() {
		return areaSize;
	}
}
