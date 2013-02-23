

import jade.core.Agent;

import java.util.ArrayList;


public abstract class AbstractSolver extends Agent{
	
	protected double waitTime = 1000; //ms
	protected ArrayList<Operation> operations = new ArrayList<>();
	protected char myChar;
	public AbstractSolver(char mych){
		this.myChar = mych;
	}
	public int auction(char operator) {
		return operator==myChar? operations.size()+1 : 1000;
	}
	public abstract double result(double num1, double num2, char operator) throws Exception;
	
}