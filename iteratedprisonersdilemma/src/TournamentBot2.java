

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
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

public class TournamentBot2 extends Agent {

	private static ArrayList<Agent> allAgents;
	private static Agent[] activeAgents;
	private static Map<Agent, Integer> scores;

	private static Map<Agent, Integer> RSCORE;

	public TournamentBot2() {
		allAgents = new ArrayList<Agent>();

		allAgents.add(new CoopAgent());
		allAgents.add(new DefectAgent());
		allAgents.add(new TitForTat());
		scores = new HashMap<>();
		RSCORE = new HashMap<>();
	}

	private void reset() {
		allAgents = new ArrayList<Agent>();
		allAgents.add(new CoopAgent());
		allAgents.add(new DefectAgent());
		allAgents.add(new TitForTat());
		scores = new HashMap<>();
		RSCORE = new HashMap<>();

	}

	@Override
	public void setup() {
		SequentialBehaviour b = new SequentialBehaviour();
		for (int NROFRNDS = 10; NROFRNDS < 40; NROFRNDS += 10) {
			b.addSubBehaviour(runTournament(NROFRNDS));
		}
	}

	private SequentialBehaviour runTournament(int nrofrnds) {
		Agent[] activeAgents = new AbstractAgent[2];
		SequentialBehaviour b = new SequentialBehaviour();
		for (Agent agent : allAgents) {
			activeAgents[0] = agent;
			for (Agent secondAgent : allAgents) {
				if (agent.equals(secondAgent))
					continue;
				activeAgents[1] = secondAgent;
				for (int i = 0; i < nrofrnds; i++) {
					runTournamentBetweenTwoAgents(activeAgents);
				}
			}
		}
		return b;
	}

	private SequentialBehaviour runTournamentBetweenTwoAgents(Agent[] agents) {
		SequentialBehaviour b = new SequentialBehaviour();
		b.addSubBehaviour(presentPrisonersWithDilemma());
		// String[] answers = receiveAnswers();
		// int[] score = calculateScore(answers);
		// informAboutScore(score);
		// addScoreToFinalScore(score);
		return b;
	}

	private OneShotBehaviour presentPrisonersWithDilemma() {
		OneShotBehaviour b = new OneShotBehaviour() {
			@Override
			public void action() {
				System.out.println("SENDING DILEMMA TO ACTIVE AGENTS: " + Arrays.toString(activeAgents));
				ACLMessage msg = new ACLMessage(ACLMessage.CFP);
				msg.setContent("DILEMMA");
				msg.setSender(getAID());
				msg.addReceiver(activeAgents[0].getAID());
				msg.addReceiver(activeAgents[1].getAID());
				send(msg);
				System.out.println("SENT!");
			}
		};
		return b;
	}

	private String[] receiveAnswers() {
		System.out.println("Trying to receive!");
		String[] answers = new String[2];
		for (int i = 0; i < 2; i++) {
			// String[] temp = receiveAnswer();
			// answers[Integer.parseInt(temp[0])] = temp[1];
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

	private OneShotBehaviour informAboutScore(final int[] score) {
		OneShotBehaviour b = new OneShotBehaviour() {
			@Override
			public void action() {

				for (int i = 0; i < activeAgents.length; i++) {
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.addReceiver(activeAgents[i].getAID());
					msg.setSender(getAID());
					msg.setContent("" + score[i]);
					send(msg);
				}

			}
		};
		return b;
	}

	private void addScoreToFinalScore(int[] score) {
		for (int i = 0; i < score.length; i++) {
			int currentScore = scores.get(activeAgents[i]);
			scores.put(activeAgents[i], currentScore + score[i]);
		}
	}

	private OneShotBehaviour calculateScores() {
		OneShotBehaviour b = new OneShotBehaviour() {

			@Override
			public void action() {
				for (Agent agent : allAgents) {
					System.out.println("Agent " + agent + " scored " + scores.get(agent));
				}
			}
		};

		return b;
	}
}
