
public class util {

	public static double clampDouble(double input, double lower, double upper){
		assert(lower<=upper);
		return Math.max(Math.min(input, upper), lower);
	}

	public static double wrapAngle(double d) {
		double tempTheta = (d%(2*Math.PI));
		if(tempTheta>=Math.PI){
			tempTheta=tempTheta-(2*Math.PI);
		}
		assert((tempTheta>=-Math.PI)&&(tempTheta<Math.PI));
		return tempTheta;
	}
	
	public static boolean withinBounds(double input, double lower, double upper){
		return (clampDouble(input, lower, upper)==input);
	}
}
