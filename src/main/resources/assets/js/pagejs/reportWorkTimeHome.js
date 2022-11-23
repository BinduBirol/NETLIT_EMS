
pagestartajaxcall();



function getDatesByWeekNo(week) {
	var wyear=week.split("-W")[0];
	var wweekno=week.split("-W")[1];	
	var thisyear="'"+wyear+"'";
	var startweek = moment(thisyear).add(wweekno, 'weeks').startOf('isoWeek').format('YYYY-MM-DD');
	var endweek = moment(thisyear).add(wweekno, 'weeks').endOf('isoWeek').format('YYYY-MM-DD');
	$("#trav_from_date").val(startweek);
	$("#trav_to_date").val(endweek);
}

$("#inputweekno").change(function() {
	$('#travdaterangeselect').val('c');
	getDatesByWeekNo($(this).val());
	setDateRangeString();
	getWeekNo();
	getTRforms();
})


$("#travdaterangeselect").change(function() {
	
	$c = $(this).val();
	if ($c == "tm") {
		$("#trav_from_date").val(moment().startOf('month').format('YYYY-MM-DD'));
		$("#trav_to_date").val(moment().endOf('month').format('YYYY-MM-DD'));

	} else if ($c == "nm") {
		$("#trav_from_date").val(moment().add(1, 'month').startOf('month').format('YYYY-MM-DD'));
		$("#trav_to_date").val(moment().add(1, 'month').endOf('month').format('YYYY-MM-DD'));

	}  else if ($c == "nw") {
		$("#trav_from_date").val(moment().add(1, 'week').startOf('isoWeek').format('YYYY-MM-DD'));
		$("#trav_to_date").val(moment().add(1, 'week').endOf('isoWeek').format('YYYY-MM-DD'));
	} else if ($c == "t") {
		$("#trav_from_date").val(moment().format('YYYY-MM-DD'));
		$("#trav_to_date").val(moment().format('YYYY-MM-DD'));

	}  else if ($c == "tw") {
		$("#trav_from_date").val(moment().startOf('isoWeek').format('YYYY-MM-DD'));
		$("#trav_to_date").val(moment().endOf('isoWeek').format('YYYY-MM-DD'));

	}else if ($c == "lw") {
		$("#trav_from_date").val(moment().add(-1, 'week').startOf('isoWeek').format('YYYY-MM-DD'));
		$("#trav_to_date").val(moment().add(-1, 'week').endOf('isoWeek').format('YYYY-MM-DD'));
	}else if ($c == "yd") {
		$("#trav_from_date").val(moment().add(-1, 'day').format('YYYY-MM-DD'));
		$("#trav_to_date").val(moment().add(-1, 'day').format('YYYY-MM-DD'));
	}else if ($c == "dby") {
		$("#trav_from_date").val(moment().add(-2, 'day').format('YYYY-MM-DD'));
		$("#trav_to_date").val(moment().add(-2, 'day').format('YYYY-MM-DD'));
	}else if ($c == "l3d") {
		$("#trav_from_date").val(moment().add(-3, 'day').format('YYYY-MM-DD'));
		$("#trav_to_date").val(moment().add(-1, 'day').format('YYYY-MM-DD'));
	}else if ($c == "n3d") {
		$("#trav_from_date").val(moment().add(1, 'day').format('YYYY-MM-DD'));
		$("#trav_to_date").val(moment().add(3, 'day').format('YYYY-MM-DD'));
	}
	
	setDateRangeString();
	getWeekNo();
	getTRforms();
	
})

$('#trav_from_date').on('change', function() {
	$("#trwshmessage").html("");
	$("#trav_to_date").val($("#trav_from_date").val());	
	getWeekNo();
});


function getWeekNo() {
	var weekno= moment($("#trav_from_date").val(), 'YYYY-MM-DD').format('WW');
	var year=moment($("#trav_from_date").val(), 'YYYY-MM-DD').format('YYYY');
	$("#trweek_no").html(weekno);
	var weekval=year+"-W"+weekno;
	$("#inputweekno").val(weekval);
}

function setDateRangeString() {
	$('#travmessage').html(moment($("#trav_from_date").val(), 'YYYY-MM-DD').format('dddd, MMMM D, YYYY') + ' - ' + moment($("#trav_to_date").val(), 'YYYY-MM-DD').format('dddd, MMMM D, YYYY'));
}

function pagestartajaxcall(){
	$("#trav_from_date").val(moment().format('YYYY-MM-DD'));
	$("#trav_to_date").val(moment().format('YYYY-MM-DD'));
	setDateRangeString();
	getWeekNo();
	getTRforms();
}


