import java.util.Hashtable;

import javax.realtime.*;

class OverrunHand extends AsyncEventHandler {
	RealtimeThread rt;
	Hashtable<Long, Long> costDict;
	
	public OverrunHand(RealtimeThread rtin, Hashtable<Long, Long> costDict){
		super(new PriorityParameters(PriorityScheduler.instance().getMaxPriority()), null, null, null, null, null);
		this.rt = rtin;
		this.costDict = costDict;
	}
	
	public void handleAsyncEvent(){
		ReleaseParameters rp = rt.getReleaseParameters();
		rp.setCost(rp.getCost().add(1,0));
		costDict.put(System.currentTimeMillis(), rp.getCost().getMilliseconds());
		System.out.println("OVERRUN");
		rt.schedulePeriodic();
	}
}
