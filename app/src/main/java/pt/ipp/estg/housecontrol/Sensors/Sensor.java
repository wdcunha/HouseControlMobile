package pt.ipp.estg.housecontrol.Sensors;

public class Sensor {
	
	protected int nSensorClass = 0;
	protected int nIdentifier = 0;
	protected int nValue = 0;
	protected String szDescription = "";
	
	public Sensor() {
		
	}

	public Sensor(int nSensorClass, int nIdentifier, String szDescription) {
		this.nSensorClass = nSensorClass;
		this.nIdentifier = nIdentifier;
		this.szDescription = szDescription;
	}
	
	public Sensor(int nSensorClass, int nIdentifier, int nValue, String szDescription) {
		this.nSensorClass = nSensorClass;
		this.nIdentifier = nIdentifier;
		this.nValue = nValue;
		this.szDescription = szDescription;
	}	
	
	public int getSensorClass() {
		return nSensorClass;
	}
	
	public void setSensorClass(int nSensorClass) {
		this.nSensorClass = nSensorClass;
	}
	
	public int getIdentifier() {
		return nIdentifier;
	}
	
	public void setIdentifier(int nIdentifier) {
		this.nIdentifier = nIdentifier;
	}

	public String getDescription() {
		return szDescription;
	}

	public void setDescription(String szDescription) {
		this.szDescription = szDescription;
	}

	public int getValue() {
		return nValue;
	}

	public void setValue(int nValue) {
		this.nValue = nValue;
	}
	
	public String toString() {
		String szSensorClass = String.valueOf(nSensorClass);
		if (szSensorClass.length() == 1) {
			szSensorClass = "0" + szSensorClass;
		}
		
		String szSensorIdentifier = String.valueOf(nIdentifier);
		if (szSensorIdentifier.length() == 1) {
			szSensorIdentifier = "0" + szSensorIdentifier;
		}
		
		return szSensorClass + szSensorIdentifier;
	}
}
