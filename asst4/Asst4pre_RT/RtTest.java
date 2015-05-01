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
				GroundVehicle gv = new GroundVehicle(temp, 10*Math.random(), Math.PI/2*(Math.random()-1/2), sim, true);
				sim.addCircleVehicle(gv);
				
			}
			catch(NumberFormatException e){
				System.out.println("ip address?");
			}
		}


		System.out.println("about to enter real time"); 				
		
		if(rt){
			System.out.println("just before priorities");	
			//PriorityParameters prip = new PriorityParameters(PriorityScheduler.instance().getNormPriority());
			System.out.println("just after");
			for(VehicleController v : sim.vehicles){
				System.out.println("is this thing on?");
				RealtimeThread realCont = new RealtimeThread(null, null, null, null, null, v);
				PeriodicParameters pp1 = new PeriodicParameters(null, new RelativeTime(1,0), null, null,new OverrunHand(realCont),new MissHand(realCont));
				//realCont.setSchedulingParameters(prip); 
				realCont.setReleaseParameters(pp1);
				realCont.schedulePeriodic();

				RealtimeThread realVeh = new RealtimeThread(null, null, null, null, null, v.getVehicleRun());
				PeriodicParameters pp2 = new PeriodicParameters(null, new RelativeTime(5,0), null, null,new OverrunHand(realVeh),new MissHand(realVeh));
				//realVeh.setSchedulingParameters(prip); 
				realVeh.setReleaseParameters(pp2);
				realVeh.schedulePeriodic();			
}
			
			RealtimeThread realSim = new RealtimeThread(null, null, null, null, null, sim);
			PeriodicParameters pp3 = new PeriodicParameters(null, new RelativeTime(5,0), null, null, new OverrunHand(realSim), new MissHand(realSim));
			//realSim.setSchedulingParameters(prip); 
			realSim.setReleaseParameters(pp3);
			realSim.schedulePeriodic();

			System.out.println("things should be scheduled");

	
			try {
				realSim.join();
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
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
