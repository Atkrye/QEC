package org.york.yqcgp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import org.york.ants.QAntOpt;
import org.york.builders.Builder;
import org.york.yqcgp.operator.AntLibrary;
import org.york.zx.ZXCalcCGP;

public class Experimenter {

	static int sessionCounter = 0;
	
	public static int getSessionID(){
		counterLock.lock();
		int ret = sessionCounter;
		sessionCounter++;
		counterLock.unlock();
		return ret;
	}
	
    static ReentrantLock counterLock = new ReentrantLock(true); // enable fairness policy
	public class Runner implements Runnable{
		String mode;
		Builder build;
		double rate;
		int maxRuns;
		int popSize;
		YabukiEvaluator eval;
		boolean free = true;
		boolean ready = false;
		Result res;
		boolean result = false;
		Random rand = new Random();
		
		public void prepare(String mode, double rate, int maxRuns, int popSize, Builder build, YabukiEvaluator eval){
			this.mode = mode;
			this.rate = rate;
			this.maxRuns = maxRuns;
			this.popSize = popSize;
			this.build = build;
			this.eval = eval;
			free = false;
		}
		
		public boolean hasResult(){
			if(result){
				result = false;
				return true;
			}
			return false;
		}
		
		public Result getResult(){
			if(res != null){
				Result copy = res;
				res = null;
				return copy;
			}
			return null;
		}
		
