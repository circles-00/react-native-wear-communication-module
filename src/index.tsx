import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-respondr_react_native_wear_module' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const ReactNativeWearCommunicationModule =
  NativeModules.ReactNativeWearCommunicationModule
    ? NativeModules.ReactNativeWearCommunicationModule
    : new Proxy(
        {},
        {
          get() {
            throw new Error(LINKING_ERROR);
          },
        }
      );

export function sendDataToClient(data: Object): void {
  return ReactNativeWearCommunicationModule.sendDataToClient(data);
}

export function getRNEventName(): string {
  return ReactNativeWearCommunicationModule.getEventName();
}
