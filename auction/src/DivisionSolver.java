
public class DivisionSolver extends AbstractSolver{
	private final static char div = '/';
	public DivisionSolver() {
		super(div);
	}

	@Override
	public double result(double num1, double num2, char operator) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setup(){
		registerService(this, "Division");
	}
}
