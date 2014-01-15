package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import main.Area;
import main.Location;

public class TransportBot extends Robot {
	
	private Object[] args;
	private Location targetLocation;
	private Location startingLocation;
	private boolean isWorking = false;
	private int victimCount = 0;

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

		addBehaviour(new CyclicBehaviour(this) {

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
	
	/* Specific transport bot methods */

	private void loadVictim() {
		victimCount++;
	}
	
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
			loadVictim();
			System.out.println(getLocalName() + ": got vimctim, inform searchbot");

			// Inform the searchbot it's done
			moveToLocation(startingLocation);
			sendMessage(msg.getSender(), null, ACLMessage.INFORM);
			isWorking = false;
			break;
		}

	}

}
