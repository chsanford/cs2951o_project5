package solver.ls;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Chooses a customer at random and moves them to a random position.
 * @author chsanfor
 *
 */
public class CustomerMoveProposal implements Proposal {

	@Override
	public VehicleConfiguration proposal(VehicleConfiguration vc) {
		// Gets a number representing a non-depot customer
		int customerIndex = ThreadLocalRandom.current().nextInt(1, vc.numCustomers);
		// Gets a vehicle to place the customer in
		int vehicleIndex = ThreadLocalRandom.current().nextInt(0, vc.numVehicles);
		
		List<List<Customer>> proposedVehicleRoutesVC = new ArrayList<>();
		for (List<Customer> vehicleRouteC : vc.vehicleRoutesVC) {
			List<Customer> newRoute = new ArrayList<>();
			for (Customer c : vehicleRouteC) {
				if (c.number != customerIndex) {
					newRoute.add(c);
				}
			}
			proposedVehicleRoutesVC.add(newRoute);
		}
		List<Customer> routeToMove = proposedVehicleRoutesVC.get(vehicleIndex);
		int insertionIndex = ThreadLocalRandom.current().nextInt(0, routeToMove.size() + 1);
		routeToMove.add(insertionIndex, vc.customers[customerIndex - 1]);
		
		return new VehicleConfiguration(vc.vrp, proposedVehicleRoutesVC);
	}

}
