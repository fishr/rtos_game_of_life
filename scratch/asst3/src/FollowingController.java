public class FollowingController extends VehicleController {

	GroundVehicle leader;
	
	public FollowingController(Simulator s, GroundVehicle gv, GroundVehicle leader){
		super(s, gv);
		
		this.leader = leader;
	}
	
	@Override
	void constructionTasks(){}
	
	@Override
	public Control getControl(Timestamp time){
		double[] thisPose = this.v.getPosition();
		double[] thatPose = this.leader.getPosition();
		double dist = (thisPose[0]-thatPose[0])*(thisPose[0]-thatPose[0])+(thisPose[1]-thatPose[1])*(thisPose[1]-thatPose[1]);
		double angle = 0;
		if((thisPose[0]-thatPose[0])==0.0){
			angle = Math.PI/4*Math.signum((thatPose[1]-thisPose[1]));
		}else{
			angle= Math.atan((thisPose[1]-thatPose[1])/(thisPose[0]-thatPose[0]));
			if(thatPose[0]-thisPose[0]<0.0){
				angle+=Math.signum(thatPose[1]-thisPose[1])*Math.PI;
				if(thatPose[1]-thisPose[1]==0.0){
					angle=-Math.PI;
				}
			}
		}
		double angErr = angle-thisPose[2];
		
		return new Control(dist, angErr);
	}
}
