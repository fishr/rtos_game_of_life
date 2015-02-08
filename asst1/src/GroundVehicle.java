public class GroundVehicle {
	
	private final double MIN_X = 0;
	private final double MAX_X = 100;
	private final double MIN_Y = 0;
	private final double MAX_Y = 100;
	private final double MIN_S_DOT = 5;
	private final double MAX_S_DOT = 10;
	private final double MIN_THETA_DOT = -Math.PI/4.0;
	private final double MAX_THETA_DOT = Math.PI/4.0;

	private double[] speeds;
	private double[] pose;
	
	public GroundVehicle(double[] pose, double s, double omega){
		assert(pose.length==3);
		assert(util.withinBounds(pose[0], MIN_X, MAX_X));
		assert(util.withinBounds(pose[1], MIN_Y, MAX_Y));
		assert((pose[2]>=-Math.PI)&&(pose[2]<Math.PI));
		
		this.pose = new double[3];
		this.pose[0]=pose[0];
		this.pose[1]=pose[1];
		this.pose[2]=pose[2];
		
		assert(util.withinBounds(s, MIN_S_DOT, MAX_S_DOT));
		assert(util.withinBounds(omega, MIN_THETA_DOT, MAX_THETA_DOT));
		
		this.speeds = new double[2];
		this.speeds[0]=s;
		this.speeds[1]=omega;
	}
	
	public double[] getPostion(){
		return pose;
	}
	
	public double[] getVelocity(){
		double[] temp = new double[3];
		temp[0]=speeds[0]*Math.cos(pose[2]);
		temp[1]=speeds[0]*Math.sin(pose[2]);
		temp[2]=speeds[1];
		return temp;
	}
	
	public void setPosition(double[] pose){
		assert(pose.length==3);
		
		this.pose[0] = util.clampDouble(pose[0], MIN_X, MAX_X);
		this.pose[1] = util.clampDouble(pose[1], MIN_Y, MAX_Y);
		this.pose[2] = util.wrapAngle(pose[2]);
	}
	
	public void setVelocity(double[] vels){
		assert(vels.length==3);
		
		this.speeds[1] = util.clampDouble(vels[2], MIN_THETA_DOT, MAX_THETA_DOT);
		//though not specified in the requirements, the example updateState suggests
		//the vehicle only moves in the direction it is facing.  It seems more reasonable
		//to adjust the direction faced than to change the direction of the incoming velocities,
		//or to have a cycle that allows for "slip."
		if(vels[0]==0){
			if(vels[1]!=0){
				this.pose[2]=Math.PI*Math.signum(vels[1]); 	//careful, need to sanitize inputs first to make
			}										//its ok if we get zero velocity inputs
		}else if(vels[0]>0){
			this.pose[2]=Math.atan(vels[1]/vels[0]);
		}else{
			this.pose[2]=util.wrapAngle(Math.atan(vels[1]/vels[0])+Math.PI); //shift these guys to the left half plane
		}

		//If input velocities do not conform in the worst way 
		//(ie both 0), the result will be the minimum allowed velocity in the current direction
		
		double s = Math.sqrt(vels[0]*vels[0]+vels[1]*vels[1]);
		Control c = new Control(s, vels[2]);
		controlVehicle(c);
	}
	
	public void controlVehicle(Control c){
		if(!util.withinBounds(c.getSpeed(), MIN_S_DOT, MAX_S_DOT)){
			if(c.getSpeed()>MAX_S_DOT){
				this.speeds[0]=MAX_S_DOT;
			}else if(c.getSpeed()<MIN_S_DOT){
				this.speeds[0]=MIN_S_DOT;
			}
		}else{
			this.speeds[0]=c.getSpeed();
		}
		this.speeds[1]=util.clampDouble(c.getRotVel(), MIN_THETA_DOT, MAX_THETA_DOT);
	}
	
	public void updateState(int sec, int msec){
		//FOR PHYSICS ENGINE:  THE ROTATIONAL VELOCITY GIVES A DURATION UNTIL A FULL CIRCLE IS MADE
		//USE THIS DURATION PLUS THE TRANSLATIONAL VELOCITY MAGNITUDE TO DETERMINE DIAMETER OF INSCRIBED
		//CIRCLE.  THEN USE TIME INTERVAL TO DETERMINE ARC SEGMENT, AND CALCULATE XY COORD OF CIRCLE
		
		
	}
}
