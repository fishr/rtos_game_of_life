
public class CircleController extends VehicleController{

	public CircleController(Simulator s, GroundVehicle v) {
		super(s, v);
	}
	
	@Override
	void constructionTasks(){
		
	}
	
	@Override
	public Control getControl(Timestamp time){
		double[] thisPose = this.getPosition();
		double offset=Math.sqrt((thisPose[0]-50)*(thisPose[0]-50)+(thisPose[1]-50)*(thisPose[1]-50))-25;
		double thetaErr = util.wrapAngle(Math.atan2(thisPose[1]-50, thisPose[0]-50)+Math.PI/2-thisPose[2]);
		return new Control(10, 0.4+offset/25+(1-offset/25)*thetaErr);
	}
}
