

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
			
		}else{
			for(VehicleController v : sim.vehicles){
				new Thread(v).start();
				v.startThreadVehicle();
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
