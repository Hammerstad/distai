import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;


public class TaskAdministrator extends Agent{
	
	public TaskAdministrator(){
		
	}
	
	public void setup(){
		CreateAgents();
		doWait(1000);
		searchForAgent("");
		printAgentInfo();
		takeDown();
	}
	
	public void takeDown(){
		System.out.println("Taking down agent!");
		super.takeDown();
		System.exit(0);
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
	
	private OneShotBehaviour sendAuction(final String agentName, final String[] task){
		OneShotBehaviour b = new OneShotBehaviour() {
			
			@Override
			public void action() {
				// TODO Auto-generated method stub
				
			}
		};
		return b;
	}
	
	private boolean CreateAgents(){
		AgentContainer a = getContainerController();
		try {
			AgentController b = a.createNewAgent("Addition", "AdditionSolver", null);
			AgentController c = a.createNewAgent("Subtraction", "SubtractionSolver", null);
			AgentController d = a.createNewAgent("Multiplication", "MultiplicationSolver", null);
			AgentController e = a.createNewAgent("Division", "DivisionSolver", null);
			b.start();
			c.start();
			d.start();
			e.start();
			System.out.println("Created and started agents!");
			return true;
		} catch (Exception e) {
			System.out.println("Failed to create agensts!");
			System.err.println(e);
			return false;
		}
	}
	
	/**
	 * Prints info about all the agents currently active, with *** in front of this agent.
	 */
	private void printAgentInfo() {
		AMSAgentDescription[] agents = null;
		try {
			SearchConstraints c = new SearchConstraints();
			c.setMaxResults(new Long(-1));
			agents = AMSService.search(this, new AMSAgentDescription(), c);
		} catch (Exception e) {
		}
		AID myID = getAID();
		for (int i = 0; i < agents.length; i++) {
			AID agentID = agents[i].getName();
			System.out.println((agentID.equals(myID) ? "*** " : "    ") + i + ": " + agentID.getName());
		}
	}
	
	private void searchForAgent(String specification){
		DFAgentDescription dfd = new DFAgentDescription();
		if(specification.length()>0){
	        ServiceDescription sd  = new ServiceDescription();
	        sd.setType( specification );
	        dfd.addServices(sd);			
		}
        
        DFAgentDescription[] result = null;
		try {
			result = DFService.search(this, dfd);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println(result.length + " results" );
        for(DFAgentDescription agent : result){
            System.out.println(" " + agent.getName() );
        }
	}
}
