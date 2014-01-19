package agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.Area;
import main.Location;
import main.Point;

public class SearchBot extends Robot {

	private Object[] args;
	private boolean done = false;
	private Location startingLocation;

	protected void setup() {

		System.out.println(getLocalName() + " setup starting");

		// Setup area and location
		args = getArguments();
		setTargetArea(((String) args[0]).charAt(0));
		setArea(Area.getInstance().getArea(getTargetArea()));
		setAreaSize(Area.getInstance().getAreaSize());
		setLimits();
		setCurrentPoint(getPointAt(new Location(Integer.valueOf((String) args[1]),
				Integer.valueOf((String) args[2]))));
		setPreviousPoint(new Point(new Location(-1, -1), false, false, false));
		startingLocation = new Location(Integer.valueOf((String) args[1]),
				Integer.valueOf((String) args[2]));

		addBehaviour(new SimpleBehaviour(this) {

			@Override
			public boolean done() {
				moveToLocation(startingLocation);
				System.out.println(getLocalName() + ": I'm Done!");
				return done;
			}

			@Override
			public void action() {
				// Wait for the start signal
				ACLMessage msg = blockingReceive();
				if (msg != null) {
					if (msg.getContent().equals(START_ACTION)) {
						// Start scanning the target area
						scanArea();
						moveToLocation(startingLocation);
					}
				}
			}
		});
	}

	/* Specific search bot methods */

	private void scanArea() {

		int pointsChecked = 0;

		// First scan
		System.out.println("------------------------");
		scanPoint(getCurrentPoint());
		System.out.println("------------------------");

		// Repeat until end location is reached
		while (pointsChecked != (getAreaSize() * getAreaSize() - 1)) {

			// Have different search patterns for each area
			switch (getTargetArea()) {
			case 'a':
				if (checkRight()) {
					moveRight();
				} else if (checkLeft()) {
					moveLeft();
				} else if (checkUp()) {
					moveUp();
				} else if (checkDown()) {
					moveDown();
				}
				break;
			case 'b':
				if (checkLeft()) {
					moveLeft();
				} else if (checkRight()) {
					moveRight();
				} else if (checkUp()) {
					moveUp();
				} else if (checkDown()) {
					moveDown();
				}
				break;
			case 'c':
				if (checkRight()) {
					moveRight();
				} else if (checkLeft()) {
					moveLeft();
				} else if (checkDown()) {
					moveDown();
				} else if (checkUp()) {
					moveUp();
				}
				break;
			case 'd':
				if (checkLeft()) {
					moveLeft();
				} else if (checkRight()) {
					moveRight();
				} else if (checkDown()) {
					moveDown();
				} else if (checkUp()) {
					moveUp();
				}
				break;
			}

			// Scan the new location
			scanPoint(getCurrentPoint());

			pointsChecked++;

			System.out.println("------------------------");
		}

		done = true;
	}

	private void scanPoint(Point p) {
		System.out.println(getLocalName() + ": Scanning: X: " + p.getLocation().getX() + " Y:"
				+ p.getLocation().getY());

		// Point is now explored
		p.setExplored(true);

		// alert a debris bot it there's debris
		if (p.isHasDebris()) {
			System.out.println(getLocalName() + ": Has debris");
			contactBot(
					String.valueOf(String.valueOf(getTargetArea()) + " "
							+ getCurrentPoint().getLocation().getX() + " "
							+ String.valueOf(getCurrentPoint().getLocation().getY())),
					Arrays.asList(new String[] { "d1", "d2", "d3", "d4" }));
		}

		// alert a transport bot if there's a victim
		if (p.isHasVictim()) {
			System.out.println(getLocalName() + ": Has victim");
			contactBot(
					String.valueOf(String.valueOf(getTargetArea()) + " "
							+ getCurrentPoint().getLocation().getX() + " "
							+ String.valueOf(getCurrentPoint().getLocation().getY())),
					Arrays.asList(new String[] { "t1", "t2", "t3", "t4" }));
		}
	}

	private boolean containsAgent(List<AID> agents, AID targetAgent) {
		for (AID agent : agents) {
			if (targetAgent.getLocalName().equals(agent.getLocalName())) {
				return true;
			}
		}
		return false;
	}
	
	private void contactBot(String message, List<String> possibleReceipients) {
		boolean canMoveOn = false;
		List<AID> excludedReceipients = new ArrayList<>();

		while (!canMoveOn) {
			// If all possibleReceipients are excluded try them all again
			if (excludedReceipients.size() == possibleReceipients.size()) {
				System.out.println("Clearing exlusions");
				excludedReceipients.clear();
			}

			AMSAgentDescription[] agents = null;

			try {
				SearchConstraints c = new SearchConstraints();
				c.setMaxResults(new Long(-1));
				agents = AMSService.search(this, new AMSAgentDescription(), c);
			} catch (FIPAException e) {
				e.printStackTrace();
			}

			// Decide whom the receiver will be
			AID receiver = null;
			for (int i = 0; i < agents.length; i++) {
				AID agentID = agents[i].getName();

				// Check agent against possible receipients
				for (String s : possibleReceipients) {
					if (agentID.getName().startsWith(s)) {
						
						// Continue if receiver hasnt been excluded earlier
						if (!containsAgent(excludedReceipients, agentID)) {
							receiver = agentID;
						}
					}
				}
			}

			if (receiver != null) {
				// Create and send the message with the target area
				sendMessage(receiver, String.valueOf(getTargetArea()), ACLMessage.CFP);
				System.out.println(getLocalName() + ": Start communication with ("
						+ receiver.getLocalName() + ")");
				ACLMessage msg = blockingReceive(1000);
				if (msg != null) {
					if (msg.getPerformative() == ACLMessage.PROPOSE) {
						// Send reply that bot can start work
						ACLMessage reply = msg.createReply();
						reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
						reply.setContent(message);
						send(reply);
						System.out.println(getLocalName() + ": Accept proposal from ("
								+ receiver.getLocalName() + "), waiting");

						// Wait for the new reply when debrisbot is done
						reply = blockingReceive();
						if (reply.getPerformative() == ACLMessage.INFORM) {
							// Debris bot is done, we can go further
							canMoveOn = true;
							System.out.println(getLocalName() + ": Received inform from ("
									+ receiver.getLocalName() + "), moving on");
						}
					} else if (msg.getPerformative() == ACLMessage.REFUSE) {
						excludedReceipients.add(receiver);
						System.out.println(getLocalName() + ": (" + receiver.getLocalName()
								+ ") Refused task");
					}
				}
			}
		}
	}

	// Method to get the edge values for the area
	private void setLimits() {
		int right = 0, left = 0, up = 0, down = 0, x, y;
		Point p;

		for (int i = 0; i < getAreaSize(); i++) {
			for (int j = 0; j < getAreaSize(); j++) {
				p = getArea()[i][j];
				x = p.getLocation().getX();
				y = p.getLocation().getY();

				if (x > right) {
					right = x;
				}

				if (y > up) {
					up = y;
				}
			}
		}
		left = right - (getAreaSize() - 1);
		down = up - (getAreaSize() - 1);

		setLimitRight(right);
		setLimitLeft(left);
		setLimitUp(up);
		setLimitDown(down);
	}
}
