public class GroundVehicle {

	private double[] speeds;
	private double[] pose;
	
	public GroundVehicle(double[] pose, double s, double omega){
		assert(pose.length>3);
		assert((pose[0]>=0)&&(pose[0]<=100));
		assert((pose[1]>=0)&&(pose[1]<=100));
		assert((pose[2]>=-Math.PI)&&(pose[2]<Math.PI));
		
		this.pose = new double[3];
		this.pose[0]=pose[0];
		this.pose[1]=pose[1];
		this.pose[2]=pose[2];
		
		assert((s>=5)&&(s<=10));
		assert((omega>=-Math.PI/4.0)&&(omega<=Math.PI/4.0));
		
		this.speeds = new double[2];
		this.speeds[0]=s;
		this.speeds[1]=omega;
	}
}
