import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.FIPAManagementVocabulary;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public abstract class AbstractAgent extends Agent {

	public static final String defect = "DEFECT";
	public static final String coop = "COOP";
	public static final String dilemma = "DILEMMA";

	public AbstractAgent() {
		super();
		// setup();
	}

	public abstract String sendMessage();

	public abstract void receiveMessage(String msg);

	@Override
	protected void setup() {
		super.setup();
		try {
			getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
			getContentManager().registerOntology(FIPAManagementOntology.getInstance(), FIPAManagementVocabulary.NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				System.out.println(this + " :: Trying to receive!");
				MessageTemplate m1 = MessageTemplate.MatchPerformative(jade.lang.acl.ACLMessage.INFORM);
				MessageTemplate m2 = MessageTemplate.MatchPerformative(jade.lang.acl.ACLMessage.CFP);
				MessageTemplate m3 = MessageTemplate.or(m1, m2);
				ACLMessage message = receive(m3);
				if (message != null) {
					System.out.println(this + " :: Received: " + message.getContent());
					if (message.getContent().equals("DILEMMA")) {
						ACLMessage acl = new ACLMessage(ACLMessage.PROPOSE);
						acl.setSender(getAID());
						acl.setContent(sendMessage());
						acl.addReceiver(message.getSender());
						send(acl);
					} else {
						System.out.println("Received a score: " + message.getContent() + " :: " + this);
						receiveMessage(message.getContent());
					}
				}
				System.out.println("Blocking: " + this);// + " :: rootname: " + root().getBehaviourName() + " :: parentname: "
				// + parent.getBehaviourName());
				block();
			}
		});
	}
}
