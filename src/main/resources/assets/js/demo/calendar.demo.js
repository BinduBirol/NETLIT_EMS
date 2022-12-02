function formatDate(date) {
	var d = new Date(date),
		month = '' + (d.getMonth() + 1),
		day = '' + d.getDate(),
		year = d.getFullYear();

	if (month.length < 2)
		month = '0' + month;
	if (day.length < 2)
		day = '0' + day;

	return [year, month, day].join('-');
}


function getcalendar() {
	$.get("/getTimereportsForcalendar", function(data, status) {
		var events = [];
		for (var i = 0; i < data.length; i++) {
			var value = {};
			var startdate = new Date(data[i].date);
			var enddate = new Date(data[i].date);
			var start = data[i].work_start;
			var end = data[i].work_end;
			var workdesc = data[i].work_desc;
			var color = app.color.blue;

			//extendedProps
			value["trid"] = data[i].tr_id;
			value["startHour"] = data[i].work_start;
			value["endHour"] = data[i].work_end;
			value["breakMin"] = data[i].work_interval;
			value["typeid"] = data[i].status;
			value["work_minute"] = data[i].work_minute;
			value["work_hour"] = data[i].work_hour;
			value["isapproved"] = data[i].isapproved;
			value["isrejected"] = data[i].isrejected;
			value["workdesc"] = workdesc;
			
			value["title"] = data[i].trTypes.typename;
			
			if (data[i].work_minute > 0) {
				var startHour = data[i].work_start.split(":")[0];
				var startmin = data[i].work_start.split(":")[1];
				var endHour = data[i].work_end.split(":")[0];
				var endmin = data[i].work_end.split(":")[0];

				startdate.setHours(startHour, startmin);
				enddate.setHours(endHour, endmin);


				//
				
				
				value["start"] = startdate;
				value["end"] = enddate;


			} else {
				//value["title"] = workdesc;
				value["start"] = startdate;
				value["allDay"] = true;
				value["color"] = app.color.blue;
			}

			if (data[i].isapproved)
				value["color"] = app.color.green;

			if (data[i].isrejected)
				value["color"] = app.color.red;
			events.push(value);
		}

		handleRenderFullcalendar(events);
	});
}


function getworkminute(start, end, lbreak) {

	var diff = ((Math.abs(new Date('2022-05-30 ' + start) - new
		Date('2022-05-30 ' + end)) / 1000 / 60) - lbreak);
	return diff;

}

$("#editModal input, select").change(function() {
	var wstart = $('#editModal #start').val();
	var wend = $('#editModal #end').val();
	var wbreak = $('#editModal #intarval').val();
	var totalWorkMinute = getworkminute(wstart, wend, wbreak);
	var totalWorkHour = minutesToHour(totalWorkMinute);
	$('#editModal #totalWorkMinute').val(totalWorkMinute);
	$('#editModal #work_hour').text(totalWorkHour);
})

$("#editTimeReportForm").submit(function(event) {
	event.preventDefault();
	var values = {};
	$.each($(this).find("input, textarea").serializeArray(), function(i, field) {
		values[field.name] = field.value;
	});

	if (values.work_minute > 480) {
		//alert("Can not report more than 8 hours");
		$("#alert-msg").text("Can not report more than 8 hours");
	} else {
		//alert(JSON.stringify(values));
		$.ajax({
			url: $(this).attr("action"),
			type: $(this).attr("method"),
			data: new FormData(this),
			processData: false,
			contentType: false,
			success: function(data, status) {
				hideprocessview();
				$("#editModal .btn-close").click();
				$('.toast-header .title').html("Response");
				$('.toast-body .toast-message').html(data);
				$('.toast').toast('show');

				if (data.includes("Successfully") == true) {
					getcalendar();
					//$('#calendar').fullCalendar( 'removeEventSource', source )
					//$('#calendar').fullCalendar( 'addEventSource', source )
				}

				return false;
			},
			error: function(xhr, desc, err) {
				hideprocessview();
				$('.toast-header .title').html("Error");
				$('.toast-body .toast-message').html(
					xhr.responseText);
				$('.toast').toast('show');
				return false;

			}
		});
	}


});


