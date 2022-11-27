pageStartAjaxcall();

$("#searchBtn").click(function() {
	ajaxcall();
})


function calculate(e) {
			$target= $(e.target).closest('tr');
			var start = $target.find(".start").val();
			var end = $target.find(".end").val();
			var lbreak = $target.find(".lbreak").val();			
			$target.find(".wmint").val(getworkminute(start,end,lbreak));
			$target.find(".work_hour").val(minutesToHour(getworkminute(start,end,lbreak)));				
		}
		
		function getworkminute(start,end,lbreak) {		
			 var diff = ((Math.abs(new Date('2022-05-30 '+start) - new
					 Date('2022-05-30 '+end))/1000/60)- lbreak);			
			return diff;
			
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
				$(z).removeClass("is-invalid");
				$(z).addClass("is-valid");
				calculate(e);
			}else{
				$target.find('.absent').prop('disabled', true);	
				$(z).removeClass("is-valid");
				$(z).addClass("is-invalid");
				calculate(e);
			}
			
		}

$("#daterangeselect").change(function() {
		$c = $(this).val();		

		if ($c == "t") {
			$("#from_date").val(moment().format('YYYY-MM-DD'));
			$("#to_date").val(moment().format('YYYY-MM-DD'));

		} else if ($c == "y") {
			$("#from_date").val(moment().subtract(1, 'days').format('YYYY-MM-DD'));
			$("#to_date").val(moment().subtract(1, 'days').format('YYYY-MM-DD')); 

		} else if ($c == "n2d") {
			$("#from_date").val(moment().add(1, 'days').format('YYYY-MM-DD'));
			$("#to_date").val(moment().add(3, 'days').format('YYYY-MM-DD')); 

		} else if ($c == "tm") {
			$("#from_date").val(moment().startOf('month').format('YYYY-MM-DD'));
			$("#to_date").val(moment().endOf('month').format('YYYY-MM-DD'));

		} else if ($c == "lm") {
			$("#from_date").val(moment().subtract(1, 'month').startOf('month').format('YYYY-MM-DD'));
			$("#to_date").val(moment().subtract(1, 'month').endOf('month').format('YYYY-MM-DD'));

		} else if ($c == "nm") {
			$("#from_date").val(moment().add(1, 'month').startOf('month').format('YYYY-MM-DD'));
			$("#to_date").val(moment().add(1, 'month').endOf('month').format('YYYY-MM-DD'));

		} else if ($c == "tw") {
			$("#from_date").val(moment().startOf('isoWeek').format('YYYY-MM-DD'));
			$("#to_date").val(moment().endOf('isoWeek').format('YYYY-MM-DD'));

		} else if ($c == "lw") {
			$("#from_date").val(moment().subtract(1, 'week').startOf('isoWeek').format('YYYY-MM-DD'));
			$("#to_date").val(moment().subtract(1, 'week').endOf('isoWeek').format('YYYY-MM-DD'));

		} else if ($c == "nw") {
			$("#from_date").val(moment().add(1, 'week').startOf('isoWeek').format('YYYY-MM-DD'));
			$("#to_date").val(moment().add(1, 'week').endOf('isoWeek').format('YYYY-MM-DD'));
		}else if ($c == "l2d") {
			$("#from_date").val(moment().add(-3, 'days').format('YYYY-MM-DD'));
			$("#to_date").val(moment().add(-1, 'days').format('YYYY-MM-DD')); 

		}
		
		ajaxcall();		
	})
	
	$("#empidselect").change(function() {
		ajaxcall();
	})
	
	
	
function ajaxcall(){
	
	var fd = $("#from_date").val();
	var td = $("#to_date").val();
	var emp= $("#empidselect").val();

	if (fd == "" || td == "") {
		$("#daterangeselect").focus();
		$('.toast-header .title').html("Abort!!");
		$('.toast-body .toast-message').html("Please select a date range.");		
		$('.toast').toast('show');
		return;
	}
	
	$.post("/getpendingTimeReports", {
		from_date : fd,
		to_date : td,
		emp_id: emp
	}, function(data, status) {		
		$("#ajaxresponse").html(data).hide().slideDown();		
		var rowCount = $('#pendingtable tr').length;
		if(rowCount==0){$("#ajaxresponse").html("<h4>No pending time reports</h4>").hide().slideDown();}
		// $("html, body").animate({ scrollTop: $(document).height() }, 500);
	});	
}

function pageStartAjaxcall(){	
	var fd = moment().subtract(1, 'year').format('YYYY-MM-DD');
	var td = moment().format('YYYY-MM-DD');
	var emp= "me";
	
	$.post("/getpendingTimeReports", {
		from_date : fd,
		to_date : td,
		emp_id: emp
	}, function(data, status) {		
		$("#ajaxresponse").html(data).hide().slideDown();		
		$("#from_date").val(fd);
		$("#to_date").val(td);
		var rowCount = $('#pendingtable tr').length;		
		if(rowCount==0){$("#ajaxresponse").html("<h4>No pending time reports</h4>").hide().slideDown();}
		// $("html, body").animate({ scrollTop: $(document).height() }, 500);
	});	
}

$(".btn-dec").click(function(e) {
	showprocessview();
	var decision=$(this).text();
	var d=$("#actionDecision").val()
	var data= $("#data").val();
	var employee= $("#employee").val();
	var action= $("#action").val();
	var dataid= $("#dataid").val();
	var ismail= $("#mail").is(":checked");
	
	$.post(action, {
		rg : data,
		ismail : ismail,
		decision: d
	}, function(data, status) {
		hideprocessview();
		$("#confirmDecisionmodal .btn-close").click();
		$('.toast-header .title').html("Response!");
		$('.toast-body .toast-message').html(data);		
		$('.toast').toast('show');
		
		var str="";
		var bgclass="";
		if(d==1){
			$("."+dataid).closest("tr").addClass("bg-opacity-25 bg-success");
			str="APPROVED";
		}else{
			$("."+dataid).closest("tr").addClass("bg-opacity-25 bg-danger");
			str="REJECTED";;
		}
		
		$("."+dataid).html(str);
		
		if(dataid=="all"){
			ajaxcall();
		}
		
		return;
	});	
});

function actionAll(d){
	var data;
	var array=[];
	$("#pendingtable").find("tr").each(function(){
		var values = {};
		$.each($(this).find("input, select, textarea").serializeArray(), function(i, field) {
		    values[field.name] = field.value;
		});
		if(!jQuery.isEmptyObject( values ) && typeof values!="undefined"){
			array.push(values);
		}
    });
	
	data=JSON.stringify(array);
	var dc=0;
	if(d){
		dc=1;
		$('#confirmDecisionmodal .modal-title').text("APPROVE ALL");
		$('#confirmDecisionmodal .modal-body p').text("You are going APPROVE all the time reports.");
		$("#confirmDecisionmodal .btn-dec").removeClass("btn-danger").addClass("btn-success").text("APPROVE ALL");
	}else{
		dc=0;
		$('#confirmDecisionmodal .modal-title').text("REJECT ALL");
		$('#confirmDecisionmodal .modal-body p').text("You are going to REJECT all the time reports.");
		$("#confirmDecisionmodal .btn-dec").removeClass("btn-success").addClass("btn-danger").text("REJECT ALL");
	}	
	$('#confirmDecisionmodal #action').val("doPendingTimeReportAction");
	$("#confirmDecisionmodal #dataid").val("all");
	$('#confirmDecisionmodal #data').val(data);
	$('#confirmDecisionmodal #actionDecision').val(dc);	
	$('#confirmDecisionmodal').modal('show');
}


