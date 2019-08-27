# The Document of Angel doctor's bluetooth device Android SDK 


## 1、Permission Assignment

Declare the Bluetooth permission(s) in your application manifest file. Also declare the Bluetooth Service,For example:


```java
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-feature    android:name="android.hardware.bluetooth_le"    android:required="false" />

<service
          android:name="com.jumper.bluetoothdevicelib.service.BluetoothLeService"
          android:enabled="true"
          android:exported="false" />
```


## 2、SDK Use

Getting started

```java
ADBlueTooth adBlueTooth = null;
if(!BlueUnit.isHaveBleFeature(this)){
	Toast.makeText(this, "The device does not support Bluetooth 4.0", Toast.LENGTH_LONG).show();
}else if(!BlueUnit.isEnabled(this)){
	Toast.makeText(this,"Bluetooth is not turned on",Toast.LENGTH_LONG).show();
}else{
	adBlueTooth = new ADBlueTooth(this,new DeviceOxygen(),new DeviceOxygenChoice());
	adBlueTooth.setResultListener(new Listener<OxygenResult>() {
		@Override
		public void onResult(OxygenResult Result) {

		}

		@Override
		public void onConnectedState(int i) {

		}
	});
	adBlueTooth.init();
}
```

Closing the Client App
Once your app has finished using a BLE device, it should call close() so the system can release resources appropriately:


```java
@Override
protected void onDestroy() {
    super.onDestroy();
    if(adBlueTooth != null)
        adBlueTooth.onDestroy();
}
```

Related instructions are as follows,the end will be accompanied by a BloodOxygen Demo

1).Build the BlueTooth Object And init some config

```java
/**
* @param context 
* @param configs device config
*/
public ADBlueTooth(Context context,DeviceConfig... configs)
```

2).Set a callback method for the result

```java
/**
* @param listener callback
* @param <T>
*/
public <T> void setResultListener(Listener<T> listener)
```

Listener has two callback methods

- onResult(T result)
- onConnectedState(int state)

Look at these state

| State                  | Value |Meaning|
| -------------------- | :--: | ---------: |
| STATE_CONNECTED      |  1   |  Bluetooth is connected
|
| STATE_DISCONNECTED   |  -1  | Bluetooth is disconnected|
| STATE_NOTIFY_SUCCESS |  2   |Open the read data feature successfully
 |

Result: result of the datas（look at the details）

3).Initialize

```java
/**
 * @return state 
 *  @see #INIT_FAIL_NOT_SUPPORT_BLE
 *  @see #INIT_FAIL_NOT_ENABLE
 *  @see #INIT_SUCCESS
 */
public int init()
```

Initialize the return of the status code

| state code   |value  |meaning|
| ------------------------- | :--: | -------: |
| INIT_FAIL_NOT_SUPPORT_BLE |  -2  | not support BLE Bluetooth|
| INIT_FAIL_NOT_ENABLE  |  -1  |Bluetooth is not turned on |
| INIT_SUCCESS  |  1   |Initialized successfully|


4).Device corresponding parameter class, result class detailed table


| Device   |                  Config                 |               Result |
| ---- | :--------------------------------------: | ------------------: |
| OXygen  |     DeviceOxygen,DeviceOxygenChoice      |        OxygenResult |
| BLoodSuger   |             DeviceBloodSuger             |    BloodSugerResult |
| Temperature   |            DeviceTemperature             |   TemperatureResult |
| Weight   | DeviceWeightConfig,DeviceWeightBodyConfig |        WeightResult |
| Urine   |               DeviceUrine                |         UrineResult |
| BloodPressure   |           DeviceBloodPressure            | BloodPressureResult |
| Therapy   |              DeviceTherapy               |       TherapyResult |
| Rule   |              DeviceRuleConfig            |       RuleResult    |
**Also you can write your owner Config ，Only need extend the DeviceConfig Class**



5).When you use Therapy Bluetooth device,understand  the following methods.


