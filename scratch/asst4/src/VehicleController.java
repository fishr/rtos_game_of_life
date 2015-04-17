
public class VehicleController implements Runnable {

	private GroundVehicle v;
	private Simulator sim;
	
	int sides = 5;
	Keyframe bookmarks;
	
	public static final int MIN_SIDES = 3;
	public static final int MAX_SIDES = 10;
	public static final int DEFAULT_SIDES = 5;
	public static final double BOUND_DIAM = 50.0;
	public static final int BASE_TIME = Simulator.SIM_UNITS/Simulator.SIM_STEP;
	
	long sec = -1;
	int usec = -1;
	
	public VehicleController(Simulator s, GroundVehicle v){
		this.sim = s;
		this.v = v;
		
		constructionTasks();
	}
	
	void constructionTasks(){
		setNumSides(DEFAULT_SIDES);
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
				
				double dTheta = (Math.PI-((this.sides-2)*Math.PI/this.sides))/2;
				long half_turn = Math.round(Math.ceil(dTheta*BASE_TIME/GroundVehicle.MAX_THETA_DOT));
				double turn_rate = dTheta*BASE_TIME/half_turn;
				double natural_side_length = BOUND_DIAM*Math.sin(Math.PI/this.sides);
				double arc_radius = half_turn*GroundVehicle.MIN_S_DOT/(dTheta*BASE_TIME);
				double turn_length_offset = arc_radius*Math.sin(dTheta);
				double side_length_remainder = natural_side_length - 2*turn_length_offset;
				long straight_time = Math.round(Math.ceil(side_length_remainder*BASE_TIME/GroundVehicle.MAX_S_DOT));
				double straight_rate = side_length_remainder*BASE_TIME/straight_time;
				
				long loop_time = sides*(2*half_turn+straight_time)*Simulator.SIM_STEP;
				Keyframe bookmarks = new Keyframe(loop_time);
	
				
				//System.out.format("%.2f for %d and theta %.2f for %d%n", turn_rate, half_turn, straight_rate, straight_time);
				for(int i = 0; i<sides; i++){
					long offset_t = 2*i*half_turn+i*straight_time;
					assert(half_turn>0);
					assert(straight_time>0);
					bookmarks.put(offset_t*Simulator.SIM_STEP, new Control(GroundVehicle.MIN_S_DOT, turn_rate));
					bookmarks.put((offset_t+half_turn)*Simulator.SIM_STEP, new Control(straight_rate, 0));
					bookmarks.put((offset_t+half_turn+straight_time)*Simulator.SIM_STEP, new Control(GroundVehicle.MIN_S_DOT, turn_rate));
				}
				
				this.bookmarks = bookmarks;
				this.sim.schedules.put(this.sides, this.bookmarks);
			}
		}
		
		return this.sides;
	}
	
	public Control getControl(Timestamp time){
		long usecs= time.sec*Simulator.SIM_UNITS+time.usec;
		return this.bookmarks.get(usecs%this.bookmarks.loop_time);
	}
	
	public synchronized long getLoopTime(){
		return this.bookmarks.loop_time;
	}
	
	public void run(){
		while(this.sec<Simulator.MAX_RUNTIME){
			Timestamp time = sim.getTime();
			v.controlVehicle(getControl(time));
			this.sec = time.sec;
			this.usec = time.usec;
			if(Thread.currentThread().getClass().equals(RealtimeThread.class)){
				RealtimeThread.waitForNextPeriod();
			}
		}
		
	}
	
	public Runnable getVehicleRun(){
		return this.v;
	}
	
	public int getVehicleCode(){
		return this.v.hashCode();
	}
	
	double[] getVehiclePosition(int hash){
		return this.sim.getVehiclePosition(hash);
	}
}
