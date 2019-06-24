package pt.ipp.estg.housecontrol;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener myAuthListener;

    // Read from the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    Button signin, signout, getdata, control, decblinder, decdoor, dechvac, declight, dectemperature,incblinder, incdoor, inchvac, inclight, inctemperature,
            saveAll, getToken;
    ImageButton saveBlinder, saveDoor, saveHvac, saveLight, saveTemperature;

    TextView showDataTbox, blinder, door, hvac, light, temperature, lblBlinder, lablDoor, lblHvac, lblLight, lblTemperature;

    String multiLineMessage = "";

    String userName, userEmail, userUid = null;


    public Sensor sensor;

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
        light = findViewById(R.id.lightTxtVw);
        temperature = findViewById(R.id.tempTxtVw);
        lblBlinder = findViewById(R.id.labelBlinder);
        lablDoor = findViewById(R.id.labelDoor);
        lblHvac = findViewById(R.id.labelHvac);
        lblLight = findViewById(R.id.labelLight);
        lblTemperature = findViewById(R.id.labelTemperature);
        showDataTbox = findViewById(R.id.showDataTbox);
        showDataTbox.setMovementMethod(new ScrollingMovementMethod());

        decblinder = findViewById(R.id.decBlinderBtn);
        decdoor = findViewById(R.id.decDoorBtn);
        dechvac = findViewById(R.id.decHvacBtn);
        declight = findViewById(R.id.decLightBtn);
        dectemperature = findViewById(R.id.decTemperatureBtn);
        incblinder = findViewById(R.id.incBlinderBtn);
        incdoor = findViewById(R.id.incDoorBtn);
        inchvac = findViewById(R.id.incHvacBtn);
        inclight = findViewById(R.id.incLightBtn);
        inctemperature = findViewById(R.id.incTemperatureBtn);

        saveBlinder = findViewById(R.id.saveBlinderBtn);
        saveDoor = findViewById(R.id.saveDoorBtn);
        saveHvac = findViewById(R.id.saveHvacBtn);
        saveLight = findViewById(R.id.saveLightBtn);
        saveTemperature = findViewById(R.id.saveTemperatureBtn);
        saveAll = findViewById(R.id.saveAllBtn);

        getToken = findViewById(R.id.tokenBtn);

        myFirebaseAuth = FirebaseAuth.getInstance();

        myAuthListener = new
                FirebaseAuth.AuthStateListener() {
                    public void onAuthStateChanged(@NonNull FirebaseAuth myFirebaseAuth) {
                        FirebaseUser myFirebaseuser = myFirebaseAuth.getCurrentUser();
                        if (myFirebaseuser != null) {
                            // Utilizador autenticado
                            Toast.makeText(MainActivity.this, "User Authenticated!! " + myFirebaseuser, Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(MainActivity.this, "Write was successful! ", Toast.LENGTH_SHORT).show();
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
                        saveLight.setEnabled(false);
                        saveLight.setVisibility(View.GONE);
                        saveTemperature.setEnabled(false);

                        getToken.setEnabled(false);
                        getToken.setVisibility(View.GONE);

                    }
                });
    }


    public void fsbGetToken(View view) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        String token = task.getResult().getToken();
                        showDataTbox.setText("Token is: "+ token);
                        Log.d(TAG, "Token is: "+token);
                    }
                });

    }


    /***************************************************************************************************
     *
     * get datas from FRD and show on screen's app
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
        saveLight.setEnabled(false);
        saveLight.setVisibility(View.GONE);
        saveTemperature.setEnabled(false);
        saveTemperature.setVisibility(View.GONE);

        multiLineMessage = "";

        Sensor sen = readSensorsFRD();

//        Utilizador user = readUsersFRD();

//        System.out.println(readUsersFRD().getNome());
//        System.out.println(readUsersFRD().getEmail());

//        multiLineMessage = multiLineMessage + "&lt;br&gt;" + "getTemperature: " + sen.getTemperature();

//        multiLineMessage = multiLineMessage + "&lt;br&gt;" + "Nome: " + readUsersFRD().getNome();
//        multiLineMessage = multiLineMessage + "&lt;br&gt;" + "Email: " + readUsersFRD().getEmail();
//
//        multiLineMessage = multiLineMessage + "&lt;br&gt;" + "Blinder: " + readSensorsFRD().getBlinder();
//        multiLineMessage = multiLineMessage + "&lt;br&gt;" + "Door: " + readSensorsFRD().getDoor();
//        multiLineMessage = multiLineMessage + "&lt;br&gt;" + "Hvac: " + readSensorsFRD().getHvac();
//        multiLineMessage = multiLineMessage + "&lt;br&gt;" + "Light: " + readSensorsFRD().getLight();
//        multiLineMessage = multiLineMessage + "&lt;br&gt;" + "Temperature: " + readSensorsFRD().getTemperature();

//        showDataTbox.setText(Html.fromHtml(Html.fromHtml(multiLineMessage).toString()));

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
        inclight.setEnabled(true);
        inclight.setVisibility(View.VISIBLE);
        inctemperature.setEnabled(true);
        inctemperature.setVisibility(View.VISIBLE);

        DatabaseReference sensorRef = database.getReference("Sensor");

        sensor = new Sensor();

        sensorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    switch (ds.getKey()){
                        case "blinder":
                            sensor.setBlinder(ds.getValue().toString());
                            lblBlinder.setText(ds.getKey());
                            blinder.setText(ds.getValue().toString());
                            break;
                        case "door":
                            sensor.setDoor(ds.getValue().toString());
                            lablDoor.setText(ds.getKey());
                            door.setText(ds.getValue().toString());
                            break;
                        case "hvac":
                            sensor.setHvac(ds.getValue().toString());
                            lblHvac.setText(ds.getKey());
                            hvac.setText(ds.getValue().toString());
                            break;
                        case "light":
                            light.setText(ds.getValue().toString());
                            lblLight.setText(ds.getKey());
                            sensor.setLight(ds.getValue().toString());
                            break;
                        case "temperature":
                            temperature.setText(ds.getValue().toString());
                            lblTemperature.setText(ds.getKey());
                            sensor.setTemperature(ds.getValue().toString());
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read Sensor value from loadSensorsFRD method!", error.toException());
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

                    System.out.println("Email: " + userDataFRD.getEmail());
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
     * This code block is responsible to create control for increase and decrease values for sensors
     */


    public void increaseBlinder(View view) {
        int val = Integer.parseInt(String.valueOf(blinder.getText()));

        if ((val >= 0) && (val < 100)) {
            val += 1;
        }
        else {
            Toast.makeText(MainActivity.this, "Number out of range: " + val, Toast.LENGTH_LONG).show();
        }
        changeBlinderField(val);

    }

    public void decreaseBlinder(View view) {
        int val = Integer.parseInt(String.valueOf(blinder.getText()));

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
                && saveLight.getVisibility() == View.VISIBLE && saveTemperature.getVisibility() == View.VISIBLE) {

            saveAll.setEnabled(true);
            saveAll.setVisibility(View.VISIBLE);
        }

    }

    public void saveBlindChangeToFRD(View view) {

        DatabaseReference sensorRef = database.getReference("Sensor");

        sensorRef.child("blinder").setValue(blinder.getText())
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
                && saveLight.getVisibility() == View.VISIBLE && saveTemperature.getVisibility() == View.VISIBLE) {

            saveAll.setEnabled(true);
            saveAll.setVisibility(View.VISIBLE);
        }

    }

    public void saveDoorChangeToFRD(View view) {

        DatabaseReference sensorRef = database.getReference("Sensor");

        sensorRef.child("door").setValue(door.getText())
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
                && saveLight.getVisibility() == View.VISIBLE && saveTemperature.getVisibility() == View.VISIBLE) {

            saveAll.setEnabled(true);
            saveAll.setVisibility(View.VISIBLE);
        }

    }

    public void saveLightChangeToFRD(View view) {

        DatabaseReference sensorRef = database.getReference("Sensor");

        sensorRef.child("light").setValue(light.getText())
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
        int val = Integer.parseInt(String.valueOf(temperature.getText()));

        if ((val >= 15) && (val < 30)) {
            val += 1;
        } else {
            Toast.makeText(MainActivity.this, "Number out of range: " + val, Toast.LENGTH_LONG).show();
        }
        changeTemperatureField(val);

    }

    public void decreaseTemperature(View view) {
        int val = Integer.parseInt(String.valueOf(temperature.getText()));

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
                && saveLight.getVisibility() == View.VISIBLE && saveTemperature.getVisibility() == View.VISIBLE) {

            saveAll.setEnabled(true);
            saveAll.setVisibility(View.VISIBLE);
        }

    }

    public void saveTemperatureChangeToFRD(View view) {

        DatabaseReference sensorRef = database.getReference("Sensor");

        sensorRef.child("temperature").setValue(temperature.getText())
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
        int val = Integer.parseInt(String.valueOf(hvac.getText()));

        if ((val >= 15) && (val < 30)) {
            val += 1;
        } else {
            Toast.makeText(MainActivity.this, "Number out of range: " + val, Toast.LENGTH_LONG).show();
        }
        changeHvacField(val);

    }

    public void decreaseHvac(View view) {
        int val = Integer.parseInt(String.valueOf(hvac.getText()));

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
                && saveLight.getVisibility() == View.VISIBLE && saveTemperature.getVisibility() == View.VISIBLE) {

            saveAll.setEnabled(true);
            saveAll.setVisibility(View.VISIBLE);
        }

    }

    public void saveHvacChangeToFRD(View view) {

        DatabaseReference sensorRef = database.getReference("Sensor");

        sensorRef.child("hvac").setValue(hvac.getText())
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

    /***************************************************************************************************
     *
     * This part writes to FRD the changes made by user on the screen
     */

    public void saveAllSensorChangesToFRDClick(View view) {


        saveAllSensorChangesToFRD((String) blinder.getText(), (String) door.getText(), (String) hvac.getText(), (String) light.getText(), (String) temperature.getText());

    }


    private void saveAllSensorChangesToFRD(String blinder, String door, String hvac, String light, String temperature) {

        Sensor sensors = new Sensor(blinder, door, hvac, light, temperature);

        createDataLogInFRD(blinder, door, hvac, light, temperature);

        DatabaseReference sensorRef = database.getReference("Sensor");

        sensorRef.setValue(sensors)
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

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss'Z'");
        String timeStamp = simpleDateFormat.format(new Date());
        Log.d("MainActivity", "Current Timestamp: " + timeStamp);

        DataChanges createLog = new DataChanges(blinder, door, hvac, light, temperature, timeStamp);

        System.out.println("userId: "+userUid);
        System.out.println("timeStamp: "+timeStamp);

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

    public Sensor readSensorsFRD() {

        DatabaseReference sensorRef = database.getReference("Sensor");

        sensor = new Sensor();

        sensorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.


                for (DataSnapshot ds : dataSnapshot.getChildren()){

//                    Sensor sensorDataFRD = ds.getValue(Sensor.class);
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
                            sensor.setBlinder(ds.getValue().toString());
                            System.out.println("switchgetBlinder: " + sensor.getBlinder());
                            multiLineMessage = "Blinder: " + sensor.getBlinder() + "&lt;br&gt;" + multiLineMessage;
                            break;
                        case "door":
                            sensor.setDoor(ds.getValue().toString());
                            System.out.println("switchgetDoor: " + sensor.getDoor());
                            multiLineMessage = "Door: " + sensor.getDoor() + "&lt;br&gt;" + multiLineMessage;
                            break;
                        case "hvac":
                            sensor.setHvac(ds.getValue().toString());
                            System.out.println("switchgetHvac: " + sensor.getHvac());
                            multiLineMessage = "Hvac: " + sensor.getHvac() + "&lt;br&gt;" + multiLineMessage;
                            break;
                        case "light":
                            sensor.setLight(ds.getValue().toString());
                            System.out.println("switchgetLight: " + sensor.getLight());
                            multiLineMessage = "Light: " + sensor.getLight() + "&lt;br&gt;" + multiLineMessage;
                            break;
                        case "temperature":
//                            MainActivity.this.sensor.setTemperature(ds.getValue().toString());
//                            showDataTbox.setText(ds.getValue().toString());
                            sensor.setTemperature(ds.getValue().toString());
                            System.out.println("switchgetTemperature: " + sensor.getTemperature());
                            multiLineMessage = "Temperature: " + sensor.getTemperature() + "&lt;br&gt;" + multiLineMessage;
                            break;
                    }
                }
                    showDataTbox.setText(Html.fromHtml(Html.fromHtml(multiLineMessage).toString()));

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read Sensor value.", error.toException());
            }
        });


        System.out.println("getBlinder: " + sensor.getBlinder());
        System.out.println("getDoor: " + sensor.getDoor());
        System.out.println("getHvac: " + sensor.getHvac());
        System.out.println("getLight: " + sensor.getLight());
        System.out.println("getTemperature: " + sensor.getTemperature());

        showDataTbox.setText(Html.fromHtml(Html.fromHtml(multiLineMessage).toString()));

        return sensor;
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
