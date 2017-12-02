package solver.ls;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RouteReversalProposal implements Proposal {

	@Override
	public VehicleConfiguration proposal(VehicleConfiguration vc) {
		// Gets a vehicle to whose route to reverse (size must be greater than 0)
		int vehicleIndex = ThreadLocalRandom.current().nextInt(0, vc.numVehicles);
		while (vc.vehicleRoutesVC.get(vehicleIndex).size() == 0) {
			vehicleIndex = ThreadLocalRandom.current().nextInt(0, vc.numVehicles);
		}
		// Get the indices to reverse between
		int routeIndex1 = ThreadLocalRandom.current().nextInt(0, vc.vehicleRoutesVC.get(vehicleIndex).size());
		int routeIndex2 = ThreadLocalRandom.current().nextInt(0, vc.vehicleRoutesVC.get(vehicleIndex).size());
		// Ensure that routeIndex1 comes first
		if (routeIndex1 > routeIndex2) {
			int temp = routeIndex1;
			routeIndex1 = routeIndex2;
			routeIndex2 = temp;
		}
		
		List<List<Customer>> proposedVehicleRoutesVC = new ArrayList<>();
		for (int v = 0; v < vc.numVehicles; v++) {
			List<Customer> newRoute = new ArrayList<>();
			List<Customer> currentRoute = vc.vehicleRoutesVC.get(v);
			for (int c = 0; c < currentRoute.size(); c++) {
				if (v != vehicleIndex || c < routeIndex1 || c > routeIndex2) {
					newRoute.add(currentRoute.get(c));
				} else {
					newRoute.add(currentRoute.get(routeIndex2 + routeIndex1 - c));
				}
			}
			proposedVehicleRoutesVC.add(newRoute);
		}
		
		return new VehicleConfiguration(vc.vrp, proposedVehicleRoutesVC);
	}

}
