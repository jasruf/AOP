package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import main.Location;
import main.Point;

public class Robot extends Agent {

	public static final String START_ACTION = "start_action";

	private Point[][] area;
	private int areaSize;
	private Point currentPoint;
	private Point previousPoint;
	private char targetArea;
	private int limitRight, limitLeft, limitUp, limitDown;

	/* Generic robot methods */
	protected void moveRight() {
		previousPoint = getPointAt(new Location(currentPoint.getLocation().getX(), currentPoint
				.getLocation().getY()));
		currentPoint = getPointAt(new Location(currentPoint.getLocation().getX() + 1, currentPoint
				.getLocation().getY()));

		System.out.println(getLocalName() + ": X: " + currentPoint.getLocation().getX() + " Y: "
				+ currentPoint.getLocation().getY());
	}

	protected void moveLeft() {
		previousPoint = getPointAt(new Location(currentPoint.getLocation().getX(), currentPoint
				.getLocation().getY()));
		currentPoint = getPointAt(new Location(currentPoint.getLocation().getX() - 1, currentPoint
				.getLocation().getY()));

		System.out.println(getLocalName() + ": X: " + currentPoint.getLocation().getX() + " Y: "
				+ currentPoint.getLocation().getY());
	}

	protected void moveUp() {
		previousPoint = getPointAt(new Location(currentPoint.getLocation().getX(), currentPoint
				.getLocation().getY()));
		currentPoint = getPointAt(new Location(currentPoint.getLocation().getX(), currentPoint
				.getLocation().getY() + 1));

		System.out.println(getLocalName() + ": X: " + currentPoint.getLocation().getX() + " Y: "
				+ currentPoint.getLocation().getY());
	}

	protected void moveDown() {
		previousPoint = getPointAt(new Location(currentPoint.getLocation().getX(), currentPoint
				.getLocation().getY()));
		currentPoint = getPointAt(new Location(currentPoint.getLocation().getX(), currentPoint
				.getLocation().getY() - 1));

		System.out.println(getLocalName() + ": X: " + currentPoint.getLocation().getX() + " Y: "
				+ currentPoint.getLocation().getY());
	}

	protected boolean checkRight() {
		// check if bot can move to the right
		Location newLoc = new Location(currentPoint.getLocation().getX() + 1, currentPoint
				.getLocation().getY());
		if (newLoc.getX() <= limitRight && !isSameLocation(newLoc, previousPoint.getLocation())) {
			System.out.println(getLocalName() + ": Going right");
			return true;
		}
		return false;
	}

	protected boolean checkLeft() {
		// check if bot can move to the left
		Location newLoc = new Location(currentPoint.getLocation().getX() - 1, currentPoint
				.getLocation().getY());
		if (newLoc.getX() >= limitLeft && !isSameLocation(newLoc, previousPoint.getLocation())) {
			System.out.println(getLocalName() + ": Going left");
			return true;
		}
		return false;
	}

	protected boolean checkUp() {
		// check if bot can move up
		Location newLoc = new Location(currentPoint.getLocation().getX(), currentPoint
				.getLocation().getY() + 1);
		if (newLoc.getY() <= limitUp && !isSameLocation(newLoc, previousPoint.getLocation())) {
			System.out.println(getLocalName() + ": Going up");
			return true;
		}
		return false;
	}

	protected boolean checkDown() {
		// check if bot can move down
		Location newLoc = new Location(currentPoint.getLocation().getX(), currentPoint
				.getLocation().getY() - 1);
		if (newLoc.getY() >= limitDown && !isSameLocation(newLoc, previousPoint.getLocation())) {
			System.out.println(getLocalName() + ": Going down");
			return true;
		}
		return false;
	}

	protected void moveToLocation(Location to) {
		int targetX = to.getX(), targetY = to.getY();
		Location compareLoc;

		while (!isSameLocation(currentPoint.getLocation(), to)) {
			compareLoc = new Location(currentPoint.getLocation().getX() + 1, currentPoint
					.getLocation().getY());
			if (currentPoint.getLocation().getX() < targetX && pointIsExplored(compareLoc)) {
				moveRight();
			}

			compareLoc = new Location(currentPoint.getLocation().getX() - 1, currentPoint
					.getLocation().getY());
			if (currentPoint.getLocation().getX() > targetX && pointIsExplored(compareLoc)) {
				moveLeft();
			}

			compareLoc = new Location(currentPoint.getLocation().getX(), currentPoint.getLocation()
					.getY() + 1);
			if (currentPoint.getLocation().getY() < targetY && pointIsExplored(compareLoc)) {
				moveUp();
			}

			compareLoc = new Location(currentPoint.getLocation().getX(), currentPoint.getLocation()
					.getY() - 1);
			if (currentPoint.getLocation().getY() > targetY && pointIsExplored(compareLoc)) {
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

	// Probably not the best way to do this
	public Point getPointAt(Location loc) {
		Point p;
		for (int i = 0; i < getAreaSize(); i++) {
			for (int j = 0; j < getAreaSize(); j++) {
				p = area[i][j];
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

	public Point[][] getArea() {
		return area;
	}

	public void setArea(Point[][] area) {
		this.area = area;
	}

	public int getAreaSize() {
		return areaSize;
	}

	public void setAreaSize(int areaSize) {
		this.areaSize = areaSize;
	}

	public Point getCurrentPoint() {
		return currentPoint;
	}

	public void setCurrentPoint(Point currentPoint) {
		this.currentPoint = currentPoint;
	}

	public Point getPreviousPoint() {
		return previousPoint;
	}

	public void setPreviousPoint(Point previousPoint) {
		this.previousPoint = previousPoint;
	}

	public char getTargetArea() {
		return targetArea;
	}

	public void setTargetArea(char targetArea) {
		this.targetArea = targetArea;
	}

	public void setLimitRight(int limitRight) {
		this.limitRight = limitRight;
	}

	public void setLimitLeft(int limitLeft) {
		this.limitLeft = limitLeft;
	}

	public void setLimitUp(int limitUp) {
		this.limitUp = limitUp;
	}

	public void setLimitDown(int limitDown) {
		this.limitDown = limitDown;
	}
}
