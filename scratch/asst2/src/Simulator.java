import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public class Simulator {
	
	private HashSet<VehicleController> vehicles;
	private Clock clk;
    Hashtable<Integer, Keyframe> schedules;
	
	private final int SIM_STEP = 100;
	private DisplayClient dc;
	
	public Simulator(){
		//this.vehicle = new GroundVehicle(temp, 5, 0);
		vehicles = new HashSet<VehicleController>();
		
		this.clk = new Clock(0,0, this.SIM_STEP);
		schedules = new Hashtable<Integer, Keyframe>();
	}
	
	public void addGroundVehicle(GroundVehicle gv){
		//have a tally of the number of vehicle objects
		//increment upon adding vehicle
		//on start, load tally value into new counter
		//when each vehicle receives the time update,
		//it will decrement the counter to show that 
		//control has been calculated
		
		VehicleController vc = new VehicleController(this, gv);
		this.vehicles.add(vc);
		
		this.clk.incUsers();
	}
	
	public Clock.Timestamp getTime(int sec, int usec){
		return this.clk.getTime(sec, usec);
	}
	
	void run(){
		Clock.Timestamp time = this.clk.getTime();
		List<Double> gvX = new ArrayList<Double>();
		List<Double> gvY = new ArrayList<Double>();
		List<Double> gvTheta = new ArrayList<Double>();
		
		while(time.sec<100){
			int vehicle_num = 0;
			
			gvX.clear();
			gvY.clear();
			gvTheta.clear();
			
			for(VehicleController vc : this.vehicles){
				vehicle_num++;
				vc.GroundVehicleUpdate(time);
				
				double[] pose = vc.getPosition();
				//System.out.format("%.2f %.2f %.2f %.1f%n", time.sec+time.usec/1000000.0, pose[0], pose[1], pose[2]*180.0/Math.PI);
				gvX.add(pose[0]);
				gvY.add(pose[1]);
				gvTheta.add(pose[2]*180.0/Math.PI);
			}
			double[] gvx = new double[vehicle_num];
			double[] gvy = new double[vehicle_num];
			double[] gvtheta = new double[vehicle_num];
			for(int i = 0; i<vehicle_num; i++){
				gvx[i] = gvX.get(i);
				gvy[i] = gvY.get(i);
				gvtheta[i] = gvTheta.get(i);
			}
			
			dc.update(vehicle_num, gvx, gvy, gvtheta);

			this.clk.incClock();
			//call update states
			time = this.clk.getTime();
			Thread.yield();
		}
		
		//System.out.println("finished sim, exiting...");
	}
	
	public static void main(String argv[]){
		Simulator sim = new Simulator();
		if(argv.length >0){
			try{
				sim.dc = new DisplayClient(argv[0]);
				
				if(argv.length>1){
					double[] temp = {75,25,0};
					for(int i =0; i<Integer.parseInt(argv[1]); i++){
						GroundVehicle gv = new GroundVehicle(temp, 5, 0);
						sim.addGroundVehicle(gv);
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
