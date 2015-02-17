import java.util.Hashtable;

public class Simulator {
	
	private GroundVehicle vehicle;
	private int sec;
	private int msec;
	private int sides;
	private Hashtable<Long, Control> bookmarks;  //stores controls keyed by the hundreths of seconds
	
	private final int MIN_SIDES = 3;
	private final int MAX_SIDES = 10;
	private final int SIM_STEP = 10;
	
	public Simulator(){
		double[] temp = {25,25,0};
		this.vehicle = new GroundVehicle(temp, 5, 0);
		
		this.sec=0;
		this.msec=0;
		bookmarks = new Hashtable<Long, Control>();
		this.sides=5;
		this.setNumSides(5);
	}
	
	int getCurrentSec(){
		return this.sec;
	}
	
	int getCurrentMSec(){
		return this.msec;
	}
	
	Control getControl(int sec, int msec){
		return bookmarks.get((long)(sec*100+msec));
	}
	
	int setNumSides(int n){
		int sides = this.sides;
		if(util.withinBounds(n, MIN_SIDES, MAX_SIDES)){
			sides=n;
		}
		double dTheta = (Math.PI-((sides-2)*Math.PI/this.sides))/2;
		long half_turn = Math.round(Math.ceil(dTheta*100.0/GroundVehicle.MAX_THETA_DOT));
		double turn_rate = dTheta*100/half_turn;
		double natural_side_length = 50.0*Math.sin(Math.PI/sides);
		double arc_radius = half_turn*GroundVehicle.MIN_S_DOT/(dTheta*100.0);
		double turn_length_offset = arc_radius*Math.sin(dTheta);
		double side_length_remainder = natural_side_length - 2*turn_length_offset;
		long straight_time = Math.round(Math.ceil(side_length_remainder*100.0/GroundVehicle.MAX_S_DOT));
		double straight_rate = side_length_remainder*100/straight_time;
		
		System.out.format("%.2f for %d and theta %.2f for %d%n", turn_rate, half_turn, straight_rate, straight_time);
		
		for(int i = 0; i<sides; i++){
			long offset_t = 2*i*half_turn+i*straight_time;
			bookmarks.put(offset_t, new Control(GroundVehicle.MIN_S_DOT, turn_rate));
			bookmarks.put(offset_t+half_turn, new Control(straight_rate, 0));
			bookmarks.put(offset_t+half_turn+straight_time, new Control(GroundVehicle.MIN_S_DOT, turn_rate));
		}
		
		return sides;
	}
	
	void run(){
		this.sec = 0;
		this.msec = 0;
		
		
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
		
		System.out.println("finished sim, exiting...");
	}
	
	public static void main(String argv[]){
		Simulator sim = new Simulator();
		if(argv.length >1){
			try{
				sim.setNumSides(Integer.parseInt(argv[1]));
			}
			catch(NumberFormatException e){
				System.out.println("input invalid");
			}
		}
		sim.run();
		System.exit(0);
	}
}
