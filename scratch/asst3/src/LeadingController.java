import java.util.HashSet;


public class LeadingController extends VehicleController{
	
	private HashSet<GroundVehicle> vehicles;
	
	private double dist;
	private double[] followerPos;
	
	public LeadingController(Simulator sim, GroundVehicle gv){
		super(sim, gv);
	}
	
	@Override
	void constructionTasks(){
		vehicles = new HashSet<GroundVehicle>();
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
		
		for (GroundVehicle gva : this.vehicles){
/*
			synchronized(sim.lock){
				while(sim.lock){
					try{
						sim.lock.wait();
					}catch (InterruptedException e){
						e.printStackTrace();
					}
				}
				sim.lock = true;
			}
			
			if(gv.hashCode()<this.v.hashCode()){
				
			}
			
			sim.lock = false;
			sim.lock.notify();*/
			thatpos = gva.getPosition();
			//above is the only access that needs to be protected
			
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
		return new Control((this.dist/-29.0)+10, 10*headingErr);
	}
	
	public void addFollower(GroundVehicle gv){
		if(gv.hashCode()==this.getVehicleCode()){
			return;
		}
		vehicles.add(gv);
	}

	
}
