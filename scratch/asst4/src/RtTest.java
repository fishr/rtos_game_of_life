import javax.realtime.*;

public class RtTest {

	private static boolean rt=false;

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
				
				if(argv.length>1){
					if(argv[1].equalsIgnoreCase("realtime")){
						rt=true;
					}else{
						throw new IllegalArgumentException("please input the number of vehicles");
					}
				}
			}
			catch(NumberFormatException e){
				System.out.println("ip address?");
			}
		}
				
		
		if(rt){
			PriorityParameters prip = new PriorityParameters(PriorityScheduler.instance().getNormPriority());
			for(VehicleController v : sim.vehicles){
				RealtimeThread realCont = new RealtimeThread(null, null, null, null, null, v);
				PeriodicParameters pp1 = new PeriodicParameters(null, new RelativeTime(1,0), null, null,new OverrunHand(realCont),new MissHand(realCont));
				realCont.setSchedulingParameters(prip); 
				realCont.setReleaseParameters(pp1);

				RealtimeThread realVeh = new RealtimeThread(null, null, null, null, null, v.getVehicleRun());
				PeriodicParameters pp2 = new PeriodicParameters(null, new RelativeTime(5,0), null, null,new OverrunHand(realVeh),new MissHand(realVeh));
				realCont.setSchedulingParameters(prip); 
				realCont.setReleaseParameters(pp2);
			}
			
			RealtimeThread realSim = new RealtimeThread(null, null, null, null, null, sim);
			PeriodicParameters pp1 = new PeriodicParameters(null, new RelativeTime(5,0), null, null, new OverrunHand(realSim), new MissHand(realSim));
			realSim.setSchedulingParameters(prip); 
			realSim.setReleaseParameters(pp1);
			
			try{
				realSim.join();
			} catch (InterruptedException e) {
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
