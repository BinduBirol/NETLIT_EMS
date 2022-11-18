pageStartAjaxcall();

$("#searchBtn").click(function() {
	ajaxcall();
})

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
		}
		
		ajaxcall();		
	})
	

$("#empidselect").change(function() {
	ajaxcall();
})
	

function pageStartAjaxcall(){
	$("#from_date").val(moment().startOf('month').format('YYYY-MM-DD'));
	$("#to_date").val(moment().endOf('month').format('YYYY-MM-DD'));
	ajaxcall();
}

function ajaxcall(){
	
	var fd = $("#from_date").val();
	var td = $("#to_date").val();

	if (fd == "" || td == "") {
		$("#daterangeselect").focus();
		$('.toast-header .title').html("Abort!!");
		$('.toast-body .toast-message').html("Please select a date range.");		
		$('.toast').toast('show');
		return;
	}
	
	$.post("/getApprovedTimeReports", {
		from_date : fd,
		to_date : td
	}, function(data, status) {	
		setDateRangeString();
		$("#ajaxresponse").hide();
		$("#ajaxresponse").html(data);
		
		var rowCount = $('.approvedtable tr').length;
		if(rowCount==0){$("#ajaxresponse").html("<h4>No approved time reports</h4>");}
		designTable();
		$("#ajaxresponse").slideDown('slow');
		
	});	
}


function designTable(){
	$(".approvedtable").each(function() {
		var id="#"+$(this).attr("id");
		//sortmyTable(0,$(this).attr("id"));
		setAutorowspan(id,0);
	});	
	
}

function setDateRangeString() {
	$('#datestr').html(moment($("#from_date").val(), 'YYYY-MM-DD').format('dddd, MMMM D, YYYY') + ' - ' + moment($("#to_date").val(), 'YYYY-MM-DD').format('dddd, MMMM D, YYYY'));
}