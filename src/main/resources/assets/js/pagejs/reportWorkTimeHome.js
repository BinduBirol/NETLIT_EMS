function getDatesByWeekNo(week) {
	var thisyear="'"+moment().year()+"'";
	var startweek = moment(thisyear).add(week, 'weeks').startOf('isoWeek').format('YYYY-MM-DD');
	var endweek = moment(thisyear).add(week, 'weeks').endOf('isoWeek').format('YYYY-MM-DD');
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
		$("#trav_from_date").val(moment().format('YYYY-MM-DD'));
		$("#trav_to_date").val(moment().endOf('isoWeek').format('YYYY-MM-DD'));

	}else if ($c == "lw") {
		$("#trav_from_date").val(moment().add(-1, 'week').startOf('isoWeek').format('YYYY-MM-DD'));
		$("#trav_to_date").val(moment().add(-1, 'week').endOf('isoWeek').format('YYYY-MM-DD'));
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
	$("#inputweekno").val(weekno);
}

function setDateRangeString() {
	$('#travmessage').html(moment($("#trav_from_date").val(), 'YYYY-MM-DD').format('dddd, MMMM D, YYYY') + ' - ' + moment($("#trav_to_date").val(), 'YYYY-MM-DD').format('dddd, MMMM D, YYYY'));
}

function getTRformsx(){
	
	var fd =$("#trav_from_date").val();
	var td =$("#trav_to_date").val();
	setDateRangeString();
	$('tbody').empty();
	
	var dates = enumerateDaysBetweenDates(fd,td);
	dates.forEach(getTableRow);
	
	$("#sorttable").trigger("click");
}


