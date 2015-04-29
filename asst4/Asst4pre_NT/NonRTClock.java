public class NonRTClock {

	private final long start_time;
	
	public NonRTClock(){
		this.start_time = System.currentTimeMillis();
	}
	
	public synchronized Timestamp getTime(){
		long thistime = System.currentTimeMillis()-start_time;
		long secs = thistime/1000l;
		int usecs = (int) (thistime%1000);
		return new Timestamp(secs, usecs);
	}
	
	public synchronized void incClock(){
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}	
}
