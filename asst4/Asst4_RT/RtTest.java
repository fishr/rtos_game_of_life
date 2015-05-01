import java.util.Hashtable;

import javax.realtime.*;

public class RtTest {

	private static boolean rt=true;

	public static void main(String argv[]){
		Simulator sim = new Simulator();
		if(argv.length >0){
			try{
				sim.dc = new DisplayClient(argv[0]);
				sim.dc.clear();
				sim.dc.traceOn();
				
				double[] temp = {100*Math.random(),100*Math.random(),(2*Math.random()-1)*Math.PI};
				GroundVehicle gv = new GroundVehicle(temp, 10*Math.random(), Math.PI/2*(Math.random()-1/2), sim, false);
				sim.addCircleVehicle(gv);
				GroundVehicle gv3 = new GroundVehicle(temp, 10*Math.random(), Math.PI/2*(Math.random()-1/2), sim, false);
				LeadingController lc = sim.addLeaderVehicle(gv3);
				GroundVehicle gv2 = new GroundVehicle(temp, 10*Math.random(), Math.PI/2*(Math.random()-1/2), sim, false);
				sim.addFollowVehicle(gv2,gv3);
				lc.addFollower(gv.hashCode());
				lc.addFollower(gv2.hashCode());
				
			}
			catch(NumberFormatException e){
				System.out.println("ip address?");
			}
		}


		System.out.println("about to enter real time"); 				
		
		Hashtable<Long, Long> missV = new Hashtable<Long, Long>();
		Hashtable<Long, Long> missVeh = new Hashtable<Long, Long>();
		Hashtable<Long, Long> missSim = new Hashtable<Long, Long>();
		Hashtable<Long, Long> overV = new Hashtable<Long, Long>();
		Hashtable<Long, Long> overVeh = new Hashtable<Long, Long>();
		Hashtable<Long, Long> overSim = new Hashtable<Long, Long>();
		
		if(rt){
			System.out.println("just before priorities");	
			//PriorityParameters prip = new PriorityParameters(PriorityScheduler.instance().getNormPriority());
			System.out.println("just after");
			for(VehicleController v : sim.vehicles){
				System.out.println("is this thing on?");
				RealtimeThread realCont = new RealtimeThread(null, null, null, null, null, v);
				PeriodicParameters pp1 = new PeriodicParameters(null, new RelativeTime(7,0), null, null,new OverrunHand(realCont, overV),new MissHand(realCont, missV));
				//realCont.setSchedulingParameters(prip); 
				realCont.setReleaseParameters(pp1);
				realCont.start();

				RealtimeThread realVeh = new RealtimeThread(null, null, null, null, null, v.getVehicleRun());
				PeriodicParameters pp2 = new PeriodicParameters(null, new RelativeTime(3,0), null, null,new OverrunHand(realVeh, overVeh),new MissHand(realVeh, missVeh));
				//realVeh.setSchedulingParameters(prip); 
				realVeh.setReleaseParameters(pp2);
				realVeh.start();			
}
			
			RealtimeThread realSim = new RealtimeThread(null, null, null, null, null, sim);
			PeriodicParameters pp3 = new PeriodicParameters(null, new RelativeTime(20,0), null, null, new OverrunHand(realSim, overSim), new MissHand(realSim, missSim));
			//realSim.setSchedulingParameters(prip); 
			realSim.setReleaseParameters(pp3);
			realSim.start();

			System.out.println("things should be scheduled");

	
			try {
				realSim.join();
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.print("missV ");
			util.dictPrint(missV);
			System.out.print("missVeh ");
			util.dictPrint(missVeh);
			System.out.print("missSim ");
			util.dictPrint(missSim);
			System.out.print("overV ");
			util.dictPrint(overV);
			System.out.print("overVeh ");
			util.dictPrint(overVeh);
			System.out.print("overSim ");
			util.dictPrint(overSim);

		}else{
			for(VehicleController v : sim.vehicles){
				new Thread(v).start();
				new Thread(v.getVehicleRun()).start();
			}
			
			Thread simTh = new Thread(sim);
			simTh.start();
			
			try {
				simTh.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
}
