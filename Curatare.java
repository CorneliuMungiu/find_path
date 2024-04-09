import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Curatare {
	private int n;
	private int m;
	private char[][] table;
	private ArrayList<Node> robots;
	private ArrayList<Node> dirty;
	public int Rez = Integer.MAX_VALUE;
	public boolean[] spotVisited; 

	//Citirea din fisier
	public void readFromFile(BufferedReader myReader) {
		try {
			String[] lineSplit = myReader.readLine().split(" ");
			this.setN(Integer.parseInt(lineSplit[0]));
			this.setM(Integer.parseInt(lineSplit[1]));
			this.table = new char[this.getN()][this.getM()];
			this.robots = new ArrayList<>();
			this.dirty = new ArrayList<>();
			String str;
			//Adauga in tabel caracterele citite din fisierul de input
			for (int i = 0; i < this.n; i++) {
				str = myReader.readLine();
				for (int j = 0; j < this.m; j++) {
					this.table[i][j] = str.charAt(j);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	//Parcurge tabelul si creeaza noduri atunci cand intalneste pata sau robot
	public void createNodes() {
		int index_counter = -1;
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				//Daca nu e robot sau pata
				if (this.table[i][j] == 'X' || this.table[i][j] == '.') {
					continue;
				}
				//Daca este pata
				if (this.table[i][j] == 'S') {
					index_counter++;
					dirty.add(new Node(index_counter, i, j, 0));
				} else { //Daca este robot
					index_counter++;
					robots.add(new Node(index_counter, i, j, 0));
				}
			}
		}
		this.spotVisited = new boolean[this.dirty.size()];
	}

	//Adauga pentru fiecare pata o lista de adiacenta cu fiecare nod care a mai ramas
	public void addAdjDirty() {
		//Adauga fiecarui node adj-nod
		for (Node i : this.dirty) {
			for (Node j : this.dirty) {
				if (i.getIndex() != j.getIndex()) {
					i.getAdj().add(new Edge(j, 0));
				}
			}
		}
	}

	//Adauga pentru fiecare robot o lista de adiacenta cu fiecare nod din lista de pete
	public void addAdjRobots() {
		for (Node i : this.robots) {
			for (Node j : this.dirty) {
				i.getAdj().add(new Edge(j, 0));
			}
		}
	}

	//Sterge din table S sau R(petele sau robotii) si le inlocuieste cu '.'
	public void removeDirtyAndRobots() {
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				if (this.table[i][j] == 'S' || this.table[i][j] == 'R') {
					this.table[i][j] = '.';
				}
			}
		}
	}

	//Verifica daca coordonatele date ca input sunt valide
	private static boolean isValid(int x, int y, char[][] grid, boolean[][] visited) {
		//Verifica daca x si y nu iese din limitele tabelului, daca nu a fost vizitata aceasta
		//casuta si daca nu este perete
		if (x >= 0 && y >= 0 && x < grid.length && y < grid[0].length && grid[x][y] != 'X'
			&& visited[x][y] == false) {
			return true;
		}
		return false;
	}

	//Calculeaza distanta minima de la sursa la destinatie
	private int minDistance(Node source, Node destination) {
		//Coada in care se vor pastra nodurile care inca nu au fost parcurse integral
		//(nu s-au verificat toate cazurile(sus,jos,stanga,dreapta))
		Queue<Node> queue = new LinkedList<>();
		//Adauga nodul sursa in coada
		queue.add(source);
		//Vector visited care va tine minte toate casutele care au fost vizitate deja(sau nu)
		boolean[][] visited = new boolean[this.getN()][this.getM()];
		//Casuta sursei se considera vizitata
		visited[source.getRow()][source.getCol()] = true;
		//Pun in tabel destinatia R(Robot)
		this.table[destination.getRow()][destination.getCol()] = 'S';
		//Atat timp cat coada nu este vida
		while (queue.isEmpty() == false) {
			//Extrage din coada primul element
			Node p = queue.remove();
			//Daca a ajuns la R(destinatie) inseamna ca a ajuns la aceasta in cel mai scurt timp
			if (this.table[p.getRow()][p.getCol()] == 'S') {
				//Reinitializez campul setat mai devreme cu R in '.'
				this.table[destination.getRow()][destination.getCol()] = '.';
				return p.getDist();
			}

			//UP
			if (isValid(p.getRow() - 1, p.getCol(), this.table, visited)) {
				queue.add(new Node(0, p.getRow() - 1, p.getCol(), p.getDist() + 1));
				visited[p.getRow() - 1][p.getCol()] = true;
			}
			//DOWN
			if (isValid(p.getRow() + 1, p.getCol(), this.table, visited)) {
				queue.add(new Node(0, p.getRow() + 1, p.getCol(), p.getDist() + 1));
				visited[p.getRow() + 1][p.getCol()] = true;
			}
			//LEFT
			if (isValid(p.getRow(), p.getCol() - 1, this.table, visited)) {
				queue.add(new Node(0, p.getRow(), p.getCol() - 1, p.getDist() + 1));
				visited[p.getRow()][p.getCol() - 1] = true;
			}
			//RIGHT
			if (isValid(p.getRow(), p.getCol() + 1, this.table, visited)) {
				queue.add(new Node(0, p.getRow(), p.getCol() + 1, p.getDist() + 1));
				visited[p.getRow()][p.getCol() + 1] = true;
			}
		}
		//Daca nu a gasit cale de la sursa la destinatie reinitializeaza campul R cu '.' si
		//returneaza -1
		this.table[destination.getRow()][destination.getCol()] = '.';
		return -1;
	}

	//Parcurge lista de roboti si de pete si calculeaza distanta de la pete la pete 
	//si de la roboti la pete adaugandu-le in lista de adiacenta
	public void setAdj() {
		for (Node i : this.dirty) {
			for (Edge j : i.getAdj()) {
				j.setDistance(minDistance(i, j.getNode()));
			}
		}
		for (Node i : this.robots) {
			for (Edge j : i.getAdj()) {
				j.setDistance(minDistance(i, j.getNode()));
			}
		}
	}
	


	public static void checkSum(ArrayList<Integer[]> res, int n, int x, Integer[] v) {
		int sum = 0;
		Integer[] aux = new Integer[n];
		for (int i = 0; i < n; i++) {
			sum += v[i];
			aux[i] = v[i];
		}
		if (sum == x) {
			res.add(aux);
		}
	}

	//Intoarce un ArrayList de Integer[] in care indexul primului 
	//ArrayList va reprezenta toate cazurile posibile cum robotii pot sterge
	//petele iar in Integer[] indexul va reprezenta care robot
	//va trebui sa stearga pata iar valoarea de la acel index cate pete va trebui
	//sa stearga(ex. [[0,4],[1,3],[2,2],[3,1][4,0]], unde [0,4] - 0 este numarul
	//de pete pe care trebuie sa le stearga robotul 0 si 4 - numarul de pete pe care
	//trebuie sa le stearga robotul 1)
	public static ArrayList<Integer[]> robotsNrOfWork(int nrOfRobots, int nrOfDirty) {
		Integer[] v = new Integer[nrOfRobots];
		//Initializeaza vectorul v
		for (int i = 0; i < nrOfRobots; i++) {
			v[i] = 0;
		}
		//check va fi conditia de iesire din while(true)
		boolean check = true;
		ArrayList<Integer[]> res = new ArrayList<>();
		//Acest while lucreaza ca o adunare in colonita incepand de la 0000 si 
		//aduna +1 iar la fiecare pas face checkSum care verifica daca suma 
		//cifrelor este egala cu numarul de pete([1,4] nu este valid pentru
		//4 pete pe cand [0,4] este valid)
		while (check) {
			//Incrementeaza ultimul element cu 1 daca acesta este mai mic
			//decat numarul de pete
			if (v[nrOfRobots - 1] < nrOfDirty) {
				v[nrOfRobots - 1] += 1;
				//Verifica daca suma este egala cu nr de pete , si daca este
				//atunci adauga vectorul in arrayList
				checkSum(res, nrOfRobots, nrOfDirty, v);
			} else {
				//Daca ultimul element din vector este numarul de pete atunci
				//merg in stanga pana gasesc un numar mai mic decat numarul de
				//pete pentru al incrementa cu 1, insa daca pe drum se intalnesc
				//elemente egale cu nr de pete atunci acestea se transforma in 0
				//(ex: pentru 4 pete si 4 roboti 0333 + 1 va deveni 1000);
				for (int j = nrOfRobots - 1; j >= 0; j--) {
					if (v[j] < nrOfDirty) {
						v[j] += 1;
					
						checkSum(res, nrOfRobots, nrOfDirty, v);
						if (j == 0 && v[j] == (nrOfDirty)) {
							check = false;
						}
						break;
					} else {
						if (j == 0 && v[j] == (nrOfDirty)) {
							check = false;
							break;
						}
						v[j] = 0;
						continue;
					}
				}
			}
		}
		return res;
	}

	//Maximum intr-un int[]
	public int getMax(int[] arr) {
		int max = -1;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > max) {
				max = arr[i];
			}
		}
		return max;
	}

	//Distanta dintre 2 noduri(Cauta in lista de adiacenta a nodului "a" nodul b
	//si extrage distanta calculata deja mai devreme)
	public int getDistanceFrom2Nodes(Node a, Node b) {
		for (int i = 0; i < a.getAdj().size(); i++) {
			if (a.getAdj().get(i).getNode().getIndex() == b.getIndex()) {
				return a.getAdj().get(i).getDistance();
			}
		}
		return -1;
	}

	public void getMin(Integer[] bkt, int robot, Node lastAddedNode, int added,
						int steps, int counterAddedDirty,
						int[] robotsSteps) {
		//Daca nu mai exista roboti il lista
		if (robot == this.robots.size()) {
			return;
		}
		//Daca au fost curatate toate petele
		if (counterAddedDirty == this.dirty.size()) {
			//Calculeaza minimum dintre valoarea care a fost si maximum dintre
			//valorile salvate in robotsSteps(daca un robot va curata toate petele
			//in 10 pasi iar al 2-lea in 12 pasi atunci se va considera ca robotii
			//au terminat munca in 12 pasi)
			int max = getMax(robotsSteps);
			this.Rez = Math.min(max, this.Rez);
			return;
		}
		//Distanta dintre 2 noduri
		int aux = 0;
		for (int i = 0; i < this.dirty.size() && added < bkt[robot]; i++) {
			//Daca aceasta casuta a fost vizitata deja
			if (this.spotVisited[i] == false) {
				//Daca a fost deja adaugat un nod atunci trebuie sa gaseasca distanta
				//dintre acel nod si nodul curent
				if (lastAddedNode != null) {
					aux = getDistanceFrom2Nodes(lastAddedNode, this.dirty.get(i));
				} else { //Daca ultimul nod este null atunci ia distanta de la robot pana la pata i
					aux = getDistanceFrom2Nodes(this.robots.get(robot), this.dirty.get(i));
				}
				//Marcheaza casuta i ca vizitata
				this.spotVisited[i] = true;
				robotsSteps[robot] = steps + aux;
				//Recursia
				getMin(bkt, robot, this.dirty.get(i), added + 1, steps + aux,
						counterAddedDirty + 1, robotsSteps);
				this.spotVisited[i] = false;
			}
		}
		robotsSteps[robot] = steps;
		steps = 0;
		//Recursia
		getMin(bkt, robot + 1, null, 0, steps, counterAddedDirty, robotsSteps);
	}

	public void genereateAllCombinations() {
		ArrayList<Integer[]> robotsWork = new ArrayList<>();
		//Genereaza toate combinatiile posibile prin care cei n roboti 
		//pot curata cele m pete
		robotsWork = robotsNrOfWork(this.robots.size(), this.dirty.size());
		//Apeleaza functia getMin pe toate combinatiile din ArrayList
		for (int i = 0; i < robotsWork.size(); i++) {
			getMin(robotsWork.get(i), 0, null, 0, 0, 0, new int[this.robots.size()]);
		}
	}



	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("curatare.in"));
		

		Curatare curatare = new Curatare();
		curatare.readFromFile(br);
		curatare.createNodes();
		curatare.addAdjDirty();
		curatare.addAdjRobots();
		curatare.removeDirtyAndRobots();
		curatare.setAdj();
		curatare.genereateAllCombinations();
		BufferedWriter bw = new BufferedWriter(new FileWriter("curatare.out"));
		bw.write("" + curatare.Rez);
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

	public char[][] getTable() {
		return table;
	}

	public void setTable(char[][] table) {
		this.table = table;
	}
}
