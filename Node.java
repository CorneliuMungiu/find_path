import java.util.ArrayList;

public class Node {
	private int index;
	private ArrayList<Edge> adj;
	private int row;
	private int col;
	private int dist;

	public Node(int index, int row, int col, int dist) {
		this.index = index;
		this.adj = new ArrayList<>();
		this.row = row;
		this.col = col;
		this.dist = dist;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public ArrayList<Edge> getAdj() {
		return adj;
	}

	public void setAdj(ArrayList<Edge> adj) {
		this.adj = adj;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getDist() {
		return dist;
	}

	public void setDist(int dist) {
		this.dist = dist;
	}
}
