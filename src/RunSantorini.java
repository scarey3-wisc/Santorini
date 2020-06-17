import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RunSantorini{
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setLocation(0, 0);
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setUndecorated(true);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.white);
		frame.add(panel);
		panel.setLayout(new BorderLayout());
		
		JPanel textPanel = new JPanel();
		textPanel.setBackground(Color.white);
		panel.add(textPanel, BorderLayout.NORTH);
		
		JLabel text = new JLabel("Waiting to Start");
		text.setFont(new Font("Sans-Serif", Font.BOLD, 24));
		textPanel.add(text);
		
		JPanel gamePanel = new JPanel();
		gamePanel.setBackground(Color.white);
		panel.add(gamePanel, BorderLayout.CENTER);
		panel.updateUI();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Agent na = new Watson(gamePanel); 
		Agent eu = new Euclid(3000);
		
		SantoriniGame myGame = new SantoriniGame(new Agent[] {na, eu}, gamePanel, text);
		myGame.createDefaultGame();
		myGame.render(null);
		myGame.notifyOfTurnStart();
		while(myGame.gameInProgress()) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(myGame.getVictoryState() == 0) {
			text.setText("Player One Wins!!!");
		}else if(myGame.getVictoryState() == 1) {
			text.setText("Player Two Wins!!!");
		}
		//Euclid.EuclidAnalytics.print();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}