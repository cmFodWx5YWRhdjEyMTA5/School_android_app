# 天使医生蓝牙设备Android SDK使用文档

## 1.权限配置

```java
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-feature    android:name="android.hardware.bluetooth_le"    android:required="false" />

<service
          android:name="com.jumper.bluetoothdevicelib.service.BluetoothLeService"
          android:enabled="true"
          android:exported="false" />
```

以上主要为蓝牙相关的权限和一个蓝牙Service。



## 2.SDK调用

sdk方法的调用

```java
ADBlueTooth adBlueTooth = null;
if(!BlueUnit.isHaveBleFeature(this)){
	Toast.makeText(this, "设备不支持蓝牙4.0", Toast.LENGTH_LONG).show();
}else if(!BlueUnit.isEnabled(this)){
	Toast.makeText(this,"蓝牙未开启",Toast.LENGTH_LONG).show();
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

sdk清理

```java
@Override
protected void onDestroy() {
    super.onDestroy();
    if(adBlueTooth != null)
        adBlueTooth.onDestroy();
}
```

相关说明如下。结尾会附带一个血氧的Demo

1).构建一个天使医生蓝牙类并配置设备参数

```java
/**
* @param context 上下文
* @param configs 设备参数
*/
public ADBlueTooth(Context context,DeviceConfig... configs)
```

2).设置一个结果监听回调

```java
/**
* 设置一个结果监听的接口
* @param listener 监听的接口
* @param <T>
*/
public <T> void setResultListener(Listener<T> listener)
```

Listener 里有两个回调方法

- onResult(T result)
- onConnectedState(int state)

state有三个种状态

| 状态码                  | 对应值  |       代表意思 |
| -------------------- | :--: | ---------: |
| STATE_CONNECTED      |  1   |     蓝牙连接成功 |
| STATE_DISCONNECTED   |  -1  |       蓝牙断开 |
| STATE_NOTIFY_SUCCESS |  2   | 开启读取数据特征成功 |

result 为最后的结果数据类（目前以有设备都有进行数据封装，详情见后面的表格）

3).初始化一些信息并开始工作

```java
/**
 * 初始化一些信息并开始工作
 * @return 初始化结果状态码<br>
 *  @see #INIT_FAIL_NOT_SUPPORT_BLE
 *  @see #INIT_FAIL_NOT_ENABLE
 *  @see #INIT_SUCCESS
 */
public int init()
```

初始化返回的状态码所代码意思


| 状态码                       | 对应值  |     代表意思 |
| ------------------------- | :--: | -------: |
| INIT_FAIL_NOT_SUPPORT_BLE |  -2  | 不支持BLE蓝牙 |
| INIT_FAIL_NOT_ENABLE      |  -1  |    蓝牙未开启 |
| INIT_SUCCESS              |  1   |    初始化成功 |


4).设备对应参数类，结果类详细表格

| 设备   |                  参数配置类                   |               结果返回类 |
| ---- | :--------------------------------------: | ------------------: |
| 血氧   |     DeviceOxygen,DeviceOxygenChoice      |        OxygenResult |
| 血糖   |             DeviceBloodSuger             |    BloodSugerResult |
| 体温   |            DeviceTemperature             |   TemperatureResult |
| 体重   | DeviceWeightConfig,DeviceWeightBodyConfig |        WeightResult |
| 尿液   |               DeviceUrine                |         UrineResult |
| 血压   |           DeviceBloodPressure            | BloodPressureResult |
| 理疗   |              DeviceTherapy               |       TherapyResult |
| 尺子   |              DeviceRuleConfig            |       RuleResult    |
**也可以写自已的参数配置类，只需继承 DeviceConfig 类**



5).在使用理疗设备时，APP发送指令到理疗设备需调用的方法详细

| 操作描述   |                  方法                   |               结果返回类 |
| :---- | :-------------------------------------- | ------------------: |
| 综合 |     TherapyManager.swithMode(DeviceTherapy.THERAPY_MODE_COMBINATION);      |  默认理疗的模式 |
| 针灸 |     TherapyManager.swithMode(DeviceTherapy.THERAPY_MODE_ACUPUNCTURE);      |     理疗模式    |
| 敲打 |     TherapyManager.swithMode(DeviceTherapy.THERAPY_MODE_TAPPING);      |     理疗模式    |
| 刮痧 |     TherapyManager.swithMode(DeviceTherapy.THERAPY_MODE_SCRAPING);      |     理疗模式    |
| 按摩 |     TherapyManager.swithMode(DeviceTherapy.THERAPY_MODE_MASSAGE);      |	   理疗模式      |
| 增加理疗强度   |             TherapyManager.addValue();             |   |
| 减小理疗强度   |            TherapyManager.subValue();             |   |
| 开始理疗   | TherapyManager.startWork(15); |设备开始工作    15:表示这次理疗的时间长度（1-15分钟）|
| 结束理疗   | TherapyManager.stopWork(); |   设备暂停工作 |

*注：操作理疗设备的注意事项：*
a.
```java
    //初始化蓝牙的同时要初始化一个发送理疗指令的class
    //用于发送各种指令到设备，控制设备运行状态
    TherapyManager therapyManager = new TherapyManager(adBlueTooth);
