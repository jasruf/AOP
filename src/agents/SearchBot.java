package agents;

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

	protected void setup() {

		System.out.println("Searchbot setup starting");

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

		//First scan
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
			
			//Scan the new location
			scanPoint(getCurrentPoint());

			pointsChecked++;

			System.out.println("------------------------");
		}

		System.out.println("X: " + curLoc.getX() + " Y:" + curLoc.getY());
		done = true;
	}

	private void scanPoint(Point p) {
		System.out.println("Scanning: X: " + p.getLocation().getX() + " Y:" + p.getLocation().getY());
		
		//Point is now explored
		p.setExplored(true);

		// alert a debris bot it there's debris
		if (p.isHasDebris()) {
			System.out.println("Has debris");
			alertDebrisBot();
			return;
		}

		// alert a transport bot if there's a victim
		if (p.isHasVictim()) {
			System.out.println("Has victim");
			alertTransportBot();
		}
	}

	private void sendMessage(String message, String[] possibleReceipiants) {
		AMSAgentDescription[] agents = null;

		try {
			SearchConstraints c = new SearchConstraints();
			c.setMaxResults(new Long(-1));
			agents = AMSService.search(this, new AMSAgentDescription(), c);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		//Decide where whom te receiver will be
		AID receiver = null;
		for (int i = 0; i < agents.length; i++) {
			AID agentID = agents[i].getName();
			//Check agent agains possible receipiants
			for(String s : possibleReceipiants){
				if(agentID.getName().startsWith(s)){
					receiver = agentID;
					break;
				}
			}
		}

		//Creat and send the message
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent(message);
		msg.addReceiver(receiver);
		send(msg);
	}

	private void alertDebrisBot() {
		sendMessage(String.valueOf(String.valueOf(getTargetArea()) + " " +getCurrentLoc().getX() +" " + String.valueOf(getCurrentLoc().getY())), new String[]{"d1", "d2", "d3", "d4"});
	}

	private void alertTransportBot() {
		sendMessage(String.valueOf(String.valueOf(getTargetArea()) + " " +getCurrentLoc().getX() +" " + String.valueOf(getCurrentLoc().getY())), new String[]{"t1", "t2", "t3", "t4"});		
	}
}
