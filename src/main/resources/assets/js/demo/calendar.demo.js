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

function getcalendar(){
	$.get("/getTimereportsForcalendar", function(data, status){
		var events=[];
		for(var i=0;i<data.length;i++){
			var value={};
			var startdate = new Date(data[i].date);
			var enddate = new Date(data[i].date);
			var start = data[i].work_start;
			var end = data[i].work_end;
			var workdesc = data[i].work_desc;
			var color = app.color.blue;
			
			if(data[i].work_minute>0){
				var startHour=data[i].work_start.split(":")[0];
				var startmin=data[i].work_start.split(":")[1];				
				var endHour=data[i].work_end.split(":")[0];
				var endmin=data[i].work_end.split(":")[0];			
				
				 startdate.setHours(startHour,startmin);
				 enddate.setHours(endHour,endmin);
				
				
				 value["title"] = workdesc;
				 value["start"] = startdate;
				 value["end"] = enddate;			 
				 
			}else{				
				value["title"] = workdesc;
				 value["start"] = startdate;
				 value["allDay"] = true;
				 value["color"] = app.color.indigo;
			}
			
			 if(data[i].isapproved)
				 value["color"] = app.color.green;
			 
			 if(data[i].isrejected)
				 value["color"] = app.color.red;
			 events.push(value);
		}
		
		handleRenderFullcalendar(events);
	 });
}


function getworkminute(start,end,lbreak) {	

	var diff = ((Math.abs(new Date('2022-05-30 '+start) - new
			 Date('2022-05-30 '+end))/1000/60)- lbreak);
	return diff;
	
}

$("#formModal input").change(function(){
	var wstart=$('#formModal #start').val();
	var wend=$('#formModal #end').val();
	var wbreak=$('#formModal #intarval').val();	
	var totalWorkMinute=getworkminute(wstart,wend,wbreak);
	var totalWorkHour=minutesToHour(totalWorkMinute);
	$('#formModal #totalWorkMinute').val(totalWorkMinute);
    $('#formModal #total').text(totalWorkHour); 
})

$("#insertTimeReportForm").submit(function(event) {
			event.preventDefault();		
		var values = {};
		$.each($(this).find("input, textarea").serializeArray(), function(i, field) {
		    values[field.name] = field.value;
		});
		
		if(values.work_minute>480){
			//alert("Can not report more than 8 hours");
			$("#alert-msg").text("Can not report more than 8 hours");			
		}else{
			//alert(JSON.stringify(values));
			$.ajax({
						url : $(this).attr("action"),
						type : $(this).attr("method"),
						data : new FormData(this),
						processData : false,
						contentType : false,
						success : function(data, status) {
							hideprocessview();
							$("#formModal .btn-close").click();
							$('.toast-header .title').html("Response");
							$('.toast-body .toast-message').html(data);
							$('.toast').toast('show');
							
							if (data.includes("Successfully") == true) {
								getcalendar();
							}							
											
							return false;
						},
						error : function(xhr, desc, err) {
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
        typeid:eventEl.getAttribute('data-typeid'),
        work_start:eventEl.getAttribute('data-work-start'),
        work_end:eventEl.getAttribute('data-work-end'),
        work_intarval:eventEl.getAttribute('data-work-intarval'),
        isWorking:eventEl.getAttribute('data-isWorking')
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
    		today:    'Today',
			month:    'Month',
			week:     'Week',
			day:      'Day'
    },
    initialView: 'dayGridMonth',
    editable: true,
    //bindu
    eventReceive: function(info) {
    	//alert(info.event.title + " is being dropped on " + info.event.start.toISOString());\
    	
    	 var shortDateFormat = 'yyyy-MM-dd';
    	 var date = new Date(info.event.start);    	
    	//JSON.stringify(info.event)
    	var totalWorkMinute=getworkminute(info.event.extendedProps.work_start,info.event.extendedProps.work_end,info.event.extendedProps.work_intarval);
    	var totalWorkHour=minutesToHour(totalWorkMinute);
    	
    	
    	
    	$('#formModal .modal-title').text(formatDate(date));
    	$('#formModal #type-name').text(info.event.title);
    	$('#formModal #date').val(formatDate(date)); 
    	$('#formModal #status').val(info.event.extendedProps.typeid);
    	$('#formModal #start').val(info.event.extendedProps.work_start); 
    	$('#formModal #end').val(info.event.extendedProps.work_end); 
    	$('#formModal #intarval').val(info.event.extendedProps.work_intarval);
    	$('#formModal #text').val("");
    	$("#alert-msg").text("");    	
    	if(info.event.extendedProps.isWorking=='false'){
			totalWorkMinute=0;			
			$("#formModal .dis").prop("disabled",true);
			}else{
				$("#formModal .dis").prop("disabled",false);
			}
    	$('#formModal #totalWorkMinute').val(totalWorkMinute);
    	$('#formModal #total').text(totalWorkHour); 
    	
    	$('#formModal').modal('show');
    	
    	info.revert();
   		
   		
    	
    	//if (!confirm("Are you sure about this change?")) {
         // info.revert();
        //}
    },    
    eventClick:  function(arg) {
    	var type= arg.event.title;
    	var start= arg.event.start;
    	var id= arg.event._def.defId;
    	alert(type);
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
	
};



/* Controller
------------------------------------------------ */
$(document).ready(function() {
	getcalendar();
});