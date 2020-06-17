import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SantoriniGame {
	public final int numPlayers;
	private GameState myGame;
	private Agent[] players;
	private int currentAgent;
	private JPanel canvas;
	private JLabel text;

	public SantoriniGame(Agent[] agents, JPanel pan, JLabel info) {
		numPlayers = agents.length;
		players = agents;
		currentAgent = 0;
		canvas = pan;
		text = info;
	}

	public void createDefaultGame() {
		Point[] workerStart = new Point[] { new Point(3, 1), new Point(1, 3), new Point(1, 1), new Point(3, 3) };
		myGame = new GameState(workerStart, this);
		for (Agent a : players) {
			a.setGame(this);
		}
	}
	public void createDefaultRandomGame() {
		LinkedList<Point> ll = new LinkedList<Point>();
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				ll.add(new Point(i, j));
			}
		}
		Point[] workerStart = new Point[4];
		for(int i = 0; i < 4; i++) {
			workerStart[i] = ll.remove((int) (Math.random() * ll.size()));
		}
		myGame = new GameState(workerStart, this);
		for (Agent a : players) {
			a.setGame(this);
		}
	}
	
	public Point getCoordinates(int mouseX, int mouseY) {
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		int startX = 0;
		int startY = 0;
		int cellSize = height / 5;
		if (width > height) {
			startX = (width - height) / 2;
		} else {
			startY = (height - width) / 2;
			cellSize = width / 5;
		}
		if(mouseX < startX || mouseY < startY) {
			return null;
		}
		if(mouseX > startX + 5 * cellSize || mouseY > startY + 5 * cellSize) {
			return null;
		}
		return new Point((mouseX - startX) / cellSize, (mouseY - startY) / cellSize);
	}
	
	public void renderTheoretical(ArrayList<Filter> arrays, GameState gs) {
		GameState temp = myGame;
		myGame = gs;
		render(arrays);
		myGame = temp;
	}

	public void render(ArrayList<Filter> filters) {
		Graphics2D graphics = (Graphics2D) canvas.getGraphics();
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		BufferedImage panel = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = panel.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		int startX = 0;
		int startY = 0;
		int cellSize = height / 5;
		if (width > height) {
			startX = (width - height) / 2;
		} else {
			startY = (height - width) / 2;
			cellSize = width / 5;
		}
		g.setColor(Color.black);
		for (int i = 0; i <= 5; i++) {
			g.fillRect(startX + cellSize * i - 1, startY, 3, cellSize * 5);
		}
		for (int j = 0; j <= 5; j++) {
			g.fillRect(startX, startY + cellSize * j - 1, cellSize * 5, 3);
		}
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				int cX = startX + cellSize / 2 + i * cellSize;
				int cY = startY + cellSize / 2 + j * cellSize;
				int levels = myGame.getTowerStatus(i, j);
				g.setColor(Color.black);
				if (levels > 0) {
					g.fillRect((int) (cX - 0.4 * cellSize), (int) (cY - 0.4 * cellSize), (int) (0.8 * cellSize),
							(int) (0.8 * cellSize));
				}
				g.setColor(Color.white);
				if (levels > 1) {
					g.fillRect((int) (cX - 0.3 * cellSize), (int) (cY - 0.3 * cellSize), (int) (0.6 * cellSize),
							(int) (0.6 * cellSize));
				}
				g.setColor(Color.black);
				if (levels > 2) {
					g.fillOval((int) (cX - 0.2 * cellSize), (int) (cY - 0.2 * cellSize), (int) (0.4 * cellSize),
							(int) (0.4 * cellSize));
					g.setColor(Color.white);
					g.fillOval((int) (cX - 0.2 * cellSize + 5), (int) (cY - 0.2 * cellSize + 5),
							(int) (0.4 * cellSize - 10), (int) (0.4 * cellSize - 10));
				}
				g.setColor(Color.blue);
				if (levels > 3) {
					g.fillOval((int) (cX - 0.2 * cellSize), (int) (cY - 0.2 * cellSize), (int) (0.4 * cellSize),
							(int) (0.4 * cellSize));
				}
			}
		}
		if(filters != null) {
			for(Filter f: filters) {
				int cX = startX + cellSize / 2  + f.where.x * cellSize;
				int cY = startY 	 + cellSize / 2  + f.where.y * cellSize;
				g.setColor(f.what);
				g.fillRect((int) (cX - 0.5 * cellSize), (int) (cY - 0.5 * cellSize), (int) (1.0 * cellSize),
						(int) (1.0 * cellSize));
			}
		}
		
		for(int i = 0; i < myGame.numWorkers(); i++) {
			Point p = myGame.getWorker(i);
			if(i / 2 == 0) {
				g.setColor(Color.gray);
			}else if(i / 2 == 1) {
				g.setColor(Color.green);
			}else {
				g.setColor(Color.red);
			}
			int cX = startX + cellSize / 2 + p.x * cellSize;
			int cY = startY + cellSize / 2 + p.y * cellSize;
			g.fillOval((int) (cX - 0.15 * cellSize), (int) (cY - 0.15 * cellSize),
					(int) (0.3 * cellSize), (int) (0.3 * cellSize));
		}
		graphics.drawImage(panel, 0, 0, null);
		if (myGame == null) {
			return;
		}
	}

	public void notifyOfTurnStart() {
		players[currentAgent].startTurn();
		if(currentAgent == 0) {
			text.setText("Player One (Grey) is Thinking");
		}else if(currentAgent == 1) {
			text.setText("Player Two (Green) is Thinking");
		}else if(currentAgent == 2) {
			text.setText("Player Three (Red) is Thinking");
		}
		render(null);
	}

	public void displayMessage(String s) {
		text.setText(s);
	}
	public void notifyOfTurnEnd(GameState nova) {
		render(null);
		ArrayList<GameState> children = myGame.generateChildren();
		boolean identical = false;
		for (GameState gs : children) {
			if (gs.identical(nova)) {
				identical = true;
				break;
			}
		}
		if (identical) {
			myGame = nova;
			render(null);
			if (myGame.checkVictoryState() == -1) {
				currentAgent = (currentAgent + 1) % numPlayers;
				notifyOfTurnStart();
			} else {
				gameEnd();
			}

		} else {
			players[currentAgent].illegalMove();
			players[currentAgent].startTurn();
		}
		render(null);
	}
	public boolean gameInProgress() {
		if(myGame == null) {
			return false;
		}else {
			return myGame.checkVictoryState() == -1;
		}
	}
	
	public int getVictoryState() {
		return myGame.checkVictoryState();
	}
	
	public void gameEnd() {
		for (Agent a : players) {
			a.endGame();
		}
		render(null);
	}

	public GameState getCurrentState() {
		return myGame;
	}
}