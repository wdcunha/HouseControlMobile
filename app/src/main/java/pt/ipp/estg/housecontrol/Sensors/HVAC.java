package pt.ipp.estg.housecontrol.Sensors;

public class HVAC extends Sensor {

	private boolean bState = false; 
	
	public HVAC(int nSensorClass, int nIdentifier, boolean bState, int nTemperature, String szDescription) {
		super(nSensorClass, nIdentifier, szDescription);
		this.setIsOn(bState);
		this.setValue(nTemperature);
	}

	public boolean isOn() {
		return this.bState;
	}

	public void setIsOn(boolean bState) {
		this.bState = bState;
	}
	
	
	public String toString() {
		
		String szValue = String.valueOf(nValue);
		if (szValue.length() == 1) {
			szValue = "0" + szValue;
		}
		
		String szState = "00";
		if (this.bState == true) {
			szState = "01";
		}
		
		return super.toString() + szValue + szState;
	}
}
