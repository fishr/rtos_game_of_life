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
		double[] thisPose = this.getPosition();
		double[] thatPose = this.leader.getPosition();
		double dist = util.getDist(thisPose, thatPose);
		double angErr = util.getAngleErr(thisPose, thatPose);
		
		return new Control(dist, angErr);
	}
}
