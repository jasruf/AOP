package agents;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import main.Location;

public class SearchBot extends Robot {

	private Object[] args;
	private Location currentLoc;
	
	protected void setup() {
		
		args = getArguments();
		currentLoc = new Location(0, 0);
		
		addBehaviour(new SimpleBehaviour(this) {
			
			@Override
			public boolean done() {
				return false;
			}
			
			@Override
			public void action() {
				scanArea(new Location(5, 4), new Location(10, 20));
			}
		});
		
	}

	/* Specific search bot methods */

	private void foundVictim(Location l, Robot r) {

	}

	private void areaSearched(Location beginLoc, Location endLoc) {

	}

	private void scanArea(Location beginLoc, Location endLoc) {

	}

	private void alertDebrisBot(Location l) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContentObject(l);
		msg.set
		AID dest = null;
		
	}
}
