package agents;
public class DefectAgent extends AbstractAgent{
  
	@Override
	public String sendMessage() {
		//Only defect.
		return defect;
	}

	@Override
	public void receiveMessage(String msg) {
	}
}
