import jade.core.AID;

public class Operation {
	public final double num1;
	public final double num2;
	public final char oper;
	public final int ID;
	/**
	 * Whether this task has been accepted or not.
	 */
	public boolean accepted = false;
	public final AID aid;
	public final int estimatedWait;

	/**
	 * Create a new operation (problem)
	 * 
	 * @param n1
	 *            - first number
	 * @param n2
	 *            - second number
	 * @param op
	 *            - operator to use on the numbers
	 * @param id
	 *            - the ID of the problem associated with this task
	 * @param aid1
	 *            - the AID of the sender
	 * @param estimatedWait
	 *            - the time we are going to use on this task
	 */
	public Operation(double n1, double n2, char op, int id, AID aid1, int estimatedWait) {
		this.num1 = n1;
		this.num2 = n2;
		this.oper = op;
		this.ID = id;
		this.aid = aid1;
		this.estimatedWait = estimatedWait;
	}
}
