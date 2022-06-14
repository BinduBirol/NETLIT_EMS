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
		$("#trav_from_date").val(moment().format('YYYY-MM-DD'));
		$("#trav_to_date").val(moment().endOf('week').format('YYYY-MM-DD'));

	}
	setDateRangeString();
	getWeekNo();
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
		$("#trav_from_date").val(moment().format('YYYY-MM-DD'));
		$("#trav_to_date").val(moment().endOf('isoWeek').format('YYYY-MM-DD'));

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
	var weekno= moment($("#trav_from_date").val(), 'YYYY-MM-DD').format('WW')
	$("#trweek_no").html(weekno);
}

function setDateRangeString() {
	$('#travmessage').html(moment($("#trav_from_date").val(), 'YYYY-MM-DD').format('dddd, MMMM D, YYYY') + ' - ' + moment($("#trav_to_date").val(), 'YYYY-MM-DD').format('dddd, MMMM D, YYYY'));
}

function getTRforms(){
	
	var fd =$("#trav_from_date").val();
	var td =$("#trav_to_date").val();
	$('tbody').empty();
	
	var dates = enumerateDaysBetweenDates(fd,td);
	dates.forEach(getTableRow);
}





function getTableRow(value, index, array) {

	var sign = value.replace('-', '').replace('-', '');
	var wstatus="1";
	var wsaart="08:00";
	var wend="17:00";
	var winterval=45;
	var wdesc="";
	var btntxt="Save";
	var inputclass="is-valid";
	var trclass="";
	
	$.get('/timereport/getbydate', {date:value}, function (data, textStatus, jqXHR) {
		if(data!=""){
			wstatus=data.status;
			wsaart=data.work_start;
			wend=data.work_end;
			winterval=data.lunch_hour;
			wdesc=data.work_desc;
			if(data.status!=1){inputclass='is-invalid';}
			btntxt= data.isapproved;
			trclass="bg-warning bg-opacity-25";
			
		}
		
		var select ="<select "
			+" onc name='status' onchange='setTRvalues(this, event)' class='form-select "+inputclass+" form-select-sm availability"+sign+"'>"
			+" <option value='1'>Worked Hours</option>	"			
			+" <option value='3'>Sick Leave</option> "
			+" <option value='4'>Vacation</option> "
			+" <option value='5'>Child Care</option> "
			+" <option value='6'>Absent for Other Reason</option> "
			+" </select> ";
		
		
		var tr = "<tr class='"+trclass+"'>";
		

		tr += "<td>"+moment(value, 'YYYY-MM-DD').format('dddd<br>D MMMM YYYY') +"</td>";
		tr += "<td>"+select+"</td>";
		
		tr += "<td><input type='time' value='"+wsaart+"' name='work_start' class='form-control form-control-sm text-center absent  "+inputclass+"'></td>";
		tr += "<td><input value='"+wend+"' type='time' name='work_end' class='form-control form-control-sm text-center absent  "+inputclass+"'></td>";
		
		tr += "<td><input value="+winterval+" type='number' name='lunch_hour' class='form-control form-control-sm text-center absent  "+inputclass+"'/></td>";
		tr += "<td><textarea name='work_desc' class='form-control form-control-sm work_desc' rows='2'>"+wdesc+"</textarea></td>";
		tr += "<td><button onclick='saveTR(this, event)' class='btn btn-sm btn-outline-theme' id='"+sign+"'>"+btntxt+"</button></td>";
		
		tr += "</tr>";
		$('#trTrTable tbody').append(tr);
		
		$('.availability'+sign+' option[value="'+wstatus+'"]').attr("selected", "selected");
		
	});
	
	
	
}




function enumerateDaysBetweenDates(startDate, endDate) {
    var dates = [];

    var currDate = moment(startDate).startOf('day');
    var lastDate = moment(endDate).startOf('day');
    dates.push(currDate.format('YYYY-MM-DD'));
    while(currDate.add(1, 'days').diff(lastDate) <= 0) {
        dates.push(currDate.format('YYYY-MM-DD'));
    }
    return dates;
};


function setTRvalues(t, e) {
	
	$target= $(e.target).closest('tr');
	
	if ($(t).val() == 1) {
		$target.find('.absent').prop('required', true);
		$target.find('.absent').prop('disabled', false);
		$(t).removeClass("is-invalid");
		$(t).addClass("is-valid");
		$target.find('.absent').removeClass("is-invalid");
		$target.find('.absent').addClass("is-valid");
	} else {		
		$target.find(".absent").removeAttr("required");
		$target.find('.absent').prop('disabled', true);
		$target.find(".absent").removeClass("is-valid");
		$target.find(".absent").addClass("is-invalid");		
		$(t).removeClass("is-valid");
		$(t).addClass("is-invalid");		
	}
}

function availablity(z) {			
	$empid = $(z).attr("emp-id");
	$.ajax({
		url : "settimereport?empid=" + $empid,
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

function saveTR(t,e) {	
	$target= $(e.target).closest('tr');
	var desc= $target.find('.work_desc').val();
	if ($.trim(desc).length > 0) {
	    alert(desc);
	}else{
		$('.toast-header .title').html("Alert!!");
		$('.toast-body .toast-message').html("Please add a note to your Time Report");
		$('.toast').toast('show');
	}
}


