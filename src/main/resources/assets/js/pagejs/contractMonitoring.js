

pageStartAjaxcall();

function pageStartAjaxcall(){	
	let searchParams = new URLSearchParams(window.location.search);
	var msg = searchParams.get('msg');
	
	if(msg!=null) {
		
	}

	
	
	var fd = moment().subtract(1, 'year').format('YYYY-MM-DD');
	var td = moment().format('YYYY-MM-DD');
	var emp= "me";
	
	$.post("/getContractTable", {
		from_date : fd,
		to_date : td
	}, function(data, status) {		
		$("#ajaxresponse").html(data).hide().slideDown();		
		//$("#from_date").val(fd);
		//$("#to_date").val(td);
		var rowCount = $('#datatable tbody tr').length;		
		if(rowCount==0){$("#ajaxresponse").html("<h4>No record to show.</h4>").hide().slideDown();}		
	});	
}

function getContractTable() {
	pageStartAjaxcall();
}
function deleteContract(e) {
	alert(1);
	
}