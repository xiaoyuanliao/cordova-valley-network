# cordova-valley-network
一个简单网络管理插件

## Requirements
- Android4+

## Installation

cordova plugin add https://github.com/xiaoyuanliao/cordova-valley-network.git


## Usage
```
var params = {
    ssid : 'peixun103',
    password : '1qaz!QAZ',
    type : '2',
    minute : 1
    };
      ValleyNetworkManager.connectWifi(params,function(result){
      console.log(result);
      },function(result){
      console.log(result);
      });
```

## License

