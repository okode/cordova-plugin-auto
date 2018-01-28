var exec = require('cordova/exec');

exports.sendMessage = function (conversationId, from, message, success, error) {
    exec(success, error, 'Auto', 'sendMessage', [conversationId, from, message]);
};

exports.isCarUIMode = function (success, error) {
    exec(success, error, 'Auto', 'isCarUIMode', []);
};

exports.register = function(success, error) {
    exec(success, error, "Auto", "register", []);
};

document.addEventListener("deviceready", exports.register, false);
