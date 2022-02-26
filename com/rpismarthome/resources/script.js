function print(str) {
    $("#status").html(str);
    console.log("STATUS: " + str)
}

/*insert ipAdress*/

var webSocket = new WebSocket("ws://" + ip + ":80/context/");


webSocket.onopen = function (message) {
    print("<i class='fa fa-link'></i>");
};

var fullMessage = "";
webSocket.onmessage = function (message) {
    msg = message.data;
    fullMessage += msg;
    if (msg.endsWith("DONE")) {
        fullMessage = fullMessage.substring(0, fullMessage.length - 4);
        var msgArray = fullMessage.split("|");
        fullMessage = "";
        console.log(msgArray);
        //====================
        if (msgArray[0] === ("UPDATE_COMPONENTS")) {
            $("#componentFiller").html(msgArray[1]);
        } else if (msgArray[0] === ("UPDATE_HTML_VALUE")) {
            $(msgArray[1]).html(msgArray[2]);
        } else if (msgArray[0] === ("UPDATE_VALUE_VALUE")) {
            $(msgArray[1]).val(msgArray[2]);
        } else if (msgArray[0] === ("UPDATE_CHECKED_VALUE")) {
            $(msgArray[1]).prop("checked", msgArray[2] === "true");
        } else if (msgArray[0] === ("INFO")) {
            newInfo(msgArray[1]);
        } else if (msgArray[0] === ("HIDE_INFO")) {
            hideInfo();
        } else {
            newComponentHandleMessage(msgArray);
        }
    }
};

function wsWrite(str){
    console.log("OUT: "+str);
    webSocket.send(str);
}

//function wsWrite(prefix, array) {
//    //Join array together with the delimiter "|"
//    var str = array.join("|");
//    //Split string every 100 characters
//    var strArray = str.match(/.{1,100}/g);
//    //Send each string in strArray individually
//    for (var i = 0; i < strArray.length; i++) {
//        wsWrite(prefix + "|" + strArray[i]);
//    }
//    wsWrite(prefix + "|DONE");
//}


webSocket.onclose = function (message) {
    print("<i class='fa fa-chain-broken'></i>");
    alert("You have been Disconnected. Reload to try again.")
    location.reload();
};
webSocket.onerror = function (message) {
    print("Internal error...");
};

function deleteComponent(id) {
    if (confirm('Are you sure you want to delete this component?')) {
        wsWrite("component|delete|" + id);
    }
}

function changeTitle(id, title) {
    wsWrite("component|changeTitle|" + id + "|" + title);

}

function componentSetValue(id, key, value) {
    wsWrite("component|setValue|" + id + "|" + key + "|" + value);
}

function resetConfig(){
    if(confirm("Resetting your configuration will clear ALL your Components.\n\
\nIt is only reccomended if you are expiriencing issues with your components.\nThis action cannot be undone.")){
         wsWrite("ResetConfig");
         location.reload();
    }
}


$('#commandBox').keypress(function (event) {
    var keycode = (event.keyCode ? event.keyCode : event.which);
    if (keycode == '13') {
        //Get if the dropdown commandType is train or command
        var commandType = $('#commandType').val();
        if (commandType == "train") {
            wsWrite("CODEX|TRAIN|" + $('#commandBox').val().replace("|", ""));
        } else {
            wsWrite("CODEX|COMMAND|" + $('#commandBox').val().replace("|", ""));
        }
        $('#commandBox').val("");
    }
});

function newInfo(str) {
    $("#infoBar").html(str);
    $("#infoBar").css("background-color", "#ffe94c");
    //wait 2 seconds and then set infobar background color to white
    setTimeout(function () {
        $("#infoBar").css("background-color", "#eee");
    }, 3000);
}

function hideInfo() {
    $("#infoBar").html("");
    $("#infoBar").css("background-color", "white");
}

function eraseTraining() {
    if (confirm("Are you sure you want to erase all training data?\n" +
            "Doing so will allow the system to learm more effectifly with new configurations.")) {
        wsWrite("CODEX|RESET");
    }
}
