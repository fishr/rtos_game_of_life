import java.util.Hashtable;


public class VehicleController extends Thread {

	private GroundVehicle v;
	private Simulator sim;
	
	private int sides = 5;
	private Keyframe bookmarks;
	
	public static final int MIN_SIDES = 3;
	public static final int MAX_SIDES = 10;
	public static final int DEFAULT_SIDES = 5;
	
	private int sec = 0;
	private int usec = 0;
	
	public VehicleController(Simulator s, GroundVehicle v){
		this.sim = s;
		this.v = v;
		
		setNumSides(DEFAULT_SIDES);
	}
	
	public void GroundVehicleUpdate(Clock.Timestamp time){
		this.v.updateState(time.sec, time.usec);
	}
	
	public double[] getPosition() {
		return v.getPosition();
	}
	
	public int setNumSides(int sides){
		this.sides = util.clampInt(sides, MIN_SIDES, MAX_SIDES);
		
		synchronized(this.sim.schedules){
			if(this.sim.schedules.containsKey(this.sides)){
				//prolly want a read/write lock on this, so 
				//as soon as a reader needs to build a new bookmarks
				//it can lock.  I think this means the stretch from
				//obtaining a reader lock to obtaining a write lock
				//needs to be synchronized or atomic or something
				this.bookmarks = this.sim.schedules.get(this.sides);
			}else{
				//else generate _completely_ locally and add to 
				//schedule and release lock
				
				double dTheta = (Math.PI-((sides-2)*Math.PI/sides))/2;
				long half_turn = Math.round(Math.ceil(dTheta*1000000.0/GroundVehicle.MAX_THETA_DOT));
				double turn_rate = dTheta*1000000.0/half_turn;
				double natural_side_length = 50.0*Math.sin(Math.PI/sides);
				double arc_radius = half_turn*GroundVehicle.MIN_S_DOT/(dTheta*1000000.0);
				double turn_length_offset = arc_radius*Math.sin(dTheta);
				double side_length_remainder = natural_side_length - 2*turn_length_offset;
				long straight_time = Math.round(Math.ceil(side_length_remainder*1000000.0/GroundVehicle.MAX_S_DOT));
				double straight_rate = side_length_remainder*1000000.0/straight_time;
				
				long loop_time = sides*(2*half_turn+straight_time);
				Keyframe bookmarks = new Keyframe(loop_time);
	
				
				//System.out.format("%.2f for %d and theta %.2f for %d%n", turn_rate, half_turn, straight_rate, straight_time);
				for(int i = 0; i<sides; i++){
					long offset_t = 2*i*half_turn+i*straight_time;
					assert(half_turn>0);
					assert(straight_time>0);
					bookmarks.put(offset_t*10, new Control(GroundVehicle.MIN_S_DOT, turn_rate));
					bookmarks.put((offset_t+half_turn)*10, new Control(straight_rate, 0));
					bookmarks.put((offset_t+half_turn+straight_time)*10, new Control(GroundVehicle.MIN_S_DOT, turn_rate));
				}
				
				this.bookmarks = bookmarks;
				this.sim.schedules.put(this.sides, this.bookmarks);
			}
		}
		
		return this.sides;
	}
	
	public Control getControl(Clock.Timestamp time){
		long usecs= time.sec*1000000+time.usec;
		return this.bookmarks.get(usecs%this.bookmarks.loop_time);
	}
	
	public void run(){
		
		while(this.sec<100){
			Clock.Timestamp time = sim.getTime(this.sec, this.usec);
			v.controlVehicle(getControl(time));
			this.sec = time.sec;
			this.usec = time.usec;
			Thread.yield();
		}
		
	}
}
