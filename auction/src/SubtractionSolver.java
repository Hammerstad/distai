
public class SubtractionSolver extends AbstractSolver{
	private final static char sub = '-';
	public SubtractionSolver() {
		super(sub);
		// TODO Auto-generated constructor stub
	}


	@Override
	public double result(double num1, double num2, char operator) {
		// TODO Auto-generated method stub
		return num1-num2;
	}

	public void setup(){
		super.setup();
		registerService(this, "Subtraction");
	}
}
