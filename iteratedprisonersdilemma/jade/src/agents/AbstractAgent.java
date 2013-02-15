package agents;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.FIPAManagementVocabulary;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public abstract class AbstractAgent extends Agent{
  
	public static final String defect = "DEFECT";
	public static final String coop = "COOP";
	public static final String dilemma = "DILEMMA";
	
	public AbstractAgent(){
		super();
	}
	
	public abstract String sendMessage();
	
	public abstract void receiveMessage(String msg);
	
	public void kjor(){
		while(true){//TODO: this might not work. DOH'
			MessageTemplate m1 = MessageTemplate.MatchPerformative(jade.lang.acl.ACLMessage.INFORM);
			MessageTemplate m2 = MessageTemplate.MatchPerformative(jade.lang.acl.ACLMessage.CFP);
			MessageTemplate m3 = MessageTemplate.or(m1, m2);
			ACLMessage message = blockingReceive(m3);
			if (message != null) {
				if(message.getContent().equals("DILEMMA")){
					ACLMessage acl = new ACLMessage(ACLMessage.PROPOSE);
					acl.setSender(getAID());
					acl.setContent(sendMessage());
					acl.addReceiver(message.getSender());
					send(acl);
				}
				else{
					receiveMessage(message.getContent());
				}
			}
		}
	}
	 protected void setup() {
		    super.setup();
		    try {
		      getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
		      getContentManager().registerOntology(FIPAManagementOntology.getInstance(), FIPAManagementVocabulary.NAME);
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		    }
//		    addBehaviour(new rec);
		  }
	
}
