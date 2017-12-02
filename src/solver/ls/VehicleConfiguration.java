package solver.ls;

import java.util.List;

public class VehicleConfiguration {
	
	
	VRPInstance vrp;
	
	int numCustomers;        		// the number of customers	   
	int numVehicles;           	// the number of vehicles
	int vehicleCapacity;			// the capacity of the vehicles
	Customer[] customers;
	Customer depot;
	
	List<List<Customer>> vehicleRoutesVC;
	int[] vehicleDemandV;
	boolean satisfiesCapacity;
	double[] vehicleDistanceV;
	double totalDistance;
	boolean isOptimal = false;
	
	public VehicleConfiguration(VRPInstance vrp, List<List<Customer>> vehicleRoutesVC) {
		this.vrp = vrp;
		
		numCustomers = vrp.numCustomers;
		numVehicles = vrp.numVehicles;
		vehicleCapacity = vrp.vehicleCapacity;
		customers = vrp.customers;
		depot = vrp.depot;
		
		this.vehicleRoutesVC = vehicleRoutesVC;
		vehicleDemandV = new int[numVehicles];
		vehicleDistanceV = new double[numVehicles];
		satisfiesCapacity = true;
		for (int v = 0; v < numVehicles; v++) {
			for (int c = 0; c < vehicleRoutesVC.get(v).size(); c++) {
				vehicleDemandV[v] += vehicleRoutesVC.get(v).get(c).demand;
				if (c == 0) {
					vehicleDistanceV[v] += vehicleRoutesVC.get(v).get(c).distanceTo(depot);
				} else {
					vehicleDistanceV[v] += vehicleRoutesVC.get(v).get(c).distanceTo(
							vehicleRoutesVC.get(v).get(c - 1));
				}
				if (c == vehicleRoutesVC.get(v).size() - 1) {
					vehicleDistanceV[v] += vehicleRoutesVC.get(v).get(c).distanceTo(depot);
				}
			}
			satisfiesCapacity = satisfiesCapacity && (vehicleDemandV[v] <= vehicleCapacity);
			totalDistance += vehicleDistanceV[v];
		}
	}
	
	

}
