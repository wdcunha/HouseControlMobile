package pt.ipp.estg.housecontrol;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pt.ipp.estg.housecontrol.Sensors.Door;
import pt.ipp.estg.housecontrol.Sensors.HVAC;
import pt.ipp.estg.housecontrol.Sensors.Light;
import pt.ipp.estg.housecontrol.Sensors.Sensor;

import static java.lang.Integer.parseInt;
import static pt.ipp.estg.housecontrol.Sensors.TreatMsgReceived.parseData;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener myAuthListener;
    String token = null;
    public final static int ONE_SECOND = 1000;
    // Read from the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    Button signin, signout, getdata, control, decblinder, decdoor, dechvac, dechvacIsOn, declight, dectemperature,incblinder, incdoor, inchvac, inchvacIsOn, inclight, inctemperature,
            saveAll, getToken;
    ImageButton saveBlinder, saveDoor, saveHvac, saveHvacIsOn, saveLight, saveTemperature;

    TextView showDataTbox, blinder, door, hvac, hvacIsOn, light, temperature, lblBlinder, lablDoor, lblHvac, lblHvacIsOn, lblLight, lblTemperature;

    String multiLineMessage = "";

    String userName, userEmail, userUid = null;

    Sensor recbBlinderData, recbDoorData, recbHvacData, recbLightData, recbTemperaturerData;

    SensorsValueShow sensorsValueShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_main);

        signin = findViewById(R.id.signinBtn);
        signout = findViewById(R.id.signOutBtn);
        getdata = findViewById(R.id.getInfoBtn);
        control = findViewById(R.id.controlBtn);
        blinder = findViewById(R.id.blinderTxtVw);
        door = findViewById(R.id.doorTxtVw);
        hvac = findViewById(R.id.hvacTxtVw);
        hvacIsOn = findViewById(R.id.hvacIsOnTxtVw);
        light = findViewById(R.id.lightTxtVw);
        temperature = findViewById(R.id.tempTxtVw);
        lblBlinder = findViewById(R.id.labelBlinder);
        lablDoor = findViewById(R.id.labelDoor);
        lblHvac = findViewById(R.id.labelHvac);
        lblHvacIsOn = findViewById(R.id.labelHvacIsOn);
        lblLight = findViewById(R.id.labelLight);
        lblTemperature = findViewById(R.id.labelTemperature);
        showDataTbox = findViewById(R.id.showDataTbox);
        showDataTbox.setMovementMethod(new ScrollingMovementMethod());

        decblinder = findViewById(R.id.decBlinderBtn);
        decdoor = findViewById(R.id.decDoorBtn);
        dechvac = findViewById(R.id.decHvacBtn);
        dechvacIsOn = findViewById(R.id.decHvacIsOnBtn);
        declight = findViewById(R.id.decLightBtn);
        dectemperature = findViewById(R.id.decTemperatureBtn);
        incblinder = findViewById(R.id.incBlinderBtn);
        incdoor = findViewById(R.id.incDoorBtn);
        inchvac = findViewById(R.id.incHvacBtn);
        inchvacIsOn = findViewById(R.id.incHvacIsOnBtn);
        inclight = findViewById(R.id.incLightBtn);
        inctemperature = findViewById(R.id.incTemperatureBtn);

        saveBlinder = findViewById(R.id.saveBlinderBtn);
        saveDoor = findViewById(R.id.saveDoorBtn);
        saveHvac = findViewById(R.id.saveHvacBtn);
        saveHvacIsOn = findViewById(R.id.saveHvacIsOnBtn);
        saveLight = findViewById(R.id.saveLightBtn);
        saveTemperature = findViewById(R.id.saveTemperatureBtn);
        saveAll = findViewById(R.id.saveAllBtn);

        getToken = findViewById(R.id.tokenBtn);

        Intent intent = getIntent();

        if (getIntent() != null){

            String fName = intent.getStringExtra("Title");
            String lName = intent.getStringExtra("Body");

            showDataTbox.setEnabled(true);
            showDataTbox.setVisibility(View.VISIBLE);

            showDataTbox.setText(fName+ "\n" + lName);
        }


        myFirebaseAuth = FirebaseAuth.getInstance();

        myAuthListener = new
                FirebaseAuth.AuthStateListener() {
                    public void onAuthStateChanged(@NonNull FirebaseAuth myFirebaseAuth) {
                        FirebaseUser myFirebaseuser = myFirebaseAuth.getCurrentUser();
                        if (myFirebaseuser != null) {
                            // Utilizador autenticado
                            Toast.makeText(MainActivity.this, "User Authenticated!! " + myFirebaseuser, Toast.LENGTH_SHORT).show();
                            showDataTbox.setText("onAuthStateChanged");


                        } else {
                            // Utilizador não autenticado
                            Toast.makeText(MainActivity.this, "User Not Authenticated!! " + myFirebaseuser, Toast.LENGTH_SHORT).show();

                        }
                    }
                };
