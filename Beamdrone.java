import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.PriorityQueue;

public class Beamdrone {
	private int n;
	private int m;
	private Nod source;
	private Nod destination;
	private char[][] table;
	private int[][] costTable;


	//Citirea din fisier
	public void readFromFile(BufferedReader myReader) throws IOException {
		String[] lineSplit = myReader.readLine().split(" ");
		this.setN(Integer.parseInt(lineSplit[0]));
		this.setM(Integer.parseInt(lineSplit[1]));
		lineSplit = myReader.readLine().split(" ");
		//Initializare nod sursa, nod destinatie, tabelul de costuri si tabelul citit din fisier
		this.source = new Nod(Integer.parseInt(lineSplit[0]), Integer.parseInt(lineSplit[1]));
		this.destination = new Nod(Integer.parseInt(lineSplit[2]), Integer.parseInt(lineSplit[3]));
		this.table = new char[this.getN()][this.getM()];
		this.costTable = new int[this.getN()][this.getM()];
		//Citirea tabelului din fisier
		for (int i = 0; i < this.getN(); i++) {
			String str = myReader.readLine();
			for (int j = 0; j < this.getM(); j++) {
				this.table[i][j] = str.charAt(j);
			}
		}
	}

	//Initializeaza tabelul de costuri cu MAX_INT
	public void initCostTable() {
		for (int i = 0; i < this.getN(); i++) {
			for (int j = 0; j < this.getM(); j++) {
				this.costTable[i][j] = Integer.MAX_VALUE;
			}
		}
	}

	//Verifica daca coodonatele primite ca input nu ies din limitele tabelului sau daca
	//este perete acolo
	public boolean isValid(int x, int y) {
		if (x < this.getN() && x >= 0 && y < this.getN() && y >= 0 && this.table[x][y] != 'W') {
			return true;
		}
		return false;
	}

	//Verifica daca coodonatele primite ca input nu ies din limitele tabelului sau daca
	//este perete acolo si daca coordonatele primite ca input nu sunt egale cu coordonatele
	//nodului parinte(pentru a nu cilca)
	public boolean isValid(int x, int y, Nod node) {
		if (x < this.getN() && x >= 0 && y < this.getN() && y >= 0 && this.table[x][y] != 'W'
				&& (x != node.getX() || y != node.getY())) {
			return true;
		}
		return false;
	}

	//Initializeaza in tabelul de costuri coordonatele sursei cu 0 si vecinii
	//acestuia de sus, jos, stanga sau dreapta daca este posibil(nu iese din 
	//limitele tabelului si nu este perete acolo) si adauga noile noduri 
	//intr-o coada de prioritati
	public PriorityQueue<Nod> initNeighOfSource() {
		PriorityQueue<Nod> queue = new PriorityQueue<Nod>();
		//Initializeaza coordonatele sursei cu 0
		this.costTable[this.getSource().getX()][this.getSource().getY()] = 0;
		//DOWN
		if (isValid(this.getSource().getX() + 1, this.getSource().getY())) {
			queue.add(new Nod(this.getSource().getX() + 1, this.getSource().getY(),
					0, 1, this.getSource()));
			this.costTable[this.getSource().getX() + 1][this.getSource().getY()] = 0;
		}
		//UP
		if (isValid(this.getSource().getX() - 1, this.getSource().getY())) {
			queue.add(new Nod(this.getSource().getX() - 1, this.getSource().getY(),
					0, 1, this.getSource()));
			this.costTable[this.getSource().getX() - 1][this.getSource().getY()] = 0;
		}
		//RIGHT
		if (isValid(this.getSource().getX(), this.getSource().getY() + 1)) {
			queue.add(new Nod(this.getSource().getX(), this.getSource().getY() + 1,
					0, 0, this.getSource()));
			this.costTable[this.getSource().getX()][this.getSource().getY() + 1] = 0;
		}
		//LEFT
		if (isValid(this.getSource().getX(), this.getSource().getY() - 1)) {
			queue.add(new Nod(this.getSource().getX(), this.getSource().getY() - 1,
					0, 0, this.getSource()));
			this.costTable[this.getSource().getX()][this.getSource().getY() - 1] = 0;
		}
		//Returneaza coada de prioritati
		return queue;
	}

