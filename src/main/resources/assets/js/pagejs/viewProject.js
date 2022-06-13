$('input[type=radio][name=approve]').on('change', function(e) {
	$target = $(e.target).closest('tr');
	var empid = $target.find(".empidapp").val();
	$.get("approveEmptoProject", {
		empid : empid,
		projectid : $("#projectid").val(),
		approve : $(this).val()
	}, function(data, status) {
		$('.toast-header .title').html(status);
		$('.toast-body .toast-message').html(data);
		$('.toast').toast('show');
	});
});

$(".make-admin").click(function(e) {
	$target = $(e.target).closest('tr');
	var empid = $target.find(".empid").val();
	$.get("makeAdmin", {
		empid : empid,
		projectid : $("#projectid").val()
	}, function(data, status) {
		$target.find(".isadmin").html(data);
	});
})

$(".remove-admin").click(
		function(e) {
			$target = $(e.target).closest('tr');
			var empid = $target.find(".empid").val();
			$.get("removeAdmin", {
				empid : empid,
				projectid : $("#projectid").val()
			}, function(data, status) {
				$target.find(".isadmin").html(data);
				if (data == "Last Admin") {
					$('.toast-header .title').html("Error!");
					$('.toast-body .toast-message').html(
							"Can't remove the only Admin");
					$('.toast').toast('show');
				}
			});
		})

function editTask(z) {
	$("tr").removeClass("table-primary");
	$(z).closest("tr").addClass("table-primary");
	$taskid = $(z).attr("task-id");
	$.ajax({
		url : "editTask?taskid=" + $taskid,
		type : 'GET',
		success : function(data) {
			$('.editTaskContent').html(data);
			$('#editTask').modal('show');
			historytablecontent(z);
		},
		error : function(xhr, desc, err) {
			$('.editTaskContent').html(xhr.responseText);
			$('#editTask').modal('show');

		}
	});
}

function updateTaskStatus(e, t) {
	$target = $(e.target).closest('tr');
	var taskid = $target.find(".taskid").val();
	var status = $(t).val();

	$.get("updateTaskStatus", {
		taskid : taskid,
		status : status
	}, function(data, status) {
		$('.toast-header .title').html("Success");
		$('.toast-body .toast-message').html(data);
		$('.toast').toast('show');
	});
}

function updateTaskPriority(e, t) {
	$target = $(e.target).closest('tr');
	var taskid = $target.find(".taskid").val();
	var p = $(t).val();
	$.get("updateTaskPriority", {
		taskid : taskid,
		priority : p
	}, function(data, status) {
		$('.toast-header .title').html("Success");
		$('.toast-body .toast-message').html(data);
		$('.toast').toast('show');
	});
}

function updateTaskType(e, t) {
	$target = $(e.target).closest('tr');
	var taskid = $target.find(".taskid").val();
	var type = $(t).val();
	$.get("updateTaskType", {
		taskid : taskid,
		type : type
	}, function(data, status) {
		$('.toast-header .title').html("Success");
		$('.toast-body .toast-message').html(data);
		$('.toast').toast('show');
	});
}

$('#activityTable').DataTable({
    dom: "<'row mb-3'<'col-sm-4'l><'col-sm-8 text-end'<'d-flex justify-content-end'fB>>>t<'d-flex align-items-center'<'me-auto'i><'mb-0'p>>",
    lengthMenu: [ 10, 20, 30, 40, 50 ],
    responsive: true,
    buttons: [ 
      { extend: 'print', className: 'btn btn-default' },
      { extend: 'csv', className: 'btn btn-default' }
    ]
  });

$('#datatableDefaultx').DataTable({
	dom: "<'row mb-3'<'col-md-4 mb-3 mb-md-0'l><'col-md-8 text-right'<'d-flex justify-content-end'fB>>>t<'row align-items-center'<'mr-auto col-md-6 mb-3 mb-md-0 mt-n2 'i><'mb-0 col-md-6'p>>",
	lengthMenu: [ 10, 20, 30, 40, 50 ],
	responsive: true,
	order: [[0, 'desc']],
	buttons: [
		{ extend: 'print', className: 'btn btn-outline-default btn-sm ms-2' },
		{ extend: 'csv', className: 'btn btn-outline-default btn-sm' }
	]
});

function getTimereportModal(z) {			
	$empid = $(z).attr("emp-id");
	$taskid = $(z).attr("task-id");
	$.get("timereporttask", {
		taskid : $taskid,
		empid : $empid
	}, function(data, status) {
		$('.availablitymodalcontent').html(data);
		$('#availablitymodal').modal('show');			
	});
}

$(".avidcheckbox").change(function (e) {			
	//$("tr").removeClass("table-primary");			
	$target= $(e.target).closest('tr');
	//$target.addClass("table-primary");
	var start = $target.find(".start").val();
	var end = $target.find(".end").val();
	var lbreak = $target.find(".lbreak").val();	
	var minutes= $target.find(".wmint").val();
	var wdesc= $target.find(".wdesc").val();
	var av_id= this.id;
	
	if (!$.trim(wdesc).length > 0){
		
		//$('#'+this.id).attr( "checked", false );
		$target.find(".wdesc").focus();
		//$target.find(".avidcheckbox").attr( "checked", false );
		
		//return false;
	}
	
	var app;
	if ($('#'+this.id).is(':checked')) { app=1; }else{ app=0; }
	var url="approveAvailablity?av_id="+av_id
			+"&start="+start
			+"&end="+end
			+"&lbreak="+lbreak
			+"&minutes="+minutes
			+"&approve="+app
			+"&wdesc="+wdesc;
	
	$.ajax({
		url : url,
		type : 'GET',
		success : function(data) {
			console.log(data);
		},
		error : function(xhr, desc, err) {
			alert(xhr.responseText);
		}
	});
	
	
})


$(".cal").change(function (e) {
	//$('#'+this.id).attr( "checked", false );	
	//$("tr").removeClass("table-primary");			
	$target= $(e.target).closest('tr');
	$target.find(".avidcheckbox").attr( "checked", false );
	//$target.addClass("table-primary");
	var start = $target.find(".start").val();
	var end = $target.find(".end").val();
	var lbreak = $target.find(".lbreak").val();			
	var diff = (Math.abs(new Date('2022-05-30 '+start) - new Date('2022-05-30 '+end))/1000/60)- lbreak;
	$target.find(".wmint").val(diff);		
	
})	

setAutorowspan('#workhtable',2);
setAutorowspan('#workhtable',1);

setAutorowspan('#workhtable',0);

