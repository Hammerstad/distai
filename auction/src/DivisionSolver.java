
public class DivisionSolver extends AbstractSolver{
	private final static char div = '/';
	public DivisionSolver() {
		super(div);
	}

	@Override
	public double result(double num1, double num2, char operator) {
		return num1/num2;
	}

	public void setup(){
		super.setup();
		registerService(this, "Division");
	}
}
