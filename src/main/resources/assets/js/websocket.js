//connect();
var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
    	stompClient.send("/app/hello", {}, JSON.stringify({
			'message' : '<b class=\'text-success\'>Joined discussion</b>',
			'color' : $("#colorpicker").val()
		}));
    	$( ".fa-circle" ).addClass( "text-success" );
    }
    else {
        //$("#conversation").hide();
    	$( ".fa-circle" ).removeClass( "text-success" );
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).senderid,
            		JSON.parse(greeting.body).sender,
            		JSON.parse(greeting.body).content,
            		JSON.parse(greeting.body).timeStamp,
            		JSON.parse(greeting.body).rcvr,
            		JSON.parse(greeting.body).rcvrid,
            		JSON.parse(greeting.body).color);
        	//showGreeting(JSON.parse(greeting.body));
        });
    });  
    
}

function disconnect() {
	if (stompClient !== null) {
		stompClient.send("/app/hello", {}, JSON.stringify({
			'message' : '<b class=\'text-danger\'>Left discussion</b>',
			'color' : $("#colorpicker").val()
		}));
		stompClient.disconnect();
	}
	setConnected(false);
	console.log("Disconnected");
}

function sendMessage() {
	stompClient.send("/app/hello", {}, JSON.stringify({
		'message' : $("#message").val(),
		'color' : $("#colorpicker").val()
	}));
	$("#message").val("");
}

function showGreeting(senderid,sender,message,time,rcvr,rcvrid,color) {
	    /*$content = "<div class='widget-chat'>"
			
			+ "<div  class='widget-chat-item'>"
			+ "<div class='widget-chat-media'><img src='assets/img/user/user-2.jpg' alt='' /></div>"
			+ "<div class='widget-chat-content'>"
			+ "<div class='widget-chat-name'>" + color+ "</div>"

			+ "<div id='greetings' class='widget-chat-message last'>" + message
			+ "</div>" 
			+ "</div>" + "</div>" + "</div>";
	    */
	$content = "<tr>" + "<td class='text-justify'><a class='text-decoration-none' href='#' emp-id='"
			+ senderid + "' onclick='viewEmlpoyee(this);'  style='color: "
			+ color + "'>" + sender + "</a>: " + message + "</td>" +
			"<td class='text-muted col-sm-3 text-center'>" + time + "</td></tr>";
	
	
    //alert($content);
    $("#conversation").append($content);
    //$('.card-body').animate({scrollTop: document.body.scrollHeight},"fast");
    $(".card-body").animate({ scrollTop: $('.card-body').height() }, 1000);
    
	
}

$('#message').keypress(function(event) {
	var keycode = (event.keyCode ? event.keyCode : event.which);
	//alert($(this).val());
	//$char= $(this).val();
	if (keycode == '13') {
		//alert('You pressed a "enter" key in textbox');
		$('#send').click();
		//$(this).val("");
	    return false;  
	}
});

function triggerToast(body) {
	$('.toast-header .title').html("Messaging");
	$('.toast-body .toast-message').html(body);					
	$('.toast').toast('show');		
}

function viewEmlpoyee(z) {
	$empid = $(z).attr("emp-id");
	//$('#viewEmployee').modal('show');
	
	$.ajax({
		url : "viewEmployee?empid=" + $empid,
		type : 'GET',
		success : function(data) {
			$('.modal-content').html(data);
			$('#viewEmployee').modal('show');
		},
		error : function(xhr, desc, err) {
			$('.modal-content').html(xhr.responseText);
			$('#viewEmployee').modal('show');

		}
	});
	//alert($empid);

}

$(function() {
	$("form").on('submit', function(e) {
		e.preventDefault();
	});
	// $joinmsg="<tr><td colspan='2'>Joined Conversation</td></tr>";
	$leftmsg = "<tr><td colspan='2'>Left Conversation</td></tr>";
	$("#connect").click(function() {
		connect();		
		triggerToast("Connected with EMS chatroom!");
	});
	$("#disconnect").click(function() {
		disconnect();
		triggerToast("Left EMS chatroom!");
		$("#conversation").append($leftmsg);
	});
	$("#send").click(function() {
		sendMessage();
	});
	
	$('#connect').click();
	
});