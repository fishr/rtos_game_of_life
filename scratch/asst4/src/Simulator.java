import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Simulator implements Runnable{
	
	/*Shared Resources*/
	Set<VehicleController> vehicles;
	private ConcurrentHashMap<Integer, GroundVehicle> groundVehicles;
	private NonRTClock clk;
    Hashtable<Integer, Keyframe> schedules;
    
    public Boolean lock;
	
	public static final int SIM_STEP = 10;
	public static final int SIM_UNITS = 1000;
	public static final int MAX_RUNTIME = 100;
	DisplayClient dc;
	
	public Simulator(){
		//this.vehicle = new GroundVehicle(temp, 5, 0);
		this.vehicles = Collections.newSetFromMap(new ConcurrentHashMap<VehicleController, Boolean>());
		this.groundVehicles = new ConcurrentHashMap<Integer, GroundVehicle>(); 
		
		this.clk = new NonRTClock();
		this.schedules = new Hashtable<Integer, Keyframe>();
		
		this.lock = new Boolean(false);
	}
	
	public void addGroundVehicle(GroundVehicle gv){
		//have a tally of the number of vehicle objects
		//increment upon adding vehicle
		//on start, load tally value into new counter
		//when each vehicle receives the time update,
		//it will decrement the counter to show that 
		//control has been calculated
		
		VehicleController vc = new VehicleController(this, gv);
		__addgroundvehicle(vc, gv);
	}
	
	public void addCircleVehicle(GroundVehicle gv){
		CircleController cc = new CircleController(this, gv);
		__addgroundvehicle(cc,gv);
	}
	
	private synchronized void __addgroundvehicle(VehicleController vc, GroundVehicle gv){
			this.vehicles.add(vc);
			this.groundVehicles.put(gv.hashCode(), gv);
	}
	
	public void run(){
		Timestamp time = this.clk.getTime();
		List<Double> gvX = new ArrayList<Double>();
		List<Double> gvY = new ArrayList<Double>();
		List<Double> gvTheta = new ArrayList<Double>();
		
		while(time.sec<MAX_RUNTIME){
			int vehicle_num = 0;
			
			gvX.clear();
			gvY.clear();
			gvTheta.clear();
			
			for(VehicleController vc : this.vehicles){
				vehicle_num++;
				
				double[] pose = vc.getPosition();
				//System.out.format("%.2f %.2f %.2f %.1f%n", time.sec+time.usec/1000000.0, pose[0], pose[1], pose[2]*180.0/Math.PI);
				gvX.add(pose[0]);
				gvY.add(pose[1]);
				gvTheta.add(pose[2]);
			}
			double[] gvx = new double[vehicle_num];
			double[] gvy = new double[vehicle_num];
			double[] gvtheta = new double[vehicle_num];
			for(int i = 0; i<vehicle_num; i++){
				gvx[i] = gvX.get(i);
				gvy[i] = gvY.get(i);
				gvtheta[i] = (gvTheta.get(i));
			}
			
			dc.update(vehicle_num, gvx, gvy, gvtheta);


			if(Thread.currentThread().getClass().equals(RealtimeThread.class)){
				RealtimeThread.waitForNextPeriod();
			}else{
				this.clk.incClock();
			}
			//call update states
			time = this.clk.getTime();
		}
		
		//System.out.println("finished sim, exiting...");
	}
	
	void incClock(){
		this.clk.incClock();
	}
	
	Timestamp getTime(){
		return this.clk.getTime();
	}
	
	public int getGroundVehicleCount(){
		return this.groundVehicles.size();
	}

	public int getVehicleCount() {
		return this.vehicles.size();
	}

	public double[] getVehiclePosition(int hash) {
		GroundVehicle gv = this.groundVehicles.get(hash);
		return gv.getPosition();
	}
}
