public class Clock {

	private int sec;
	private int usec;
	private int step;
	
	private int users;
	private int not_updated;
	
	public Clock(int sec, int usec, int timestep){
		this.sec=sec;
		this.usec=usec;
		this.step = timestep;
		
		this.users=0;
		this.not_updated = 0;
	}
	
	public synchronized Timestamp getTime(int sec, int usec){
		/*Conditional Critical Region*/
		while((sec==this.sec)&&(usec==this.usec)){
			try{
				wait();
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}
		}
		Timestamp time = new Timestamp(this.sec, this.usec);
		this.not_updated--;
		assert(this.not_updated>=0);
		notifyAll();
		return time;
	}
	
	public synchronized Timestamp getTime(){
		return new Timestamp(this.sec, this.usec);
	}
	
	public synchronized int getCount(){
		return this.not_updated;
	}
	
	public synchronized void incClock(){
		/*Conditional Critical Region*/
		while(this.not_updated>0){
			try{
				wait();
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}
		}
		
		this.usec+=this.step;
		if(this.usec==Simulator.SIM_UNITS){
			this.usec=0;
			this.sec+=1;
		}
		this.not_updated=getUsers();
		notifyAll();
	}
	
	synchronized int getUsers(){
		return this.users;
	}
	
	public synchronized void incUsers(){
		this.users++;
	}	
}
