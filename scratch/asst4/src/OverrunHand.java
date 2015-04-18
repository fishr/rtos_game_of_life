import javax.realtime.*;

class OverrunHand extends AsyncEventHandler {
	RealtimeThread rt;
	
	public OverrunHand(RealtimeThread rtin){
		super(new PriorityParameters(PriorityScheduler.instance().getMaxPriority()), null, null, null, null, null);
		rt = rtin;
	}
	
	public void handleAsyncEvent(){
		ReleaseParameters rp = rt.getReleaseParameters();
		rp.setCost(rp.getCost().add(1,0));
		rt.schedulePeriodic();
	}
}
