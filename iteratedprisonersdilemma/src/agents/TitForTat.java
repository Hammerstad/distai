package agents;
public class TitForTat extends AbstractAgent {

  private String message = coop;

	@Override
	public String sendMessage() {
		return message;
	}

	@Override
	public void receiveMessage(String msg) {
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