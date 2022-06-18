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