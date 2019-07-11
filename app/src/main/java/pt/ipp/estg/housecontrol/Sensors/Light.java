package pt.ipp.estg.housecontrol.Sensors;

public class Light extends Sensor {
	
	private boolean bState = false; 
	
	public Light(int nSensorClass, int nIdentifier, boolean bState, String szDescription) {
		super(nSensorClass, nIdentifier, szDescription);
		this.setIsOn(bState);
	}

	public boolean isOn() {
		return this.bState;
	}

	public void setIsOn(boolean bState) {
		this.bState = bState;
	}
	
	public String toString() {
		
		String szValue = "00";
		if (this.bState == true) {
			szValue = "01";
		}
		
		return super.toString() + szValue;
	}
}
