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

public class SearchBot extends Robot {

	private static final int SLEEP_TIME = 1000;

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
				sendMessage("3 5");
				System.out.println(getLocalName() + ": I'm Done!");
				return done;
			}

			@Override
			public void action() {
				//Wait for the start signal
				ACLMessage msg = blockingReceive();
				if (msg != null) {
					if (msg.getContent().equals(START_ACTION)) {
						//Start scanning the target area
						scanArea2('c', getCurrentLoc());
					}
				}
			}
		});
	}

	/* Specific search bot methods */

	private void foundVictim(Location l, Robot r) {
		
	}

	private void areaSearched(Location beginLoc, Location endLoc) {
	}

	private void scanArea2(char subArea, Location curLoc) {

		int pointsChecked = 0;

		// Repeat until end location is reached
		while (pointsChecked != (getAreaSize() * getAreaSize() - 1)) {
			if (checkRight()) {
				moveRight();
				System.out.println("------------------------");
			} else if (checkLeft()) {
				moveLeft();
				System.out.println("------------------------");
			} else if (checkUp()) {
				moveUp();
				System.out.println("------------------------");
			} else if (checkDown()) {
				moveDown();
				System.out.println("------------------------");
			}

			// Update the current location
			curLoc = getCurrentLoc();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			pointsChecked++;
		}

		System.out.println("X: " + curLoc.getX() + " Y:" + curLoc.getY());
		done = true;
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
			for (int i = endLoc.getX(); i < beginLoc.getX(); i++) {
				for (int j = endLoc.getY(); j > beginLoc.getY(); j--) {
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
	
	private void sendMessage(String message){
		AMSAgentDescription[] agents = null;

		try {
			SearchConstraints c = new SearchConstraints();
			c.setMaxResults(new Long(-1));
			agents = AMSService.search(this, new AMSAgentDescription(), c);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		
        AID receiver = null;
        for (int i=0; i<agents.length;i++)
        {
            AID agentID = agents[i].getName();
            if(agentID.getName().startsWith("d1")){
            	receiver = agentID;
            }
        }
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent(message);
		msg.addReceiver(receiver);
		send(msg);
	}

	private void alertDebrisBot(Location l) {


		// ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		// msg.setContentObject(l);
		// msg.set
		// AID dest = null;
	}
}
