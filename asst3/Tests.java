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
		Assert.assertEquals(Math.PI/4, util.wrapAngle(-7*Math.PI/4), 0);
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
		Simulator sim = new Simulator();
		GroundVehicle gv = new GroundVehicle(pose, 7, 0, sim);
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
	public void getControl(){
		double[] pose = {25, 25, 0};
		Simulator sim = new Simulator();
		GroundVehicle gv = new GroundVehicle(pose, 7, 0, sim);
		VehicleController cont = new VehicleController(sim, gv);
		
		final double[] expected = {GroundVehicle.MIN_S_DOT, GroundVehicle.MAX_THETA_DOT};
		Control cont1 = cont.getControl(new Timestamp(0,0));
		final double[] actual = {cont1.getSpeed(), cont1.getRotVel()};
		Assert.assertArrayEquals(expected, actual, .1);
		
		Control cont2 = cont.getControl(new Timestamp(0,1));
		Assert.assertNull(cont2);
		Control cont3 = cont.getControl(new Timestamp(0,10));
		Assert.assertNull(cont3);
	}
	
	@Test
	public void getRandomControl(){
		double[] pose = {25, 25, 0};
		Simulator sim = new Simulator();
		GroundVehicle gv = new GroundVehicle(pose, 7, 0, sim);
		RandomController cont = new RandomController(sim, gv);
		
		Control contr = cont.getControl(new Timestamp(0,0));
		Assert.assertTrue(Double.isFinite(contr.getSpeed()));
		
	}
	
	/* Simulator Tests */
	
	 @Test
	 public void incClock(){
		 Simulator sim = new Simulator();
		 Timestamp expected = new Timestamp(0, Simulator.SIM_STEP);
		 Assert.assertEquals(0, sim.getCount());
		 Assert.assertEquals(0, sim.getUsers());
		 
		 sim.incClock();
		 Timestamp actual = sim.getTime();
		 
		 Assert.assertEquals(0, sim.getCount());
		 Assert.assertEquals(0, sim.getUsers());
		 
		 Assert.assertEquals(expected.sec, actual.sec);
		 Assert.assertEquals(expected.usec, actual.usec);
		 
		 double[] pose = {25,50,0};
		 GroundVehicle gv = new GroundVehicle(pose, 7, 0, sim);
		 sim.addGroundVehicle(gv);
		 
		 Assert.assertEquals(0, sim.getCount());
		 Assert.assertEquals(2, sim.getUsers());
		 
		 sim.incClock();
		 
		 Assert.assertEquals(2, sim.getCount());
		 Assert.assertEquals(2, sim.getUsers());
		 
		 sim.getTime(0, 0);
		 sim.getTime(0, 0);
		 
		 Assert.assertEquals(0, sim.getCount());
		 Assert.assertEquals(2, sim.getUsers());
	 }
	 
	 /* Ground Vehicle */

	@Test
	public void GroundVehicleUpdate(){
		double[] pose = {25, 25, 0};
		Simulator sim = new Simulator();
		GroundVehicle gv = new GroundVehicle(pose, 10, 0, sim);
		
		Timestamp time = new Timestamp(1, 0);
		gv.controlVehicle(new Control(10, 0));
		
		gv.updateState(time.sec, time.usec);
		double[] pose1 = {35, 25, 0};
		
		Assert.assertArrayEquals(pose1, gv.getPosition(), 0);
	}
	
	@Test
	public void setVelocity(){
		final double[] pose1 = {0.0,0.0,0.0};
		Simulator sim = new Simulator();
		GroundVehicle testVehicle = new GroundVehicle(pose1, 7, 0, sim);
		final double[] vels1 = {7.0, 0.0, 0.0};
		testVehicle.setVelocity(vels1);
		Assert.assertArrayEquals(vels1, testVehicle.getVelocity(), 1E-15);
		Assert.assertArrayEquals(pose1, testVehicle.getPosition(), 1E-15);
		
		final double[] pose2 = {0.0,0.0,Math.PI/2};
		final double[] vels2 = {0.0, 7.0, 0.0};
		testVehicle.setVelocity(vels2);
		Assert.assertArrayEquals(pose2, testVehicle.getPosition(), 1E-15);
		Assert.assertArrayEquals(vels2, testVehicle.getVelocity(), 1E-15);
		
		final double[] pose3 = {0.0,0.0,-Math.PI/2};
		final double[] vels3 = {0.0, -7.0, 0.0};
		testVehicle.setVelocity(vels3);
		Assert.assertArrayEquals(pose3, testVehicle.getPosition(), 1E-15);
		Assert.assertArrayEquals(vels3, testVehicle.getVelocity(), 1E-15);
		
		final double[] pose4 = {0.0,0.0,-Math.PI};
		final double[] vels4 = {-7.0, 0.0, 0.0};
		testVehicle.setVelocity(vels4);
		Assert.assertArrayEquals(pose4, testVehicle.getPosition(), 1E-15);
		Assert.assertArrayEquals(vels4, testVehicle.getVelocity(), 1E-15);
		
		final double[] pose5 = {0.0,0.0,Math.PI/2};
		final double[] vels5 = {0.0, 20.0, 3.0};
		final double[] vals5 = {0.0, GroundVehicle.MAX_S_DOT, GroundVehicle.MAX_THETA_DOT};
		testVehicle.setVelocity(vels5);
		Assert.assertArrayEquals(pose5, testVehicle.getPosition(), 1E-15);
		Assert.assertArrayEquals(vals5, testVehicle.getVelocity(), 1E-15);
		
		final double[] pose6 = {0.0,0.0,Math.PI/2};
		final double[] vels6 = {0.0, 0.0, -3.0};
		final double[] vals6 = {0.0, GroundVehicle.MIN_S_DOT, GroundVehicle.MIN_THETA_DOT};
		testVehicle.setVelocity(vels6);
		Assert.assertArrayEquals(pose6, testVehicle.getPosition(), 1E-15);
		Assert.assertArrayEquals(vals6, testVehicle.getVelocity(), 1E-15);
		
		final double[] pose7 = {0.0,0.0,Math.PI/4};
		final double[] vels7 = {4.0, 4.0, 0.0};
		final double[] vals7 = {Math.sqrt(32.0), 0};
		testVehicle.setVelocity(vels7);
		Assert.assertArrayEquals(pose7, testVehicle.getPosition(), 1E-15);
		Assert.assertArrayEquals(vals7, testVehicle.getRawVelocity(), 1E-15);
		
		final double[] pose8 = {0.0,0.0,Math.PI/4};
		final double[] vels8 = {Double.MAX_VALUE, Double.MAX_VALUE, 0.0};
		final double[] vals8 = {GroundVehicle.MAX_S_DOT, 0};
		testVehicle.setVelocity(vels8);
		Assert.assertArrayEquals(pose8, testVehicle.getPosition(), 1E-15);
		Assert.assertArrayEquals(vals8, testVehicle.getRawVelocity(), 1E-15);
	}
	
	@Test
	public void getVelocity(){
		final double[] pose1 = {0.0,0.0,0.0};
		Simulator sim = new Simulator();
		GroundVehicle testVehicle = new GroundVehicle(pose1, 7, 0, sim);
		final double[] vels1 = {7.0, 0.0, 0.0};
		Assert.assertArrayEquals(vels1, testVehicle.getVelocity(), 1E-15);
		
		final double[] pose2= {0.0,0.0,Math.PI/2};
		testVehicle.setPosition(pose2);
		final double[] vels2 = {0.0, 7.0, 0.0};
		Assert.assertArrayEquals(vels2, testVehicle.getVelocity(), 1E-15);
		
		final double[] pose3 = {0.0,0.0,-Math.PI/2};
		testVehicle.setPosition(pose3);
		final double[] vels3 = {0.0, -7.0, 0.0};
		Assert.assertArrayEquals(vels3, testVehicle.getVelocity(), 1E-15);
		
		final double[] pose4 = {0.0,0.0,-Math.PI};
		testVehicle.setPosition(pose4);
		final double[] vels4 = {-7.0, 0.0, 0.0};
		Assert.assertArrayEquals(vels4, testVehicle.getVelocity(), 1E-15);
	}
	
	@Test
	public void updateState(){
		final double[] pose1 = {0.0,0.0,0.0};
		Simulator sim = new Simulator();
		GroundVehicle testVehicle = new GroundVehicle(pose1, 7, 0, sim);
		final double[] pose2 = {7.0, 0.0, 0.0};
		testVehicle.updateState(1, 0);
		Assert.assertArrayEquals(pose2, testVehicle.getPosition(), 1E-15);

		testVehicle.setPosition(pose1);
		final double[] pose3 = {.7, 0.0, 0.0};
		testVehicle.updateState(0, 100);
		Assert.assertArrayEquals(pose3, testVehicle.getPosition(), 1E-15);

		testVehicle.setPosition(pose1);
		final double[] vels4 = {7.0, 0.0, Double.MIN_VALUE};
		testVehicle.setVelocity(vels4);
		final double[] pose4 = pose3.clone();
		testVehicle.updateState(0, 100);
		Assert.assertArrayEquals(pose4, testVehicle.getPosition(), 1E-15);
		
		testVehicle.setPosition(pose1);
		final double[] vels5 = {7.0, 0.0, Math.PI/4};
		testVehicle.setVelocity(vels5);
		final double[] pose5 = {0.0, 56.0/Math.PI, -Math.PI};
		testVehicle.updateState(4, 0);
		Assert.assertArrayEquals(pose5, testVehicle.getPosition(), 2E-15);
		
		testVehicle.setPosition(pose1);
		final double[] vels6 = {0.0, 7.0, -Math.PI/4};
		testVehicle.setVelocity(vels6);
		final double[] pose6 = {56.0/Math.PI, 0.0, -Math.PI/2.0};
		testVehicle.updateState(4, 0);
		Assert.assertArrayEquals(pose6, testVehicle.getPosition(), 2E-15);

		final double[] pose7 = {50.0,50.0,0.0};
		testVehicle.setPosition(pose7);
		final double[] vels7 = {7.0, 0.0, 0.0};
		testVehicle.setVelocity(vels7);
		testVehicle.updateState(1, 0);
		final double[] postpose7 = {57.0, 50.0, 0.0};
		Assert.assertArrayEquals(postpose7, testVehicle.getPosition(), 1E-15);
	}

}