function getTRforms(){
	
	var fd =$("#trav_from_date").val();
	var td =$("#trav_to_date").val();
	setDateRangeString();
	
	$.get('/getTimeFormsBydate', {from_date:fd,to_date:td}, function (data, textStatus, jqXHR) {
		$("#getTablediv").html(data).hide().fadeIn(1000);
	});
		
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
	var disablebtn="";
	var work_minute=getworkminute(wsaart,wend,winterval);
	
	$.get('/timereport/getbydate', {date:value}, function (data, textStatus, jqXHR) {
		if(data!=""){
			wstatus=data.status;
			wsaart=data.work_start;
			wend=data.work_end;
			winterval=data.lunch_hour;
			wdesc=data.work_desc;
			if(data.status!=1){inputclass='is-invalid';}
			if(data.isapproved){btntxt='Approved';trclass="bg-success ";}else{btntxt='Pending';trclass="bg-warning ";}
			work_minute=data.work_minute;			
			disablebtn="disabled";			
		}
		
		var select ="<select "
			+"  name='status' onchange='setTRvalues(this, event)' class='status form-select "+inputclass+" form-select-sm availability"+sign+"'>"
			+" <option value='1'>Worked Hours</option>	"			
			+" <option value='3'>Sick Leave</option> "
			+" <option value='4'>Vacation</option> "
			+" <option value='5'>Child Care</option> "
			+" <option value='6'>Absent for Other Reason</option> "
			+" </select> ";
		
		
		var tr = " <tr class=' bg-opacity-25 "+trclass+"'>";
		
		tr += "<td class='sort d-none'> "+moment(value, 'YYYY-MM-DD').format('MDD')+"</td>";

		tr += "<td class='text-white'> <input type='hidden' class='date' value='"+value+"'>"+moment(value, 'YYYY-MM-DD').format('dddd<br>D MMMM YYYY') +"</td>";
		tr += "<td> "+select+"</td>";
		
		tr += "<td><input "+disablebtn+" type='time' value='"+wsaart+"' onchange='calculate(event)' name='work_start' class='start cal form-control form-control-sm text-center absent  "+inputclass+"'></td>";
		tr += "<td><input "+disablebtn+" value='"+wend+"' onchange='calculate(event)' type='time' name='work_end' class='end cal form-control form-control-sm text-center absent  "+inputclass+"'></td>";
		
		tr += "<td><input "+disablebtn+" value="+winterval+" onchange='calculate(event)' type='number' name='lunch_hour' class='lbreak cal form-control form-control-sm text-center absent  "+inputclass+"'/></td>";
		tr += "<td><input value="+work_minute+" type='text' name='work_minute' readonly class='wmint form-control form-control-sm text-center  '/></td>";
		tr += "<td><textarea onchange='calculate(event)' name='work_desc' class='form-control form-control-sm work_desc' rows='1'>"+wdesc+"</textarea></td>";
		tr += "<td><button  onclick='saveTR(this, event)'  "+disablebtn+" class='btn btn-sm btn-outline-theme' >"+btntxt+"</button></td> ";
		
		tr += " </tr> ";
		$('#trTrTable tbody').append($(tr).hide().fadeIn(1000));
		
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
	}
	
	$target.find('.btn').prop('disabled', false);
	$target.find('.btn').html('Save');
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

function calculate(e) {
	$target= $(e.target).closest('tr');
	var start = $target.find(".start").val();
	var end = $target.find(".end").val();
	var lbreak = $target.find(".lbreak").val();			
	$target.find(".wmint").val(getworkminute(start,end,lbreak));
	$target.find('.btn').prop('disabled', false);
	$target.find('.btn').html('Save');
	
}

function getworkminute(start,end,lbreak) {		
	var diff = (Math.abs(new Date('2022-05-30 '+start) - new Date('2022-05-30 '+end))/1000/60)- lbreak;
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
	
	if (validate(e)) {
		 $.post("saveDateTimeReport",
				  {
			 		status: status,
			 		from_date: date,
			 		work_start:start,
			 		work_end:end,
			 		lunch_hour:lbreak,
			 		work_desc:desc,
			 		work_minute:wmint
			 		
				  },
				  function(data, status){
				    //alert("Data: " + data + "\nStatus: " + status);
				    $('.toast-header .title').html("Response");
					$('.toast-body .toast-message').html(data);
					$('.toast').removeClass("text-danger");
					$('.toast').toast('show');
					if (data.includes("Successfully") == true) {					
						$target= $(e.target).closest('tr').addClass("bg-warning");
						$target.find('.btn').attr("disabled","disabled");
						$target.find('.btn').html("PENDING");
					}
					
				  });
	}
}

function validate(e){	
	$target= $(e.target).closest('tr');
	
	var desc= $target.find('.work_desc').val();
	var start = $target.find(".start").val();
	var end = $target.find(".end").val();
	var lbreak = $target.find(".lbreak").val();	
	var date = $target.find(".date").val();
	var status = $target.find(".status").val();	
	var wmint = $target.find(".wmint").val();
	
	if(status==1 && $.trim(desc).length>0 && $.trim(start).length>0 && $.trim(end).length>0){
		return true;
	} else if(status!=1 && wmint==0 && $.trim(desc).length>0){
		return true;
	} else{
		$('.toast-header .title').html("Alert!!");
		$('.toast-body .toast-message').html("Work Start/End/Description can't be empty!!");
		$('.toast').addClass("text-danger");
		$('.toast').toast('show');
		return false;
	}
}

function sortTable(n) {
	  var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
	  table = document.getElementById("trTrTable");
	  switching = true;
	  // Set the sorting direction to ascending:
	  dir = "asc"; 
	  /*
		 * Make a loop that will continue until no switching has been done:
		 */
	  while (switching) {
	    // start by saying: no switching is done:
	    switching = false;
	    rows = table.rows;
	    /*
		 * Loop through all table rows (except the first, which contains table
		 * headers):
		 */
	    for (i = 1; i < (rows.length - 1); i++) {
	      // start by saying there should be no switching:
	      shouldSwitch = false;
	      /*
			 * Get the two elements you want to compare, one from current row
			 * and one from the next:
			 */
	      x = rows[i].getElementsByTagName("TD")[n];
	      y = rows[i + 1].getElementsByTagName("TD")[n];
	      /*
			 * check if the two rows should switch place, based on the
			 * direction, asc or desc:
			 */
	      if (dir == "asc") {
	        if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
	          // if so, mark as a switch and break the loop:
	          shouldSwitch= true;
	          break;
	        }
	      } else if (dir == "desc") {
	        if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
	          // if so, mark as a switch and break the loop:
	          shouldSwitch = true;
	          break;
	        }
	      }
	    }
	    if (shouldSwitch) {
	      /*
			 * If a switch has been marked, make the switch and mark that a
			 * switch has been done:
			 */
	      rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
	      switching = true;
	      // Each time a switch is done, increase this count by 1:
	      switchcount ++;      
	    } else {
	      /*
			 * If no switching has been done AND the direction is "asc", set the
			 * direction to "desc" and run the while loop again.
			 */
	      if (switchcount == 0 && dir == "asc") {
	        dir = "desc";
	        switching = true;
	      }
	    }
	  }
	}
