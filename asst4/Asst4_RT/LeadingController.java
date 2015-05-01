import java.util.HashSet;


public class LeadingController extends VehicleController{
	
	public static final double BORDER = 20;
	
	private HashSet<Integer> vehicles;
	
	private double dist;
	private double[] followerPos;
	
	public LeadingController(Simulator sim, GroundVehicle gv){
		super(sim, gv);
	}
	
	@Override
	void constructionTasks(){
		vehicles = new HashSet<Integer>();
	}
	
	@Override
	public Control getControl(Timestamp time){
		if(this.vehicles.isEmpty()){
			return (Control) null;
		}
		
		
		double[] thispos;
		double[] thatpos;
		
		thispos = this.getPosition();
		if(thispos.length==0)
			return (Control) null;
		
		this.dist = Double.MAX_VALUE;
		
		for (Integer gva : this.vehicles){
			thatpos = getVehiclePosition(gva);
			
			double dist = util.getDist(thispos, thatpos);
			if(dist<this.dist){
				this.dist = dist;
				this.followerPos = thatpos;
			}
		}

		if(this.followerPos==null)
			return (Control) null;
		double heading = util.wrapAngle(util.getAngle(thispos, this.followerPos)+Math.PI);
		double headingErr = heading-thispos[2];
		
		if((thispos[0] < GroundVehicle.MIN_X+LeadingController.BORDER)||(thispos[0]>GroundVehicle.MAX_X-LeadingController.BORDER)||
				(thispos[1] < GroundVehicle.MIN_Y+LeadingController.BORDER)||(thispos[1]>GroundVehicle.MAX_Y-LeadingController.BORDER)){
			headingErr = GroundVehicle.MAX_THETA_DOT;
		}
		
		return new Control((this.dist/-29.0)+10, 10*headingErr);
	}
	
	public void addFollower(int gv){
		if(gv==this.getVehicleCode()){
			return;
		}
		vehicles.add(gv);
	}

	
}
