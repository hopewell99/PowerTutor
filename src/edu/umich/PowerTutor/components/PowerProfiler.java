package edu.umich.PowerTutor.components;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

enum PowerDataField {
    POWER_AUDIO,
    POWER_BATTERY_CAPACITY,
    POWER_BLUETOOTH_ACTIVE,
    POWER_BLUETOOTH_AT_CMD,
    POWER_BLUETOOTH_ON,
    POWER_CPU_ACTIVE,
    POWER_CPU_AWAKE,
    POWER_CPU_IDLE,
    POWER_CPU_SPEEDS,
    POWER_GPS_ON,
    POWER_NONE,
    POWER_RADIO_ACTIVE,
    POWER_RADIO_ON,
    POWER_RADIO_SCANNING,
    POWER_SCREEN_FULL,
    POWER_SCREEN_ON,
    POWER_VIDEO,
    POWER_WIFI_ACTIVE,
    POWER_WIFI_BATCHED_SCAN,
    POWER_WIFI_ON,
    POWER_WIFI_SCAN;
}

class L {
    private final static String TAG = "CHROME_POWER_PROFILER";

    public static void log(int value) {
        Log.d(TAG, String.valueOf(value));
    }

    public static void log(double value) {
        Log.d(TAG, String.valueOf(value));
    }

    public static void log(double[] values) {
        for (double value: values)
            log(value);
    }

    public static void log(String value) {
        Log.d(TAG, value);
    }

    public static void log(String key, double value) {
        Log.d(TAG, key + " = " + String.valueOf(value) + " mAh");
    }

    public static void log(String key, String value) {
        Log.d(TAG, key + " " + value);
    }

    public static void ex(String msg) {
        Log.d(TAG, "Exception??? " + msg);
    }
}

class PowerProfile {
    public double mAudio = 0.0f;
    public double mBatteryCapacity = 0.0f;
    public double mBluetoothActive = 0.0f;
    public double mBluetoothATCmd = 0.0f;
    public double mBluetoothOn = 0.0f;
    public double mCPUAwake = 0.0f;
    public double mCPUIdle = 0.0f;

    public double[] mCPUSpeeds;
    public double[] mCPUActive;
    public int mNumberOfCPUSpeedSteps = 0;

    public double mGPSOn = 0.0f;
    public double mNone = 0.0f;
    public double mRadioActive = 0.0f;
    public double mRadioOn = 0.0f;
    public double mRadioScanning = 0.0f;
    public double mScreenFull = 0.0f;
    public double mScreenOn = 0.0f;
    public double mVideo = 0.0f;
    public double mWiFiActive = 0.0f;
    public double mWiFiBatchedScan = 0.0f;
    public double mWiFiOn = 0.0f;
    public double mWiFiScan = 0.0f;


    PowerProfiler mProfiler;

    public PowerProfile(PowerProfiler powerProfiler) {
        mProfiler = powerProfiler;
        read();
    }

    private void read() {
        try {
            Class<?> powerProfileClazz = Class.forName("com.android.internal.os.PowerProfile");
            //Class<?> batteryStatsImplClazz = Class.forName("com.android.internal.os.BatteryStatsImpl");
            //Method[] methods2 = batteryStatsImplClazz.getMethods();

            /*for (Method m: methods2) {
                Log.d("VIVEK", m.toString().replace("com.android.internal.os.BatteryStatsImpl.",""));
            }*/

            Class[] argTypes = { Context.class };
            Constructor constructor = powerProfileClazz.getDeclaredConstructor(argTypes);
            Object[] arguments = { mProfiler.context() };

            Object powerProInstance = constructor.newInstance(arguments);

            Method getBatteryCapacity = null;
            Method getNumSpeedSteps = null;
            Method getAveragePower = null;
            Method getAveragePowerArg = null;

            for (Method m: powerProfileClazz.getMethods()) {
                //Log.d("VIVEK", m.getName());
                if (m.getName().equals("getAveragePower")) {
                  if (m.getParameterTypes().length == 1)
                    getAveragePower = m;
                  else if (m.getParameterTypes().length == 2)
                    getAveragePowerArg = m;
                } else if (m.getName().equals("getBatteryCapacity")) {
                  getBatteryCapacity = m;
                } else if (m.getName().equals("getNumSpeedSteps")) {
                  getNumSpeedSteps = m;
                }
            }

            Map powerDataFieldMap = new HashMap();

            for (PowerDataField pField: PowerDataField.values())
                powerDataFieldMap.put(pField.name(), pField);

            mNumberOfCPUSpeedSteps = (Integer)getNumSpeedSteps.invoke(powerProInstance);
            for (Field f: powerProfileClazz.getFields()) {
                double value = (Double)getAveragePower.invoke(powerProInstance, f.get(powerProInstance));
                PowerDataField fieldIdObject = (PowerDataField)powerDataFieldMap.get(f.getName());

                switch(fieldIdObject) {
                    case POWER_AUDIO:
                        mAudio = value;
                        break;
                    case POWER_BATTERY_CAPACITY:
                        mBatteryCapacity = value;
                        break;
                    case POWER_BLUETOOTH_ACTIVE:
                        mBluetoothActive = value;
                        break;
                    case POWER_BLUETOOTH_AT_CMD:
                        mBluetoothATCmd = value;
                        break;
                    case POWER_BLUETOOTH_ON:
                        mBluetoothOn = value;
                        break;
                    case POWER_CPU_AWAKE:
                        mCPUAwake = value;
                        break;
                    case POWER_CPU_IDLE:
                        mCPUIdle = value;
                        break;
                    case POWER_GPS_ON:
                        mGPSOn = value;
                        break;
                    case POWER_NONE:
                        mNone = value;
                        break;
                    case POWER_RADIO_ACTIVE:
                        mRadioActive = value;
                        break;
                    case POWER_RADIO_ON:
                        mRadioOn = value;
                        break;
                    case POWER_RADIO_SCANNING:
                        mRadioScanning = value;
                        break;
                    case POWER_SCREEN_FULL:
                        mScreenFull = value;
                        break;
                    case POWER_SCREEN_ON:
                        mScreenOn = value;
                        break;
                    case POWER_VIDEO:
                        mVideo = value;
                        break;
                    case POWER_WIFI_ACTIVE:
                        mWiFiActive = value;
                        break;
                    case POWER_WIFI_BATCHED_SCAN:
                        mWiFiBatchedScan = value;
                        break;
                    case POWER_WIFI_ON:
                        mWiFiOn = value;
                        break;
                    case POWER_WIFI_SCAN:
                        mWiFiScan = value;
                        break;
                }
            }

            mCPUSpeeds = new double[mNumberOfCPUSpeedSteps];
            mCPUActive = new double[mNumberOfCPUSpeedSteps];
            for (int p = 0; p < mNumberOfCPUSpeedSteps; p++) {
                mCPUSpeeds[p] = (Double)getAveragePowerArg.invoke(powerProInstance, "cpu.speeds", p);
                mCPUActive[p] = (Double)getAveragePowerArg.invoke(powerProInstance, "cpu.active", p);
            }
        } catch(Exception e) {
            L.ex(e.getMessage());
        }
    }

