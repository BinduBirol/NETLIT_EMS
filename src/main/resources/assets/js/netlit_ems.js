//$(".toast").toast({ autohide: false });
//setting up active menu
//alert(window.location.pathname);
$("[href='" + window.location.pathname + "']").parent().addClass('active');
$("[href='" + window.location.pathname + "']").parent().parent().parent()
		.addClass('active');

function googleTranslateElementInit() {
	new google.translate.TranslateElement({
		pageLanguage : 'en',
		// includedLanguages: 'sv,en',
		layout : google.translate.TranslateElement.InlineLayout.SIMPLE
	}, 'google_translate_element');
}

function showprocessview() {
	$(".processview").prop("disabled", true);
	// add spinner to button
	$(".processview") .html("<span class='spinner-border spinner-border-sm' role='status' aria-hidden='true'></span> Loading...");
}

function hideprocessview() {
	$(".processview").prop("disabled", false);
	$(".processview").html("Try Submit");
}

$("#toast-reload-btn").click(function() {
	location.reload();
})

function viewEmlpoyee(z) {
	$("tr").removeClass("table-primary");
	$(z).closest("tr").addClass("table-primary");
	$empid = $(z).attr("emp-id");
	$.ajax({
		url : "viewEmployee?empid=" + $empid,
		type : 'GET',
		success : function(data) {
			$('.empContent').html(data);
			$('#viewEmployee').modal('show');
			historytablecontent(z);
		},
		error : function(xhr, desc, err) {
			$('.empContent').html(xhr.responseText);
			$('#viewEmployee').modal('show');

		}
	});
}

function setAutorowspan(tableid, rowindex) {	
	const table = document.querySelector(tableid);
	let headerCell = null;
	for (let row of table.rows) {
	  const firstCell = row.cells[rowindex];	  
	  if (headerCell === null || firstCell.innerText !== headerCell.innerText) {
	    headerCell = firstCell;
	  } else {
	    headerCell.rowSpan++;
	    firstCell.remove();
	  }
	}
}


$('#search_all_users').autocomplete({	
	source: function(request, response) {
	    $.getJSON("fetchallusers", { roleid: $('#role').val(), searchtext: $('#search_all_users').val() },
	              response);
	  },
    minLength: 1,
    select: function(event, ui)
    {
    	$(this).val(ui.item.full_name+" #"+ui.item.empid);
    	return false;
    }
  }).data('ui-autocomplete')._renderItem = function(ul, item){
    return $("<li class='ui-autocomplete-row'></li>")
      .data("item.autocomplete", item)
      .append("<img width='40' class='mx-auto rounded-circle' onerror=\"this.onerror=null; this.src='assets/img/user/user.png'\"  src='data:image/jpeg;base64, "+ item.emp_image_encoded + "'/>  "+item.full_name)
      .appendTo(ul);
  }

function viewTask(z) {
	$("tr").removeClass("table-primary");
	$(z).closest("tr").addClass("table-primary");
	$taskid = $(z).attr("task-id");
	$.ajax({
		url : "viewTask?taskid=" + $taskid,
		type : 'GET',
		success : function(data) {
			$('.viewTaskContent').html(data);
			$('#viewTask').modal('show');
			historytablecontent(z);
		},
		error : function(xhr, desc, err) {
			$('.viewTaskContent').html(xhr.responseText);
			$('#viewTask').modal('show');

		}
	});
}

function minutesToHour(totalMinutes) {
	 var hours = Math.floor(totalMinutes / 60);          
	 var minutes = totalMinutes % 60;
	 return hours+" H "+minutes+" Min"
	return totalMinutes;
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

function sortTable(n) {
	  var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
	  table = document.getElementById("trTrTablexxxxx");
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