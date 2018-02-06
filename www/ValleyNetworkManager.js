module.exports = {
    connectWifi: function (arg0, successCallback, errorCallback) {
        var ssid = arg0.ssid || '';
        var password = arg0.password || '';
        var type = arg0.type || 0;
        var minute = arg0.minute || 0;

        cordova.exec(successCallback,
            errorCallback, // No failure callback
            "ValleyNetworkManager",
            "connectWifi",
            [ssid, password,type,minute]);
    },
    closeWifi: function(arg0, successCallback, errorCallback) {
        cordova.exec(successCallback,
            errorCallback, // No failure callback
            "ValleyNetworkManager",
            "closeWifi",
            []);
    },
    requestPermission: function(successCallback, errorCallback) {
        cordova.exec(successCallback,
            errorCallback, // No failure callback
            "ValleyNetworkManager",
            "requestPermission",
            []);
    }
};