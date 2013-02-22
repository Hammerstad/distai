package Agents;

import java.util.ArrayList;

import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.FIPAManagementVocabulary;

public class OwnStrategyErikAndEirik extends abstractAgent {

  public static final String defect = "DEFECT";
	public static final String coop = "COOP";
	public static final String dilemma = "DILEMMA";

	protected void setup() {
		super.setup();
		// TODO: insert STUFF here!
	}

	String message = defect;
	ArrayList<Boolean> enemydefected = new ArrayList<>();
	ArrayList<Boolean> wedefected = new ArrayList<>();
	private int nrOfRounds = 1;
	private boolean enemyAllwaysDefect = true;
	private boolean enemyAllaysCoop = true;
	private int nrOfRoundsCoopInARow = 0;
	private int coopInARow = 0;

	public String sendMessage() {
		wedefected.add(message.equals(defect));
		return message;
	}

	// d c
	// defect 2 5
	// coop 0 3
	public void receiveMessage(String msg) {

		if (msg.equals("5")) { // enemy coop
			enemyAllwaysDefect = false;
			enemydefected.add(false);
			wedefected.add(true);
			nrOfRounds++;
		} else if (msg.equals("3")) { // enemy coop
			enemyAllwaysDefect = false;
			enemydefected.add(false);
			nrOfRounds++;
			wedefected.add(false);
		} else if (msg.equals("2")) { // enemy defect
			enemyAllaysCoop = false;
			enemydefected.add(true);
			wedefected.add(true);
			nrOfRounds++;
		} else if (msg.equals("0")) { // enemy defect
			enemydefected.add(true);
			enemyAllaysCoop = false;
			nrOfRounds++;
			wedefected.add(false);
		} else {
			// WRONG!
			System.out
					.println("THE POINTS ARE WRONG IN ERIK AND EIRIK's STRATEGY!!!!");
		}

		if (enemyAllaysCoop) { // If enemy is only coop, keep defecting.
			nrOfRoundsCoopInARow++;
			message = defect;
		} else {
			if (nrOfRoundsCoopInARow == 0 && nrOfRounds == 2) {
				message = coop;
			} else if (nrOfRoundsCoopInARow == 0 && nrOfRounds == 3) {
				message = coop;
			} else if (nrOfRoundsCoopInARow == 0 && nrOfRounds >= 4) {
				if (!enemydefected.get(enemydefected.size() - 1)) {
					message = coop;
				} else {
					message = defect;
				}
			} else {
				if (nrOfRoundsCoopInARow == 1 && nrOfRounds <= 3) {
					message = coop;
				} else if (nrOfRoundsCoopInARow == 1) {
					if (!enemydefected.get(enemydefected.size() - 1)) {
						message = coop;
					} else {
						message = defect;
					}
				} else {
					if (nrOfRounds <= 3) {
						message = coop;
					} else {
						if (coopInARow >= nrOfRoundsCoopInARow - 1) {
							coopInARow = 0;
							message = coop;
						} else {
							message = defect;
							coopInARow++;
						}
					}
				}
			}
		}
	}
}
