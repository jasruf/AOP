package agents;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import main.Area;
import main.Location;
import main.Victim;

public class TransportBot extends Robot {
	
	private Object[] args;
	private boolean done = false;
	private Location targetLocation;

	protected void setup() {

		System.out.println("Transport setup starting");

		// Setup area and location
		args = getArguments();
		setTargetArea(((String) args[0]).charAt(0));
		setCurrentLoc(new Location(Integer.valueOf((String) args[1]),
				Integer.valueOf((String) args[2])));
		setArea(Area.getInstance());
		setAreaSize(getArea().getAreaSize());

		addBehaviour(new SimpleBehaviour(this) {

			@Override
			public boolean done() {
				System.out.println(getLocalName() + ": I'm Done!");
				return done;
			}

			@Override
			public void action() {
				// Wait for the start signal
				ACLMessage msg = blockingReceive();
				if (msg != null) {
					String[] msgContent = msg.getContent().split(" ");
					targetLocation = new Location(Integer.valueOf(msgContent[1]),
							Integer.valueOf(msgContent[2]));
					System.out.println(getLocalName() + " moving to: X:" + targetLocation.getX()
							+ ", Y: " + targetLocation.getY());
					moveToLocation(targetLocation);
					done = true;
				}
			}
		});
	}
	
	/* Specific transport bot methods */

	private void loadVictim(Victim v) {

	}

	private void goToHospital(Location l) {

	}

}
