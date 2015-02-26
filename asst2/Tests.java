import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

public class Tests {
	@Test
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
	
	@Test
	public void controlVehicle(){
		//TODO
	}
	
	@Test
	public void setPosition(){
		//TODO
	}
	
	@Test
	public void getVelocity(){
		//TODO
	}
	
	@Test
	public void setVelocity(){
		//TODO
	}
	
	@Test
	public void updateState(){
		//TODO	
	}
	
	@Test
	public void getControl(){
		//TODO
	}
}
