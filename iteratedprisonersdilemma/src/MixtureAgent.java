public class MixtureAgent extends AbstractAgent {

	private static String message = coop;

	@Override
	public String sendMessage() {
		message = (message.equals(coop) ? defect : coop);
		System.out.println(this + " sending message: " + message);
		// Only cooperate.
		return message;
	}

	@Override
	public void receiveMessage(String msg) {
		System.out.println(this + ": " + msg + " ");
	}
}