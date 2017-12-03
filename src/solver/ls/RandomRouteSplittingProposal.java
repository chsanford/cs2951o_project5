package solver.ls;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Randomly select two vehicles, split and swap customers on the routes selected.
 * @author rbaker2
 *
 */
public class RandomRouteSplittingProposal implements Proposal  {
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
		int randomCustomer1vehicle1 = ThreadLocalRandom.current().nextInt(0, vehicleOriginalRoute1.size() + 1);
		int randomCustomer2vehicle1 = ThreadLocalRandom.current().nextInt(0, vehicleOriginalRoute1.size() + 1);
		int randomCustomer1vehicle2 = ThreadLocalRandom.current().nextInt(0, vehicleOriginalRoute2.size() + 1);
		int randomCustomer2vehicle2 = ThreadLocalRandom.current().nextInt(0, vehicleOriginalRoute2.size() + 1);
		// Make sure the customer 1 index value is smaller than the customer 2 index.
		// This will just make the  calculation a lot easier to manage later on.
		int temp = 0;	   
		if (randomCustomer1vehicle1 > randomCustomer2vehicle1) 
		{
			temp = randomCustomer2vehicle1;
			randomCustomer2vehicle1 = randomCustomer1vehicle1; 
			randomCustomer1vehicle1 = temp;
		}
		if (randomCustomer1vehicle2 > randomCustomer2vehicle2) 
		{
			temp = randomCustomer2vehicle2;
			randomCustomer2vehicle2 = randomCustomer1vehicle2;
			randomCustomer1vehicle2 = temp;
		}

		// The customers within a range will go into one array, then the values out side 
		// the  other range will go into another array.

		List<Customer> vehicleNewRoute1 = new ArrayList<>();
		List<Customer> vehicleNewRoute2 = new ArrayList<>();	
		
		for (int i = 0; i < vc.vehicleRoutesVC.get(vehicleIndex1).size(); i++) 
		{
			vehicleNewRoute1.add(vc.vehicleRoutesVC.get(vehicleIndex1).get(i));
			
			if ( i == randomCustomer1vehicle1 ) 
			{
				for (int j = randomCustomer1vehicle2; j <= randomCustomer2vehicle2; j++) 
				{
					vehicleNewRoute1.add(vc.vehicleRoutesVC.get(vehicleIndex2).get(j));
				}
				
				i = randomCustomer2vehicle1;
			}
		}

		for (int i = 0; i < vc.vehicleRoutesVC.get(vehicleIndex2).size(); i++) 
		{
			vehicleNewRoute2.add(vc.vehicleRoutesVC.get(vehicleIndex2).get(i));
			
			if ( i == randomCustomer1vehicle2 ) 
			{
				for (int j = randomCustomer1vehicle1; j <= randomCustomer2vehicle1; j++) 
				{
					vehicleNewRoute2.add(vc.vehicleRoutesVC.get(vehicleIndex1).get(j));
				}
				
				i = randomCustomer2vehicle2;
			}
		}
		
		// Empty the list that are currently set
		vc.vehicleRoutesVC.get(vehicleIndex1).clear();		
		// Fill the new list structure.
		for (Customer c : vehicleNewRoute1) 
		{
			vc.vehicleRoutesVC.get(vehicleIndex1).add(c);
		}

		// Empty the list that are currently set
		vc.vehicleRoutesVC.get(vehicleIndex2).clear();
		// Fill the new list structure.
		for (Customer c : vehicleNewRoute2) 
		{
			vc.vehicleRoutesVC.get(vehicleIndex2).add(c);
		}
		
		// Copy original configuration
		List<List<Customer>> proposedVehicleRoutesVC = new ArrayList<>();
		
		for (List<Customer> vehicleRouteC : vc.vehicleRoutesVC) 
		{
			proposedVehicleRoutesVC.add(vehicleRouteC);	
		}
		
		return new VehicleConfiguration(vc.vrp, proposedVehicleRoutesVC);		
	}
}
