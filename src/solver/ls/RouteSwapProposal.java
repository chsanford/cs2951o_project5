package solver.ls;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RouteSwapProposal implements Proposal {

	@Override
	public VehicleConfiguration proposal(VehicleConfiguration vc) {
		// Gets a vehicle to place the customer in
		int vehicleIndex1 = ThreadLocalRandom.current().nextInt(0, vc.numVehicles);
		// Gets a vehicle to place the customer in
		int vehicleIndex2 = ThreadLocalRandom.current().nextInt(0, vc.numVehicles);
		// Ensure that both of the index that were selected are not the same.
		while (vehicleIndex1 == vehicleIndex2)
		{
			vehicleIndex2 = ThreadLocalRandom.current().nextInt(0, vc.numVehicles);
		}

		// Get the two route from the listing
		List<Customer> vehicleOriginalRoute1 = vc.vehicleRoutesVC.get(vehicleIndex1);
		List<Customer> vehicleOriginalRoute2 = vc.vehicleRoutesVC.get(vehicleIndex2);

		// Randomly select an index from the two
		int splitCustomer1 = ThreadLocalRandom.current().nextInt(0, vehicleOriginalRoute1.size() + 1);
		int splitCustomer2 = ThreadLocalRandom.current().nextInt(0, vehicleOriginalRoute2.size() + 1);
		
		boolean flipVehicle2 = ThreadLocalRandom.current().nextBoolean();
		
		List<Customer> newRoute1 = new ArrayList<>();
		List<Customer> newRoute2 = new ArrayList<>();
		for (int c = 0; c < splitCustomer1; c++) {
			newRoute1.add(vehicleOriginalRoute1.get(c));
		}
		if (flipVehicle2) {
			for (int c = splitCustomer2 - 1; c >= 0; c--) {
				newRoute1.add(vehicleOriginalRoute2.get(c));
			}
			for (int c = vehicleOriginalRoute2.size() - 1; c >= splitCustomer2; c--) {
				newRoute2.add(vehicleOriginalRoute2.get(c));
			}
		} else {
			for (int c = splitCustomer2; c < vehicleOriginalRoute2.size(); c++) {
				newRoute1.add(vehicleOriginalRoute2.get(c));
			}
			for (int c = 0; c < splitCustomer2; c++) {
				newRoute2.add(vehicleOriginalRoute2.get(c));
			}
		}
		for (int c = splitCustomer1; c < vehicleOriginalRoute1.size(); c++) {
			newRoute2.add(vehicleOriginalRoute1.get(c));
		}

		List<List<Customer>> proposedVehicleRoutesVC = new ArrayList<>();

		for (int v = 0; v < vc.numVehicles; v++) {
			if (v != vehicleIndex1 && v != vehicleIndex2) {
				//List<Customer> newRoute = new ArrayList<>();
				List<Customer> currentRoute = vc.vehicleRoutesVC.get(v);

				//for (int c = 0; c < currentRoute.size(); c++) {
				//	newRoute.add(currentRoute.get(c));
				//}
				//proposedVehicleRoutesVC.add(newRoute);
				proposedVehicleRoutesVC.add(currentRoute);
			}
		}

		proposedVehicleRoutesVC.add(newRoute1);
		proposedVehicleRoutesVC.add(newRoute2);
		
		return new VehicleConfiguration(vc.vrp, proposedVehicleRoutesVC);		
	}
}
