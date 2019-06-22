package pt.ipp.estg.housecontrol;

public class Sensor {

    private String 	blinder,  door, hvac, light, temperature;

    public Sensor() {}

    public Sensor(String blinder, String door, String hvac, String light, String temperature) {
        this.temperature = temperature;
        this.blinder = blinder;
        this.door = door;
        this.light = light;
        this.hvac = hvac;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getBlinder() {
        return blinder;
    }

    public void setBlinder(String blinder) {
        this.blinder = blinder;
    }

    public String getDoor() {
        return door;
    }

    public void setDoor(String door) {
        this.door = door;
    }

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public String getHvac() {
        return hvac;
    }

    public void setHvac(String hvac) {
        this.hvac = hvac;
    }
}
