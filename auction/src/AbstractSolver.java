
import jade.core.AID;
import jade.core.Agent;

import java.util.ArrayList;
import java.util.StringTokenizer;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;



public abstract class AbstractSolver extends Agent{
	
	protected double waitTime = 1.0; 
	protected ArrayList<Operation> operations = new ArrayList<>();
	protected char myChar;
	public AbstractSolver(char mych){
		this.myChar = mych;
	}
	public double auction(char operator) {
		return operator==myChar? (operations.size()+1)*waitTime : (1000+operations.size()+1)*waitTime;
	}
	public abstract double result(double num1, double num2, char operator);

	public void next(){
		if(operations.size()>0){
			for (int i = 0; i < operations.size(); i++) {
				if(operations.get(i).accepted){
					doWait(1000);
					send(operations.get(i).aid, result(operations.get(i).num1, operations.get(i).num2, operations.get(i).oper));
				}
			}
			
		}
	}
	
	public void send(AID receiver, double result){
		
	}
	
	public void receive(String s, AID aid){
		StringTokenizer st = new StringTokenizer(s);
		String a1 = st.nextToken();
		if(a1.equals("true")){
			int ID = Integer.parseInt(st.nextToken());
			boolean found= false;
			for (int i = 0; i < operations.size(); i++) {
				if(operations.get(i).ID==ID	){
					operations.get(i).accepted = true;
					found = true;
				}
			}
			if(!found){
				System.err.println("ERROR! The operation is not in this BOT! THIS BOT's OPERATION IS: " +myChar );
			}
		}else if( a1.equals("false")){
			int ID = Integer.parseInt(st.nextToken());
			boolean found= false;
			for (int i = 0; i < operations.size(); i++) {
				if(operations.get(i).ID==ID	){
					operations.remove(i);
					found = true;
				}
			}
			if(!found){
				System.err.println("ERROR! The operation is not in this BOT! THIS BOT's OPERATION IS: " +myChar );
			}
		}else{
			int CRASH = 0;
			try{
				
				double num1 = Double.parseDouble(a1);
				CRASH++;
				double num2 = Double.parseDouble(st.nextToken());
				CRASH++;
				char operator = st.nextToken().charAt(0);
				CRASH++;
				int taskID = Integer.parseInt(st.nextToken());
				CRASH++;
				operations.add(new Operation(num1, num2, operator, taskID, aid));
			}catch(Exception e ){
				System.err.println("ERROR THE STRING DOESN'T FIT! IT CAME TO CRASH NR: " + CRASH);
			}
		}
		
	}
	protected void registerService(AbstractSolver agent, String service) {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("buyer");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(agent, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

}
