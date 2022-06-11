# React-Native Wear Communication Module

React-Native Wear Communication Module is a Native Module for React-Native applications that lets RN applications communicate with WearOS devices through native Data-Client API. More information <a href="https://developers.google.com/android/reference/com/google/android/gms/wearable/DataClient">here</a>. <br/>
<br />
This module is very helpful if you're developing an application used on an Android phone and a helper application used on a WearOS Smart Watch.

# Authors
### <a href="https://github.com/darknet-00/"> Nikola Krezeski</a>
### <a href="https://gitlab.com/IvanJanjik">Ivan Janjikj</a>

## Before you start

### Make sure that both of your applications:
- Share the same package name everywhere in the project (AndroidManifest, build.gradle, the project files etc.)
- Share the same applicationId in build.gradle
- Are signed with the same key (for development and debugging both of the applications can be unsigned, triple check this, as you can lose a lot of time if not done right)

### Triple check the above steps !!!
## Installation

```sh
npm install react-native-wear-communication-module
```

## Usage

### Sending data to a Client

Use case: You need to login a user and take accessToken and validTo timestamp from a remote server. You can use axios to make the request, and then you can pass the data as a JS Object. The sendDataToClient function receives only one parameter of type Object, so you can pass any data as long as it is bundled as an Object. The method is generic and will pass any data to the client.
```js
import { sendDataToClient } from "react-native-respondr_react_native_wear_module";


axios.post("URL").then(({data}) => {
  const accessToken = data?.accessToken
  const validTo = data?.validTo
  sendDataToClient({"accessToken": accessToken, "validTo": validTo})
}).catch((err) => console.error(err))

```

### Receiving data from a Client
Use case: The client needs to submit a query to the React-Native application, so the RN application will then for example fetch the accessToken and send it back to the client. <br />

For this you need to register a listener using the DeviceEventEmitter. You also need to get the name of the Event, for which you can use an exported function of the module.
```js
import { getRNEventName } from "react-native-respondr_react_native_wear_module";
import { DeviceEventEmitter } from 'react-native';


const onReceiveEventHandler = () => {
  // Some logic
  // You would probably want to use the sendDataToClient function here, but not necessarily,
  // You can use the listener for any logic that you need in the application
}

useEffect(() => {
  DeviceEventEmitter.addListener(getRNEventName(), onReceiveEventHandler);
}, [])

```

### Receiving and Sending data on the WearOS Application
For receiving data on the WearOS application please refer <a href="https://developer.android.com/training/wearables/data/events">here</a>

## Note: First read the above link, then -

- To successfully send data to the React-Native application make a request to: "/data-query" using the native Data-Client
- For receiving data on the WearOS application, you need to listen on this path: "/data-response"

### If more people request (if unclear), we will include more information on the Wear OS application side in the future.
## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
