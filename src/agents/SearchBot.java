package agents;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import main.Location;

public class SearchBot extends Robot {

	private static final int SLEEP_TIME = 1000;

	private Object[] args;
	private boolean done = false;
	private Location currentLoc;
	private int[][] agentArea;

	protected void setup() {

		args = getArguments();
		currentLoc = new Location(0, 0);

		addBehaviour(new SimpleBehaviour(this) {

			@Override
			public boolean done() {
				System.out.println("I'm Done!");
				return done;
			}

			@Override
			public void action() {
				scanArea('c', new Location(0, 10), new Location(5, 5));
			}
		});
	}

	/* Specific search bot methods */

	private void foundVictim(Location l, Robot r) {

	}

	private void areaSearched(Location beginLoc, Location endLoc) {

	}

	private void scanArea(char subArea, Location beginLoc, Location endLoc) {
		int count = 0;
		int beginX, endX, beginY, endY;
		switch (subArea) {
		case 'a':
			for (int i = beginLoc.getX(); i < endLoc.getX(); i++) {
				for (int j = beginLoc.getY(); j < endLoc.getY(); j++) {
					scanPoint(new Location(i, j));
					count++;
				}
			}
			break;
		case 'b':
			for (int i = beginLoc.getX(); i > endLoc.getX(); i--) {
				for (int j = beginLoc.getY(); j < endLoc.getY(); j++) {
					scanPoint(new Location(i, j));
					count++;
				}
			}
			break;
		case 'c':
			for (int i = endLoc.getX(); i < beginLoc.getX(); i++){
				for (int j = beginLoc.getY(); j > endLoc.getY(); j--) {
					scanPoint(new Location(i, j));
					count++;
				}
			}
			break;
		case 'd':
			for (int i = beginLoc.getX(); i > endLoc.getX(); i--) {
				for (int j = beginLoc.getY(); j < endLoc.getY(); j++) {
					scanPoint(new Location(i, j));
					count++;
				}
			}
			break;
		}

		System.out.println("Searched " + count + " points");
	}

	private void scanPoint(Location point) {
		System.out.println("Scan point, X: " + point.getX() + ", Y: " + point.getY());
		done = true;
	}

	private void alertDebrisBot(Location l) {
		// ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		// msg.setContentObject(l);
		// msg.set
		// AID dest = null;

	}
}
