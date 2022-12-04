var mycalendar;

function hexToRGB(hex, alpha) {
    var r = parseInt(hex.slice(1, 3), 16),
        g = parseInt(hex.slice(3, 5), 16),
        b = parseInt(hex.slice(5, 7), 16);

    if (alpha) {
        return "rgba(" + r + ", " + g + ", " + b + ", " + alpha + ")";
    } else {
        return "rgb(" + r + ", " + g + ", " + b + ")";
    }
}


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
$("#editModal input").change(function() {
	var wstart = $('#editModal #start').val();
	var wend = $('#editModal #end').val();
	var wbreak = $('#editModal #intarval').val();
	var totalWorkMinute = getworkminute(wstart, wend, wbreak);
	var totalWorkHour = minutesToHour(totalWorkMinute);
	$('#editModal #totalWorkMinute').val(totalWorkMinute);
	$('#editModal #work_hour_hidden').val(totalWorkHour);
	$('#editModal #work_hour').text(totalWorkHour);	
})

function setTimeParams(){
	var option = $("#status option:selected"); 
	var start= option.attr("start");
	var end= option.attr("end");
	var lbreak= option.attr("lbreak");
	var isworking= option.attr("isworking");
	
	var totalWorkMinute = getworkminute(start, end, lbreak);
	var totalWorkHour = minutesToHour(totalWorkMinute);	
	
	$('#editModal #start').val(start);
	$('#editModal #end').val(end);
	$('#editModal #intarval').val(lbreak);		
	$('#editModal #totalWorkMinute').val(totalWorkMinute);
	$('#editModal #work_hour_hidden').val(totalWorkHour);
	$('#editModal #work_hour').text(totalWorkHour);
	
	if(isworking=="true"){
		$('#editModal .dis').removeAttr('disabled');		
	}else{
		$('#editModal .dis').attr('disabled','disabled');
	}
}

function getworkminute(start, end, lbreak) {
	var diff = ((Math.abs(new Date('2022-05-30 ' + start) - new
		Date('2022-05-30 ' + end)) / 1000 / 60) - lbreak);
	if(diff>0) return diff;
	return 0;
}


$("#editTimeReportForm").submit(function(event) {
	event.preventDefault();
	var values = {};
	$.each($(this).find("input, textarea").serializeArray(), function(i, field) {
		values[field.name] = field.value;
	});

	if (values.work_minute > 480) {
		$("#alert-msg").text("Can not report more than 8 hours");
	} else {
		$.ajax({
			url: $(this).attr("action"),
			type: $(this).attr("method"),
			data: new FormData(this),
			processData: false,
			contentType: false,
			success: function(data, status) {
				$("#editModal .btn-close").click();
				$('.toast-header .title').html("Response");
				$('.toast-body .toast-message').html(data);
				$('.toast').toast('show');

				if (data.includes("Successfully") == true) {
					mycalendar.refetchEvents();					
				}				
				return false;
			},
			error: function(xhr, desc, err) {
				$('.toast-header .title').html("Error");
				$('.toast-body .toast-message').html(
					xhr.responseText);
				$('.toast').toast('show');
				return false;
			}
		});
	}


});


var handleRenderFullcalendar = function() {
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
			var shortDateFormat = 'yyyy-MM-dd';
			var date = new Date(info.event.start);
			//JSON.stringify(info.event)
			var totalWorkMinute = getworkminute(info.event.extendedProps.work_start, info.event.extendedProps.work_end, info.event.extendedProps.work_intarval);
			var totalWorkHour = minutesToHour(totalWorkMinute);

			$('#editModal .modal-footer').show();
			$('#editModal input, textarea').val("");
			$('#editModal .modal-content').css('background-color', "");
			$("#alert-msg , #status_str").text("");
			$('#editModal input, select,textarea').prop("disabled",false);
			
			$('#editModal .modal-title').text("Week #"+moment(date).format("WW dddd, MMM Do YYYY"));
			
			$('#editModal #date').val(formatDate(date));
			$('#editModal #status').val(info.event.extendedProps.typeid);
			$('#editModal #start').val(info.event.extendedProps.work_start);
			$('#editModal #end').val(info.event.extendedProps.work_end);
			$('#editModal #intarval').val(info.event.extendedProps.work_intarval);
			$('#editModal #work_hour_hidden').val(totalWorkHour);			
			
			if(info.event.extendedProps.isWorking=="true"){
				$('#editModal .dis').removeAttr('disabled');		
			}else{
				totalWorkMinute = 0;
				$('#editModal .dis').attr('disabled','disabled');
			}
			
			
			$('#editModal #totalWorkMinute').val(totalWorkMinute);
			$('#editModal #work_hour').text(totalWorkHour);
			
			$('#editModal #submit').prop("disabled",false);
			
			$('#editModal').modal('show');

			info.revert();
		},
		eventClick: function(arg) {
			var data = arg.el.fcSeg.eventRange.def.extendedProps;
			var type = arg.event.title;
			var start = arg.event.start;
			var id = arg.event._def.defId;

			var shortDateFormat = 'yyyy-MM-dd';
			var date = new Date(start);
			
			var bgclr=arg.event.backgroundColor;
			
			
			$('#editModal .modal-content').css('background-color', hexToRGB(bgclr,.2));
			
			$('#editModal input, textarea').val("");
			
			$('#editModal .modal-title').text("Week #"+moment(date).format("WW dddd, MMM Do YYYY"));
			
			$('#editModal #tr_id').val(data.trid);
			$('#editModal #status').val(data.typeid);
			$('#editModal #date').val(formatDate(date));
			$('#editModal #start').val(data.startHour);
			$('#editModal #end').val(data.endHour);
			$('#editModal #intarval').val(data.breakMin);
			$('#editModal #totalWorkMinute').val(data.work_minute);
			$('#editModal #work_hour').html(minutesToHour(data.work_minute));
			$('#editModal #work_hour_hidden').val(minutesToHour(data.work_minute));			
			$('#editModal #text').val(data.workdesc);
			var status_str ="[ PENDING ]";
			//$('#editModal #text').val(JSON.stringify(arg.event));
			
			if(data.isapproved){
				status_str ="[ APPROVED ]";
				$('#editModal .modal-footer').hide();				
				$('#editModal input, select, textarea').prop("disabled",true);
			}else{
				$('#editModal .modal-footer').show();
				$('#editModal input, select,textarea').prop("disabled",false);
			}
			if(data.isrejected)status_str ="[ REJECTED ]";
			
			$("#editModal #status_str").text(status_str);
			$("#editModal #alert-msg").text("");
			$('#editModal').modal('show');
			
		},
		//ends bindu
		dayMaxEvents: 3,
		droppable: true,
		themeSystem: 'bootstrap',
		eventLimit: true,
		views: {
			timeGrid: {
				eventLimit: 3 
			}
		},
		events: '/getTimereportEventsForcalendar',
		//firstDay: moment().startOf('isoWeek'),
		timezone: 'local'
		
	});
	calendar.render();
	mycalendar= calendar;
};

/* Controller
------------------------------------------------ */
$(document).ready(function() {
	handleRenderFullcalendar();
});