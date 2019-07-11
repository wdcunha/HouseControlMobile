package pt.ipp.estg.housecontrol.Sensors;

public class Door extends Sensor {

	private boolean bState = false; 
	
	public Door(int nSensorClass, int nIdentifier, boolean bState, String szDescription) {
		super(nSensorClass, nIdentifier, szDescription);
		this.setIsOpen(bState);
	}

	public boolean isOpen() {
		return this.bState;
	}

	public void setIsOpen(boolean bState) {
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
