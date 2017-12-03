package solver.ls;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ilog.cp.*;
import ilog.concert.*;

public class VRPInstance
{
	// VRP Input Parameters
	int numCustomers;        		// the number of customers	   
	int numVehicles;           	// the number of vehicles
	int vehicleCapacity;			// the capacity of the vehicles
	int[] demandOfCustomer;		// the demand of each customer
	int[] demandOfNonDepotCustomer; // demand of all customers, excluding the depot
	double[] xCoordOfCustomer;	// the x coordinate of each customer
	double[] yCoordOfCustomer;	// the y coordinate of each customer
		
	Customer depot;
	Customer[] customers;
	
	public VRPInstance(String fileName)
	{
		Scanner read = null;
		try
		{
			read = new Scanner(new File(fileName));
		} catch (FileNotFoundException e)
		{
			System.out.println("Error: in VRPInstance() " + fileName + "\n" + e.getMessage());
			System.exit(-1);
		}

		numCustomers = read.nextInt(); 
		numVehicles = read.nextInt();
		vehicleCapacity = read.nextInt();

		System.out.println("Number of customers: " + numCustomers);
		System.out.println("Number of vehicles: " + numVehicles);
		System.out.println("Vehicle capacity: " + vehicleCapacity);

		demandOfCustomer = new int[numCustomers];
		demandOfNonDepotCustomer = new int[numCustomers - 1];
		xCoordOfCustomer = new double[numCustomers];
		yCoordOfCustomer = new double[numCustomers];
		// customers only reporesents non-depots
		customers = new Customer[numCustomers - 1];
		
		for (int i = 0; i < numCustomers; i++)
		{
			demandOfCustomer[i] = read.nextInt();
			xCoordOfCustomer[i] = read.nextDouble();
			yCoordOfCustomer[i] = read.nextDouble();
			if (i == 0) {
				depot = new Customer(
						xCoordOfCustomer[i], yCoordOfCustomer[i], demandOfCustomer[i], i);
			} else {
				customers[i - 1] = new Customer(
						xCoordOfCustomer[i], yCoordOfCustomer[i], demandOfCustomer[i], i);
				demandOfNonDepotCustomer[i - 1] = demandOfCustomer[i];
			}
		}
		
		for (int i = 0; i < numCustomers - 1; i++)
			System.out.println(demandOfCustomer[i] + " " + xCoordOfCustomer[i] + " " + yCoordOfCustomer[i]);
	}


	public VehicleConfiguration findFeasibleSolution() {
		try {
			IloCP cp = new IloCP();
			IloIntVar[][] assignedToVehicleVC = new IloIntVar[numVehicles][numCustomers - 1];
			IloIntExpr[][] assignedToVehicleCV = new IloIntExpr[numCustomers - 1][numVehicles];
			for (int v = 0; v < numVehicles; v++) {
				for (int c = 0; c < numCustomers - 1; c++) {
					assignedToVehicleVC[v][c] = cp.intVar(0, 1);
					assignedToVehicleCV[c][v] = assignedToVehicleVC[v][c];
				}
				cp.add(cp.le(cp.prod(assignedToVehicleVC[v], demandOfNonDepotCustomer), vehicleCapacity));
			}
			for (int c = 0; c < numCustomers - 1; c++) {
				cp.add(cp.eq(cp.sum(assignedToVehicleCV[c]), 1));
			}
			if (cp.solve()) {
				List<List<Customer>> vehicleRoutesVC = new ArrayList<List<Customer>>(numVehicles);
				for (int v = 0; v < numVehicles; v++) {
					vehicleRoutesVC.add(new ArrayList<Customer>());
					for (int c = 0; c < numCustomers - 1; c++) {
						if (cp.getValue(assignedToVehicleVC[v][c]) == 1) {
							vehicleRoutesVC.get(v).add(customers[c]);
						}
					}
				}
				VehicleConfiguration vc = new VehicleConfiguration(this, vehicleRoutesVC);
				cp.clearModel();
				return vc;
			} else {
				cp.clearModel();
				return null;
			}
		} catch (IloException e) {
			System.out.println("Error: " + e);
			return null;
		}
	}
	
	/**
	 * Iteratively improves on a single solution. Random proposals are generated and accepted iff
	 * they have a better objective function than the current configuration.
	 */
	public VehicleConfiguration iterativeImprovement(VehicleConfiguration vc, Proposal prop) {
		int MAX_TIME = 10000;
		long start_time = System.currentTimeMillis();
		long end_time = System.currentTimeMillis();
		while (end_time - start_time < MAX_TIME) {
			VehicleConfiguration proposedVC = prop.proposal(vc);
			if (proposedVC.satisfiesCapacity && proposedVC.totalDistance < vc.totalDistance) {
				vc = proposedVC;
			}
			end_time = System.currentTimeMillis();
		}
		return vc;
		
	}
	
	
	public VehicleConfiguration simulatedAnnealing(VehicleConfiguration vc, Proposal prop) {
		int maxTime = 270 * 1000;
		double tempDecay = 0.95;
		int proposalsPerTemp = (int) Math.pow(numCustomers, 2);
		long startTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis();
		double temperature = vc.totalDistance;
		int iterationsNoChange = 0;
		int maxIterationsNoChange = 5;
		while (endTime - startTime < maxTime && iterationsNoChange < maxIterationsNoChange) {
			double initialTotalDistance = vc.totalDistance;
			for (int i = 0; i < proposalsPerTemp; i++) {
				VehicleConfiguration proposedVC = prop.proposal(vc);
				if (proposedVC.satisfiesCapacity && proposedVC.totalDistance <= vc.totalDistance) {
					vc = proposedVC;
				} else if (proposedVC.satisfiesCapacity &&
						Math.exp((vc.totalDistance - proposedVC.totalDistance) / temperature) > Math.random()) {
					vc = proposedVC;
				}
				endTime = System.currentTimeMillis();
			}
			if (vc.totalDistance == initialTotalDistance) iterationsNoChange++;
			else iterationsNoChange = 0;
			System.out.println("Temp: " + temperature + ", Time: " + (endTime - startTime) +
					", Distance: " + vc.totalDistance);
			temperature *= tempDecay;
		}
		return vc;
	}

	
	public void outputSolution(VehicleConfiguration vc, String fileName) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));
			int optNum = 0;
			if (vc.isOptimal) optNum = 1;
			writer.append(vc.totalDistance + " " + optNum + "\n");
			for (List<Customer> vehicleRoute : vc.vehicleRoutesVC) {
				writer.append("0 ");
				for (Customer c : vehicleRoute) {
					writer.append((c.number) + " ");
				}
				writer.append("0\n");
			}
			writer.close();
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
	}

}
