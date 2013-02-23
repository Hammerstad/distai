
import jade.core.Agent;

import java.util.ArrayList;

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
		return operator==myChar? (operations.size()+1)*waitTime : 1000*waitTime;
	}
	public abstract double result(double num1, double num2, char operator);

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
