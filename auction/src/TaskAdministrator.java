import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;


public class TaskAdministrator {
	
	public TaskAdministrator(){
		
	}
	
	public void setup(){
		
	}
	
	private CyclicBehaviour receiveMessage(){
		CyclicBehaviour c = new CyclicBehaviour() {
			
			@Override
			public void action() {
				// TODO Auto-generated method stub
				
			}
		};
		
		return c;
	}
	
	private OneShotBehaviour sendAuction(){
		OneShotBehaviour b = new OneShotBehaviour() {
			
			@Override
			public void action() {
				// TODO Auto-generated method stub
				
			}
		};
		return b;
	}
	
	private boolean CreateAgents(){
		return true;
	}
}
