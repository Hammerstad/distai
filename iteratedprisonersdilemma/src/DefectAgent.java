
public class DefectAgent extends AbstractAgent{
  
	@Override
	public String sendMessage() {
		System.out.println(this + " sending message: " + defect);
		//Only defect.
		return defect;
	}

	@Override
	public void receiveMessage(String msg) {
		System.out.println(this + ": " + msg + " ");
	}
}
