import javax.realtime.*;

class OverrunHand extends AsyncEventHandler {
	RealtimeThread rt;
	Simulator sim;
	
	public OverrunHand(RealtimeThread rtin, Simulator sim){
		super(new PriorityParameters(PriorityScheduler.instance().getMaxPriority()), null, null, null, null, null);
		this.rt = rtin;
		this.sim = sim;
	}
	
	public void handleAsyncEvent(){
		ReleaseParameters rp = rt.getReleaseParameters();
		rp.setCost(rp.getCost().add(1,0));
		sim.setCost(rp.getCost().getMilliseconds());
		System.out.println("OVERRUN");
		rt.schedulePeriodic();
	}
}
