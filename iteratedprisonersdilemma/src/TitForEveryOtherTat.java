

public class TitForEveryOtherTat extends AbstractAgent{

  String message = coop;
	boolean defected = false;
	@Override
	public String sendMessage() {
		System.out.println(this + " sending message: " + message);
		return message;
	}

	/*		 d  c
	 *defect 2	5
	 *coop   0	3
	 */
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
			if(defected){
				message = defect;
				defected= false;
			}
			else{
				defected = true;
				message  = coop;
			}
		}
	}
}
