import java.util.Hashtable;

import javax.realtime.*;

class MissHand extends AsyncEventHandler {
	 RealtimeThread rt;
	 Hashtable<Long, Long> missDict;
	 private int miss=0;
	 
	 public MissHand(RealtimeThread rtin, Hashtable<Long, Long> missDict){
		 super(new PriorityParameters(PriorityScheduler.instance().getMaxPriority()), null, null, null, null, null);
		 this.rt=rtin;
		 this.missDict = missDict;
	 }
	 
	 public void handleAsyncEvent(){
		 miss++;
		 missDict.put(System.currentTimeMillis(), (long) this.miss);
		 System.out.println("MISS");
		 this.rt.schedulePeriodic();
	 }
}