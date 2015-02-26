public class Control {
	
	private double s;
	private double omega;
	
	public Control(double s, double omega){
		this.s = util.clampDouble(s, GroundVehicle.MIN_S_DOT, GroundVehicle.MAX_S_DOT);;
		
		this.omega=util.clampDouble(omega, GroundVehicle.MIN_THETA_DOT, GroundVehicle.MAX_THETA_DOT);
	}
	
	double getSpeed(){
		return this.s;
	}

	double getRotVel(){
		return this.omega;
	}
}