	//Returneaza numarul de pasi minim
	public void getMinDistance() {
		//Adauga nodul sursa si vecinii acestuia in lista de prioritati
		PriorityQueue<Nod> queue = this.initNeighOfSource();
		//Pana cand lista nu este vida
		while (!queue.isEmpty()) {
			//Extrage din coada de prioritati
			Nod currentNode = queue.remove();
			//Daca costul nodului curent este mai mare decat valoarea de la coordonatele
			//x y a nodului nu mai are rost sa continuie intrucat am ajuns in acel nod
			//mai derveme pe o alta cale mai scurta
			if (currentNode.getCost()
				> this.getCostTable()[currentNode.getX()][currentNode.getY()]) {
				continue;
			}
			//DOWN
			if (isValid(currentNode.getX() + 1, currentNode.getY(), currentNode.getPrevios())) {
				//Daca directia este orizontala (0) si valoarea coordonatelor noi din tabelul de
				//costuri este mai mare sau egala decat valoarea de la coodonatele (x,y) + 1,
				//coordonatele curente + 1 care schimba directia(intrucat daca ma duc in jos si
				//directia a fost orizontala inseamna ca schimb directia)
				if (currentNode.getDirection() == 0
						&& this.costTable[currentNode.getX() + 1][currentNode.getY()]
						>= this.costTable[currentNode.getX()][currentNode.getY()] + 1) {
					//Adauga in coada noul nod
					queue.add(new Nod(currentNode.getX() + 1, currentNode.getY(),
							currentNode.getCost() + 1, 1, currentNode));
					
					//Actualizeaza tabelul de costuri
					this.costTable[currentNode.getX() + 1][currentNode.getY()]
									= this.costTable[currentNode.getX()][currentNode.getY()] + 1;
					//Daca are aceeasi directie si valoarea coordonatelor noi din tabelul de
					//costuri este mai mare sau egala decat valoarea de la coordonatele (x,y)
					//coordonatele curente + 0 care inseamna ca nu a fost schimbata orientarea
				} else if (currentNode.getDirection() == 1
							&& this.costTable[currentNode.getX() + 1][currentNode.getY()]
							>= this.costTable[currentNode.getX()][currentNode.getY()]) {
					//Adauga in coada noul nod
					queue.add(new Nod(currentNode.getX() + 1, currentNode.getY(),
							currentNode.getCost(), 1, currentNode));
					//Actualizeaza tabelul de costuri
					this.costTable[currentNode.getX() + 1][currentNode.getY()]
						= this.costTable[currentNode.getX()][currentNode.getY()];
				}
			}
			//UP
			if (isValid(currentNode.getX() - 1, currentNode.getY(), currentNode.getPrevios())) {
				//Daca directia este orizontala (0) si valoarea coordonatelor noi din tabelul de
				//costuri este mai mare sau egala decat valoarea de la coodonatele (x,y) + 1,
				//coordonatele curente + 1 care schimba directia(intrucat daca ma duc in sus si
				//directia a fost orizontala inseamna ca schimb directia)
				if (currentNode.getDirection() == 0
					&& this.costTable[currentNode.getX() - 1][currentNode.getY()]
					>= this.costTable[currentNode.getX()][currentNode.getY()] + 1) {

					//Adauga in coada noul nod
					queue.add(new Nod(currentNode.getX() - 1, currentNode.getY(),
							currentNode.getCost() + 1, 1, currentNode));
					//Actualizeaza tabelul de costuri
					this.costTable[currentNode.getX() - 1][currentNode.getY()]
						= this.costTable[currentNode.getX()][currentNode.getY()] + 1;
					//Daca are aceeasi directie si valoarea coordonatelor noi din tabelul de
					//costuri este mai mare sau egala decat valoarea de la coordonatele (x,y)
					//coordonatele curente + 0 care inseamna ca nu a fost schimbata orientarea		
				} else if (currentNode.getDirection() == 1
						&& this.costTable[currentNode.getX() - 1][currentNode.getY()]
						>= this.costTable[currentNode.getX()][currentNode.getY()]) {
					//Adauga in coada noul nod
					queue.add(new Nod(currentNode.getX() - 1, currentNode.getY(),
							currentNode.getCost(), 1, currentNode));
					//Actualizeaza tabelul de costuri
					this.costTable[currentNode.getX() - 1][currentNode.getY()]
						= this.costTable[currentNode.getX()][currentNode.getY()];
				}
			}
			//RIGHT
			if (isValid(currentNode.getX(), currentNode.getY() + 1, currentNode.getPrevios())) {
				//Daca directia este orizontala (0) si valoarea coordonatelor noi din tabelul de
				//costuri este mai mare sau egala decat valoarea de la coodonatele (x,y) + 1,
				//coordonatele curente + 1 care schimba directia(intrucat daca ma duc in jos si
				//directia a fost orizontala inseamna ca schimb directia)
				if (currentNode.getDirection() == 0
						&& this.costTable[currentNode.getX()][currentNode.getY() + 1]
						>= this.costTable[currentNode.getX()][currentNode.getY()]) {
					//Adauga in coada noul nod
					queue.add(new Nod(currentNode.getX(), currentNode.getY() + 1,
							currentNode.getCost(), 0, currentNode));
					//Actualizeaza tabelul de costuri
					this.costTable[currentNode.getX()][currentNode.getY() + 1]
						= this.costTable[currentNode.getX()][currentNode.getY()];
					//Daca are aceeasi directie si valoarea coordonatelor noi din tabelul de
					//costuri este mai mare sau egala decat valoarea de la coordonatele (x,y)
					//coordonatele curente + 0 care inseamna ca nu a fost schimbata orientarea
				} else if (currentNode.getDirection() == 1
						&& this.costTable[currentNode.getX()][currentNode.getY() + 1]
						>= this.costTable[currentNode.getX()][currentNode.getY()] + 1) {
					//Adauga in coada noul nod
					queue.add(new Nod(currentNode.getX(), currentNode.getY() + 1,
							currentNode.getCost() + 1, 0, currentNode));
					//Actualizeaza tabelul de costuri
					this.costTable[currentNode.getX()][currentNode.getY() + 1]
						= this.costTable[currentNode.getX()][currentNode.getY()] + 1;
				}
			}
			//LEFT
			if (isValid(currentNode.getX(), currentNode.getY() - 1, currentNode.getPrevios())) {
				//Daca directia este orizontala (0) si valoarea coordonatelor noi din tabelul de
				//costuri este mai mare sau egala decat valoarea de la coodonatele (x,y) + 1,
				//coordonatele curente + 1 care schimba directia(intrucat daca ma duc in jos si
				//directia a fost orizontala inseamna ca schimb directia)
				if (currentNode.getDirection() == 0
						&& this.costTable[currentNode.getX()][currentNode.getY() - 1]
						>= this.costTable[currentNode.getX()][currentNode.getY()]) {
					//Adauga in coada noul nod
					queue.add(new Nod(currentNode.getX(), currentNode.getY() - 1,
							currentNode.getCost(), 0, currentNode));
					//Actualizeaza tabelul de costuri
					this.costTable[currentNode.getX()][currentNode.getY() - 1]
						= this.costTable[currentNode.getX()][currentNode.getY()];
					//Daca are aceeasi directie si valoarea coordonatelor noi din tabelul de
					//costuri este mai mare sau egala decat valoarea de la coordonatele (x,y)
					//coordonatele curente + 0 care inseamna ca nu a fost schimbata orientarea
				} else if (currentNode.getDirection() == 1
						&& this.costTable[currentNode.getX()][currentNode.getY() - 1]
						>= this.costTable[currentNode.getX()][currentNode.getY()] + 1) {
					//Adauga in coada noul nod
					queue.add(new Nod(currentNode.getX(), currentNode.getY() - 1,
							currentNode.getCost() + 1, 0, currentNode));
					//Actualizeaza tabelul de costuri
					this.costTable[currentNode.getX()][currentNode.getY() - 1]
						= this.costTable[currentNode.getX()][currentNode.getY()] + 1;

				}
			}

		}

	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("beamdrone.in"));

		Beamdrone beamdrone = new Beamdrone();
		beamdrone.readFromFile(br);
		beamdrone.initCostTable();
		beamdrone.getMinDistance();
		BufferedWriter bw = new BufferedWriter(new FileWriter("beamdrone.out"));
		bw.write("" + beamdrone.costTable[beamdrone.getDestination().getX()]
										[beamdrone.getDestination().getY()]);
		bw.close();
		br.close();
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getM() {
		return m;
	}

	public void setM(int m) {
		this.m = m;
	}

	public Nod getSource() {
		return source;
	}

	public void setSource(Nod source) {
		this.source = source;
	}

	public Nod getDestination() {
		return destination;
	}

	public void setDestination(Nod destination) {
		this.destination = destination;
	}

	public char[][] getTable() {
		return table;
	}

	public void setTable(char[][] table) {
		this.table = table;
	}

	public int[][] getCostTable() {
		return costTable;
	}

	public void setCostTable(int[][] costTable) {
		this.costTable = costTable;
	}
}
