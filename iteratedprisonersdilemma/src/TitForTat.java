
public class TitForTat extends AbstractAgent {

  private static String message = coop;

	@Override
	public String sendMessage() {
		System.out.println(this + " sending message: " + message);
		return message;
	}

	@Override
	public void receiveMessage(String msg) {
		System.out.println(this + ": " + msg + " ");
		if(msg.equals("5")){
			message = coop;
		}else if(msg.equals("3")){
			message = coop;
		}else if(msg.equals("2")){
			message = defect;
		}else if(msg.equals("0")){
			message = defect;
		}
	}
}
