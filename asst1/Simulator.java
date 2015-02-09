public class Simulator {
	
	private GroundVehicle vehicle;
	private int sec;
	private int msec;
	private int sides;
	private final int MIN_SIDES = 3;
	private final int MAX_SIDES = 10;
	
	public Simulator(){
		double[] temp = {50,50,0};
		this.vehicle = new GroundVehicle(temp, 5, 0);
		
		this.sec=0;
		this.msec=0;
		this.sides=5;
	}
	
	int getCurrentSec(){
		return this.sec;
	}
	
	int getCurrentMSec(){
		return this.msec;
	}
	
	Control getControl(int sec, int msec){
		return new Control(5,0);
		//TODO
	}
	
	int setNumSides(int n){
		if(!util.withinBounds(n, MIN_SIDES, MAX_SIDES)){
			return this.sides;
		}else{
			this.sides=n;
			return n;
		}
	}
	
	void run(){
		this.sec = 0;
		this.msec = 0;
		
		while(this.sec<100){
			this.vehicle.controlVehicle(this.getControl(this.sec, this.msec));
			this.vehicle.updateState(this.sec, this.msec);
			double[] pose = this.vehicle.getPostion();
			System.out.format("%.2f %.2f %.2f %.1f%n", this.sec+this.msec/1000.0, pose[0], pose[1], pose[2]*180.0/Math.PI);
			msec+=10;
			if(this.msec==1000){
				this.msec=0;
				this.sec+=1;
			}
		}
		
		System.out.println("finished sim, exiting...");
	}
	
	public static void main(String argv[]){
		Simulator sim = new Simulator();
		sim.run();
		System.exit(0);
	}
}
