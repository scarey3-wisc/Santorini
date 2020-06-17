public abstract class Agent{
	protected SantoriniGame myGame;
	private boolean inProgress;
	public void startTurn() {
		inProgress = true;
	}
	protected void endTurn(GameState nova) {
		inProgress = false;
		myGame.notifyOfTurnEnd(nova);
	}
	public boolean thinking() {
		return inProgress;
	}
	public void illegalMove() {
		
	}
	public void endGame() {
		
	}
	public void setGame(SantoriniGame game) {
		myGame = game;
	}
}