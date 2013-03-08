import java.util.ArrayList;
import java.util.Arrays;

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
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class TaskAdministrator extends Agent {

	/**
	 * A list of the names of all the agents, e.g: Addition.
	 */
	private ArrayList<String> allAgents = new ArrayList<>();

	/**
	 * A list of all the problems we are trying to solve
	 */
	private ArrayList<Problem> problems = new ArrayList<>();

	/**
	 * Dummy constructor
	 */
	public TaskAdministrator() {
	}

	/**
	 * The setup function. Creates the agents and adds appropriate behaviours.
	 */
	public void setup() {
		// Create the four agents
		createAgents();
		// Print the info of all the agents, to be sure
		printAgentInfo();
		// Add a cyclic behaviour which let's us receive messages
		addBehaviour(receiveMessage());
		// Create all the problems (5 in total)
		createProblems();
		// Broadcast all the problems we have to all the agents
		broadcastProblems();
		// Add a cyclic behaviour to check if all agents have bid on a problem
		addBehaviour(checkForProblemsWithAllBids());
		// Add a cyclic behaviour to check if we are done
		addBehaviour(checkIfWeAreDone());
	}

	/**
	 * Broadcast all the problems to all the agents.
	 */
	private void broadcastProblems() {
		for (int i = 0; i < problems.size(); i++) {
			System.out.println("Broadcasting initial problem number " + i + " to all the agents");
			addBehaviour(sendAuctionOffer(problems.get(i).getNextSubProblem(), i));
		}
	}

	/**
	 * Creates a set of problems from the static list Problem.PROBLEM_SET and adds them to the problems list.
	 */
	private void createProblems() {
		for (String[] problem : Problem.PROBLEM_SET) {
			problems.add(new Problem(problem));
		}
	}

	/**
	 * Takes down the agent
	 */
	public void takeDown() {
		System.out.println("Taking down agent!");
		super.takeDown();
		// Kill it with fire!
		doWait(1000);
		System.exit(0);
	}

	/**
	 * A cyclic behaviour for receiving messages. Can either be: </br> - An offer, in which case the offer is registered with the problem in question,
	 * or</br> - A solution to a problem, in which case we see if the problem has been solved. If it hasn't, broadcast a message with the now new
	 * subproblem.
	 * 
	 * @return A cyclic behaviour which receives and processes messages.
	 */
	private CyclicBehaviour receiveMessage() {
		CyclicBehaviour c = new CyclicBehaviour() {

			@Override
			public void action() {
				ACLMessage message = receive();
				if (message != null) {
					System.out.println("Task Administrator received message from " + message.getSender().getName() + "!\nContent: "
							+ message.getContent());
					String[] msg = message.getContent().split(",");
					int id = Integer.parseInt(msg[2]);
					if (!msg[0].equals("NaN")) {
						System.out.println("The message contains a solution for problem number " + id);
						problems.get(id).solveNextSubProblem(msg[0]);
						if (problems.get(id).hasMoreSubProblems()) {
							addBehaviour(sendAuctionOffer(problems.get(id).getNextSubProblem(), id));
						} else {
							System.out.println("We have solved the problem!\n ID: " + id + "\nAnswer: " + problems.get(id).getSolution());
						}
					} else if (!msg[1].equals("-1")) {
						System.out.println("The message contained an offer for problem number " + id);
						problems.get(id).addOffer(message.getSender().getName().split("@")[0], Integer.parseInt(msg[1]));
					}
				}
			}
		};

		return c;
	}

	/**
	 * Creates a cyclic behaviour which checks if we have solved all the problems.
	 * 
	 * @return A cyclic behaviour which checks if we are done.
	 */
	private CyclicBehaviour checkIfWeAreDone() {
		return new CyclicBehaviour() {

			@Override
			public void action() {
				boolean done = true;
				for (Problem problem : problems) {
					if (!problem.isSolved()) {
						done = false;
						break;
					}
				}
				if (done) {
					System.out.println("We are done, tearing down system.");
					takeDown();
				}
			}
		};
	}

	/**
	 * Creates a cyclic behaviour which checks all of the problems if any of them have received an offer from all of the agents. If so is the case,
	 * all agents are notified of who gets to do the honorable task of solving the problem.
	 * 
	 * @return A cyclic behaviour which handles problems which all agents have bid.
	 */
	private CyclicBehaviour checkForProblemsWithAllBids() {
		CyclicBehaviour c = new CyclicBehaviour() {

			@Override
			public void action() {
				for (int i = 0; i < problems.size(); i++) {
					if (problems.get(i).hasAmountOfOffers(allAgents.size())) {
						String bestAgent = problems.get(i).getBestOfferingAgent();
						ACLMessage falsemsg = new ACLMessage(ACLMessage.INFORM);
						for (String agentName : allAgents) {
							if (agentName.equals(bestAgent))
								continue;
							falsemsg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
						}
						falsemsg.setSender(getAID());
						falsemsg.setContent("false " + i);
						send(falsemsg);

						ACLMessage truemsg = new ACLMessage(ACLMessage.INFORM);
						truemsg.addReceiver(new AID(bestAgent, AID.ISLOCALNAME));
						truemsg.setSender(getAID());
						truemsg.setContent("true " + i);
						send(truemsg);
						problems.get(i).removeOffers();
						System.out.println("All agents have bid on problem number " + i + ". Accepting offer from " + bestAgent);
					}
				}
			}
		};
		return c;
	}

	/**
	 * Creates a OneShotBehavior which sends an auction-offer to all the agents.
	 * 
	 * @param task
	 *            - the postfix task from the problem.
	 * @param id
	 *            - the ID of the task.
	 * @return a OneShotBehaviour which sends a problem to all the agents.
	 */
	private OneShotBehaviour sendAuctionOffer(final String[] task, final int id) {
		OneShotBehaviour b = new OneShotBehaviour() {

			@Override
			public void action() {
				System.out.println("Preparing to send auction for task: " + Arrays.toString(task));
				ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
				for (String agentName : allAgents) {
					msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
				}
				msg.setSender(getAID());
				String sender = "";
				for (String tmp : task) {
					sender += tmp;
					sender += " ";
				}
				sender += Integer.toString(id);
				msg.setContent(sender);
				System.out.println("Sending the following message to all agents: " + sender);
				send(msg);
			}
		};
		return b;
	}

	/**
	 * Tries to create all the agents. Adds the agents to the allAgents list, and starts them.
	 * 
	 * @return true if succeeded, false if shit failed.
	 */
	private boolean createAgents() {
		AgentContainer a = getContainerController();
		try {
			AgentController b = a.createNewAgent("Addition", "AdditionSolver", null);
			AgentController c = a.createNewAgent("Subtraction", "SubtractionSolver", null);
			AgentController d = a.createNewAgent("Multiplication", "MultiplicationSolver", null);
			AgentController e = a.createNewAgent("Division", "DivisionSolver", null);
			allAgents.add("Addition");
			allAgents.add("Subtraction");
			allAgents.add("Multiplication");
			allAgents.add("Division");
			b.start();
			c.start();
			d.start();
			e.start();
			System.out.println("Created and started all agents. Total amount: " + allAgents.size());
			return true;
		} catch (Exception e) {
			System.out.println("Failed to create agensts!");
			System.err.println(e);
			takeDown();
			return false;
		}
	}

	/**
	 * Prints info about all the agents currently active, with *** in front of this agent.
	 */
	private void printAgentInfo() {
		AMSAgentDescription[] agents = new AMSAgentDescription[10];
		try {
			SearchConstraints c = new SearchConstraints();
			c.setMaxResults(new Long(-1));
			agents = AMSService.search(this, new AMSAgentDescription(), c);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		AID myID = getAID();
		System.out.println("Information about all agents currently running: ");
		for (int i = 0; i < agents.length; i++) {
			AID agentID = agents[i].getName();
			System.out.println((agentID.equals(myID) ? "*** " : "    ") + i + ": " + agentID.getName());
		}
	}

	/**
	 * Old function which searches for agents who has added their services to the DFService.
	 * 
	 * @param specification
	 *            - can search by name, if empty string searches for everyone.
	 * @return A list of AIDs of matching agents.
	 */
	@Deprecated
	public AID[] searchForAgent(String specification) {
		DFAgentDescription dfd = new DFAgentDescription();
		if (specification.length() > 0) {
			ServiceDescription sd = new ServiceDescription();
			sd.setType(specification);
			dfd.addServices(sd);
		}

		DFAgentDescription[] result = null;
		AID[] returnValues = null;
		try {
			result = DFService.search(this, dfd);
			returnValues = new AID[result.length];
			for (int i = 0; i < result.length; i++) {
				returnValues[i] = result[i].getName();
			}
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		return returnValues;
	}
}
