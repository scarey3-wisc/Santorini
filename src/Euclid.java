import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;

public class Euclid extends Agent{
	private long timeoutLength;
	public Euclid(long time) {
		timeoutLength = time;
		EuclidAnalytics.initialize();
	}
	
	public void startTurn() {
		super.startTurn();
		Thread t = new Thread(new StateSearch());
		t.start();
	}
	
	private GameState bestMove(GameState current, long timeout) {
		ValuePair best = null;
		long time = 0;
		int depth = 0;
		while(time < timeout){
			depth++;
			long start = System.currentTimeMillis();
			best = minimax(current, 0, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
			time = System.currentTimeMillis() - start;
			System.out.print(depth + " ");
			if(best.itsValue == Integer.MAX_VALUE || best.itsValue == Integer.MIN_VALUE) {
				myGame.displayMessage("Euclid Smells Victory");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		System.out.println();
		return best.theState;
	}

	private ValuePair minimax(GameState current, int depth, int maxDepth, int alpha, int beta) {
		if (current.checkVictoryState() != -1 || depth == maxDepth) {
			return new ValuePair(current, calculateValue(current));
		}
		if (current.isPlayerOneTurn()) {
			ValuePair best = null;
			int index = 0;
			int bestIndex = 0;
			ArrayList<GameState> children = current.generateChildren();
			if(depth + 1 < maxDepth) {
				try {
					children.sort(new Sorter(false));
				} catch (IllegalArgumentException e){
					
				}
			}
			for (GameState child : children) {
				ValuePair proposed = minimax(child, depth + 1, maxDepth, alpha, beta);
				if (best == null || proposed.itsValue > best.itsValue) {
					best = new ValuePair(child, proposed.itsValue);
					bestIndex = index;
				}
				if (best.itsValue > alpha) {
					alpha = best.itsValue;
				}
				if (beta <= alpha) {
					break;
				}
				index++;
			}
			EuclidAnalytics.log(maxDepth - 1, depth, bestIndex);
			return best;
		} else if (current.isPlayerTwoTurn()) {
			ValuePair best = null;
			int index = 0;
			int bestIndex = 0;
			ArrayList<GameState> children = current.generateChildren();
			if(depth + 1 < maxDepth) {
				try {
					children.sort(new Sorter(true));
				} catch (IllegalArgumentException e){
				}
			}
			for (GameState child : children) {
				ValuePair proposed = minimax(child, depth + 1, maxDepth, alpha, beta);
				if (best == null || proposed.itsValue < best.itsValue) {
					best = new ValuePair(child, proposed.itsValue);
					bestIndex = index;
				}
				if (best.itsValue < beta) {
					beta = best.itsValue;
				}
				if (beta <= alpha) {
					break;
				}
				index++;
			}
			EuclidAnalytics.log(maxDepth - 1, depth, bestIndex);
			return best;
		} else {
			System.err.println("WHOSE TURN IS IT???");
			return null;
		}
	}

	public int calculateValue(GameState current) {
		if(current.hasWon(0) || current.hasLost(1)) {
			return Integer.MAX_VALUE;
		}
		if(current.hasLost(0) || current.hasWon(1)) {
			return Integer.MIN_VALUE;
		}
		return scoreForPlayer(current, 0) - scoreForPlayer(current, 1);
	}

	private int scoreForPlayer(GameState current, int p) {
		if (current.hasWon(p)) {
			return Integer.MAX_VALUE;
		}
		if (current.hasLost(p)) {
			return Integer.MIN_VALUE;
		}
		return scoreForWorker(current, p * 2) + scoreForWorker(current, p * 2 + 1);
	}

	private int scoreForWorker(GameState current, int w) {
		Point work = current.getWorker(w);
		int curHeight = current.getTowerStatus(work.x, work.y);
		int totalScore = 3 * curHeight * curHeight;
		for (int i = work.x - 1; i <= work.x + 1; i++) {
			if (i < 0 || i >= 5) {
				continue;
			}
			for (int j = work.y - 1; j <= work.y + 1; j++) {
				if (j < 0 || j >= 5) {
					continue;
				}
				if (current.getTowerStatus(i, j) <= curHeight + 1 && current.getTowerStatus(i, j) < 4
						&& !current.workerPresent(i, j)) {
					if(current.getTowerStatus(i, j) == 1) {
						totalScore += 1;
					}else if(current.getTowerStatus(i, j) == 2) {
						totalScore += 4;
					}else if(current.getTowerStatus(i, j) == 3){
						totalScore += 100;
					}
				}
			}
		}
		return totalScore;
	}
	private class Sorter implements Comparator<GameState>{
		private boolean s;
		public Sorter(boolean smallToLarge) {
			s = smallToLarge;
		}

		@Override
		public int compare(GameState arg0, GameState arg1) {
			int valueOne = calculateValue(arg0);
			int valueTwo = calculateValue(arg1);
			if(!s) {
				return valueTwo - valueOne;
			}
			return valueOne - valueTwo;
		}
		
	}
	private class StateSearch implements Runnable{
		
		@Override
		public void run() {
			GameState choice = bestMove(myGame.getCurrentState(), timeoutLength);
			endTurn(choice);
		}
	}
	private class ValuePair {
		public GameState theState;
		public int itsValue;

		public ValuePair(GameState state, int value) {
			theState = state;
			itsValue = value;
		}
	}
	public static class EuclidAnalytics{
		public static ArrayList<ArrayList<ArrayList<Integer>>> moves;
		public static void initialize() {
			moves = new ArrayList<ArrayList<ArrayList<Integer>>>();
		}
		public static void print() {
			for(int i = 0; i < moves.size(); i++) {
				for(int k = 0; k < moves.get(i).size(); k++) {
					System.out.println("At depth " + (k + 1) + "/" + (i + 1) + " the system's move statistics were: ");
					int sum = 0;
					for(int j = 0; j < moves.get(i).get(k).size(); j++) {
						sum += moves.get(i).get(k).get(j);
					}
					int curSum = 0;
					for(int j = 0; j < moves.get(i).get(k).size(); j++) {
						curSum += moves.get(i).get(k).get(j);
						System.out.println((j + 1) + ": " + curSum + "/" + sum);
					}
				}
			}
		}
		public static void log(int maxDepth, int depth, int option) {
			while(maxDepth >= moves.size()) {
				moves.add(new ArrayList<ArrayList<Integer>>());
			}
			while(depth >= moves.get(maxDepth).size()) {
				moves.get(maxDepth).add(new ArrayList<Integer>());
			}
			while(option >= moves.get(maxDepth).get(depth).size()) {
				moves.get(maxDepth).get(depth).add(0);
			}
			moves.get(maxDepth).get(depth).set(option, moves.get(maxDepth).get(depth).get(option) + 1);
		}
	}
}