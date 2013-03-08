
public class MultiplicationSolver extends AbstractSolver{
	private final static char mul = '*';
	public MultiplicationSolver() {
		super(mul);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double result(double num1, double num2, char operator) {
		return num1*num2;
	}

	public void setup(){
		super.setup();
		registerService(this, "Multiplication");
	}
}
