
public class util {

	public static double clampDouble(double input, double lower, double upper){
		assert(lower<=upper);
		return Math.max(Math.min(input, upper), lower);
	}
	
	public static int clampInt(int input, int lower, int upper){
		assert(lower<=upper);
		return Math.max(Math.min(input, upper), lower);
	}

	public static double wrapAngle(double d) {
		assert(Double.isInfinite(d)||Double.isNaN(d));
		double tempTheta = (d%(2*Math.PI));
		if(tempTheta>=Math.PI){
			tempTheta=tempTheta-(2*Math.PI);
		}else if(tempTheta<-Math.PI){
			tempTheta=tempTheta+(2*Math.PI);
		}
		assert((tempTheta>=-Math.PI)&&(tempTheta<Math.PI)):Double.toString(tempTheta);
		return tempTheta;
	}
	
	public static boolean withinBounds(double input, double lower, double upper){
		return (clampDouble(input, lower, upper)==input);
	}
	
	public static boolean withinBounds(int input, int lower, int upper){
		return (clampInt(input, lower, upper)==input);
	}
	
	public static double getAngleErr(double[] thisPose, double[] thatPose){
		double angle = 0;
		if((thisPose[0]-thatPose[0])==0.0){
			angle = Math.PI/4*Math.signum((thatPose[1]-thisPose[1]));
		}else{
			angle= Math.atan((thatPose[1]-thisPose[1])/(thatPose[0]-thisPose[0]));
			if(thatPose[0]-thisPose[0]<0.0){
				angle+=Math.signum(thatPose[1]-thisPose[1])*Math.PI;
				if(thatPose[1]-thisPose[1]==0.0){
					angle=-Math.PI;
				}
			}
		}
		return util.wrapAngle(util.wrapAngle(angle)-thisPose[2]);
	}
	
	public static double getAngle(double[] thisPose, double[] thatPose){
		double angle = 0;
		if((thisPose[0]-thatPose[0])==0.0){
			angle = Math.PI/4*Math.signum((thatPose[1]-thisPose[1]));
		}else{
			angle= Math.atan((thatPose[1]-thisPose[1])/(thatPose[0]-thisPose[0]));
			if(thatPose[0]-thisPose[0]<0.0){
				angle+=Math.signum(thatPose[1]-thisPose[1])*Math.PI;
				if(thatPose[1]-thisPose[1]==0.0){
					angle=-Math.PI;
				}
			}
		}
		return util.wrapAngle(angle);
	}
	
	public static double getDist(double[] thisPose, double[] thatPose){
		double dist = (thisPose[0]-thatPose[0])*(thisPose[0]-thatPose[0])+(thisPose[1]-thatPose[1])*(thisPose[1]-thatPose[1]);
		
		return Math.sqrt(dist);
	}
}
