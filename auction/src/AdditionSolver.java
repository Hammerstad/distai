
public class AdditionSolver extends AbstractSolver{
	private final static char add = '+';
	public AdditionSolver() {
		super(add);
	}

	@Override
	public double result(double num1, double num2, char operator){
		return num1+num2;
	}
	
	public void setup(){
		super.setup();
		registerService(this, "Addition");
	}

}