function getTRforms(){	
	var fd =$("#trav_from_date").val();
	var td =$("#trav_to_date").val();	
	if(!fd&&!td){
		$("#travdaterangeselect").focus();
		$('.toast-header .title').html("ALERT!");
		$('.toast-body .toast-message').html("Please select a daterange.");		
		$('.toast').toast('show');
	}else{
		setDateRangeString();
		$.get('/getTimeFormsBydate', {from_date:fd,to_date:td}, function (data, textStatus, jqXHR) {
			$("#getTablediv").html(data).hide().slideDown();
		});
	}	
		
}




function setTRvalues(t, e) {	
	$target= $(e.target).closest('tr');	
	if ($(t).val() == 1) {
		$target.find('.absent').prop('required', true);
		$target.find('.absent').prop('disabled', false);
		$(t).removeClass("is-invalid");
		$(t).addClass("is-valid");
		$target.find('.absent').removeClass("is-invalid");
		$target.find('.absent').addClass("is-valid");
		calculate(e);
	} else {		
		$target.find(".absent").removeAttr("required");
		$target.find('.absent').prop('disabled', true);
		$target.find(".absent").removeClass("is-valid");
		$target.find(".absent").addClass("is-invalid");		
		$(t).removeClass("is-valid");
		$(t).addClass("is-invalid");
		
		$target.find(".wmint").val(0);
		$target.find(".start").val("");
		$target.find(".end").val("");
		$target.find(".lbreak").val(0);
		calculate(e);
	}
	$target.find('.btn-outline-theme').prop('disabled', false);
	$target.find('.btn-outline-theme').html('SUBMIT');
}

function applyVacation(z) {			
	$empid = $(z).attr("emp-id");
	$.ajax({
		url : "getVacationRequestModal?empid=" + $empid,
		type : 'GET',
		success : function(data) {					
			$('.availablitymodalcontent').html(data);
			$('#availablitymodal').modal('show');					
		},
		error : function(xhr, desc, err) {
			$('.availablitymodalcontent').html(xhr.responseText);
			$('#availablitymodal').modal('show');
		}
	});
}

function calculate(e) {
	$target= $(e.target).closest('tr');
	var start = $target.find(".start").val();
	var end = $target.find(".end").val();
	var lbreak = $target.find(".lbreak").val();	
	var obmin = $target.find(".obminute").val();		
	$target.find(".wmint").val(getworkminute(start,end,lbreak));
	$target.find(".work_hour").val(minutesToHour(getworkminute(start,end,lbreak)));	
	$target.find('.savebtn').prop('disabled', false);
	$target.find('.savebtn').addClass("btn-outline-theme").html('SUBMIT');
	
}



function getworkminute(start,end,lbreak) {	

	var diff = ((Math.abs(new Date('2022-05-30 '+start) - new
			 Date('2022-05-30 '+end))/1000/60)- lbreak);
	return diff;
	
}


function saveTR(t,e) {	
	$target= $(e.target).closest('tr');
	
	var desc= $target.find('.work_desc').val();
	var start = $target.find(".start").val();
	var end = $target.find(".end").val();
	var lbreak = $target.find(".lbreak").val();	
	var date = $target.find(".date").val();
	var status = $target.find(".status").val();	
	var wmint = $target.find(".wmint").val();
	var obtype = $target.find(".obtype").val();	
	var obminute = $target.find(".obminute").val();	
	var work_hour = $target.find(".work_hour").val();
	var action = $target.find(".saveaction").val();
	
	if (validate(e)) {
		 $.post("saveDateTimeReport",
				  {
			 		status: status,
			 		from_date: date,
			 		work_start:start,
			 		work_end:end,
			 		lunch_hour:lbreak,
			 		work_desc:desc,
			 		work_minute:wmint,
			 		obtype:obtype,
			 		obminute:obminute,
			 		work_hour:work_hour
				  },
				  function(data, status){
				    //alert("Data: " + data + "\nStatus: " + status);
				    $('.toast-header .title').html("Response");
					$('.toast-body .toast-message').html(data);
					
					$('.toast').toast('show');
					if (data.includes("Successfully") == true) {					
						$target= $(e.target).closest('tr').addClass("bg-warning");
						$target.find('.btn-outline-theme').attr("disabled","disabled");
						$target.find('.btn-outline-theme').html("PENDING");
					}
					
				  });
	}
}






function showOBtr(e, z) {
	$target= $(e.target).closest('tr');
	var nxttr=$target.next('tr');
	var nxtnxttr=nxttr.next('tr');
	
	if($(nxttr).is(":visible")){
		$(nxtnxttr).removeClass("d-none").show(500);
	}else{		
		$(nxttr).removeClass("d-none").show(500);
	}
	
	
}