| operation description  |                  method                   |               result |
| :---- | :-------------------------------------- | ------------------: |
| combination |     TherapyManager.swithMode(DeviceTherapy.THERAPY_MODE_COMBINATION);      |  Physiotherapy mode|
| acupuncture |     TherapyManager.swithMode(DeviceTherapy.THERAPY_MODE_ACUPUNCTURE);      |Physiotherapy mode   |
| tapping |     TherapyManager.swithMode(DeviceTherapy.THERAPY_MODE_TAPPING);      |     Physiotherapy mode|
| scraping |     TherapyManager.swithMode(DeviceTherapy.THERAPY_MODE_SCRAPING);      |Physiotherapy mode|
| massage |     TherapyManager.swithMode(DeviceTherapy.THERAPY_MODE_MASSAGE);      |	   Physiotherapy mode|
| increased strength |             TherapyManager.addValue();             |   |
| Reduce strength   |            TherapyManager.subValue();             |   |
| start work  | TherapyManager.startWork(15); |Device start work,    15:1-15(min)|
| stop work   | TherapyManager.stopWork(); |Device stop work |
 
*Note: Considerations for operating a physiotherapy device:*
a.
```java
    //Initialize the TherapyManager,control the operation of the device
    TherapyManager therapyManager = new TherapyManager(adBlueTooth);
```
b.
```java
    @Override
    public void onConnectedState(int state) {
        //do some processing according to the Bluetooth state
        if (state == Result.STATE_CONNECTED) {
            //When the Bluetooth connection successfully, call this TherapyManager this method gets a heartbeat packet once a second
            if (therapyManager != null) therapyManager.connectDevice();
        }
    }
```
c.
```java
    // When Acticity is onDestroy, you need to cancel the TherapyManager
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (therapyManager != null) therapyManager.onDestroy();
    }
```

6).When you use Rule Bluetooth device,understand  the following methods.

| operation description  |                  method                   |               result|
| :---- | :-------------------------------------- | ------------------: |
| get energy |   RuleManager.writeBytes(DeviceRuleCommand.getEnergyCommand());      |  ruleResult object |
| set unit |   RuleManager.writeBytes(DeviceRuleCommand.setUnitCommand("constant"));  |  ruleResult object    |
| set position |   RuleManager.writeBytes(DeviceRuleCommand.setUnitCommand("constant"));  |  set successfully  |

Attached: Corresponding constant table:


| body position  |                  constant                |       
| ---- | :--------------------------------------: | 
| shoulder   |    DeviceRuleConfig.POSITION_SHOULDER   |      
| arm   |    DeviceRuleConfig.POSITION_ARM        |    
| chest   |    DeviceRuleConfig.POSITION_CHEST      |   
| waist   |    DeviceRuleConfig.POSITION_WAIST      |   
| hip   |    DeviceRuleConfig.POSITION_HIP        |         
| ham   |    DeviceRuleConfig.POSITION_HAM        | 
| shank|    DeviceRuleConfig.POSITION_SHANK      | 

| ruler unit  |       constant |       
| ---- | :--------------------------------------: | 
| cm   |    DeviceRuleConfig.UNIT_CM             |      
| inch   |    DeviceRuleConfig.UNIT_INCH        |   

Attached:：BloodOxygen Demo

```java
public class OxygenActivity extends AppCompatActivity implements Listener<OxygenResult> {

    ADBlueTooth adBlueTooth = null;

    TextView tvOxygen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        tvOxygen = (TextView)findViewById(R.id.tvBP);

        if(!BlueUnit.isHaveBleFeature(this)){
            Toast.makeText(this, "The device does not support Bluetooth 4.0", Toast.LENGTH_LONG).show();
        }else if(!BlueUnit.isEnabled(this)){
            Toast.makeText(this,"Bluetooth is not turned on",Toast.LENGTH_LONG).show();
        }else{
            adBlueTooth = new ADBlueTooth(this,new DeviceOxygen(),new DeviceOxygenChoice());
            adBlueTooth.setResultListener(this);
            adBlueTooth.init();
        }


    }


    @Override
    public void onResult(final OxygenResult result) {
        if(result == null) return;
        // need to switch to the UI mainThread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvOxygen.setText(result.toString());
            }
        });


        L.d(result.toString());
    }

    @Override
    public void onConnectedState(int state) {
        //do some processing according to the Bluetooth state
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adBlueTooth != null)
            adBlueTooth.onDestroy();
    }
}
```