```
b.
```java
    @Override
    public void onConnectedState(int state) {
        //这里可以根据蓝牙状态做一些处理
        if (state == Result.STATE_CONNECTED) {
            //当蓝牙连接成功 调用TherapyManager的此方法获取一秒一次的心跳包
            if (therapyManager != null) therapyManager.connectDevice();
        }
    }
```
c.
```java
    //Acticity销毁的时候 同时销毁TherapyManager
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (therapyManager != null) therapyManager.onDestroy();
    }
```

6).在使用尺子设备时，APP发送指令到尺子设备需调用的方法详细

| 操作描述   |                  方法                   |               结果返回类 |
| :---- | :-------------------------------------- | ------------------: |
| 获取电量 |   RuleManager.writeBytes(DeviceRuleCommand.getEnergyCommand());      |  返回ruleResult对象 |
| 设置单位 |   RuleManager.writeBytes(DeviceRuleCommand.setUnitCommand(单位常数));  |  返回ruleResult对象    |
| 设置部位 |   RuleManager.writeBytes(DeviceRuleCommand.setUnitCommand(部位常数));  |  设置成功    |

附：对应的常数表：

| 尺子部位   |                  常数                |       
| ---- | :--------------------------------------: | 
| 肩部   |    DeviceRuleConfig.POSITION_SHOULDER   |      
| 手臂   |    DeviceRuleConfig.POSITION_ARM        |    
| 胸部   |    DeviceRuleConfig.POSITION_CHEST      |   
| 腰部   |    DeviceRuleConfig.POSITION_WAIST      |   
| 臀部   |    DeviceRuleConfig.POSITION_HIP        |         
| 大腿   |    DeviceRuleConfig.POSITION_HAM        | 
| 小腿   |    DeviceRuleConfig.POSITION_SHANK      | 

| 尺子单位   |                  常数                |       
| ---- | :--------------------------------------: | 
| 厘米   |    DeviceRuleConfig.UNIT_CM             |      
| 英寸   |    DeviceRuleConfig.UNIT_INCH           |   

附：血氧Demo

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
            Toast.makeText(this, "设备不支持蓝牙4.0", Toast.LENGTH_LONG).show();
        }else if(!BlueUnit.isEnabled(this)){
            Toast.makeText(this,"蓝牙未开启",Toast.LENGTH_LONG).show();
        }else{
            adBlueTooth = new ADBlueTooth(this,new DeviceOxygen(),new DeviceOxygenChoice());
            adBlueTooth.setResultListener(this);
            adBlueTooth.init();
        }


    }


    @Override
    public void onResult(final OxygenResult result) {
        if(result == null) return;
        // 这里如果要做UI处理，得切换到主线程
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
        //这里可以根据蓝牙状态做一些处理
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adBlueTooth != null)
            adBlueTooth.onDestroy();
    }
}
```