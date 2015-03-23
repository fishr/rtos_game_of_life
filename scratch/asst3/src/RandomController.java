
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
		double randomS = Math.random()*(GroundVehicle.MAX_S_DOT-GroundVehicle.MIN_S_DOT)+GroundVehicle.MIN_S_DOT;
		double randomTheta = Math.random()*(GroundVehicle.MAX_THETA_DOT-GroundVehicle.MIN_THETA_DOT)+GroundVehicle.MIN_THETA_DOT;
		return new Control(randomS, randomTheta);
	}
}