		public boolean isReady(){
			if(ready){
				ready = false;
				return true;
			}
			else{
				return false;
			}
		}
		@Override
		public void run() {
			ready = true;
			free = true;
			while(true){
				if(free == false){
					runExp();
				}
				else{
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		public void runExp(){

			int sessionID = getSessionID();
			System.out.println("Run on session " + sessionID);
			if(mode.equals(ACO)){
				QAntOpt qAnt = new QAntOpt(AntLibrary.library(), build);
				build.setup(qAnt);
				res = qAnt.runExperiment(popSize, maxRuns, rate, eval, sessionID);
				result = true;
				ready = true;
			}
			if(mode.equals(ZX)){

				ZXCalcCGP zx = new ZXCalcCGP(Main.zxLib(), build);
				zx.rand = rand;
				res = zx.runExperiment(popSize, maxRuns, rate, eval, sessionID);
				result = true;
				ready = true;
			}
			free = true;
		}
		
	}
	
	public final static int threads = 1;
	public final static String ACO = "ACO";
	public final static String ZX = "ZX";
	public final static String RandACO = "RandACO";
	HashMap<String, ArrayList<Result>> results = new HashMap<String, ArrayList<Result>>();
	
	public void run(String[] names, int[] runs, String[] modes, double[] rates, int[] populationSizes, int[] maxRuns, Builder build, YabukiEvaluator eval){
		ArrayList<String> nameArray = new ArrayList<String>();
		ArrayList<String> modeArray = new ArrayList<String>();
		ArrayList<Integer> runArray = new ArrayList<Integer>();
		ArrayList<Double> rateArray = new ArrayList<Double>();
		ArrayList<Integer> popArray = new ArrayList<Integer>();
		ArrayList<Integer> maxArray = new ArrayList<Integer>();
		for(int i = 0; i < names.length; i++){
			nameArray.add(names[i]);
			modeArray.add(modes[i]);
			runArray.add(runs[i]);
			rateArray.add(rates[i]);
			popArray.add(populationSizes[i]);
			maxArray.add(maxRuns[i]);
		}
		
		run(nameArray, runArray, modeArray, rateArray, popArray, maxArray, build, eval);
	}
	
	public void run(ArrayList<String> names, ArrayList<Integer> runs, ArrayList<String> modes, ArrayList<Double> rates, ArrayList<Integer> populationSizes, ArrayList<Integer> maxRuns, Builder builder, YabukiEvaluator eval){
		ArrayList<YabukiEvaluator> evals = new ArrayList<YabukiEvaluator>();
		ArrayList<Builder> builders = new ArrayList<Builder>();
		for(int i = 0; i < names.size(); i++){
			evals.add(eval);
			builders.add(builder);
		}
		run(names, runs, modes, rates, populationSizes, maxRuns, builders, evals);
	}
	
	public void run(ArrayList<String> names, ArrayList<Integer> runs, ArrayList<String> modes, ArrayList<Double> rates, ArrayList<Integer> populationSizes, ArrayList<Integer> maxRuns, ArrayList<Builder> builders, ArrayList<YabukiEvaluator> evals){

		ArrayList<Runner> runners = new ArrayList<Runner>();
		ArrayList<Thread> runnerThreads = new ArrayList<Thread>();
		if(threads > 1){
			for(int ru = 0; ru < threads; ru++){
				Runner r = new Runner();
				Thread s = new Thread(r, ru + "thread");
				s.start();
				runners.add(r);
				runnerThreads.add(s);
			}
		}
		for(int i = 0; i < names.size(); i++){
			String name = names.get(i);
			String mode = modes.get(i);
			double rate = rates.get(i);
			int run = runs.get(i);
			int popSize = populationSizes.get(i);
			int maxRun = maxRuns.get(i);
			Builder build = builders.get(i);
			YabukiEvaluator eval = evals.get(i);
			ArrayList<Result> res = new ArrayList<Result>();
			
			System.out.println("Begin experiment " + name);
			
			int r = 0;
			int d = 0;
			
			for(Runner ru : runners){
				ru.free = true;
				ru.ready = true;
			}
			
			while(d < run){
				if(threads > 1){
					for(Runner ru : runners){
						if(ru.hasResult()){
							res.add(ru.getResult());
							d++;
							System.out.println(d + " / " + run + " complete.");
						}
						else if(ru.isReady() && r < run){
							System.out.println("Running " + name + "-" + r + ".");
							ru.prepare(mode, rate, maxRun, popSize, build, eval);
							r++;
						}
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else{

					int sessionID = getSessionID();
					System.out.println("Run single thread on session " + sessionID);
					if(mode.equals(ACO)){
						QAntOpt qAnt = new QAntOpt(AntLibrary.library(), build);
						build.setup(qAnt);
						res.add(qAnt.runExperiment(popSize, maxRun, rate, eval, sessionID));
					}
					if(mode.equals(ZX)){
						ZXCalcCGP zx = new ZXCalcCGP(Main.zxLib(), build);
						res.add(zx.runExperiment(popSize, maxRun, rate, eval, sessionID));
					}
					d++;
				}
			}
			System.out.println("Putting " + name + ": " + res.size());
			results.put(name, res);
		}
		for(Thread s : runnerThreads){
			s.stop();
		}
		System.out.println("Experiments run. Enter a command to continue: ");
		while(true){
	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        System.out.print("\n>");
	        try {
				runCommand(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void printExperiment(String name){
		if(results.keySet().contains(name)){
			System.out.println("Displaying results for experiment " + name);
			ArrayList<Result> res = results.get(name);
			for(int i = 0; i < res.size(); i++){
				printRun(name, i);
			}
		}
		else{
			System.out.println("Experiment " + name + " not found.");
		}
	}
	
	public void printRun(String name, int run){
		if(results.keySet().contains(name)){
			ArrayList<Result> res = results.get(name);
			if(run >= res.size() || run < 0){
				System.out.println("Run " + run + " does not exist fo experiment " + name);
			}
			else{
				Result result = res.get(run);
				System.out.println(name + "-" + run + ", " + result);
			}
		}
		else{
			System.out.println("Experiment " + name + " not found.");
		}
		
	}
	
	public void printStats(String name){
		if(results.keySet().contains(name)){
			System.out.println("\nStatistics for experiment " + name);
			ArrayList<Result> res = results.get(name);
			double successful = 0.0;
			double successfulSize = 0.0;
			double meanRuns = 0.0;
			double meanEvals = 0.0;
			for(Result r : res){
				if(r.isPerfectScore()){
					successful++;
				}
				if(r.isPerfectSizeScore()){
					successfulSize++;
				}
				meanRuns += r.getRuns();
				meanEvals += r.getEvaluations();
			}
			double count = Double.valueOf(res.size());
			successful = successful / count;
			successfulSize = successfulSize / count;
			meanRuns = meanRuns / count;
			meanEvals = meanEvals / count;
			double varianceRuns = 0.0;
			double varianceEvals = 0.0;
			for(Result r : res){
				varianceRuns += Math.pow((r.getRuns() - meanRuns), 2);
				varianceEvals += Math.pow((r.getEvaluations() - meanEvals), 2);
			}
			varianceRuns = varianceRuns / count;
			varianceEvals = varianceEvals / count;
			System.out.println("Success rate: " + successful);
			System.out.println("Human competitive size rate: " + successfulSize);
			System.out.println("Average generations: " + meanRuns);
			System.out.println("Generation variance: " + varianceRuns);
			System.out.println("Average evaluations: " + meanEvals);
			System.out.println("Evaluations variance: " + varianceEvals);
		}
		else{
			System.out.println("Experiment " + name + " not found.");
		}
		
	}
	
	public void runCommand(String command){
		command = command.toLowerCase();
		String[] args = command.split(" ");
		for(int i = 0; i < args.length; i++){
			args[i] = args[i].trim();
		}
		String mainCommand = args[0];
		if(mainCommand.equals("results")){
			//Display results
			if(args.length > 1){
				//Specific experiment
				String experiment = args[1];
				if(args.length > 2){
					//Specific run
					int run = Integer.valueOf(args[2]);
					printRun(experiment, run);
				}
				else{
					printExperiment(experiment);
				}
			}
			else{
				//All experiments
				for(String exp : results.keySet()){
					printExperiment(exp);
				}
			}
		}
		else if(mainCommand.equals("stats")){
			//Display statistics
			if(args.length > 1){
				//Specific experiment
				String experiment = args[1];
				printStats(experiment);
			}
			else{
				//All experiments
				for(String exp : results.keySet()){
					printStats(exp);
				}
			}
		}
		else{
			System.out.println("Command " + mainCommand + " not found.");
		}
	}
	
}
