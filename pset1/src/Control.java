public class Control {
	
	private double s;
	private double omega;
	
	public Control(double s, double omega){
		assert((s>=5)&&(s<=10));
		this.s = s;
		
		assert((omega>=-Math.PI)&&(omega<=Math.PI));
		this.omega=omega;
	}
	
	public double getSpeed(){
		return this.s;
	}

	public double getRotVel(){
		return this.omega;
	}
}
