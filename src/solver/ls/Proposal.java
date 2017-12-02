package solver.ls;

/**
 * When implemented, generates proposals for a given VehicleConfiguration.
 * @author chsanfor
 */
public interface Proposal {
	
	public VehicleConfiguration proposal(VehicleConfiguration vc);

}
