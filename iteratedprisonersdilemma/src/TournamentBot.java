

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
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

public class TournamentBot extends Agent {

	private ArrayList<Agent> allAgents;
	private Agent[] activeAgents;
	private Map<Agent, Integer> scores;

	private Map<Agent, Integer> RSCORE;
	private static int NROFRNDS = 0;

	public TournamentBot() {
		allAgents = new ArrayList<Agent>();
		AMSAgentDescription[] agents = null;
		AgentContainer c = getContainerController();
		try {
			Agent coop = new CoopAgent();
			AgentController a = c.acceptNewAgent("test", coop);
			// a.start();
		} catch (Exception e) {
			System.out.println("FUCK!");
			System.out.println(e);
		}
		try {
			SearchConstraints d = new SearchConstraints();
			d.setMaxResults(new Long(-1));
			agents = AMSService.search(this, new AMSAgentDescription(), d);
		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println(Arrays.toString(agents));

		// allAgents.add(new CoopAgent());
		// allAgents.add(new DefectAgent());
		// allAgents.add(new TitForTat());
		activeAgents = new Agent[2];
		// for (final Agent agent : allAgents) {
		// try {
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// agent.run();
		//
		// }
		// }).start();
		// } catch (Exception e) {
		// System.out.println("FAK");
		//
		// }
		// }
		scores = new HashMap<>();
		RSCORE = new HashMap<>();
	}

	private void reset() {
		allAgents = new ArrayList<Agent>();
		allAgents.add(new CoopAgent());
		allAgents.add(new DefectAgent());
		allAgents.add(new TitForTat());
		activeAgents = new Agent[2];
		scores = new HashMap<>();
		RSCORE = new HashMap<>();

	}

	@Override
	public void setup() {
		for (NROFRNDS = 10; NROFRNDS < 40; NROFRNDS += 10) {
			this.runTournament();
			calculateScores();
			this.reset();
		}
	}

	private void runTournament() {
		for (Agent agent : allAgents) {
			activeAgents[0] = agent;
			for (Agent secondAgent : allAgents) {
				if (agent.equals(secondAgent))
					continue;
				activeAgents[1] = secondAgent;
				for (int i = 0; i < NROFRNDS; i++) {
					runTournamentBetweenTwoAgents();
				}
			}
		}
	}

	private void runTournamentBetweenTwoAgents() {
		presentPrisonersWithDilemma();
		String[] answers = receiveAnswers();
		int[] score = calculateScore(answers);
		informAboutScore(score);
		addScoreToFinalScore(score);
	}

	private void presentPrisonersWithDilemma() {
		System.out.println("SENDING DILEMMA TO ACTIVE AGENTS: " + Arrays.toString(activeAgents));
		ACLMessage msg = new ACLMessage(ACLMessage.CFP);
		msg.setContent("DILEMMA");
		msg.setSender(getAID());
		msg.addReceiver(activeAgents[0].getAID());
		msg.addReceiver(activeAgents[1].getAID());
		send(msg);
		System.out.println("SENT!");
	}

	private String[] receiveAnswers() {
		System.out.println("Trying to receive!");
		String[] answers = new String[2];
		for (int i = 0; i < 2; i++) {
			String[] temp = receiveAnswer();
			answers[Integer.parseInt(temp[0])] = temp[1];
		}
		System.out.println("Received?");
		return answers;
	}

	private int[] calculateScore(String[] decisions) {
		if (decisions[0].equals("COOP") && decisions[1].equals("DEFECT")) {
			return new int[] { 0, 5 };
		} else if (decisions[0].equals("COOP") && decisions[1].equals("COOP")) {
			return new int[] { 3, 3 };
		} else if (decisions[0].equals("DEFECT") && decisions[1].equals("COOP")) {
			return new int[] { 5, 0 };
		} else if (decisions[0].equals("DEFECT") && decisions[1].equals("DEFECT")) {
			return new int[] { 2, 2 };
		}
		return null;
	}

	private void informAboutScore(int[] score) {
		for (int i = 0; i < activeAgents.length; i++) {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(activeAgents[i].getAID());
			msg.setSender(getAID());
			msg.setContent("" + score[i]);
			send(msg);
		}
	}

	private void addScoreToFinalScore(int[] score) {
		for (int i = 0; i < score.length; i++) {
			int currentScore = scores.get(activeAgents[i]);
			scores.put(activeAgents[i], currentScore + score[i]);
		}
	}

	private String[] receiveAnswer() {
		System.out.println("RECEIVE!!");
		String[] decisions = new String[2];
		MessageTemplate m1 = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
		ACLMessage message = blockingReceive(m1);
		if (message != null) {
			if (message.getSender() == activeAgents[0].getAID()) {
				decisions[0] = "0";
				decisions[1] = message.getContent();
			} else if (message.getSender() == activeAgents[1].getAID()) {
				decisions[0] = "1";
				decisions[1] = message.getContent();
			}
		}
		System.out.println("HOLY DIVAH!");
		return decisions;
	}

	private void calculateScores() {
		System.out.println("CALCULATE " + NROFRNDS);
		for (Agent agent : allAgents) {
			System.out.println("Agent " + agent + " scored " + scores.get(agent));
		}
		Behaviour b = new CyclicBehaviour() {

			@Override
			public void action() {
				// TODO Auto-generated method stub

			}
		};
		Behaviour w = new WrapperBehaviour(b) {
			public boolean done() {
				return true;
			}
		};
	}
}
