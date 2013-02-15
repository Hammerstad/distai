
public class TitForEveryOtherTat extends abstractAgent{

  String message = coop;
	boolean defected = false;
	@Override
	public String sendMessage() {
		return message;
	}

	/*		 d  c
	 *defect 2	5
	 *coop   0	3
	 */
	@Override
	public void receiveMessage(String msg) {
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
