package pt.ipp.estg.housecontrol;

import com.google.firebase.database.IgnoreExtraProperties;

import pt.ipp.estg.housecontrol.Sensors.Sensor;

@IgnoreExtraProperties
public class SensorsValueShow {

    private Sensor blinder, door, hvac, light, temperature;

    public SensorsValueShow() {}

    public SensorsValueShow(Sensor blinder, Sensor door, Sensor hvac, Sensor light, Sensor temperature) {
        this.temperature = temperature;
        this.blinder = blinder;
        this.door = door;
        this.light = light;
        this.hvac = hvac;
    }

    public Sensor getTemperature() {
        return temperature;
    }

    public void setTemperature(Sensor temperature) {
        this.temperature = temperature;
    }

    public Sensor getBlinder() {
        return blinder;
    }

    public void setBlinder(Sensor blinder) {
        this.blinder = blinder;
    }

    public Sensor getDoor() {
        return door;
    }

    public void setDoor(Sensor door) {
        this.door = door;
    }

    public Sensor getLight() {
        return light;
    }

    public void setLight(Sensor light) {
        this.light = light;
    }

    public Sensor getHvac() {
        return hvac;
    }

    public void setHvac(Sensor hvac) {
        this.hvac = hvac;
    }
}
