package solver.ls;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main
{  
  public static void main(String[] args) 
  {
	if(args.length == 0)
	{
		System.out.println("Usage: java Main <file>");
		return;
	}

	String input = args[0];
	Path path = Paths.get(input);
	String filename = path.getFileName().toString();
	System.out.println("Instance: " + input);

	Timer watch = new Timer();
	watch.start();
	VRPInstance instance = new VRPInstance(input);
	VehicleConfiguration initVC = instance.findFeasibleSolution();
	instance.outputSolution(initVC, filename + ".init.sol");
	Proposal prop = new RandomProposal(new Proposal[]{
					new CustomerSwapProposal(),
					new CustomerMoveProposal(),
					new RouteReversalProposal()});
	VehicleConfiguration solution = instance.simulatedAnnealing(initVC, prop);
	instance.outputSolution(solution, filename + ".sol");
	watch.stop();

	System.out.println("Instance: " + filename + 
					   " Time: " 	+ String.format("%.2f",watch.getTime()) +
					   " Result: " 	+ solution.totalDistance + " Solution: " + solution.asString());
  }
}