import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WrapperBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TournamentBot5 extends Agent {

	public static ArrayList<String> allAgents = new ArrayList<>();
	public static TournamentBot5 instance = null;
	public static Map<String, Integer> score10 = new HashMap<>();
	public static Map<String, Integer> score20 = new HashMap<>();
	public static Map<String, Integer> score30 = new HashMap<>();

	public TournamentBot5() {
		instance = this;
	}

	public void setup() {
		super.setup();
		createAgents();
		printAgentInfo();

		SequentialBehaviour b = new SequentialBehaviour();
		b.setBehaviourName("toptop!");
		for (int NROFRNDS = 10; NROFRNDS < 40; NROFRNDS += 10) {
			b.addSubBehaviour(runTournament(NROFRNDS));
		}
		addBehaviour(b);
	}

	private boolean createAgents() {
		AgentContainer d = getContainerController();
		try {
			AgentController a = d.createNewAgent("Coop", "CoopAgent", null);
			AgentController b = d.createNewAgent("Defect", "DefectAgent", null);
			AgentController c = d.createNewAgent("TitForTat", "TitForTat", null);
			AgentController e = d.createNewAgent("TitForEveryOtherTat", "TitForEveryOtherTat", null);
			AgentController f = d.createNewAgent("OwnStrategyErikAndEirik", "OwnStrategyErikAndEirik", null);
			a.start();
			b.start();
			c.start();
			e.start();
			f.start();
			allAgents.add("Coop");
			allAgents.add("Defect");
			allAgents.add("TitForTat");
			allAgents.add("TitForEveryOtherTat");
			allAgents.add("OwnStrategyErikAndEirik");
			System.out.println("Created and started agents!");
			return true;
		} catch (Exception e) {
			System.out.println("Failed to create agensts!");
			return false;
		}
	}

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

	private SequentialBehaviour runTournament(final int rounds) {
		SequentialBehaviour b = new SequentialBehaviour();
		b.setBehaviourName("runTournament");
		for (int i = 0; i < rounds; i++) {
			b.addSubBehaviour(runTournamentBetweenAllAgents(rounds));
		}
		b.addSubBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				System.err.println("Score start:");
				System.err.println("Number of rounds: " + rounds);
				for (String name : score10.keySet()) {
					System.err.println(name + ": " + score10.get(name));
				}
				for (String name : score20.keySet()) {
					System.err.println(name + ": " + score20.get(name));
				}
				for (String name : score30.keySet()) {
					System.err.println(name + ": " + score30.get(name));
				}
				System.err.println("Score end");

			}
		});
		return b;
	}

	private SequentialBehaviour runTournamentBetweenAllAgents(int rounds) {
		SequentialBehaviour b = new SequentialBehaviour();
		b.setBehaviourName("runTournamentBetweenAllAgents");
		String activeAgents[] = new String[2];
		for (String agent : allAgents) {
			activeAgents[0] = agent;
			for (String secondAgent : allAgents) {
				if (agent.equals(secondAgent))
					continue;
				activeAgents[1] = secondAgent;
				b.addSubBehaviour(runTournamentBetweenTwoAgents(rounds, agent, secondAgent));
			}
		}
		return b;
	}

	private SequentialBehaviour runTournamentBetweenTwoAgents(int rounds, String one, String two) {
		System.out.println(rounds + " :: " + "Adding sequential behaviour - tournament between two agents: " + one + " and " + two);
		SequentialBehaviour b = new SequentialBehaviour();
		b.setBehaviourName("runTournamentBetweenTwoAgents");
		b.addSubBehaviour(presentPrisonersWithDilemma(one, two));
		b.addSubBehaviour(receiveReply(rounds, one, two));
		return b;
	}

	private SequentialBehaviour receiveReply(final int rounds, final String one, final String two) {
		SequentialBehaviour b = new SequentialBehaviour();
		b.setBehaviourName("ReceiveReply");
		final String[] answer = new String[2];
		final int[] score = new int[2];

		CyclicBehaviour c = new CyclicBehaviour() {
			@Override
			public int onEnd() {
				System.out.println("ENDING CYCLIC BEHAVIOUR!");
				return super.onEnd();
			}
			@Override
			public void action() {
				System.out.println("Trying to receive.");
				MessageTemplate m1 = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
				ACLMessage message = receive(m1);
				if (message != null) {
					System.out.println("Received from " + message.getSender().getName() + " :: " + message.getContent());
					if (message.getSender().getName().equals(new AID(one, AID.ISLOCALNAME).getName())) {
						System.out.println("Received from " + one + " :: " + message.getContent());
						answer[0] = message.getContent();
					} else if (message.getSender().getName().equals(new AID(two, AID.ISLOCALNAME).getName())) {
						System.out.println("Received from " + two + " :: " + message.getContent());
						answer[1] = message.getContent();
					}
					System.out.println("Done receiving.");
					if (answer[0] != null && answer[1] != null) {
						System.out.println("Calculating score");
						int temp[] = calculateScore(answer);
						System.out.println("Score calculated");
						score[0] = temp[0];
						score[1] = temp[1];
						System.out.println(rounds + " :: " + new AID(one, AID.ISLOCALNAME).getName() + " and "
								+ new AID(two, AID.ISLOCALNAME).getName() + ": " + Arrays.toString(temp));
					}
				}
				System.out
						.println("Blocking " + this);// + " :: rootname: " + root().getBehaviourName() + " :: parentname: " + parent.getBehaviourName());
				block();
			}
		};
		WrapperBehaviour w = new WrapperBehaviour(c) {
			public boolean done() {
				System.out.println("Checking if we're done: " + (score[0] != 0 || score[1] != 0));
				System.out.println(parent.getBehaviourName() + " :: "+parent);
				System.out.println(root().getBehaviourName() + " :: "+root());
				return score[0] != 0 || score[1] != 0;
			}
		};
		b.addSubBehaviour(w);
		// Inform about score
		b.addSubBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				System.out.println(rounds + " :: " + "INFORMING " + one + " and " + two + "about score: " + Arrays.toString(score)
						+ Arrays.toString(answer));
				// Player 1
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(new AID(one, AID.ISLOCALNAME));
				msg.setSender(getAID());
				msg.setContent("" + score[0]);
				send(msg);

				// Player 2
				ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
				msg2.addReceiver(new AID(two, AID.ISLOCALNAME));
				msg2.setSender(getAID());
				msg2.setContent("" + score[1]);
				send(msg2);
				System.out.println("Information sent");
			}
		});
		// Add score
		b.addSubBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				System.out.println("Adding score " + Arrays.toString(score) + " to " + one + " and " + two);
				if (rounds == 10) {
					score10.put(one, (score10.containsKey(one) ? score10.get(one) + score[0] : score[0]));
					score10.put(two, (score10.containsKey(two) ? score10.get(two) + score[1] : score[1]));
				} else if (rounds == 20) {
					score20.put(one, (score20.containsKey(one) ? score20.get(one) + score[0] : score[0]));
					score20.put(two, (score20.containsKey(two) ? score20.get(two) + score[1] : score[1]));
				} else if (rounds == 30) {
					score30.put(one, (score30.containsKey(one) ? score30.get(one) + score[0] : score[0]));
					score30.put(two, (score30.containsKey(two) ? score30.get(two) + score[1] : score[1]));
				}
				System.out.println("Score added");
			}
		});
		return b;
	}

	private static int[] calculateScore(String[] decisions) {
		if (decisions[0].equals("COOP") && decisions[1].equals("DEFECT")) {
			return new int[] { 0, 5 };
		} else if (decisions[0].equals("COOP") && decisions[1].equals("COOP")) {
			return new int[] { 3, 3 };
		} else if (decisions[0].equals("DEFECT") && decisions[1].equals("COOP")) {
			return new int[] { 5, 0 };
		} else if (decisions[0].equals("DEFECT") && decisions[1].equals("DEFECT")) {
			return new int[] { 2, 2 };
		}
		System.out.println("SCORE CANNOT BE CALCULATED: " + Arrays.toString(decisions));
		return null;
	}

	private OneShotBehaviour presentPrisonersWithDilemma(final String one, final String two) {
		OneShotBehaviour b = new OneShotBehaviour() {
			@Override
			public void action() {
				System.out.println("\n");
				System.out.println("Presenting " + one + " and " + two + " with the dilemma.");
				ACLMessage msg = new ACLMessage(ACLMessage.CFP);
				msg.setContent("DILEMMA");
				msg.setSender(getAID());
				msg.addReceiver(new AID(one, AID.ISLOCALNAME));
				msg.addReceiver(new AID(two, AID.ISLOCALNAME));
				send(msg);
				System.out.println("Dilemma sent");
			}
		};
		return b;
	}

}
