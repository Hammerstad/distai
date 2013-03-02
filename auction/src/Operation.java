import jade.core.AID;

public class Operation {
	public final double num1;
	public final double num2;
	public final char oper;
	public final int ID;
	public boolean accepted = false;
	public final AID aid;
	public Operation(double n1, double n2, char op, int id, AID aid1){
		this.num1=n1;
		this.num2=n2;
		this.oper=op;
		this.ID  = id;
		this.aid =aid1;
	}
	
}
