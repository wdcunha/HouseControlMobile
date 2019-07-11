package pt.ipp.estg.housecontrol.Sensors;

public class TreatMsgReceived {


    public static Sensor parseData(String szResponseLine) {

        if (szResponseLine == null || szResponseLine.length() < 5) {
            return null;
        }

        Sensor mSensor = null;

        // Parse sensorsValueShow class
        String szSensorClass = szResponseLine.substring(0, 2);
        int nSensorClass = Integer.parseInt(szSensorClass);

        String szSensorId = szResponseLine.substring(2, 4);
        int nSensorId = Integer.parseInt(szSensorId);

        switch(nSensorClass) {
            case 0:
                String szTemperature = szResponseLine.substring(4, 6);
                int nSensorTemperature = Integer.parseInt(szTemperature);
                mSensor = new Temperature(nSensorClass, nSensorId, nSensorTemperature, "");
                break;

            case 1:
                String szBlinderPercentage = szResponseLine.substring(4, 7);
                int nBlinderPercentage = Integer.parseInt(szBlinderPercentage);
                mSensor = new Blinder(nSensorClass, nSensorId, nBlinderPercentage, "");
                break;

            case 2:
                String szDoorState = szResponseLine.substring(4, 6);
                int nDoorState = Integer.parseInt(szDoorState);

                boolean bIsDoorOpen = false;
                if (nDoorState == 1) {
                    bIsDoorOpen = true;
                }

                mSensor = new Door(nSensorClass, nSensorId, bIsDoorOpen, "");
                break;

            case 3:
                String szLightState = szResponseLine.substring(4, 6);
                int nLightState = Integer.parseInt(szLightState);

                boolean bIsLightOn = false;
                if (nLightState == 1) {
                    bIsLightOn = true;
                }

                mSensor = new Light(nSensorClass, nSensorId, bIsLightOn, "");
                break;

            case 4:
                String szHVACState = szResponseLine.substring(6, 8);
                String szHVACTemperature = szResponseLine.substring(4, 6);
                int nHVACState = Integer.parseInt(szHVACState);
                int nHVACTemperature = Integer.parseInt(szHVACTemperature);

                boolean bIsHVACOn = false;
                if (nHVACState == 1) {
                    bIsHVACOn = true;
                }

                mSensor = new HVAC(nSensorClass, nSensorId, bIsHVACOn, nHVACTemperature, "");
                break;
        }

        return mSensor;
    }


}