var handleRenderFullcalendar = function(events) {
	// external events
	var containerEl = document.getElementById('external-events');
	var Draggable = FullCalendarInteraction.Draggable;
	new Draggable(containerEl, {
		itemSelector: '.fc-event-link',
		eventData: function(eventEl) {
			return {
				title: eventEl.innerText,
				color: eventEl.getAttribute('data-color'),
				typeid: eventEl.getAttribute('data-typeid'),
				work_start: eventEl.getAttribute('data-work-start'),
				work_end: eventEl.getAttribute('data-work-end'),
				work_intarval: eventEl.getAttribute('data-work-intarval'),
				isWorking: eventEl.getAttribute('data-isWorking')
			};
		}
	});

	// fullcalendar
	var d = new Date();
	var month = d.getMonth() + 1;
	month = (month < 10) ? '0' + month : month;
	var year = d.getFullYear();
	var day = d.getDate();
	var today = moment().startOf('day');
	var calendarElm = document.getElementById('calendar');







	var calendar = new FullCalendar.Calendar(calendarElm, {
		headerToolbar: {
			left: 'dayGridMonth,timeGridWeek,timeGridDay',
			center: 'title',
			right: 'prev,next today'
		},
		buttonText: {
			today: 'Today',
			month: 'Month',
			week: 'Week',
			day: 'Day'
		},
		initialView: 'dayGridMonth',
		editable: false,
		//bindu
		eventReceive: function(info) {
			//alert(info.event.title + " is being dropped on " + info.event.start.toISOString());\

			var shortDateFormat = 'yyyy-MM-dd';
			var date = new Date(info.event.start);
			//JSON.stringify(info.event)
			var totalWorkMinute = getworkminute(info.event.extendedProps.work_start, info.event.extendedProps.work_end, info.event.extendedProps.work_intarval);
			var totalWorkHour = minutesToHour(totalWorkMinute);


			$('#editModal #tr_id').val("");
			$('#editModal .modal-title').text(formatDate(date));
			$('#editModal #type-name').text(info.event.title);
			$('#editModal #date').val(formatDate(date));
			$('#editModal #status').val(info.event.extendedProps.typeid);
			$('#editModal #start').val(info.event.extendedProps.work_start);
			$('#editModal #end').val(info.event.extendedProps.work_end);
			$('#editModal #intarval').val(info.event.extendedProps.work_intarval);
			$('#editModal #text').val("");
			$("#alert-msg").text("");
			if (info.event.extendedProps.isWorking == 'false') {
				totalWorkMinute = 0;
				$("#editModal .dis").prop("disabled", true);
			} else {
				$("#editModal .dis").prop("disabled", false);
			}
			$('#editModal #totalWorkMinute').val(totalWorkMinute);
			$('#editModal #work_hour').text(totalWorkHour);
			
			$('#editModal #submit').prop("disabled",false);
			
			$('#editModal').modal('show');

			info.revert();



			//if (!confirm("Are you sure about this change?")) {
			// info.revert();
			//}
		},
		eventClick: function(arg) {
			var data = arg.el.fcSeg.eventRange.def.extendedProps;
			var type = arg.event.title;
			var start = arg.event.start;
			var id = arg.event._def.defId;

			var shortDateFormat = 'yyyy-MM-dd';
			var date = new Date(start);
			//JSON.stringify(info.event)
			//var totalWorkMinute = getworkminute(info.event.extendedProps.work_start, info.event.extendedProps.work_end, info.event.extendedProps.work_intarval);
			//var totalWorkHour = minutesToHour(totalWorkMinute);

			var RGB=arg.event.backgroundColor;
			var A='0.5';
			var RGBA='('+parseInt(RGB.substring(1,3),16)+','+parseInt(RGB.substring(3,5),16)+','+parseInt(RGB.substring(5,7),16)+','+A+')';
			
			var bgclr="bg-primary";
			if(data.isapproved)bgclr="bg-success";
			if(data.isrejected)bgclr="bg-danger";
			
			//$('#editModal .modal-content').alterClass( 'bg-*', bgclr+' bg-opacity-25' );
			$('#editModal .modal-title').text(formatDate(date));
			//$('#editModal #text').val(JSON.stringify(arg.event));

			
			$('#editModal #tr_id').val(data.trid);
			$('#editModal #status').val(data.typeid);
			$('#editModal #date').val(formatDate(date));
			$('#editModal #start').val(data.startHour);
			$('#editModal #end').val(data.endHour);
			$('#editModal #intarval').val(data.breakMin);
			$('#editModal #totalWorkMinute').val(data.work_minute);
			$('#editModal #work_hour').html(minutesToHour(data.work_minute));
			  
			$('#editModal #text').val(data.workdesc);
			
			if(data.isapproved){
				$('#editModal #submit').prop("disabled",true);
			}else{
				$('#editModal #submit').prop("disabled",false);
			}

			$("#alert-msg").text("");
			$('#editModal').modal('show');
			
		},
		//ends bindu
		dayMaxEvents: 3,
		droppable: true,
		themeSystem: 'bootstrap',
		eventLimit: true, // for all non-TimeGrid views
		views: {
			timeGrid: {
				eventLimit: 3 // adjust to 3 only for timeGridWeek/timeGridDay
			}
		},
		events: events,

	});



	calendar.render();
	
	getcalendarEvents(calendar);

};



/* Controller
------------------------------------------------ */
$(document).ready(function() {
	//handleRenderFullcalendar([]);
	getcalendar();
});