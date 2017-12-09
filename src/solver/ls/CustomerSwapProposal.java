package solver.ls;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Chooses two customers at random and swaps their positions in vehicles.
 * These customers can be in the same or different vehicles.
 * @author chsanfor
 *
 */
public class CustomerSwapProposal implements Proposal {

	@Override
	public VehicleConfiguration proposal(VehicleConfiguration vc) {
		// Gets a number representing a non-depot customer
		int customerIndex1 = ThreadLocalRandom.current().nextInt(1, vc.numCustomers);
		// Gets a different number representing a non-depot customer
		int customerIndex2 = ThreadLocalRandom.current().nextInt(1, vc.numCustomers);
		while (customerIndex2 == customerIndex1) {
			customerIndex2 = ThreadLocalRandom.current().nextInt(1, vc.numCustomers);
		}
		
		List<List<Customer>> proposedVehicleRoutesVC = new ArrayList<>();
		for (List<Customer> vehicleRouteC : vc.vehicleRoutesVC) {
			if (vehicleRouteC.contains(vc.customers[customerIndex1 - 1]) ||
					vehicleRouteC.contains(vc.customers[customerIndex2 - 1])) {
				List<Customer> newRoute = new ArrayList<>();
				for (Customer c : vehicleRouteC) {
					if (c.number == customerIndex1) {
						newRoute.add(vc.customers[customerIndex2 - 1]);
					} else if (c.number == customerIndex2) {
						newRoute.add(vc.customers[customerIndex1 - 1]);
					} else {
						newRoute.add(c);
					}
				}
				proposedVehicleRoutesVC.add(newRoute);
			} else {
				proposedVehicleRoutesVC.add(vehicleRouteC);
			}
		}

		return new VehicleConfiguration(vc.vrp, proposedVehicleRoutesVC);
	}

}
