package main;

public class Point {

	private Location location;
	private boolean hasVictim;
	private boolean hasDebris;
	private boolean explored;

	public Point(Location location, boolean hasVictim, boolean hasDebris, boolean explored) {
		super();
		this.location = location;
		this.hasVictim = hasVictim;
		this.hasDebris = hasDebris;
		this.explored = explored;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public boolean isHasVictim() {
		return hasVictim;
	}

	public void setHasVictim(boolean hasVictim) {
		this.hasVictim = hasVictim;
	}

	public boolean isHasDebris() {
		return hasDebris;
	}

	public void setHasDebris(boolean hasDebris) {
		this.hasDebris = hasDebris;
	}

	public boolean isExplored() {
		return explored;
	}

	public void setExplored(boolean explored) {
		this.explored = explored;
	}
}
