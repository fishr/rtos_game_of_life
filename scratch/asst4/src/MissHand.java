import javax.realtime.*;

class MissHand extends AsyncEventHandler {
	 RealtimeThread rt;
	 
	 public MissHand(RealtimeThread rtin){
		 super(new PriorityParameters(PriorityScheduler.instance().getMaxPriority()), null, null, null, null, null);
		 rt=rtin;
	 }
	 
	 public void handleAsyncEvent(){
		 System.out.println("MISS");
		 rt.schedulePeriodic();
	 }
}