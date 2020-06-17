import java.awt.Point;
import java.util.ArrayList;

public class GameState{
	private int whoseTurn;
	private int[][] towerStatus;
	private Point[] workers;
	private SantoriniGame theGame;
	public GameState(Point[] workers, SantoriniGame theGame) {
		this.workers = workers;
		this.theGame = theGame;
		whoseTurn = 0;
		towerStatus = new int[5][5];
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				towerStatus[i][j] = 0;
			}
		}
	}
	public GameState(GameState clone) {
		whoseTurn = clone.whoseTurn;
		theGame = clone.theGame;
		towerStatus = new int[5][5];
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				towerStatus[i][j] = clone.towerStatus[i][j];
			}
		}
		workers = new Point[clone.workers.length];
		for(int i = 0; i < workers.length; i++) {
			workers[i] = new Point(clone.workers[i].x, clone.workers[i].y);
		}
	}
	public void print() {
		if(whoseTurn == 0) {
			System.out.println("It's Player One's Turn!");
		}else {
			System.out.println("It's Player Two's Turn!");
		}
		for(int j = 0; j < 5; j++) {
			String contents = "|";
			for(int i = 0; i < 5; i++) {
				contents += towerStatus[i][j];
				if(getWorker(0).x == i && getWorker(0).y == j) {
					contents += " a";
				}else if(getWorker(1).x == i && getWorker(1).y == j) {
					contents += " a";
				}else if(getWorker(2).x == i && getWorker(2).y == j) {
					contents += " B";
				}else if(getWorker(3).x == i && getWorker(3).y == j) {
					contents += " B";
				}else {
					contents += "  ";
				}
				contents += "|";
			}
			System.out.println(contents);
		}
	}
	public ArrayList<GameState> generateChildren(){
		return generateChildren(whoseTurn);
	}
	private ArrayList<GameState> generateChildren(int whoseTurn){
		ArrayList<GameState> children = new ArrayList<GameState>();
		addChildrenFromWorkerMove(whoseTurn * 2, children);
		addChildrenFromWorkerMove(whoseTurn * 2 + 1, children);
		return children;
	}
	public boolean hasWon(int p) {
		Point one = workers[2 * p];
		Point two = workers[2 * p + 1];
		if(towerStatus[one.x][one.y] == 3) {
			return true;
		}
		if(towerStatus[two.x][two.y] == 3) {
			return true;
		}
		return false;
	}
	public boolean hasLost(int p) {
		return generateChildren(p).size() == 0;
	}
	public int checkVictoryState() {
		for(int i = 0; i < workers.length; i++) {
			Point w = workers[i];
			if(towerStatus[w.x][w.y] == 3) {
				return i / 2;
			}
		}
		for(int i = 0; i < theGame.numPlayers; i++) {
			if(hasLost(i) && !hasLost((i + 1) % theGame.numPlayers)) {
				return (i + 1) % theGame.numPlayers;
			}
		}
		return -1;
	}
	public int getTowerStatus(int x, int y) {
		return towerStatus[x][y];
	}
	public Point getWorker(int x) {
		return workers[x];
	}
	public int numWorkers() {
		return workers.length;
	}
	public boolean workerPresent(int x, int y) {
		return whichWorker(x, y) != -1;
	}
	public int whichWorker(int x, int y) {
		for(int i = 0; i < workers.length; i++) {
			if(workers[i].x == x && workers[i].y == y) {
				return i;
			}
		}
		return -1;
	}
	public int whoseTurn() {
		return whoseTurn;
	}
	public boolean isPlayerOneTurn() {
		return whoseTurn == 0;
	}
	public boolean isPlayerTwoTurn() {
		return whoseTurn == 1;
	}
	private void addChildrenFromWorkerMove(int worker, ArrayList<GameState> list) {
		Point theWorker = workers[worker];
		for(int i = theWorker.x - 1; i <= theWorker.x + 1; i++) {
			if(i < 0 || i >= 5) {
				continue;
			}
			for(int j = theWorker.y - 1; j <= theWorker.y + 1; j++) {
				if(j < 0 || j >= 5) {
					continue;
				}
				if(workerPresent(i, j)) {
					continue;
				}
				if(towerStatus[i][j] > 3) {
					continue;
				}
				if(towerStatus[i][j] > towerStatus[theWorker.x][theWorker.y] + 1) {
					continue;
				}
				GameState child = new GameState(this);
				child.workers[worker].x = i;
				child.workers[worker].y = j;
				child.addChildrenFromWorkerBuild(worker, list);
			}
		}
	}
	private void addChildrenFromWorkerBuild(int worker, ArrayList<GameState> list) {
		Point theWorker = workers[worker];
		for(int i = theWorker.x - 1; i <= theWorker.x + 1; i++) {
			if(i < 0 || i >= 5) {
				continue;
			}
			for(int j = theWorker.y - 1; j <= theWorker.y + 1; j++) {
				if(j < 0 || j >= 5) {
					continue;
				}
				if(workerPresent(i, j)) {
					continue;
				}
				if(towerStatus[i][j] > 3) {
					continue;
				}
				GameState child = new GameState(this);
				child.build(i, j);
				child.incrementTurn();
				list.add(child);
			}
		}
	}
	public void build(int x, int y) {
		towerStatus[x][y]++;
	}
	public void incrementTurn() {
		whoseTurn++;
		whoseTurn %= theGame.numPlayers;
	}
	public boolean identical(GameState w) {
		if(w == this) {
			return true;
		}
		if(w.whoseTurn != this.whoseTurn) {
			return false;
		}
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				if(w.towerStatus[i][j] != this.towerStatus[i][j]) {
					return false;
				}
			}
		}
		if(w.workers.length != this.workers.length) {
			return false;
		}
		for(int i = 0; i < workers.length; i++) {
			if(w.workers[i].x != this.workers[i].x) {
				return false;
			}
			if(w.workers[i].y != this.workers[i].y) {
				return false;
			}
		}
		
		return true;
	}
}