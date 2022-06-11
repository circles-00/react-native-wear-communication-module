package com.rn_wear_communication_module;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;


@ReactModule(name = ReactNativeWearCommunicationModule.NAME)
public class ReactNativeWearCommunicationModule extends ReactContextBaseJavaModule implements DataClient.OnDataChangedListener {
  private final String TAG = "RNWearCommModule";
  public final static String NAME = "ReactNativeWearCommunicationModule";
  public final static String RN_EVENT_NAME = "dataQuery";


  public ReactNativeWearCommunicationModule(ReactApplicationContext reactContext) {
    super(reactContext);
    Wearable.getDataClient(reactContext).addListener(this);
  }

  @ReactMethod
  public void sendDataToClient (ReadableMap data) {
    PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/data-response");

    ReadableMapKeySetIterator iterator = data.keySetIterator();
    while(iterator.hasNextKey()) {
      String key = iterator.nextKey();
      ReadableType readableType = data.getType(key);
      switch (readableType) {
        case String:
          String stringValue = data.getString(key);
          putDataMapReq.getDataMap().putString(key, stringValue);
          break;
        case Number:
          double numberValue = data.getDouble(key);
          putDataMapReq.getDataMap().putDouble(key, numberValue);
          break;
        case Boolean:
          boolean booleanValue = data.getBoolean(key);
          putDataMapReq.getDataMap().putBoolean(key, booleanValue);
          break;
      }
    }

    putDataMapReq.setUrgent();

    Task<DataItem> putDataTask = Wearable.getDataClient(getReactApplicationContext()).putDataItem(putDataMapReq.asPutDataRequest());
    putDataTask.addOnSuccessListener(onSuccessListener);
    putDataTask.addOnFailureListener(onFailureListener);
  }

  @NonNull
  @Override
  public String getName() {
    return ReactNativeWearCommunicationModule.NAME;
  }

  private final OnSuccessListener onSuccessListener = o -> Log.d(TAG, "SUCCESSFULLY SENT DATA");

  private final OnFailureListener onFailureListener = e -> Log.d(TAG, "FAILED TO SEND DATA");

  @Override
  public void onDataChanged(DataEventBuffer dataEventBuffer) {
    for (DataEvent event : dataEventBuffer) {
      if (event.getType() == DataEvent.TYPE_CHANGED) {
        // DataItem changed
        DataItem item = event.getDataItem();
        if (item.getUri().getPath().compareTo("/data-query") == 0) {
          DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
          Log.d(TAG, "DATA RECEIVED: " + dataMap);
          Toast.makeText(getReactApplicationContext(), "MESSAGE RECEIVED FROM Wearable DEVICE",
            Toast.LENGTH_LONG).show();
          if(dataMap.get("dataQuery") != null) {
            sendEventToRN(getReactApplicationContext(), ReactNativeWearCommunicationModule.RN_EVENT_NAME, null);
          }
        }
      }
    }
  }

  @ReactMethod
  public String getEventName() {
    return ReactNativeWearCommunicationModule.RN_EVENT_NAME;
  }


  private void sendEventToRN(ReactContext reactContext, String eventName, WritableMap params) {
    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(eventName, params);
  }
}
