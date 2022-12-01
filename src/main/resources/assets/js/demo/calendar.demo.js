
var handleRenderFullcalendar = function(events) {
	// external events
	var containerEl = document.getElementById('external-events');
  var Draggable = FullCalendarInteraction.Draggable;
	new Draggable(containerEl, {
    itemSelector: '.fc-event-link',
    eventData: function(eventEl) {
      return {
        title: eventEl.innerText,
        color: eventEl.getAttribute('data-color')
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
    	//alert(info.event.title + " is being dropped on " + info.event.start.toISOString());
    	
   
    	//var date = info.event.start;
    	info.event.start.setHours(8,00,00,0);
    	
    	//alert(date);
    	//info.event.setStart(date)
    	alert(info.event.start);
    	
    	if (!confirm("Are you sure about this change?")) {
          info.revert();
        }
    },    
    eventClick:  function(arg) {
    	var type= arg.event.title;
    	var start= arg.event.start;
    	var id= arg.event._def.defId;
    	alert(type);
    },
    //ends bindu
    
    droppable: true,
  	themeSystem: 'bootstrap',
  	eventLimit: true, // for all non-TimeGrid views
		views: {
			timeGrid: {
				eventLimit: 6 // adjust to 6 only for timeGridWeek/timeGridDay
			}
		},
  	events: events,
		eventDrop: function(info) {
		    alert(info.event.title + " was dropped on " + info.event.start.toISOString());

		    if (!confirm("Are you sure about this change?")) {
		      info.revert();
		    }
		  }
	});
	
	calendar.render();
};


/* Controller
------------------------------------------------ */
$(document).ready(function() {
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
				 //value["color"] = app.color.red;
			}
			
			 if(data[i].isapproved)
				 value["color"] = app.color.green;
			 
			 if(data[i].isrejected)
				 value["color"] = app.color.red;
			
			
			
			
			 events.push(value);
		}
		
		handleRenderFullcalendar(events);
	 });
});