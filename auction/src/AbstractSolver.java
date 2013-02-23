

import jade.core.Agent;

import java.util.ArrayList;


public abstract class AbstractSolver extends Agent{
	
	protected double waitTime = 1.0; 
	protected ArrayList<Operation> operations = new ArrayList<>();
	protected char myChar;
	public AbstractSolver(char mych){
		this.myChar = mych;
	}
	public double auction(char operator) {
		return operator==myChar? (operations.size()+1)*waitTime : 1000*waitTime;
	}
	public abstract double result(double num1, double num2, char operator);
	
}