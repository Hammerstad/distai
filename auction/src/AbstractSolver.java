import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.StringTokenizer;

public abstract class AbstractSolver extends Agent {

	/**
	 * A constant which influences the wait-time, increase to increase the wait time
	 */
	protected double waitTime = 1.0;

	/**
	 * All problems we currently know about
	 */
	protected ArrayList<Operation> operations = new ArrayList<>();

	/**
	 * This agent's operator, e.g. '+' for Addition-agent
	 */
	protected char myChar;

	/**
	 * Create a new AbstractSolver
	 * 
	 * @param myOperator
	 *            - this agent's operator, e.g. '+' for Addition-agent
	 */
	public AbstractSolver(char myOperator) {
		this.myChar = myOperator;
	}

	/**
	 * Get the auction-time this agent is willing to bid for a task.
	 * 
	 * @param operator
	 *            - the operator of given task
	 * @return a int > 0: how many ms we are willing to bid. Low means more confident.
	 */
	private int auction(char operator) {
		return (int) (operator == myChar ? (operations.size() + 1) * waitTime : (1000 + operations.size() + 1) * waitTime);
	}

	/**
	 * A method all AbstractSolvers have to implement. Returns a result of a task.
	 * 
	 * @param num1
	 *            - first number in the task (example: 8)
	 * @param num2
	 *            - second number in the task (example: 2)
	 * @param operator
	 *            - the operator to use on these two numbers (example: /)
	 * @return the answer to the mathematical problem (in the example: 4.0)
	 */
	public abstract double result(double num1, double num2, char operator);

	/**
	 * Old function used to "solve" the next problem. No longer in use.
	 */
	@Deprecated
	public void next() {
		if (operations.size() > 0) {
			for (int i = 0; i < operations.size(); i++) {
				if (operations.get(i).accepted) {
					doWait(1000);
					send(operations.get(i).aid, result(operations.get(i).num1, operations.get(i).num2, operations.get(i).oper), -1,
							operations.get(i).ID);
					break;
				}
			}

		}
	}

	/**
	 * A private function for sending messages. Has a receiver, an ID and EITHER result/bid. Null-value for result is Double.NaN, for bid: -1 (0 is
	 * best bid).
	 * 
	 * @param receiver
	 *            - to whom are we sending?
	 * @param result
	 *            - the result of the task (If this is a bid, set result to Double.NaN).
	 * @param bid
	 *            - how much we bid for the task (If this is a result, and not a bid, set to -1).
	 * @param id
	 *            - the id of the task
	 */
	private void send(AID receiver, double result, int bid, int id) {
		ACLMessage acl = new ACLMessage(ACLMessage.PROPOSE);
		acl.setSender(getAID());
		acl.setContent(result + "," + bid + "," + id); // TODO: skal den sende meir enn berre resultat?
		acl.addReceiver(receiver);
		send(acl);
	}

	/**
	 * A cyclic behaviour which checks if we have received a new message recently. If so, process it with our receive(content, sender) function.
	 * 
	 * @return A cyclic behaviour checking for messages.
	 */
	private CyclicBehaviour receiveMessage() {
		CyclicBehaviour c = new CyclicBehaviour() {
			@Override
			public void action() {
				ACLMessage message = receive();
				if (message != null) {
					receive(message.getContent(), message.getSender());
				}
				block();
			}
		};
		return c;
	}

	/**
	 * Sets up our AbstractSolver: Adds a cyclic behaviour for listening for messages.
	 */
	public void setup() {
		super.setup();
		addBehaviour(receiveMessage());
	}

	/**
	 * Processes a received message. Accepts on format (without the brackets):</br>[true id] - this means we got to solve problem(id)</br>[false id] -
	 * this means we did not get to solve problem(id).</br>[double double oper id] - this is a new problem, let's make a bid for it!
	 * 
	 * @param s
	 *            - the content of the message.
	 * @param aid
	 *            - the AID of the sender.
	 */
	private void receive(String s, AID aid) {
		// Everything is space-separated, let's use a StringTokenizer to split the arguments.
		StringTokenizer st = new StringTokenizer(s);
		String a1 = st.nextToken();
		if (a1.equals("true")) {
			// This means we got to solve the problem.
			int ID = Integer.parseInt(st.nextToken());
			boolean found = false;
			for (int i = 0; i < operations.size(); i++) {
				if (operations.get(i).ID == ID) {
					operations.get(i).accepted = true;
					found = true;
					doWait(operations.get(i).estimatedWait);
					System.out.println("We (" + operations.get(i).oper + ") got to solve problem " + operations.get(i).ID);
					send(operations.get(i).aid, result(operations.get(i).num1, operations.get(i).num2, operations.get(i).oper), -1,
							operations.get(i).ID);
				}
			}
			if (!found) {
				// Debug message.
				System.err.println("DEBUG :: ERROR! The operation is not in this BOT! THIS BOT's OPERATION IS: " + myChar);
			}
		} else if (a1.equals("false")) {
			// We are not worthy of solving the task. A shame...
			int ID = Integer.parseInt(st.nextToken());
			boolean found = false;
			for (int i = 0; i < operations.size(); i++) {
				if (operations.get(i).ID == ID) {
					operations.remove(i);
					found = true;
				}
			}
			if (!found) {
				// Debug message.
				System.err.println("DEBUG :: ERROR! The operation is not in this BOT! THIS BOT's OPERATION IS: " + myChar);
			}
		} else {
			int CRASH = 0;
			try {
				// Get the first number
				double num1 = Double.parseDouble(a1);
				CRASH++;
				// And the second number
				double num2 = Double.parseDouble(st.nextToken());
				CRASH++;
				// And the operator
				char operator = st.nextToken().charAt(0);
				CRASH++;
				// And id
				int taskID = Integer.parseInt(st.nextToken());
				CRASH++;
				// Let's estimate how much time we can spend on this. (Low number if our operator, high else).
				int estimatedWait = auction(operator);
				CRASH++;
				// Add the problem to our list of problems
				operations.add(new Operation(num1, num2, operator, taskID, aid, estimatedWait));
				// And send our reply to the task administrator
				send(aid, Double.NaN, estimatedWait, taskID);
			} catch (Exception e) {
				// Debug message:
				System.err.println("ERROR THE STRING DOESN'T FIT! IT CAME TO CRASH NR: " + CRASH);
				System.err.println(e);
			}
		}

	}

	/**
	 * Old deprecated function for registering the agent's service with the DFService.
	 * @param agent - the agent in question
	 * @param service - a string representing the service we are offering
	 */
	@Deprecated
	protected void registerService(AbstractSolver agent, String service) {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(service);
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(agent, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

}
