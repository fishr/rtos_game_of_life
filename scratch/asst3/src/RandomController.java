
public class RandomController extends VehicleController
{
	public RandomController(Simulator sim, GroundVehicle gv){
		super(sim, gv);
	}
	
	@Override
	void constructionTasks(){}
	
	@Override
	public Control getControl(Timestamp time){
		if(time.usec!=0)
			return null;
		double randomS = Math.random()*5.0+5.0;
		double randomTheta = (Math.random()*4.0-1.0)*Math.PI/4;
		return new Control(randomS, randomTheta);
	}
}
