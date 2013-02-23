import java.util.ArrayList;

import jade.core.Agent;

public abstract class AbstractSolver extends Agent{
	
	protected double waitTime = 1000; //ms
	protected ArrayList<Operation> operations = new ArrayList<>();
	protected char myChar;
	public AbstractSolver(char mych){
		this.myChar = mych;
	}
	
	public abstract int auction(char operator);
	
	public abstract double result(double num1, double num2, char operator);
	
}