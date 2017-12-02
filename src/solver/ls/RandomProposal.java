package solver.ls;

/**
 * Chooses a proposal uniformly at random from several.
 * @author chsanfor
 *
 */
public class RandomProposal implements Proposal {
	
	Proposal[] props;
	
	public RandomProposal(Proposal[] props) {
		this.props = props;
	}

	@Override
	public VehicleConfiguration proposal(VehicleConfiguration vc) {
		return props[(int) Math.floor(Math.random() * props.length)].proposal(vc);
	}

}
