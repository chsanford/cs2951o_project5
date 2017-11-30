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
	double[] xCoordOfCustomer;	// the x coordinate of each customer
	double[] yCoordOfCustomer;	// the y coordinate of each customer
		
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
		xCoordOfCustomer = new double[numCustomers];
		yCoordOfCustomer = new double[numCustomers];
		customers = new Customer[numCustomers];
		
		for (int i = 0; i < numCustomers; i++)
		{
			demandOfCustomer[i] = read.nextInt();
			xCoordOfCustomer[i] = read.nextDouble();
			yCoordOfCustomer[i] = read.nextDouble();
			customers[i] = new Customer(
					xCoordOfCustomer[i], yCoordOfCustomer[i], demandOfCustomer[i], i);
		}

		for (int i = 0; i < numCustomers; i++)
			System.out.println(demandOfCustomer[i] + " " + xCoordOfCustomer[i] + " " + yCoordOfCustomer[i]);
	}


	public VehicleConfiguration findFeasibleSolution() {
		try {
			IloCP cp = new IloCP();
			IloIntVar[][] assignedToVehicleVC = new IloIntVar[numVehicles][numCustomers];
			IloIntExpr[][] assignedToVehicleCV = new IloIntExpr[numCustomers][numVehicles];
			for (int v = 0; v < numVehicles; v++) {
				for (int c = 0; c < numCustomers; c++) {
					assignedToVehicleVC[v][c] = cp.intVar(0, 1);
					assignedToVehicleCV[c][v] = assignedToVehicleVC[v][c];
				}
				cp.add(cp.le(cp.prod(assignedToVehicleVC[v], demandOfCustomer), vehicleCapacity));
			}
			for (int c = 0; c < numCustomers; c++) {
				cp.add(cp.eq(cp.sum(assignedToVehicleCV[c]), 1));
			}
			if (cp.solve()) {
				List<List<Customer>> vehicleRoutesVC = new ArrayList<List<Customer>>(numVehicles);
				for (int v = 0; v < numVehicles; v++) {
					vehicleRoutesVC.add(new ArrayList<Customer>());
					for (int c = 0; c < numCustomers; c++) {
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
