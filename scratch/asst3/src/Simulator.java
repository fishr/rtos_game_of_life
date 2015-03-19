import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public class Simulator extends Thread{
	
	/*Shared Resources*/
	private HashSet<VehicleController> vehicles;
	private Clock clk;
    Hashtable<Integer, Keyframe> schedules;
	
	public static final int SIM_STEP = 10000;
	public static final int SIM_UNITS = 1000000;
	public static final int MAX_RUNTIME = 30;
	private DisplayClient dc;
	
	public Simulator(){
		//this.vehicle = new GroundVehicle(temp, 5, 0);
		vehicles = new HashSet<VehicleController>();
		
		this.clk = new Clock(0,0, SIM_STEP);
		schedules = new Hashtable<Integer, Keyframe>();
	}
	
	public void incUsers(){
		this.clk.incUsers();
	}
	
	public void addGroundVehicle(GroundVehicle gv){
		//have a tally of the number of vehicle objects
		//increment upon adding vehicle
		//on start, load tally value into new counter
		//when each vehicle receives the time update,
		//it will decrement the counter to show that 
		//control has been calculated
		
		RandomController vc = new RandomController(this, gv);
		this.vehicles.add(vc);
	}
	
	public void addFollowVehicle(GroundVehicle gv, GroundVehicle leader){
		FollowingController vc = new FollowingController(this, gv, leader);
		this.vehicles.add(vc);
	}
	
	public Timestamp getTime(int sec, int usec){
		return this.clk.getTime(sec, usec);
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
				//vc.GroundVehicleUpdate(time);
				//TODO they told us to move this out of sim?
				
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
				gvtheta[i] = -(gvTheta.get(i));
			}
			
			dc.update(vehicle_num, gvx, gvy, gvtheta);

			this.clk.incClock();
			//call update states
			time = this.clk.getTime();
			Thread.yield();
		}
		
		//System.out.println("finished sim, exiting...");
	}
	
	void incClock(){
		this.clk.incClock();
	}
	
	Timestamp getTime(){
		return this.clk.getTime();
	}
	
	public static void main(String argv[]){
		Simulator sim = new Simulator();
		if(argv.length >0){
			try{
				sim.dc = new DisplayClient(argv[1]);
				sim.dc.clear();
				sim.dc.traceOn();
				
				if(argv.length>1){
					int arg1 = Integer.parseInt(argv[0]);
					if(arg1>0){
						double[] temp = {100*Math.random(),100*Math.random(),(2*Math.random()-1)*Math.PI};
						GroundVehicle gv = new GroundVehicle(temp, 10*Math.random(), Math.PI/2*(Math.random()-1/2), sim);
						sim.addGroundVehicle(gv);
						arg1--;
						for(int i =0; i<arg1; i++){
							double[] temp1 = {100*Math.random(),100*Math.random(),(2*Math.random()-1)*Math.PI};
							GroundVehicle gv2 = new GroundVehicle(temp1, 10*Math.random(), Math.PI/2*(Math.random()-1/2), sim);
							sim.addFollowVehicle(gv2, gv);
						}
					}else{
						throw new IllegalArgumentException("please input the number of vehicles");
					}
				}
			}
			catch(NumberFormatException e){
				System.out.println("input invalid");
			}
		}
		
		for(VehicleController v : sim.vehicles){
			v.start();
		}
		
		sim.run();
		
		System.exit(0);
	}
}
