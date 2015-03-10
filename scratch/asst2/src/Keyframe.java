import java.util.Hashtable;
import java.util.Iterator;


public class Keyframe{

	private Hashtable<Long, Control> bookmarks;
	final long loop_time;
	
	public Keyframe(long loop)
	{
		this.bookmarks = new Hashtable<Long, Control>();
		this.loop_time=loop;
	}
	
	public synchronized void put(long usecs, Control cont){
		this.bookmarks.put(usecs, cont);
	}

	public synchronized Control get(Clock.Timestamp time){
		long usecs= time.sec*1000000+time.usec;
		return this.bookmarks.get(usecs);
	}
	
	public synchronized Control get(long usecs){
		return this.bookmarks.get(usecs);
	}
}
