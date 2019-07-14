package pt.ipp.estg.housecontrol;

public class DataChanges {

    private String 	blinder, door, hvac, light, temperature, timestamp, userId, userName;

    public DataChanges() {}

    public DataChanges(String blinder, String door, String hvac, String light, String temperature, String timestamp, String userId, String userName) {
        this.temperature = temperature;
        this.blinder = blinder;
        this.door = door;
        this.light = light;
        this.hvac = hvac;
        this.timestamp = timestamp;
        this.userId = userId;
        this.userName = userName;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
