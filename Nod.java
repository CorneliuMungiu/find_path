public class Nod implements Comparable<Nod> {
	private int x;
	private int y;
	private int cost;
	private int direction;
	private Nod previos;

	public Nod(int x, int y, int cost, int direction, Nod previos) {
		this.x = x;
		this.y = y;
		this.cost = cost;
		this.direction = direction;
		this.previos = previos;
	}

	public Nod(int x, int y) {
		this.x = x;
		this.y = y;
		this.cost = 0;
		this.direction = 0;
		this.previos = null;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public Nod getPrevios() {
		return previos;
	}

	public void setPrevios(Nod previos) {
		this.previos = previos;
	}

	@Override
	public int compareTo(Nod o) {
		return this.cost - o.cost;
	}
}
