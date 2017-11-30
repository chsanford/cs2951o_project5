package solver.ls;

public class Customer {
	
	double xCoord;
	double yCoord;
	int demand;
	int number;
	
	public Customer(double xCoord, double yCoord, int demand, int number) {
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.demand = demand;
		this.number = number;
	}
	
	public double distanceTo(Customer c) {
		return Math.sqrt(
				Math.pow(this.xCoord - c.xCoord, 2) +
				Math.pow(this.yCoord - c.yCoord, 2));
	}
	
	public double distanceTo(double xCoord, double yCoord) {
		return Math.sqrt(
				Math.pow(this.xCoord - xCoord, 2) +
				Math.pow(this.yCoord - yCoord, 2));
	}

}
