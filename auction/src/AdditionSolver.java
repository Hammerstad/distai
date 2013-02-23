
public class AdditionSolver extends AbstractSolver{
	private final static char add = '+';
	public AdditionSolver() {
		super(add);
	}

	@Override
	public int auction(char operator) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double result(double num1, double num2, char operator) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void setup(){
		registerService(this, "Addition");
	}

}
