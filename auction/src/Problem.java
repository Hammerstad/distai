import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Problem {
	public static final String[][] PROBLEM_SET = new String[][] { { "2", "2", "+", "3", "-" }, // 1
			{ "5", "4", "*", "10", "/" }, // 2
			{ "3", "3", "-", "17", "+" }, // 17
			{ "20", "4", "-", "2", "/" }, // 8
			{ "8", "4", "/", "3", "*" } };// 6

	/**
	 * String-representation of our problem, each cell represents a number or an operator
	 */
	private String[] problem;

	/**
	 * If someone has asked this task about the next subproblem
	 */
	private boolean hasInformedAboutNextSubproblem;

	/**
	 * If this task has more subproblems unsolved
	 */
	private boolean hasMoreSubProblems;

	/**
	 * If this problem is solved
	 */
	private boolean isSolved;

	/**
	 * A map with the offers done to solve the next subproblem of this problem. </br>String=agent name, Integer=offer.
	 */
	private Map<String, Integer> offers;

	/**
	 * Creates a new problem from a string (postfix).
	 * 
	 * @param problem
	 *            - postfix notation, f.ex: 1 1 + solves to 2
	 */
	public Problem(String[] problem) {
		this.problem = problem;
		this.hasInformedAboutNextSubproblem = false;
		this.hasMoreSubProblems = true;
		this.isSolved = false;
		this.offers = new HashMap<String, Integer>();
	}

	/**
	 * Returns the next subproblem, if there are one. Returns null if there are none.
	 * 
	 * @return - a String[] of length 3, the next subproblem (postfix notation).
	 */
	public String[] getNextSubProblem() {
		if (this.problem.length < 3) {
			this.hasMoreSubProblems = false;
			return null;
		}
		this.hasInformedAboutNextSubproblem = true;
		return Arrays.copyOfRange(this.problem, 0, 3);
	}

	/**
	 * Accepts a answer to the next subproblem.
	 * 
	 * @param answer
	 *            - a double disguised as a String.
	 */
	public void solveNextSubProblem(String answer) {
		String[] newProblem = Arrays.copyOfRange(problem, 2, problem.length);
		newProblem[0] = answer;
		this.problem = newProblem;
		this.hasInformedAboutNextSubproblem = false;
		hasMoreSubProblems();
		isSolved();
	}

	/**
	 * Returns whether or not this problem has been solved.
	 * 
	 * @return
	 */
	public boolean isSolved() {
		this.isSolved = this.problem.length == 1;
		return this.isSolved;
	}

	/**
	 * Returns whether or not this problem has informed about the next subproblem.
	 * 
	 * @return
	 */
	public boolean hasInformedAboutNextSubproblem() {
		return this.hasInformedAboutNextSubproblem;
	}

	/**
	 * Returns whether or not this problem has more subproblems remaining unsolved.
	 * 
	 * @return
	 */
	public boolean hasMoreSubProblems() {
		this.hasMoreSubProblems = this.problem.length >= 3;
		return this.hasMoreSubProblems;
	}

	/**
	 * Returns the solution if there is one. Else returns null.
	 * 
	 * @return
	 */
	public String getSolution() {
		if (isSolved()) {
			return problem[0];
		}
		return null;
	}

	/**
	 * Adds an offer from an agent to this problem.
	 * 
	 * @param agentName
	 *            - name of the agent
	 * @param offer
	 *            - the offer
	 */
	public void addOffer(String agentName, int offer) {
		this.offers.put(agentName, offer);
	}

	/**
	 * Returns whether or not this problem has an amount of offers.
	 * 
	 * @param amount
	 *            - amount of offers
	 * @return True if there have been made "amount" offers on this problem.
	 */
	public boolean hasAmountOfOffers(int amount) {
		return this.offers.size() == amount;
	}

	/**
	 * Returns the best offer from an agent. Best is the lowest.
	 * 
	 * @return
	 */
	public String getBestOfferingAgent() {
		Map.Entry<String, Integer> minEntry = null;
		for (Map.Entry<String, Integer> entry : this.offers.entrySet()) {
			if (minEntry == null || entry.getValue().compareTo(minEntry.getValue()) <= 0) {
				minEntry = entry;
			}
		}
		return minEntry.getKey();
	}

	/**
	 * Clears all current offers for this problem.
	 */
	public void removeOffers() {
		this.offers = new HashMap<String, Integer>();
	}
}
