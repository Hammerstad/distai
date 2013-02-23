
public class CoopAgent extends AbstractAgent{

  @Override
	public String sendMessage() {
		System.out.println(this + " sending message: " + coop);
		//Only cooperate.
		return coop;
	}

	@Override
	public void receiveMessage(String msg) {
		System.out.println(this + ": " + msg + " ");
	}
}
