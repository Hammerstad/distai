package agents;
public class CoopAgent extends AbstractAgent{

  @Override
	public String sendMessage() {
		//Only cooperate.
		return coop;
	}

	@Override
	public void receiveMessage(String msg) {
	}
}
