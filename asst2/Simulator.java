import java.util.Hashtable;

public class Simulator {
	
	private GroundVehicle vehicle;
	private int sec;
	private int msec;
	private int sides;
	private long loop_time;
	private Hashtable<Long, Control> bookmarks;  //stores controls keyed by the hundreths of seconds
	
	private final int MIN_SIDES = 3;
	private final int MAX_SIDES = 10;
	private final int SIM_STEP = 10;
	
	public Simulator(){
		double[] temp = {75,25,0};
		this.vehicle = new GroundVehicle(temp, 5, 0);
		
		this.sec=0;
		this.msec=0;
		bookmarks = new Hashtable<Long, Control>();
		bookmarks.clear();
		this.sides=5;
		this.setNumSides(5);
	}
	
	int getCurrentSec(){
		//TODO
		return this.sec;
	}
	
	int getCurrentMSec(){
		//TODO
		return this.msec;
	}
	
	public void addGroundVehicle(GroundVehicle gv){
		//TODO
	}
	
	void run(){
		this.sec = 0;
		this.msec = 0;
		
		//TODO
		/*
		while(this.sec<100){
			this.vehicle.controlVehicle(this.getControl(this.sec, this.msec));
			this.vehicle.updateState(0, SIM_STEP);
			double[] pose = this.vehicle.getPosition();
			System.out.format("%.2f %.2f %.2f %.1f%n", this.sec+this.msec/1000.0, pose[0], pose[1], pose[2]*180.0/Math.PI);
			msec+=SIM_STEP;
			if(this.msec==1000){
				this.msec=0;
				this.sec+=1;
			}
		}
		*/
		
		//System.out.println("finished sim, exiting...");
	}
	
	public static void main(String argv[]){
		Simulator sim = new Simulator();
		if(argv.length >0){
			try{
				//TODO
				//sim.setNumSides(Integer.parseInt(argv[0]));
			}
			catch(NumberFormatException e){
				System.out.println("input invalid");
			}
		}
		sim.run();
		//TODO
		System.exit(0);
	}
}
