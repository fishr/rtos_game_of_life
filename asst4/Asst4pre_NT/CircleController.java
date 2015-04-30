
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
		return new Control(10, 0.4+offset);
	}
}
