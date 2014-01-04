package agents;

import jade.core.Agent;
import main.Area;
import main.Location;

public class Robot extends Agent {

	public static final String START_ACTION = "start_action";

	private String name;
	private Location targetLocation;
	private Area area;
	private int areaSize;
	private Object[] args;
	private Location currentLoc;
	private Location previousLoc;
	private char targetArea;

	/* Generic robot methods */

	protected void moveRight() {
		previousLoc = new Location(currentLoc.getX(), currentLoc.getY());
		currentLoc.setX(currentLoc.getX() + 1);

		System.out.println("pX: " + previousLoc.getX() + " pY:" + previousLoc.getY());
		System.out.println("X: " + currentLoc.getX() + " Y:" + currentLoc.getY());
	}

	protected void moveLeft() {
		previousLoc = new Location(currentLoc.getX(), currentLoc.getY());
		currentLoc.setX(currentLoc.getX() - 1);

		System.out.println("pX: " + previousLoc.getX() + " pY:" + previousLoc.getY());
		System.out.println("X: " + currentLoc.getX() + " Y:" + currentLoc.getY());
	}

	protected void moveUp() {
		previousLoc = new Location(currentLoc.getX(), currentLoc.getY());
		currentLoc.setY(currentLoc.getY() + 1);

		System.out.println("pX: " + previousLoc.getX() + " pY:" + previousLoc.getY());
		System.out.println("X: " + currentLoc.getX() + " Y:" + currentLoc.getY());
	}

	protected void moveDown() {
		previousLoc = new Location(currentLoc.getX(), currentLoc.getY());
		currentLoc.setY(currentLoc.getY() - 1);

		System.out.println("pX: " + previousLoc.getX() + " pY:" + previousLoc.getY());
		System.out.println("X: " + currentLoc.getX() + " Y:" + currentLoc.getY());
	}

	protected boolean checkRight() {
		// check if bot can move to the right
		Location newLoc = new Location(currentLoc.getX() + 1, currentLoc.getY());
		if (newLoc.getX() < areaSize && !isSameLocation(newLoc, previousLoc)) {
			System.out.println("Going right");
			return true;
		}
		return false;
	}

	protected boolean checkLeft() {
		// check if bot can move to the left
		Location newLoc = new Location(currentLoc.getX() - 1, currentLoc.getY());
		if (newLoc.getX() >= 0 && !isSameLocation(newLoc, previousLoc)) {
			System.out.println("Going left");
			return true;
		}
		return false;
	}

	protected boolean checkUp() {
		// check if bot can move up
		Location newLoc = new Location(currentLoc.getX(), currentLoc.getY() + 1);
		if (newLoc.getY() < areaSize && !isSameLocation(newLoc, previousLoc)) {
			System.out.println("Going up");
			return true;
		}
		return false;
	}

	protected boolean checkDown() {
		// check if bot can move down
		Location newLoc = new Location(currentLoc.getX(), currentLoc.getY() - 1);
		if (newLoc.getY() >= 0 && !isSameLocation(newLoc, previousLoc)) {
			System.out.println("Going down");
			return true;
		}
		return false;
	}

	protected void moveToLocation(Location to) {
		int targetX = to.getX(), targetY = to.getY();

		while (!isSameLocation(currentLoc, to)) {
			if (currentLoc.getX() < targetX) { // move right
				moveRight();
			}

			if (currentLoc.getX() > targetX) { // move left
				moveLeft();
			}

			if (currentLoc.getY() < targetY) { // move up
				moveUp();
			}

			if (currentLoc.getY() > targetY) { // move down
				moveDown();
			}
		}
	}

	protected boolean isSameLocation(Location newLoc, Location oldLoc) {
		if (oldLoc != null) {
			if (newLoc.getX() == oldLoc.getX() && newLoc.getY() == oldLoc.getY()) {
				return true;
			}
		}
		return false;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public int getAreaSize() {
		return areaSize;
	}

	public void setAreaSize(int areaSize) {
		this.areaSize = areaSize;
	}

	public Location getCurrentLoc() {
		return currentLoc;
	}

	public void setCurrentLoc(Location currentLoc) {
		this.currentLoc = currentLoc;
	}

	public char getTargetArea() {
		return targetArea;
	}

	public void setTargetArea(char targetArea) {
		this.targetArea = targetArea;
	}
}
