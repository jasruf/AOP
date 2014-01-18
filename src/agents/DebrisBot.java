package agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import main.Area;
import main.Location;
import main.Point;

public class DebrisBot extends Robot {

	private Object[] args;
	private Location startingLocation;
	private Location targetLocation;
	private boolean isWorking = false;

	protected void setup() {

		System.out.println(getLocalName() + " setup starting");

		// Setup area and location
		args = getArguments();
		setTargetArea(((String) args[0]).charAt(0));
		setArea(Area.getInstance().getArea(getTargetArea()));
		setAreaSize(Area.getInstance().getAreaSize());
		setCurrentPoint(getPointAt(new Location(Integer.valueOf((String) args[1]),
				Integer.valueOf((String) args[2]))));
		startingLocation = new Location(Integer.valueOf((String) args[1]),
				Integer.valueOf((String) args[2]));

		addBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				// Wait for the start signal
				ACLMessage msg = blockingReceive();
				if (msg != null) {
					communicate(msg);
				}
			}
		});
	}

	/* specific debris bot methods */

	private void communicate(ACLMessage msg) {
		switch (msg.getPerformative()) {
		case ACLMessage.CFP:
			if (isWorking) {
				// We're already doing something, refusing
				sendMessage(msg.getSender(), null, ACLMessage.REFUSE);
				System.out.println(getLocalName() + ": Refusing work");
			} else {
				sendMessage(msg.getSender(), null, ACLMessage.PROPOSE);
				System.out.println(getLocalName() + ": Proposing work");
			}
			break;
		case ACLMessage.ACCEPT_PROPOSAL:
			// Get to work
			isWorking = true;
			String[] msgContent = msg.getContent().split(" ");
			targetLocation = new Location(Integer.valueOf(msgContent[1]),
					Integer.valueOf(msgContent[2]));
			System.out.println(getLocalName() + ": moving to: X:" + targetLocation.getX() + ", Y: "
					+ targetLocation.getY());
			moveToLocation(targetLocation);
			clearDebris(getCurrentPoint().getLocation());
			System.out.println(getLocalName() + ": Cleared debris, inform searchbot");

			// Inform the searchbot it's done
			moveToLocation(startingLocation);
			sendMessage(msg.getSender(), null, ACLMessage.INFORM);
			isWorking = false;
			break;
		}

	}

	private void alertTransportBot(Location l) {

	}

	private void clearDebris(Location l) {

	}

}
