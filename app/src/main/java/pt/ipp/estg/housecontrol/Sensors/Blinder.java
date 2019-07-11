package pt.ipp.estg.housecontrol.Sensors;

public class Blinder extends Sensor {

	public Blinder(int nSensorClass, int nIdentifier, int nPercentage, String szDescription) {
		super(nSensorClass, nIdentifier, szDescription);
		this.setValue(nPercentage);
	}

	public void setValue(int nPercentage) {
		
		if (nPercentage < 0 || nPercentage > 100) {
			System.out.println("Invalid value");
			return;
		}
		
		this.nValue = nPercentage;
	}
	
	public String toString() {
		String szValue = String.valueOf(nValue);
		if (szValue.length() == 1) {
			szValue = "00" + szValue;
		} else if (szValue.length() == 2) {
			szValue = "0" + szValue;
		}
		
		return super.toString() + szValue;
	}
}
