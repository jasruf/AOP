package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import main.Area;
import main.Location;
import main.Point;

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

		System.out.println(getLocalName() + ": X: " + currentLoc.getX() + " Y: "
				+ currentLoc.getY());
	}

	protected void moveLeft() {
		previousLoc = new Location(currentLoc.getX(), currentLoc.getY());
		currentLoc.setX(currentLoc.getX() - 1);

		System.out.println(getLocalName() + ": X: " + currentLoc.getX() + " Y: "
				+ currentLoc.getY());
	}

	protected void moveUp() {
		previousLoc = new Location(currentLoc.getX(), currentLoc.getY());
		currentLoc.setY(currentLoc.getY() + 1);

		System.out.println(getLocalName() + ": X: " + currentLoc.getX() + " Y: "
				+ currentLoc.getY());
	}

	protected void moveDown() {
		previousLoc = new Location(currentLoc.getX(), currentLoc.getY());
		currentLoc.setY(currentLoc.getY() - 1);

		System.out.println(getLocalName() + ": X: " + currentLoc.getX() + " Y: "
				+ currentLoc.getY());
	}

	protected boolean checkRight() {
		// check if bot can move to the right
		Location newLoc = new Location(currentLoc.getX() + 1, currentLoc.getY());
		if (newLoc.getX() < areaSize && !isSameLocation(newLoc, previousLoc)) {
			System.out.println(getLocalName() + ": Going right");
			return true;
		}
		return false;
	}

	protected boolean checkLeft() {
		// check if bot can move to the left
		Location newLoc = new Location(currentLoc.getX() - 1, currentLoc.getY());
		if (newLoc.getX() >= 0 && !isSameLocation(newLoc, previousLoc)) {
			System.out.println(getLocalName() + ": Going left");
			return true;
		}
		return false;
	}

	protected boolean checkUp() {
		// check if bot can move up
		Location newLoc = new Location(currentLoc.getX(), currentLoc.getY() + 1);
		if (newLoc.getY() < areaSize && !isSameLocation(newLoc, previousLoc)) {
			System.out.println(getLocalName() + ": Going up");
			return true;
		}
		return false;
	}

	protected boolean checkDown() {
		// check if bot can move down
		Location newLoc = new Location(currentLoc.getX(), currentLoc.getY() - 1);
		if (newLoc.getY() >= 0 && !isSameLocation(newLoc, previousLoc)) {
			System.out.println(getLocalName() + ": Going down");
			return true;
		}
		return false;
	}

	protected void moveToLocation(Location to) {
		int targetX = to.getX(), targetY = to.getY();
		Location compareLoc;

		while (!isSameLocation(currentLoc, to)) {
			compareLoc = new Location(currentLoc.getX() + 1, currentLoc.getY());
			if (currentLoc.getX() < targetX && pointIsExplored(compareLoc)) {
				moveRight();
			}

			compareLoc = new Location(currentLoc.getX() -1, currentLoc.getY());
			if (currentLoc.getX() > targetX && pointIsExplored(compareLoc)) {
				moveLeft();
			}

			compareLoc = new Location(currentLoc.getX(), currentLoc.getY() +1);
			if (currentLoc.getY() < targetY && pointIsExplored(compareLoc)) {
				moveUp();
			} 

			compareLoc = new Location(currentLoc.getX(), currentLoc.getY()-1);
			if (currentLoc.getY() > targetY && pointIsExplored(compareLoc)) {
				moveDown();
			}
		}
	}

	private boolean pointIsExplored(Location to) {
		if (getPointAt(to).isExplored()) {
			return true;
		}
		return false;
	}

	protected boolean isSameLocation(Location newLoc, Location oldLoc) {
		if (oldLoc != null) {
			if (newLoc.getX() == oldLoc.getX() && newLoc.getY() == oldLoc.getY()) {
				return true;
			}
		}
		return false;
	}

	// Very inefficient, should switch over to keeping track on which point the
	// robot is at, not the location
	public Point getCurrentPoint() {
		Point p;
		Location loc;
		for (int i = 0; i < area.getAreaSize(); i++) {
			for (int j = 0; j < area.getAreaSize(); j++) {
				p = area.getArea()[i][j];
				loc = p.getLocation();
				if (isSameLocation(loc, getCurrentLoc())) {
					return p;
				}

			}
		}
		return null;
	}

	public Point getPointAt(Location loc) {
		Point p;
		for (int i = 0; i < area.getAreaSize(); i++) {
			for (int j = 0; j < area.getAreaSize(); j++) {
				p = area.getArea()[i][j];
				loc = p.getLocation();
				if (isSameLocation(loc, p.getLocation())) {
					return p;
				}

			}
		}
		return null;
	}

	public void sendMessage(AID receiver, String message, int messageType) {
		ACLMessage msg = new ACLMessage(messageType);
		msg.setContent(message);
		msg.addReceiver(receiver);
		send(msg);
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
