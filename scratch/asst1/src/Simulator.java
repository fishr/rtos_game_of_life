public class Simulator {
	
	private GroundVehicle vehicle;
	private int sec;
	private int msec;
	private int sides;
	private double dTheta;
	private double[] targets_x;
	private double[] targets_y;
	
	private final int MIN_SIDES = 3;
	private final int MAX_SIDES = 10;
	private final int SIM_STEP = 10;
	
	public Simulator(){
		double[] temp = {75,50,0};
		this.vehicle = new GroundVehicle(temp, 5, 0);
		
		this.sec=0;
		this.msec=0;
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
		
		
		
		//TODO the whole thing
		return new Control(5, 0);
	}
	
	int setNumSides(int n){
		int sides = this.sides;
		if(util.withinBounds(n, MIN_SIDES, MAX_SIDES)){
			sides=n;
		}
		this.dTheta = Math.PI/this.sides;
		this.targets_x=new double[sides];
		this.targets_y=new double[sides];
		for(int i=0; i<sides; i++)
		{
			this.targets_x[i]=50+Math.cos((i+1)*this.dTheta)*25.0;
			this.targets_y[i]=50+Math.sin((i+1)*this.dTheta)*25.0;
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
