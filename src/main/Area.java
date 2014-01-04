package main;

public class Area {

	private static Area instance;
	private int[][] area;
	
	private Area(int size) {
		super();
		//Create new area
		this.area = new int[size][size];
		for (int i = 0; i < size; i++){
			for (int j = 0; j < size; j++){
				area[i][j] = j;
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
 
	public int[][] getArea() {
		return getInstance().area;
	}
}
