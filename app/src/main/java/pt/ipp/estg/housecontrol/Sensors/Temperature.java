package pt.ipp.estg.housecontrol.Sensors;

public class Temperature extends Sensor {

	public Temperature(int nSensorClass, int nIdentifier, int nValue, String szDescription) {
		super(nSensorClass, nIdentifier, szDescription);
		this.setValue(nValue);
	}
	
	public void setValue(int nValue) {
		
		if (nValue < -100 || nValue > 100) {
			System.out.println("Invalid value");
			return;
		}
		
		this.nValue = nValue;
	}
	
	public String toString() {
		String szValue = String.valueOf(nValue);
		if (szValue.length() == 1) {
			szValue = "0" + szValue;
		}
		
		return super.toString() + szValue;
	}
}
