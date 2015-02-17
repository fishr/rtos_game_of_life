public class GroundVehicle {
	
	private final static double MIN_X = 0;
	private final static double MAX_X = 100;
	private final static double MIN_Y = 0;
	private final static double MAX_Y = 100;
	public final static double MIN_S_DOT = 5;
	public final static double MAX_S_DOT = 10;
	public final static double MIN_THETA_DOT = -Math.PI/4.0;
	public final static double MAX_THETA_DOT = Math.PI/4.0;

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
	
	double[] getPosition(){
		return pose.clone();
	}
	
	double[] getVelocity(){
		double[] temp = new double[3];
		temp[0]=speeds[0]*Math.cos(pose[2]);
		temp[1]=speeds[0]*Math.sin(pose[2]);
		temp[2]=speeds[1];
		return temp;
	}
	
	void setPosition(double[] pose){
		assert(pose.length==3);
		
		this.pose[0] = util.clampDouble(pose[0], MIN_X, MAX_X);
		this.pose[1] = util.clampDouble(pose[1], MIN_Y, MAX_Y);
		this.pose[2] = util.wrapAngle(pose[2]);
	}
	
	void setVelocity(double[] vels){
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
			this.pose[2]=Math.atan(vels[1]/vels[0]);  //thankfully atan handles over and underflow pretty well
		}else{
			this.pose[2]=util.wrapAngle(Math.atan(vels[1]/vels[0])+Math.PI); //shift these guys to the left half plane
		}

		//If input velocities do not conform in the worst way 
		//(ie both 0), the result will be the minimum allowed velocity in the current direction
		
		double s = Math.sqrt(vels[0]*vels[0]+vels[1]*vels[1]);
		Control c = new Control(s, vels[2]);
		controlVehicle(c);
	}
	
	void controlVehicle(Control c){
		if(c==null){
			return;
		}
		
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
	
	void updateState(int sec, int msec){
		assert(sec>=0);
		assert(msec>=0);
		//FOR PHYSICS ENGINE:  THE ROTATIONAL VELOCITY GIVES A DURATION UNTIL A FULL CIRCLE IS MADE
		//USE THIS DURATION PLUS THE TRANSLATIONAL VELOCITY MAGNITUDE TO DETERMINE DIAMETER OF INSCRIBED
		//CIRCLE.  THEN USE TIME INTERVAL TO DETERMINE ARC SEGMENT, AND CALCULATE XY COORD OF CIRCLE
		double s = this.speeds[0];
		double omega = this.speeds[1];
		
		double[] inPose = getPosition();
		double[] returnPose = new double[3];
		
		if(Math.abs(omega)<=(s*2*Math.PI/Double.MAX_VALUE)){
			double dist = s*sec+s*msec/1000.0;
			returnPose[0] = inPose[0] + Math.cos(inPose[2])*dist;
			returnPose[1] = inPose[1] + Math.sin(inPose[2])*dist;
			returnPose[2] = inPose[2];
		}else{
			double timePerCycle = 2*Math.PI/omega;
			double circumference = s*timePerCycle;  //may be up to MAX_VAL
			double arc = (omega*sec+omega*msec/1000.0)%(2*Math.PI);
			double radius = circumference/(2*Math.PI); 
			
			double dx = Math.cos(inPose[2]+Math.PI*Math.signum(radius)/2.0)*radius;  //these values may get large enough
			double dy = Math.sin(inPose[2]+Math.PI*Math.signum(radius)/2.0)*radius;  //that they no longer work as deltas
																					//may consider broadening "straight line"
			double center_x = inPose[0]+dx;
			double center_y = inPose[1]+dy;
			double end_ang = inPose[2]+arc-Math.PI/2.0;
			
			returnPose[0] = center_x+Math.cos(end_ang)*radius;
			returnPose[1] = center_y+Math.sin(end_ang)*radius;
			returnPose[2] += arc;
		}
		setPosition(returnPose);
	}
}
