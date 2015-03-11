import java.util.Hashtable;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

public class Tests {
	@Test
	
	/* UTIL TESTS */
	public void clamp(){
		Assert.assertEquals(0.0, util.clampDouble(0.0, -1.0, 1.0), 0);
		Assert.assertEquals(-1.0, util.clampDouble(-10.0, -1.0, 1.0), 0);
		Assert.assertEquals(1.0, util.clampDouble(10.0, -1.0, 1.0), 0);
		Assert.assertEquals(1.0, util.clampDouble(Double.POSITIVE_INFINITY, -1.0, 1.0), 0);
		Assert.assertEquals(1.0, util.clampDouble(Double.MAX_VALUE, -1.0, 1.0), 0);
		
		Assert.assertEquals(0.0, util.clampInt(0, -1, 1), 0);
		Assert.assertEquals(-1.0, util.clampInt(-10, -1, 1), 0);
		Assert.assertEquals(1.0, util.clampInt(10, -1, 1), 0);
	}
	
	@Test
	public void wrap(){
		Assert.assertEquals(0, util.wrapAngle(0),0);
		Assert.assertEquals(0, util.wrapAngle(2*Math.PI),0);
		Assert.assertEquals(0, util.wrapAngle(-2*Math.PI),0);
		Assert.assertEquals(-Math.PI, util.wrapAngle(-Math.PI),0);
		Assert.assertEquals(-Math.PI, util.wrapAngle(Math.PI),0);
	}
	
	/* CONTROL CLASS TESTS */
	
	@Test
	public void Control(){
		final double[] inputs = {0, -2, 7, 0, 20, 3};
		final double[] expecteds = {GroundVehicle.MIN_S_DOT, GroundVehicle.MIN_THETA_DOT, 7, 0, GroundVehicle.MAX_S_DOT, GroundVehicle.MAX_THETA_DOT};
		final double[] outputs = new double[6];
		for(int i =0; i<3; i++){
			Control testee = new Control(inputs[2*i], inputs[2*i+1]);
			outputs[2*i] = testee.getSpeed();
			outputs[2*i+1] = testee.getRotVel();
		}
		Assert.assertArrayEquals(expecteds, outputs, 0);
	}
	
	/* VehicleController Tests */
	
	
	@Test
	public void setNumSides(){
		double[] pose = {25, 25, 0};
		GroundVehicle gv = new GroundVehicle(pose, 7, 0);
		Simulator sim = new Simulator();
		VehicleController cont = new VehicleController(sim, gv);
		
		Assert.assertTrue(sim.schedules.containsKey(5));
		
		cont.setNumSides(8);
		Assert.assertTrue(sim.schedules.containsKey(8));
		long zeroth = cont.getLoopTime();
		
		cont.setNumSides(10);
		Assert.assertTrue(sim.schedules.containsKey(10));
		long first = cont.getLoopTime();
		Assert.assertNotEquals(zeroth,first);
		
		cont.setNumSides(11);
		Assert.assertTrue(!sim.schedules.containsKey(11));
		long second = cont.getLoopTime();
		Assert.assertEquals(first,second);
	}
	
	@Test
	public void GroundVehicleUpdate(){
		double[] pose = {25, 25, 0};
		GroundVehicle gv = new GroundVehicle(pose, 10, 0);
		Simulator sim = new Simulator();
		VehicleController cont = new VehicleController(sim, gv);
		
		Timestamp time = new Timestamp(1, 0);
		gv.controlVehicle(new Control(10, 0));
		
		cont.GroundVehicleUpdate(time);
		double[] pose1 = {35, 25, 0};
		
		Assert.assertArrayEquals(pose1, gv.getPosition(), 0);
	}
	
	/* Simulator Tests */
	
	 
}