//        myFirebaseAuth.AuthStateListener(myAuthListener); // Adicionar o Listener
//        myFirebaseAuth.removeAuthStateListener(myAuthListener); // Remover o Listener
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = myFirebaseAuth.getCurrentUser();
        Toast.makeText(MainActivity.this, "Current user: " + currentUser, Toast.LENGTH_LONG).show();
//        updateUI(currentUser);
        showDataTbox.setText("onStart");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {

                signin.setEnabled(false);
                signin.setVisibility(View.GONE);
                signout.setEnabled(true);
                signout.setVisibility(View.VISIBLE);
                getdata.setEnabled(true);
                getdata.setVisibility(View.VISIBLE);
                control.setEnabled(true);
                control.setVisibility(View.VISIBLE);
                showDataTbox.setEnabled(true);
                showDataTbox.setVisibility(View.VISIBLE);
                getToken.setEnabled(true);
                getToken.setVisibility(View.VISIBLE);

                // Successfully signed in
                FirebaseUser myFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if (myFirebaseUser != null) {
                    userName = myFirebaseUser.getDisplayName();
                    userEmail = myFirebaseUser.getEmail();
                    userUid = myFirebaseUser.getUid();

                    writeNewUser(userUid, userName, userEmail);
                    writeNewToken(getTokenFCM(), userName);
                }
                Toast.makeText(MainActivity.this, "User loged in: " + userName + "\n" + userEmail + "\n" + userUid, Toast.LENGTH_LONG).show();


                // ...
            } else {
                /** Sign in failed. If response is null the user canceled the
                //  sign-in flow using the back button. Otherwise check
                //  response.getError().getErrorCode() and handle the error.
                **/
                System.out.println("Error when trying to Sign in: "+response.getError().getErrorCode());
                Toast.makeText(MainActivity.this, "Error when trying to Sign in: " + response.getError().getErrorCode(), Toast.LENGTH_SHORT).show();
// ...
            }
        }
    }


    public String getCurrentDate() {

        //        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss'Z'");
        String timeStamp = simpleDateFormat.format(new Date());
        Log.d("MainActivity", "Current Timestamp: " + timeStamp);

        return timeStamp;
    }
    /***************************************************************************************************
     *
     * Writes the user info to FRD
     */

    private void writeNewUser(String userId, String name, String email) {

        Utilizador user = new Utilizador(name, email);

        DatabaseReference usersRef = database.getReference("users");

        usersRef.child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(MainActivity.this, "Write user was successful! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(MainActivity.this, "Write user failed!!! " + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /***************************************************************************************************
     *
     * This methods treat about token, specially for saving it in FRD
     */

    public void showToken(View view) {

//        showDataTbox.setText("Token is: "+ getTokenFCM());
        showDataTbox.setText(getTokenFCM());
        Log.d(TAG, "Token is: "+getTokenFCM());

    }

    public String getTokenFCM() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        token = task.getResult().getToken();
                    }
                });
        return token;
    }


    private void writeNewToken(String token, String username) {

        DatabaseReference appsInfo = database.getReference("AppsInfo");

        Toast.makeText(MainActivity.this, "Token: "+token, Toast.LENGTH_SHORT).show();

        TokenFCM tokenToWrite = new TokenFCM(token, username, getCurrentDate());

        appsInfo.child(userUid).setValue(tokenToWrite)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(MainActivity.this, "Write token was successful! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(MainActivity.this, "Write token failed!!! " + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /***************************************************************************************************
     *
     * Sign-in and sign-out methods
     */


    public void signInFunc(View view) {

        // Criar lista com os providers a utilizar
        List<AuthUI.IdpConfig> myProviders = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Criar o Intent e arrancar a Activity
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(myProviders)
                        .setIsSmartLockEnabled(false)
                        .build(), 1000);
    }

    public void signOutFunc(View view) {

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Toast.makeText(MainActivity.this, "SignOut done!! " + task, Toast.LENGTH_LONG).show();

                        signin.setEnabled(true);
                        signin.setVisibility(View.VISIBLE);
                        signout.setEnabled(false);
                        signout.setVisibility(View.GONE);
                        getdata.setEnabled(false);
                        getdata.setVisibility(View.GONE);
                        control.setEnabled(false);
                        control.setVisibility(View.GONE);
                        showDataTbox.setEnabled(false);
                        showDataTbox.setVisibility(View.GONE);

                        blinder.setEnabled(false);
                        blinder.setVisibility(View.GONE);
                        door.setEnabled(false);
                        door.setVisibility(View.GONE);
                        hvac.setEnabled(false);
                        hvac.setVisibility(View.GONE);
                        hvacIsOn.setEnabled(false);
                        hvacIsOn.setVisibility(View.GONE);
                        light.setEnabled(false);
                        light.setVisibility(View.GONE);
                        temperature.setEnabled(false);
                        temperature.setVisibility(View.GONE);
                        lblBlinder.setEnabled(false);
                        lblBlinder.setVisibility(View.GONE);
                        lablDoor.setEnabled(false);
                        lablDoor.setVisibility(View.GONE);
                        lblHvac.setEnabled(false);
                        lblHvac.setVisibility(View.GONE);
                        lblHvacIsOn.setEnabled(false);
                        lblHvacIsOn.setVisibility(View.GONE);
                        lblLight.setEnabled(false);
                        lblLight.setVisibility(View.GONE);
                        lblTemperature.setEnabled(false);
                        lblTemperature.setVisibility(View.GONE);

                        decblinder.setEnabled(false);
                        decblinder.setVisibility(View.GONE);
                        decdoor.setEnabled(false);
                        decdoor.setVisibility(View.GONE);
                        dechvac.setEnabled(false);
                        dechvac.setVisibility(View.GONE);
                        dechvacIsOn.setEnabled(false);
                        dechvacIsOn.setVisibility(View.GONE);
                        declight.setEnabled(false);
                        declight.setVisibility(View.GONE);
                        dectemperature.setEnabled(false);
                        dectemperature.setVisibility(View.GONE);

                        incblinder.setEnabled(false);
                        incblinder.setVisibility(View.GONE);
                        incdoor.setEnabled(false);
                        incdoor.setVisibility(View.GONE);
                        inchvac.setEnabled(false);
                        inchvac.setVisibility(View.GONE);
                        inchvacIsOn.setEnabled(false);
                        inchvacIsOn.setVisibility(View.GONE);
                        inclight.setEnabled(false);
                        inclight.setVisibility(View.GONE);
                        inctemperature.setEnabled(false);
                        inctemperature.setVisibility(View.GONE);

                        saveAll.setEnabled(false);
                        saveAll.setVisibility(View.GONE);
                        saveBlinder.setEnabled(false);
                        saveBlinder.setVisibility(View.GONE);
                        saveDoor.setEnabled(false);
                        saveDoor.setVisibility(View.GONE);
                        saveHvac.setEnabled(false);
                        saveHvac.setVisibility(View.GONE);
                        saveHvacIsOn.setEnabled(false);
                        saveHvacIsOn.setVisibility(View.GONE);
                        saveLight.setEnabled(false);
                        saveLight.setVisibility(View.GONE);
                        saveTemperature.setEnabled(false);

                        getToken.setEnabled(false);
                        getToken.setVisibility(View.GONE);

                    }
                });
    }


    /***************************************************************************************************
     *
     * get datas from FRD and show on screen's app
     *
     */


    public void getDataFRD(View view) {

        showDataTbox.setEnabled(true);
        showDataTbox.setVisibility(View.VISIBLE);
        blinder.setEnabled(false);
        blinder.setVisibility(View.GONE);
        door.setEnabled(false);
        door.setVisibility(View.GONE);
        hvac.setEnabled(false);
        hvac.setVisibility(View.GONE);
        hvacIsOn.setEnabled(false);
        hvacIsOn.setVisibility(View.GONE);
        light.setEnabled(false);
        light.setVisibility(View.GONE);
        temperature.setEnabled(false);
        temperature.setVisibility(View.GONE);
        lblBlinder.setEnabled(false);
        lblBlinder.setVisibility(View.GONE);
        lablDoor.setEnabled(false);
        lablDoor.setVisibility(View.GONE);
        lblHvac.setEnabled(false);
        lblHvac.setVisibility(View.GONE);
        lblHvacIsOn.setEnabled(false);
        lblHvacIsOn.setVisibility(View.GONE);
        lblLight.setEnabled(false);
        lblLight.setVisibility(View.GONE);
        lblTemperature.setEnabled(false);
        lblTemperature.setVisibility(View.GONE);

        decblinder.setEnabled(false);
        decblinder.setVisibility(View.GONE);
        decdoor.setEnabled(false);
        decdoor.setVisibility(View.GONE);
        dechvac.setEnabled(false);
        dechvac.setVisibility(View.GONE);
        dechvacIsOn.setEnabled(false);
        dechvacIsOn.setVisibility(View.GONE);
        declight.setEnabled(false);
        declight.setVisibility(View.GONE);
        dectemperature.setEnabled(false);
        dectemperature.setVisibility(View.GONE);
        incblinder.setEnabled(false);
        incblinder.setVisibility(View.GONE);
        incdoor.setEnabled(false);
        incdoor.setVisibility(View.GONE);
        inchvac.setEnabled(false);
        inchvac.setVisibility(View.GONE);
        inchvacIsOn.setEnabled(false);
        inchvacIsOn.setVisibility(View.GONE);
        inclight.setEnabled(false);
        inclight.setVisibility(View.GONE);
        inctemperature.setEnabled(false);
        inctemperature.setVisibility(View.GONE);

        saveAll.setEnabled(false);
        saveAll.setVisibility(View.GONE);
        saveBlinder.setEnabled(false);
        saveBlinder.setVisibility(View.GONE);
        saveDoor.setEnabled(false);
        saveDoor.setVisibility(View.GONE);
        saveHvac.setEnabled(false);
        saveHvac.setVisibility(View.GONE);
        saveHvacIsOn.setEnabled(false);
        saveHvacIsOn.setVisibility(View.GONE);
        saveLight.setEnabled(false);
        saveLight.setVisibility(View.GONE);
        saveTemperature.setEnabled(false);
        saveTemperature.setVisibility(View.GONE);

        multiLineMessage = "";

        SensorsValueShow sen = readSensorsFRD();

    }

    public void loadSensorsFRD(View view) {

        showDataTbox.setEnabled(false);
        showDataTbox.setVisibility(View.GONE);
        blinder.setEnabled(true);
        blinder.setVisibility(View.VISIBLE);
        door.setEnabled(true);
        door.setVisibility(View.VISIBLE);
        hvac.setEnabled(true);
        hvac.setVisibility(View.VISIBLE);
        hvacIsOn.setEnabled(true);
        hvacIsOn.setVisibility(View.VISIBLE);
        light.setEnabled(true);
        light.setVisibility(View.VISIBLE);
        temperature.setEnabled(true);
        temperature.setVisibility(View.VISIBLE);
        lblBlinder.setEnabled(true);
        lblBlinder.setVisibility(View.VISIBLE);
        lablDoor.setEnabled(true);
        lablDoor.setVisibility(View.VISIBLE);
        lblHvac.setEnabled(true);
        lblHvac.setVisibility(View.VISIBLE);
        lblHvacIsOn.setEnabled(true);
        lblHvacIsOn.setVisibility(View.VISIBLE);
        lblLight.setEnabled(true);
        lblLight.setVisibility(View.VISIBLE);
        lblTemperature.setEnabled(true);
        lblTemperature.setVisibility(View.VISIBLE);

        decblinder.setEnabled(true);
        decblinder.setVisibility(View.VISIBLE);
        decdoor.setEnabled(true);
        decdoor.setVisibility(View.VISIBLE);
        dechvac.setEnabled(true);
        dechvac.setVisibility(View.VISIBLE);
        dechvacIsOn.setEnabled(true);
        dechvacIsOn.setVisibility(View.VISIBLE);
        declight.setEnabled(true);
        declight.setVisibility(View.VISIBLE);
        dectemperature.setEnabled(true);
        dectemperature.setVisibility(View.VISIBLE);
        incblinder.setEnabled(true);
        incblinder.setVisibility(View.VISIBLE);
        incdoor.setEnabled(true);
        incdoor.setVisibility(View.VISIBLE);
        inchvac.setEnabled(true);
        inchvac.setVisibility(View.VISIBLE);
        inchvacIsOn.setEnabled(true);
        inchvacIsOn.setVisibility(View.VISIBLE);
        inclight.setEnabled(true);
        inclight.setVisibility(View.VISIBLE);
        inctemperature.setEnabled(true);
        inctemperature.setVisibility(View.VISIBLE);

        DatabaseReference sensorRef = database.getReference("Sensor");

        sensorsValueShow = new SensorsValueShow();

        sensorRef.child("server").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    switch (ds.getKey()){
                        case "blinder":
                            System.out.println("blinder ds.getValue(): "+ ds.getValue());
                            recbBlinderData = parseData(ds.getValue().toString());
                            System.out.println("----> recbBlinderData.getValue: "+ recbBlinderData.getValue());

                            sensorsValueShow.setBlinder(String.valueOf(recbBlinderData.getValue()));
                            lblBlinder.setText(ds.getKey());
                            blinder.setText(String.valueOf(recbBlinderData.getValue()));
                            break;
                        case "door":
                            recbDoorData = parseData(ds.getValue().toString());

                            sensorsValueShow.setDoor(String.valueOf(((Door)recbDoorData).isOpen()));
                            lablDoor.setText(ds.getKey());
                            door.setText(String.valueOf(((Door)recbDoorData).isOpen()));
                            break;
                        case "hvac":
                            recbHvacData = parseData(ds.getValue().toString());

                            sensorsValueShow.setHvac("On/off: "+((HVAC)recbHvacData).isOn()+", Temp: "+recbHvacData.getValue());
                            lblHvac.setText(ds.getKey()+"(Temp)");
                            lblHvacIsOn.setText(ds.getKey()+"(On/off)");
                            hvac.setText(String.valueOf(recbHvacData.getValue()));
                            hvacIsOn.setText(String.valueOf(((HVAC)recbHvacData).isOn()));
                            break;
                        case "light":
                            recbLightData = parseData(ds.getValue().toString());

                            sensorsValueShow.setLight(String.valueOf(recbLightData.getValue()));
                            lblLight.setText(ds.getKey());
                            light.setText(String.valueOf(((Light)recbLightData).isOn()));
                            break;
                        case "temperature":
                            recbTemperaturerData = parseData(ds.getValue().toString());

                            sensorsValueShow.setTemperature(String.valueOf(recbTemperaturerData.getValue()));
                            lblTemperature.setText(ds.getKey());
                            temperature.setText(String.valueOf(recbTemperaturerData.getValue()));
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read SensorsValueShow value from loadSensorsFRD method!", error.toException());
            }
        });
    }

    public Utilizador readUsersFRD() {

        DatabaseReference usersRef  = database.getReference("users");

        Utilizador userDataFRD = new Utilizador();

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                 This method is called once with the initial value and again
//                 whenever data at this location is updated.

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Utilizador userDataFRD = ds.getValue(Utilizador.class);
                    System.out.println("Nome: " + userDataFRD.getNome());
                    System.out.println("Email: " + userDataFRD.getEmail());
                    multiLineMessage = multiLineMessage + "&lt;br&gt;" + "Nome: " + userDataFRD.getNome();
                    multiLineMessage = multiLineMessage + "&lt;br&gt;" + "Email: " + userDataFRD.getEmail();

                }

                showDataTbox.setText(Html.fromHtml(Html.fromHtml(multiLineMessage).toString()));

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read Users value.", error.toException());
            }
        });

        return userDataFRD;
    }

    /***************************************************************************************************
     *
     * This code block is responsible to create control for increase and decrease values for sensorsValueShow
     */


    public void increaseBlinder(View view) {
        int val = parseInt(String.valueOf(blinder.getText()));

        if ((val >= 0) && (val < 100)) {
            val += 1;
        }
        else {
            Toast.makeText(MainActivity.this, "Number out of range: " + val, Toast.LENGTH_LONG).show();
        }
        changeBlinderField(val);

    }

    public void decreaseBlinder(View view) {
        int val = parseInt(String.valueOf(blinder.getText()));

        if ((val > 0) && (val <= 100)) {
            val -= 1;
        }
        else {
            Toast.makeText(MainActivity.this, "Number out of range: " + val, Toast.LENGTH_LONG).show();
        }
        changeBlinderField(val);
    }

    private void changeBlinderField(int value) {

        blinder.setText(Integer.toString(value));

        saveBlinder.setSaveEnabled(true);
        saveBlinder.setVisibility(View.VISIBLE);

        if (saveBlinder.getVisibility() == View.VISIBLE && saveDoor.getVisibility() == View.VISIBLE && saveHvac.getVisibility() == View.VISIBLE
                && saveLight.getVisibility() == View.VISIBLE && saveTemperature.getVisibility() == View.VISIBLE && saveHvacIsOn.getVisibility() == View.VISIBLE) {

            saveAll.setEnabled(true);
            saveAll.setVisibility(View.VISIBLE);
        }
    }

    public void saveBlindChangeToFRD(View view) {

        DatabaseReference sensorRef = database.getReference("Sensor").child("mobile");

        recbBlinderData.setValue(parseInt(String.valueOf(blinder.getText())));

        sensorRef.child("blinder").setValue(recbBlinderData.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(MainActivity.this, "Write for blinder was successful! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(MainActivity.this, "Write for blinder failed!!! " + e, Toast.LENGTH_SHORT).show();
                    }
                });
        saveBlinder.setSaveEnabled(false);
        saveBlinder.setVisibility(View.GONE);
    }

    public void increaseDoor(View view) {
        boolean val = Boolean.parseBoolean(String.valueOf(door.getText()));

        if (val == false) val = true;
        changeDoorField(val);

    }

    public void decreaseDoor(View view) {
        boolean val = Boolean.parseBoolean(String.valueOf(door.getText()));

        if (val == true) val = false;
        changeDoorField(val);

    }

    private void changeDoorField(boolean value) {

        door.setText(Boolean.toString(value));

        saveDoor.setSaveEnabled(true);
        saveDoor.setVisibility(View.VISIBLE);

        if (saveBlinder.getVisibility() == View.VISIBLE && saveDoor.getVisibility() == View.VISIBLE && saveHvac.getVisibility() == View.VISIBLE
                && saveLight.getVisibility() == View.VISIBLE && saveTemperature.getVisibility() == View.VISIBLE && saveHvacIsOn.getVisibility() == View.VISIBLE) {

            saveAll.setEnabled(true);
            saveAll.setVisibility(View.VISIBLE);
        }

    }

    public void saveDoorChangeToFRD(View view) {

        DatabaseReference sensorRef = database.getReference("Sensor").child("mobile");

        ((Door)recbDoorData).setIsOpen(Boolean.parseBoolean(String.valueOf(door.getText())));

        sensorRef.child("door").setValue(recbDoorData.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(MainActivity.this, "Write for door was successful! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(MainActivity.this, "Write for door failed!!! " + e, Toast.LENGTH_SHORT).show();
                    }
                });
        saveDoor.setSaveEnabled(false);
        saveDoor.setVisibility(View.GONE);
    }

    public void increaseLight(View view) {
        boolean val = Boolean.parseBoolean(String.valueOf(light.getText()));

        if (val == false) val = true;
        changeLightField(val);

    }

    public void decreaseLight(View view) {
        boolean val = Boolean.parseBoolean(String.valueOf(light.getText()));

        if (val == true) val = false;
        changeLightField(val);
    }

    private void changeLightField(boolean value) {

        light.setText(Boolean.toString(value));

        saveLight.setSaveEnabled(true);
        saveLight.setVisibility(View.VISIBLE);

        if (saveBlinder.getVisibility() == View.VISIBLE && saveDoor.getVisibility() == View.VISIBLE && saveHvac.getVisibility() == View.VISIBLE
                && saveLight.getVisibility() == View.VISIBLE && saveTemperature.getVisibility() == View.VISIBLE && saveHvacIsOn.getVisibility() == View.VISIBLE) {

            saveAll.setEnabled(true);
            saveAll.setVisibility(View.VISIBLE);
        }
    }

    public void saveLightChangeToFRD(View view) {

        DatabaseReference sensorRef = database.getReference("Sensor").child("mobile");

        ((Light)recbLightData).setIsOn(Boolean.parseBoolean(String.valueOf(light.getText())));

        sensorRef.child("light").setValue(recbLightData.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(MainActivity.this, "Write for light was successful! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(MainActivity.this, "Write for light failed!!! " + e, Toast.LENGTH_SHORT).show();
                    }
                });
        saveLight.setSaveEnabled(false);
        saveLight.setVisibility(View.GONE);
    }


    public void increaseTemperature(View view) {
        int val = parseInt(String.valueOf(temperature.getText()));

        if ((val >= 15) && (val < 30)) {
            val += 1;
        } else {
            Toast.makeText(MainActivity.this, "Number out of range: " + val, Toast.LENGTH_LONG).show();
        }
        changeTemperatureField(val);

    }

    public void decreaseTemperature(View view) {
        int val = parseInt(String.valueOf(temperature.getText()));

        if ((val > 15) && (val <= 30)) {
            val -= 1;
        } else {
            Toast.makeText(MainActivity.this, "Number out of range: " + val, Toast.LENGTH_LONG).show();
        }
        changeTemperatureField(val);
    }

    private void changeTemperatureField(int value) {

        temperature.setText(Integer.toString(value));

        saveTemperature.setSaveEnabled(true);
        saveTemperature.setVisibility(View.VISIBLE);

        if (saveBlinder.getVisibility() == View.VISIBLE && saveDoor.getVisibility() == View.VISIBLE && saveHvac.getVisibility() == View.VISIBLE
                && saveLight.getVisibility() == View.VISIBLE && saveTemperature.getVisibility() == View.VISIBLE && saveHvacIsOn.getVisibility() == View.VISIBLE) {

            saveAll.setEnabled(true);
            saveAll.setVisibility(View.VISIBLE);
        }

    }

    public void saveTemperatureChangeToFRD(View view) {

        DatabaseReference sensorRef = database.getReference("Sensor").child("mobile");

        recbTemperaturerData.setValue(parseInt(String.valueOf(temperature.getText())));

        sensorRef.child("temperature").setValue(recbTemperaturerData.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(MainActivity.this, "Write for temperature was successful! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(MainActivity.this, "Write for temperature failed!!! " + e, Toast.LENGTH_SHORT).show();
                    }
                });
        saveTemperature.setSaveEnabled(false);
        saveTemperature.setVisibility(View.GONE);

    }


    public void increaseHvac(View view) {
        int val = parseInt(String.valueOf(hvac.getText()));

        if ((val >= 15) && (val < 30)) {
            val += 1;
        } else {
            Toast.makeText(MainActivity.this, "Number out of range: " + val, Toast.LENGTH_LONG).show();
        }
        changeHvacField(val);

    }

    public void decreaseHvac(View view) {
        int val = parseInt(String.valueOf(hvac.getText()));

        if ((val > 15) && (val <= 30)) {
            val -= 1;
        } else {
            Toast.makeText(MainActivity.this, "Number out of range: " + val, Toast.LENGTH_LONG).show();
        }
        changeHvacField(val);
    }

    private void changeHvacField(int value) {

        hvac.setText(Integer.toString(value));

        saveHvac.setSaveEnabled(true);
        saveHvac.setVisibility(View.VISIBLE);

        if (saveBlinder.getVisibility() == View.VISIBLE && saveDoor.getVisibility() == View.VISIBLE && saveHvac.getVisibility() == View.VISIBLE
                && saveLight.getVisibility() == View.VISIBLE && saveTemperature.getVisibility() == View.VISIBLE && saveHvacIsOn.getVisibility() == View.VISIBLE) {

            saveAll.setEnabled(true);
            saveAll.setVisibility(View.VISIBLE);
        }

    }

    public void saveHvacChangeToFRD(View view) {

        DatabaseReference sensorRef = database.getReference("Sensor").child("mobile");

        recbHvacData.setValue(parseInt(String.valueOf(hvac.getText())));

        sensorRef.child("hvac").setValue(recbHvacData.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(MainActivity.this, "Write for hvac was successful! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(MainActivity.this, "Write for hvac failed!!! " + e, Toast.LENGTH_SHORT).show();
                    }
                });
        saveHvac.setEnabled(false);
        saveHvac.setVisibility(View.GONE);

    }


    public void increaseHvacIsOn(View view) {
        boolean val = Boolean.parseBoolean(String.valueOf(hvacIsOn.getText()));

        if (val == false) val = true;
        changeHvacIsOnField(val);

    }

    public void decreaseHvacIsOn(View view) {
        boolean val = Boolean.parseBoolean(String.valueOf(hvacIsOn.getText()));

        if (val == true) val = false;
        changeHvacIsOnField(val);
    }

    private void changeHvacIsOnField(boolean value) {

        hvacIsOn.setText(Boolean.toString(value));

        saveHvacIsOn.setSaveEnabled(true);
        saveHvacIsOn.setVisibility(View.VISIBLE);

        if (saveBlinder.getVisibility() == View.VISIBLE && saveDoor.getVisibility() == View.VISIBLE && saveHvac.getVisibility() == View.VISIBLE
                && saveLight.getVisibility() == View.VISIBLE && saveTemperature.getVisibility() == View.VISIBLE && saveHvacIsOn.getVisibility() == View.VISIBLE) {

            saveAll.setEnabled(true);
            saveAll.setVisibility(View.VISIBLE);
        }

    }

    public void saveHvacIsOnChangeToFRD(View view) {

        DatabaseReference sensorRef = database.getReference("Sensor").child("mobile");

        ((HVAC)recbHvacData).setIsOn(Boolean.parseBoolean(String.valueOf(hvacIsOn.getText())));

        sensorRef.child("hvac").setValue(recbHvacData.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(MainActivity.this, "Write for hvacIsOn was successful! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(MainActivity.this, "Write for hvacIsOn failed!!! " + e, Toast.LENGTH_SHORT).show();
                    }
                });
        saveHvacIsOn.setEnabled(false);
        saveHvacIsOn.setVisibility(View.GONE);

    }

    /***************************************************************************************************
     *
     * This part create log and writes it to FRD the changes made by user on the screen
     */

    public void saveAllSensorChangesToFRDClick(View view) {


        saveAllSensorChangesToFRD((String) blinder.getText(), (String) door.getText(), (String) hvac.getText(), (String) hvacIsOn.getText(), (String) light.getText(), (String) temperature.getText());

    }


    private void saveAllSensorChangesToFRD(String blinder, String door, String hvac, String hvacIsOn, String light, String temperature) {

        SensorsValueShow sensorsValueShow = new SensorsValueShow(blinder, door, hvac, light, temperature);

        createDataLogInFRD(blinder, door, hvac, light, temperature);

        DatabaseReference sensorRef = database.getReference("Sensor");

        sensorRef.setValue(sensorsValueShow)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(MainActivity.this, "Write was successful! ", Toast.LENGTH_SHORT).show();
                        saveBlinder.setSaveEnabled(false);
                        saveBlinder.setVisibility(View.GONE);
                        saveDoor.setSaveEnabled(false);
                        saveDoor.setVisibility(View.GONE);
                        saveHvac.setSaveEnabled(false);
                        saveHvac.setVisibility(View.GONE);
                        saveHvacIsOn.setSaveEnabled(false);
                        saveHvacIsOn.setVisibility(View.GONE);
                        saveLight.setSaveEnabled(false);
                        saveLight.setVisibility(View.GONE);
                        saveTemperature.setSaveEnabled(false);
                        saveTemperature.setVisibility(View.GONE);
                        saveAll.setEnabled(false);
                        saveAll.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(MainActivity.this, "Write failed!!! " + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /***************************************************************************************************
     *
     * This method creates a Log to have an history of changes and write it in FRD
     */

    private void createDataLogInFRD(String blinder, String door, String hvac, String light, String temperature) {

        DataChanges createLog = new DataChanges(blinder, door, hvac, light, temperature, getCurrentDate());

        System.out.println("userId: "+userUid);
        System.out.println("timeStamp: "+getCurrentDate());

        DatabaseReference dataChangesRef = database.getReference("Log");

        dataChangesRef.child(userUid).setValue(createLog)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(MainActivity.this, "Data Log was saved successfully! ", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(MainActivity.this, "Data Log saving failed!!! " + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /***************************************************************************************************
     *
     * Code for reading infos from existing in FRD and show them on screen app
     */

    public SensorsValueShow readSensorsFRD() {

        DatabaseReference sensorRef = database.getReference("Sensor");

        sensorsValueShow = new SensorsValueShow();

        sensorRef.child("server").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.


                for (DataSnapshot ds : dataSnapshot.getChildren()){

//                    SensorsValueShow sensorDataFRD = ds.getValue(SensorsValueShow.class);
//                    System.out.println("Temperature: " + sensorDataFRD.getTemperature());
//                    multiLineMessage = "Temperature: " + sensorDataFRD.getTemperature() + "&lt;br&gt;" + multiLineMessage;
//                    System.out.println("Blinder: " + sensorDataFRD.getBlinder());
//                    multiLineMessage = "Blinder: " + sensorDataFRD.getBlinder() + "&lt;br&gt;" + multiLineMessage;
//                    System.out.println("Door: " + sensorDataFRD.getDoor());
//                    multiLineMessage = "Door: " + sensorDataFRD.getDoor() + "&lt;br&gt;" + multiLineMessage;
//                    System.out.println("Light: " + sensorDataFRD.getLight());
//                    multiLineMessage = "Light: " + sensorDataFRD.getLight() + "&lt;br&gt;" + multiLineMessage;
//                    System.out.println("Hvac: " + sensorDataFRD.getHvac());
//                    multiLineMessage = "Light: " + sensorDataFRD.getLight() + "&lt;br&gt;" + multiLineMessage;

//                    System.out.println("getKey: " + ds.getKey());
//                    multiLineMessage = multiLineMessage + "&lt;br&gt;" + "getKey: " + ds.getKey();
//                    System.out.println("getValue: " + ds.getValue());
//                    multiLineMessage = multiLineMessage + "&lt;br&gt;" + "getValue: " + ds.getValue();

//                    showDataTbox.setText(Html.fromHtml(Html.fromHtml(multiLineMessage).toString()));

                    switch (ds.getKey()){
                        case "blinder":
                            sensorsValueShow.setBlinder(ds.getValue().toString());
                            System.out.println("switchgetBlinder: " + sensorsValueShow.getBlinder());
                            multiLineMessage = "Blinder: " + sensorsValueShow.getBlinder() + "&lt;br&gt;" + multiLineMessage;
                            break;
                        case "door":
                            sensorsValueShow.setDoor(ds.getValue().toString());
                            System.out.println("switchgetDoor: " + sensorsValueShow.getDoor());
                            multiLineMessage = "Door: " + sensorsValueShow.getDoor() + "&lt;br&gt;" + multiLineMessage;
                            break;
                        case "hvac":
                            sensorsValueShow.setHvac(ds.getValue().toString());
                            System.out.println("switchgetHvac: " + sensorsValueShow.getHvac());
                            multiLineMessage = "Hvac: " + sensorsValueShow.getHvac() + "&lt;br&gt;" + multiLineMessage;
                            break;
                        case "light":
                            sensorsValueShow.setLight(ds.getValue().toString());
                            System.out.println("switchgetLight: " + sensorsValueShow.getLight());
                            multiLineMessage = "Light: " + sensorsValueShow.getLight() + "&lt;br&gt;" + multiLineMessage;
                            break;
                        case "temperature":
//                            MainActivity.this.sensorsValueShow.setTemperature(ds.getValue().toString());
//                            showDataTbox.setText(ds.getValue().toString());
                            sensorsValueShow.setTemperature(ds.getValue().toString());
                            System.out.println("switchgetTemperature: " + sensorsValueShow.getTemperature());
                            multiLineMessage = "Temperature: " + sensorsValueShow.getTemperature() + "&lt;br&gt;" + multiLineMessage;
                            break;
                    }
                }
                    showDataTbox.setText(Html.fromHtml(Html.fromHtml(multiLineMessage).toString()));

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read SensorsValueShow value.", error.toException());
            }
        });


        System.out.println("getBlinder: " + sensorsValueShow.getBlinder());
        System.out.println("getDoor: " + sensorsValueShow.getDoor());
        System.out.println("getHvac: " + sensorsValueShow.getHvac());
        System.out.println("getLight: " + sensorsValueShow.getLight());
        System.out.println("getTemperature: " + sensorsValueShow.getTemperature());

        showDataTbox.setText(Html.fromHtml(Html.fromHtml(multiLineMessage).toString()));

        return sensorsValueShow;
    }

    public DataChanges dataChangesFRD() {

        DatabaseReference dataChangesRef = database.getReference("Data").child("Changes");

        DataChanges dataChanges = new DataChanges();

        dataChangesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (DataSnapshot ds : dataSnapshot.getChildren()){

//                    System.out.println("Temperature (*): " + sensorDataFRD.getTemperature());
//                    System.out.println("Blinder (*): " + sensorDataFRD.getBlinder());
//                    System.out.println("Door (*): " + sensorDataFRD.getDoor());
//                    System.out.println("Light (*): " + sensorDataFRD.getLight());
//                    System.out.println("Hvac (*): " + sensorDataFRD.getHvac());

//                    DataChanges dataChangeFRD = ds.getValue(DataChanges.class);
//                    System.out.println("Blinder (*): " + dataChangeFRD.getBlinder());
//                    multiLineMessage = "Blinder (*): " + dataChangeFRD.getBlinder() + "&lt;br&gt;" + multiLineMessage;
//                    System.out.println("Timestamp: " + dataChangeFRD.getDoor());
//                    multiLineMessage = "Timestamp: " + dataChangeFRD.getDoor() + "&lt;br&gt;" + multiLineMessage;
//                    System.out.println("Light: " + dataChangeFRD.getLight());
//                    multiLineMessage = "Light: " + dataChangeFRD.getLight() + "&lt;br&gt;" + multiLineMessage;
//                    System.out.println("Hvac: " + dataChangeFRD.getHvac());
//                    multiLineMessage = "Hvac: " + dataChangeFRD.getHvac() + "&lt;br&gt;" + multiLineMessage;
//
//                    showDataTbox.setText(Html.fromHtml(Html.fromHtml(multiLineMessage).toString()));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read Data Changes value.", error.toException());
            }
        });
        return dataChanges;
    }

}