function setTypeval(e,z) {
	var start = $('option:selected', z).attr('start');
	var end = $('option:selected', z).attr('end');
	var interval = $('option:selected', z).attr('interval');
	var type=$(z).val();
	
	$target= $(e.target).closest('tr');
	$target.find(".start").val(start);
	$target.find(".end").val(end);
	$target.find(".lbreak").val(interval);
	$target.find(".wmint").val(getworkminute(start,end,0));
	$target.find(".work_hour").val(minutesToHour(getworkminute(start,end,0)));
	
	if (start!= null) {
		$target.find('.absent').prop('disabled', false);
		$(z).removeClass("border-warning").addClass("border-success");		
		
	}else{
		$target.find('.absent').prop('disabled', true);	
		$(z).removeClass("border-success").addClass("border-warning");		
	}
	calculate(e);	
	$target.find('.savebtn').prop('disabled', false);
	$target.find('.savebtn').html('SUBMIT');
}

function save(t,e) {
	
	
	var action = $(t).attr('saveaction');
	
	
	$target= $(e.target).closest('tr');
	
	var av_id = $target.find(".av_id").val();
	var desc= $target.find(".work_desc").val();
	var start = $target.find(".start").val();
	var end = $target.find(".end").val();
	var lbreak = $target.find(".lbreak").val();	
	var date = $target.find(".date").val();
	var status = $target.find(".status").val();	
	var wmint = $target.find(".wmint").val();
	var work_hour = $target.find(".work_hour").val();
	var obno = $target.find(".obno").val();
	var user_id = $("#this_user_id").val();	
	
	var datetxt= $target.find("date-tr").html();
	
	if (validate(e)) {	
		var data ={
		 		status: status,
		 		from_date: date,
		 		work_start:start,
		 		work_end:end,
		 		work_interval:lbreak,
		 		work_desc:desc,
		 		work_minute:wmint,			 		
		 		work_hour:work_hour,
		 		obno:obno,
		 		userid:user_id,
		 		tr_id:av_id
			  }
		$("#modalConfirm .modal-title").html("CONFIRM");
		$("#modalConfirm .cnfTxtMsg").html("You are going to submit time report for date "+date+"<br/>Total "+work_hour);
		$("#modalinputData").val(JSON.stringify(data));
		$("#modalinputTrID").val(av_id);
		$('#modalConfirm').modal('show');
	}
}

function saveAjaxCall(){
	var action="saveTimeReport";
	var strdata= $("#modalinputData").val();
	var tr_id=$("#modalinputTrID").val();
	
	$.post(action,
			{str: strdata},
			  function(data, status){		    
			    
				$("#modalConfirm .btn-close").click();
				$('.toast-header .title').html("Response");
				$('.toast-body .toast-message').html(data);				
				$('.toast').toast('show');
				
				if (data.includes("Successfully") == true) {
					//$("[tr-id="+tr_id+"]").closest("tr").addClass("bg-warning");
					$("[tr-id="+tr_id+"]").attr("disabled","disabled").text("PENDING").removeClass().addClass("savebtn btn btn-sm btn-outline-default");
					
					//$target= $(e.target).closest('tr').addClass("");
					//$target.find('.savebtn').attr("disabled","disabled");
					//$target.find('.savebtn').html("PENDING");						
				}
				
			  });	
	
}

function validate(e){	
	$target= $(e.target).closest('tr');	
	var av_id = $target.find(".av_id").val();
	var desc= $target.find(".work_desc").val();
	var start = $target.find(".start").val();
	var end = $target.find(".end").val();
	var lbreak = $target.find(".lbreak").val();	
	var date = $target.find(".date").val();
	var status = $target.find(".status").val();	
	var wmint = $target.find(".wmint").val();
	
	if(wmint>480){
		$target.find(".end").focus();
		$('.toast-header .title').html("Alert!!");
		$('.toast-body .toast-message').html("Can not report for more than 8 hours!!");		
		$('.toast').toast('show');
		return false;
	}
	
	if(!status){
		$target.find(".status").focus();
		$('.toast-header .title').html("Alert!!");
		$('.toast-body .toast-message').html("Select a Time Report type!!");
		
		$('.toast').toast('show');
		return false;
	}
	
	if(status==1 && $.trim(desc).length>0 && $.trim(start).length>0 && $.trim(end).length>0){
		return true;
	} else if(status!=1  && $.trim(desc).length>0){
		return true;
	} else if($.trim(desc).length==0){
		$target.find(".work_desc").focus();
		$('.toast-header .title').html("Alert!!");
		$('.toast-body .toast-message').html("Job description can't be empty!!");
		
		$('.toast').toast('show');
		return false;
	}else if($.trim(start).length==0 || $.trim(end).length==0){
		$('.toast-header .title').html("Alert!!");
		$('.toast-body .toast-message').html("Start/End Time can't be empty!!");
		
		$('.toast').toast('show');
		return false;
	}
}
