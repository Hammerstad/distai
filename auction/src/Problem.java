import java.util.Arrays;

public class Problem {
	private static final String[][] PROBLEM_SET = new String[][] { { "2", "2", "+", "3", "-" }, //1
																  { "5", "4", "*", "10", "/" }, //2
																  { "3", "3", "-", "17", "+" }, //17
																  { "20", "4", "-", "2", "/" }, //8
																  { "8", "4", "/", "3", "*"} };//12

	private String[] problem;
	private boolean hasInformedAboutNextSubproblem;
	private boolean hasMoreSubProblems;
	private boolean isSolved;

	public Problem(String[] problem) {
		this.problem = problem;
		this.hasInformedAboutNextSubproblem = false;
		this.hasMoreSubProblems = true;
		this.isSolved = false;
	}

	public String[] getNextSubProblem() {
		if (this.problem.length < 3) {
			this.hasMoreSubProblems = false;
			return null;
		}
		this.hasInformedAboutNextSubproblem = true;
		return Arrays.copyOfRange(this.problem, 0, 3);
	}

	public void solveNextSubProblem(String answer) {
		String[] newProblem = Arrays.copyOfRange(problem, 2, problem.length);
		newProblem[0] = answer;
		this.problem = newProblem;
		this.hasInformedAboutNextSubproblem = false;
		hasMoreSubProblems();
		isSolved();
	}

	public boolean isSolved() {
		this.isSolved = this.problem.length == 1;
		return this.isSolved;
	}

	public boolean hasInformedAboutNextSubproblem() {
		return this.hasInformedAboutNextSubproblem;
	}

	public boolean hasMoreSubProblems() {
		this.hasMoreSubProblems = this.problem.length >= 3;
		return this.hasMoreSubProblems;
	}
	
	public String getSolution(){
		if(isSolved()){
			return problem[0];
		}
		return null;
	}

	public static void main(String[] args) {
		Problem prob = new Problem(Problem.PROBLEM_SET[0]);
		System.out.println(Arrays.toString(Problem.PROBLEM_SET[0]));
		System.out.println(prob.hasInformedAboutNextSubproblem());
		System.out.println(prob.hasMoreSubProblems());
		System.out.println(prob.isSolved());
		String[] problem = prob.getNextSubProblem();
		System.out.println(Arrays.toString(problem));
		System.out.println();
		prob.solveNextSubProblem("4");

		System.out.println(prob.hasInformedAboutNextSubproblem());
		System.out.println(prob.hasMoreSubProblems());
		System.out.println(prob.isSolved());
		
		System.out.println(Arrays.toString(prob.problem));
		problem = prob.getNextSubProblem();

		System.out.println(prob.hasInformedAboutNextSubproblem());
		System.out.println(prob.hasMoreSubProblems());
		System.out.println(prob.isSolved());
		System.out.println(Arrays.toString(problem));
		prob.solveNextSubProblem("1");
		System.out.println(prob.hasInformedAboutNextSubproblem());
		System.out.println(prob.hasMoreSubProblems());
		System.out.println(prob.isSolved());
		System.out.println(prob.getSolution());
	}
}
