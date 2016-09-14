package org.york.yqcgp;

public class Result {

	private boolean perfectScore = false;
	private boolean perfectSizeScore = false;
	private double bestScore;
	private double bestSizeScore;
	private int runs;
	private int evaluations;
	private String mode;
	private Object bestSolution;
	
	@Override
	public String toString(){
		String ret = mode + ": ";
		if(perfectScore){
			ret = ret + " perfect performance found";
		}
		else{
			ret = ret + " no perfect performance found";
		}
		ret = ret + " (" + bestScore + ").";
		if(perfectSizeScore){
			ret = ret + " Perfect size found";
		}
		else{
			ret = ret + " No perfect size found";
		}
		ret = ret + " (" + bestSizeScore + ").";
		ret = ret + "\n" + runs + " runs taken, using " + evaluations + " evaluations.";
		return ret;
	}
	
	public Result(boolean perfectScore, boolean perfectSizeScore, double bestScore, double bestSizeScore, int runs, int evals, String mode, Object best){
		this.setPerfectScore(perfectScore);
		this.setPerfectSizeScore(perfectSizeScore);
		this.setBestScore(bestScore);
		this.setBestSizeScore(bestSizeScore);
		this.setRuns(runs);
		this.setEvaluations(evals);
		this.setMode(mode);
		this.setBestSolution(best);
	}

	public boolean isPerfectScore() {
		return perfectScore;
	}

	public void setPerfectScore(boolean perfectScore) {
		this.perfectScore = perfectScore;
	}

	public boolean isPerfectSizeScore() {
		return perfectSizeScore;
	}

	public void setPerfectSizeScore(boolean perfectSizeScore) {
		this.perfectSizeScore = perfectSizeScore;
	}

	public double getBestScore() {
		return bestScore;
	}

	public void setBestScore(double bestScore) {
		this.bestScore = bestScore;
	}

	public int getRuns() {
		return runs;
	}

	public void setRuns(int runs) {
		this.runs = runs;
	}

	public double getBestSizeScore() {
		return bestSizeScore;
	}

	public void setBestSizeScore(double bestSizeScore) {
		this.bestSizeScore = bestSizeScore;
	}

	public int getEvaluations() {
		return evaluations;
	}

	public void setEvaluations(int evaluations) {
		this.evaluations = evaluations;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Object getBestSolution() {
		return bestSolution;
	}

	public void setBestSolution(Object bestSolution) {
		this.bestSolution = bestSolution;
	}
}
