import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Watson extends Agent implements MouseListener{
	private int workerIndex;
	private boolean moved;
	private GameState curr;
	
	public Watson(JPanel panel) {
		panel.addMouseListener(this);
		workerIndex = -1;
		moved = false;
	}
	
	public void startTurn() {
		super.startTurn();
	}
	
	protected void endTurn(GameState nova) {
		super.endTurn(nova);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(!thinking()) {
			return;
		}
		Point hit = myGame.getCoordinates(arg0.getX(), arg0.getY());
		if(hit == null) {
			return;
		}
		if(hit.x < 0 || hit.y < 0 || hit.x >= 5 || hit.y >= 5) {
			return;
		}
		if(workerIndex == -1) {
			curr = myGame.getCurrentState();

			int worker = curr.whichWorker(hit.x, hit.y);
			if(worker != -1 && worker / 2 == curr.whoseTurn()) {
				workerIndex = worker;
			}else {
				return;
			}
			ArrayList<Filter> myFilters = new ArrayList<Filter>();
			int cH = curr.getTowerStatus(hit.x, hit.y);
			for(int i = hit.x - 1; i <= hit.x + 1; i++) {
				if(i < 0 || i >= 5) {
					continue;
				}
				for(int j = hit.y - 1; j <= hit.y + 1; j++) {
					if(j < 0 || j >= 5) {
						continue;
					}
					int pH = curr.getTowerStatus(i, j);
					if(pH <= cH + 1 && !curr.workerPresent(i, j)) {
						myFilters.add(new Filter(new Point(i, j), new Color(0, 128, 255, 128)));
					}
				}
			}
			myGame.renderTheoretical(myFilters, curr);
		}else if(!moved) {
			Point workerSelected = curr.getWorker(workerIndex);
			int cH = curr.getTowerStatus(workerSelected.x, workerSelected.y);
			int pH = curr.getTowerStatus(hit.x, hit.y);
			if(Math.abs(workerSelected.x - hit.x) > 1 || Math.abs(workerSelected.y - hit.y) > 1) {
				workerIndex = -1;
				myGame.renderTheoretical(null, curr);
				return;
			}
			if(pH <= cH + 1 && !curr.workerPresent(hit.x, hit.y)) {
				curr = new GameState(curr);
				curr.getWorker(workerIndex).x = hit.x;
				curr.getWorker(workerIndex).y = hit.y;
				moved = true;
			}else {
				workerIndex = -1;
			}
			ArrayList<Filter> myFilters = new ArrayList<Filter>();
			for(int i = hit.x - 1; i <= hit.x + 1; i++) {
				if(i < 0 || i >= 5) {
					continue;
				}
				for(int j = hit.y - 1; j <= hit.y + 1; j++) {
					if(j < 0 || j >= 5) {
						continue;
					}
					if(!curr.workerPresent(i, j) && curr.getTowerStatus(i, j) < 4) {
						myFilters.add(new Filter(new Point(i, j), new Color(255, 128, 0, 128)));
					}
				}
			}
			myGame.renderTheoretical(myFilters, curr);
		}else {
			Point worker = curr.getWorker(workerIndex);
			if(Math.abs(worker.x - hit.x) > 1 || Math.abs(worker.y - hit.y) > 1) {
				workerIndex = -1;
				moved = false;
				curr = myGame.getCurrentState();
				myGame.renderTheoretical(null, curr);
				return;
			}
			if(curr.workerPresent(hit.x, hit.y) || curr.getTowerStatus(hit.x, hit.y) >= 4) {
				workerIndex = -1;
				moved = false;
				curr = myGame.getCurrentState();
				myGame.renderTheoretical(null, curr);
				return;
			}
			curr = new GameState(curr);
			curr.build(hit.x, hit.y);
			curr.incrementTurn();
			endTurn(curr);
			moved = false;
			workerIndex = -1;
			curr = myGame.getCurrentState();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
	
}