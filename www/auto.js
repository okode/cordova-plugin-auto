var exec = require('cordova/exec');

exports.sendMessage = function (conversationId, from, message, success, error) {
    exec(success, error, 'Auto', 'sendMessage', [conversationId, from, message]);
};

exports.isCarUIMode = function (success, error) {
    exec(success, error, 'Auto', 'isCarUIMode', []);
};

exports.bindDocumentEvent = function() {
    exec(function(e) {
      console.log("Message received: " + e.message + " on conversation " + e.conversationId);
    }, null, "Auto", "register", []);
};

document.addEventListener("deviceready", exports.bindDocumentEvent, false);
