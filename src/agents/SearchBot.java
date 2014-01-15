package agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
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
		setCurrentLoc(new Location(Integer.valueOf((String) args[1]),
				Integer.valueOf((String) args[2])));
		startingLocation = new Location(Integer.valueOf((String) args[1]),
				Integer.valueOf((String) args[2]));
		setArea(Area.getInstance());
		setAreaSize(getArea().getAreaSize());

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
						scanArea('c', getCurrentLoc());
					}
				}
			}
		});
	}

	/* Specific search bot methods */

	private void foundVictim(Location l, Robot r) {
	}

	private void scanArea(char subArea, Location curLoc) {

		int pointsChecked = 0;

		// First scan
		scanPoint(getCurrentPoint());
		System.out.println("------------------------");

		// Repeat until end location is reached
		while (pointsChecked != (getAreaSize() * getAreaSize() - 1)) {

			if (checkRight()) {
				moveRight();
			} else if (checkLeft()) {
				moveLeft();
			} else if (checkUp()) {
				moveUp();
			} else if (checkDown()) {
				moveDown();
			}

			// Update the current location
			curLoc = getCurrentLoc();

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
			contactDebrisBot(
					String.valueOf(String.valueOf(getTargetArea()) + " " + getCurrentLoc().getX()
							+ " " + String.valueOf(getCurrentLoc().getY())),
					Arrays.asList(new String[] { "d1", "d2", "d3", "d4" }));
		}

		// alert a transport bot if there's a victim
		if (p.isHasVictim()) {
			System.out.println(getLocalName() + ": Has victim");
			contactTransportBot(
					String.valueOf(String.valueOf(getTargetArea()) + " " + getCurrentLoc().getX()
							+ " " + String.valueOf(getCurrentLoc().getY())),
					Arrays.asList(new String[] { "t1", "t2", "t3", "t4" }));
		}
	}

	private boolean containsAgent(List<AID> agents, AID targetAgent) {
		for (AID agent : agents) {
			if (targetAgent.getLocalName() == agent.getLocalName()) {
				return false;
			}
		}
		return true;
	}

	private void contactTransportBot(String message, List<String> possibleReceipients) {
		boolean canMoveOn = false;
		List<AID> excludedReceipients = new ArrayList<>();

		while (!canMoveOn) {
			// If all possibleReceipients are excluded try them all again
			if (excludedReceipients.size() == possibleReceipients.size()) {
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
						receiver = agentID;

						// Continue if receiver has been excluded earlier
						if (!containsAgent(excludedReceipients, receiver)) {
							break;
						}
					}
				}
			}

			if (receiver != null) {
				// Create and send the message
				System.out.println(getLocalName() + ": Start communication with transportbot ("
						+ receiver.getLocalName() + ")");
				sendMessage(receiver, null, ACLMessage.CFP);
				ACLMessage msg = blockingReceive();
				if (msg.getPerformative() == ACLMessage.PROPOSE) {
					// Send reply that bot can start work
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					reply.setContent(message);
					send(reply);
					System.out.println(getLocalName()
							+ ": Accept proposal from transportbot, waiting");

					// Wait for the new reply when debrisbot is done
					reply = blockingReceive();
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Debris bot is done, we can go further
						canMoveOn = true;
						System.out.println(getLocalName()
								+ ": Received inform from transportbot, moving on");
					}
				} else if (msg.getPerformative() == ACLMessage.REFUSE) {
					excludedReceipients.add(receiver);
					System.out.println(getLocalName() + ": Transportbot Refused task (asshole)");
				}
			}
		}
	}

	private void contactDebrisBot(String message, List<String> possibleReceipients) {
		boolean canMoveOn = false;
		List<AID> excludedReceipients = new ArrayList<>();

		while (!canMoveOn) {
			// If all possibleReceipients are excluded try them all again
			if (excludedReceipients.size() == possibleReceipients.size()) {
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
						receiver = agentID;

						// Continue if receiver has been excluded earlier
						if (!containsAgent(excludedReceipients, receiver)) {
							break;
						}
					}
				}
			}

			if (receiver != null) {
				// Create and send the message
				sendMessage(receiver, null, ACLMessage.CFP);
				System.out.println(getLocalName() + ": Start communication with debrisbot ("
						+ receiver.getLocalName() + ")");
				ACLMessage msg = blockingReceive();
				if (msg.getPerformative() == ACLMessage.PROPOSE) {
					// Send reply that bot can start work
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					reply.setContent(message);
					send(reply);
					System.out
							.println(getLocalName() + ": Accept proposal from debrisbot, waiting");

					// Wait for the new reply when debrisbot is done
					reply = blockingReceive();
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Debris bot is done, we can go further
						canMoveOn = true;
						System.out.println(getLocalName()
								+ ": Received inform from debris, moving on");
					}
				} else if (msg.getPerformative() == ACLMessage.REFUSE) {
					excludedReceipients.add(receiver);
					System.out.println(getLocalName() + ": Debrisbot Refused task (asshole)");
				}
			}
		}
	}
}