    public void printSummary() {
        L.log("Power profile summary for:", Build.MODEL);
        L.log("=======================================");
        L.log("POWER_AUDIO", mAudio);
        L.log("POWER_BATTERY_CAPACITY", mBatteryCapacity);
        L.log("POWER_BLUETOOTH_ACTIVE", mBluetoothActive);
        L.log("POWER_BLUETOOTH_AT_CMD", mBluetoothATCmd);
        L.log("POWER_BLUETOOTH_ON", mBluetoothOn);
        L.log("POWER_CPU_AWAKE", mCPUAwake);
        L.log("POWER_CPU_IDLE", mCPUIdle);
        L.log("POWER_CPU_SPEEDS = POWER_CPU_ACTIVE");
        for (int i = 0; i < mNumberOfCPUSpeedSteps; ++i) {
            L.log("\t" + String.format("%1$,.2f", mCPUSpeeds[i]) + " = " + String.format("%1$,.2f", mCPUActive[i]));
        }
        L.log("POWER_GPS_ON", mGPSOn);
        L.log("POWER_NONE", mNone);
        L.log("POWER_RADIO_ACTIVE", mRadioActive);
        L.log("POWER_RADIO_ON", mRadioOn);
        L.log("POWER_RADIO_SCANNING", mRadioScanning);
        L.log("POWER_SCREEN_FULL", mScreenFull);
        L.log("POWER_SCREEN_ON", mScreenOn);
        L.log("POWER_VIDEO", mVideo);
        L.log("POWER_WIFI_ACTIVE", mWiFiActive);
        L.log("POWER_WIFI_BATCHED_SCAN", mWiFiBatchedScan);
        L.log("POWER_WIFI_ON", mWiFiOn);
        L.log("POWER_WIFI_SCAN", mWiFiScan);
    }
}

class CPUPowerData {

}

class DisplayPowerData {
    private double mScreenOn = 0.0f;
    private double mScreenFull = 0.0f;
    private double[] mForBrightnessLevel;

    public final int NUMBER_OF_BRIGHTNESS_LEVELS = 255;

    public DisplayPowerData(double powerScreenOn, double powerScreenFull) {
        mScreenOn = powerScreenOn;
        mScreenFull = powerScreenFull;

        mForBrightnessLevel = new double[NUMBER_OF_BRIGHTNESS_LEVELS + 1];
        for (int i = 0; i <= NUMBER_OF_BRIGHTNESS_LEVELS; ++i) {
            mForBrightnessLevel[i] = mScreenOn + (mScreenFull * i / NUMBER_OF_BRIGHTNESS_LEVELS);
        }
    }

    public double getDisplayBrightnessPower(int brightnessLevel) {
        if (brightnessLevel < 0 || brightnessLevel > NUMBER_OF_BRIGHTNESS_LEVELS)
            return 0.0f;
        return mForBrightnessLevel[brightnessLevel];
    }
}

public class PowerProfiler {
    private static PowerProfiler sInstance = null;
    public Context mContext = null;
    private PowerProfile mProfile = null;
    private DisplayPowerData mDisplayPowerData = null;

    public static PowerProfiler getInstance(Context context) {
        if (sInstance == null)
            sInstance = new PowerProfiler(context);
        return sInstance;
    }

    private void initialize() {
        mProfile = new PowerProfile(this);
        mDisplayPowerData = new DisplayPowerData(mProfile.mScreenOn,
                                                 mProfile.mScreenFull);
        mProfile.printSummary();
    }

    protected PowerProfiler(Context context) {
        mContext = context;
        initialize();
    }

    Context context() {
        return mContext;
    }

    public double getDisplayPower() {
        try {
            int brightnessLevel = Settings.System.getInt(mContext.getContentResolver(),
                                              Settings.System.SCREEN_BRIGHTNESS);
            return mDisplayPowerData.getDisplayBrightnessPower(brightnessLevel);
        } catch (Exception ex) {
            L.log("Exception: " + ex.getMessage());
            return -1.0f;
        }
    }

  }
