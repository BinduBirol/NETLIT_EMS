$("#daterangeselect").change(function() {
		$c = $(this).val();

		if ($c == "t") {
			$("#from_date").val(moment().format('YYYY-MM-DD'));
			$("#to_date").val(moment().format('YYYY-MM-DD'));

		} else if ($c == "y") {
			$("#from_date").val(moment().subtract(1, 'days').format('YYYY-MM-DD'));
			$("#to_date").val(moment().subtract(1, 'days').format('YYYY-MM-DD')); 

		} else if ($c == "n2d") {
			$("#from_date").val(moment().add(1, 'days').format('YYYY-MM-DD'));
			$("#to_date").val(moment().add(3, 'days').format('YYYY-MM-DD')); 

		} else if ($c == "tm") {
			$("#from_date").val(moment().startOf('month').format('YYYY-MM-DD'));
			$("#to_date").val(moment().endOf('month').format('YYYY-MM-DD'));

		} else if ($c == "lm") {
			$("#from_date").val(moment().subtract(1, 'month').startOf('month').format('YYYY-MM-DD'));
			$("#to_date").val(moment().subtract(1, 'month').endOf('month').format('YYYY-MM-DD'));

		} else if ($c == "nm") {
			$("#from_date").val(moment().add(1, 'month').startOf('month').format('YYYY-MM-DD'));
			$("#to_date").val(moment().add(1, 'month').endOf('month').format('YYYY-MM-DD'));

		} else if ($c == "tw") {
			$("#from_date").val(moment().startOf('isoWeek').format('YYYY-MM-DD'));
			$("#to_date").val(moment().endOf('isoWeek').format('YYYY-MM-DD'));

		} else if ($c == "lw") {
			$("#from_date").val(moment().subtract(1, 'week').startOf('isoWeek').format('YYYY-MM-DD'));
			$("#to_date").val(moment().subtract(1, 'week').endOf('isoWeek').format('YYYY-MM-DD'));

		} else if ($c == "nw") {
			$("#from_date").val(moment().add(1, 'week').startOf('isoWeek').format('YYYY-MM-DD'));
			$("#to_date").val(moment().add(1, 'week').endOf('isoWeek').format('YYYY-MM-DD'));
		}
		
		if ($c != "c") {
			$("#searchform").submit();
		}		
	})

	
	var urlParams = new URLSearchParams(window.location.search); // get all
																	// parameters
	var c = urlParams.get('c'); // extract the foo parameter - this will return
								// NULL if foo isn't a parameter
	var from_date = urlParams.get('from_date');
	var to_date = urlParams.get('to_date');
	var empid = urlParams.get('empid');
	
	if(c){			
		$('#empidselect option[value="'+empid+'"]').attr("selected", "selected");
		$('#daterangeselect option[value="'+c+'"]').attr("selected", "selected");
		$("#from_date").val(from_date);
		$("#to_date").val(to_date);
		$('#daterangehtml').html(moment(from_date, 'YYYY-MM-DD').format('MMMM D, YYYY') + ' - ' + moment(to_date, 'YYYY-MM-DD').format('MMMM D, YYYY'));
	}else{
		// $('#advance-daterange span').html(moment().format('MMMM D, YYYY') + '
		// - ' + moment().format('MMMM D, YYYY'));
		// $('#daterangeinput').val(moment().subtract('days',
		// 29).format('YYYY-MM-DD') + 'A' + moment().format('YYYY-MM-DD'));
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

		function workschedule(z) {
			//$("tr").removeClass("table-primary");
			//$(z).closest("tr").addClass("table-primary");
			$empid = $(z).attr("emp-id");			

			$.ajax({
				url : "workschedule?empid=" + $empid,
				type : 'GET',
				success : function(data) {					
					$('.wschcontent').html(data);
					$('#workschedule').modal('show');
					historytablecontent(z);
				},
				error : function(xhr, desc, err) {
					$('.wschcontent').html(xhr.responseText);
					$('#workschedule').modal('show');

				}
			});		

		}
		
		function historytablecontent(z) {
			// $(z).addClass("table-active");
			$("tr").removeClass("table-primary");
			$(z).closest("tr").addClass("table-primary");
			$empid = $(z).attr("emp-id");
			// $('#viewEmployee').modal('show');

			$.ajax({
				url : "workschedulehistory?empid=" + $empid,
				type : 'GET',
				success : function(data) {
					$('#historytablecontent').html("");
					$('#historytablecontent').html(data);					
					// $('#workschedule').modal('show');
					reloadHistorytable();
				},
				error : function(xhr, desc, err) {
					$('#historytablecontent').html("");
					$('#historytablecontent').html(xhr.responseText);
					reloadHistorytable();
					// $('#workschedule').modal('show');

				}
			});
			// alert($empid);

		}
		
	
			$('#availtable').DataTable({
			    dom: "<'row mb-3'<'col-sm-4'l><'col-sm-8 text-end'<'d-flex justify-content-end'fB>>>t<'d-flex align-items-center'<'me-auto'i><'mb-0'p>>",
			    lengthMenu: [ 10, 20, 30, 40, 50 ],
			    responsive: true,
			    buttons: [ 
			      
			      { extend: 'csv', className: 'btn btn-outline-theme btn-sm' }
			    ]
			  });
		
		
		$(".avidcheckboxx").change(function (e) {			
			//$("tr").removeClass("table-primary");			
			$target= $(e.target).closest('tr');
			//$target.addClass("table-primary");
			var start = $target.find(".start").val();
			var end = $target.find(".end").val();
			var lbreak = $target.find(".lbreak").val();	
			var minutes= $target.find(".wmint").val();
			var wdesc= $target.find(".wdesc").val();
			var obmint= $target.find(".obminute").val();
			var work_hour=$target.find(".work_hour").val();
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
					+"&obmint="+obmint
					+"&work_hour="+work_hour
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
		
		$(".rejectcheckbox").change(function (e) {	
			var av_id=  $(this).attr("av-id");
			
			$target= $(e.target).closest('tr');
			var wdesc= $target.find(".wdesc").val();
			var url="rejectAvailablity?av_id="+av_id+"&wdesc="+wdesc;
			$.ajax({
				url : url,
				type : 'GET',
				success : function(data) {
					$target.find(".avidcheckbox").prop( "checked", false );
					$target.find(".avidcheckbox").attr( "disabled", true );
					$(this).attr( "disabled", true );
					console.log(data);
				},
				error : function(xhr, desc, err) {
					$(this).prop( "checked", false );
					alert(xhr.responseText);
				}
			});							
			
		})
		
		/*
		$(".cal").change(function (e) {
			//$('#'+this.id).attr( "checked", false );	
			//$("tr").removeClass("table-primary");			
			$target= $(e.target).closest('tr');
			$target.find(".avidcheckbox").attr( "checked", false );
			//$target.addClass("table-primary");
			var start = $target.find(".start").val();
			var end = $target.find(".end").val();
			var lbreak = $target.find(".lbreak").val();	
			var obmin= $target.find(".obminute").val();	
			var diff = (Math.abs(new Date('2022-05-30 '+start) - new Date('2022-05-30 '+end))/1000/60)- lbreak+parseInt(obmin);
			$target.find(".wmint").val(diff);
			$target.find(".work_hour").val(minutesToHour(parseInt(diff)));
			
		})		*/
		
			
		
		$('#checkAll').on("change", function(){
			var v;
			if ($(this).is(':checked')) {
				v=1;
			}else{
				v=0;
			}
			
			alert(v);
			$("#datatableDefault input[type='checkbox']").each(function(){
		        if(v==1){$(this).attr('checked', 'checked');}
		        else if(v==0){$(this).removeAttr('checked');}      
		        
		    });	
		});
		
		
		function setTypeval(e,z) {
			var start = $('option:selected', z).attr('start');
			var end = $('option:selected', z).attr('end');
			var interval = $('option:selected', z).attr('interval');
			var type=$(z).val();
			
			$target= $(e.target).closest('tr');
			$target.find(".start").val(start);
			$target.find(".end").val(end);
			$target.find(".lbreak").val(interval);
			$target.find(".wmint").val(getworkminute(start,end,0));
			$target.find(".work_hour").val(minutesToHour(getworkminute(start,end,0)));
			
			if (start!= null) {
				$target.find('.absent').prop('disabled', false);
				$(z).removeClass("is-invalid");
				$(z).addClass("is-valid");
				calculate(e);
			}else{
				$target.find('.absent').prop('disabled', true);	
				$(z).removeClass("is-valid");
				$(z).addClass("is-invalid");
				calculate(e);
			}
			
		}
		
		function calculate(e) {
			$target= $(e.target).closest('tr');
			var start = $target.find(".start").val();
			var end = $target.find(".end").val();
			var lbreak = $target.find(".lbreak").val();			
			$target.find(".wmint").val(getworkminute(start,end,lbreak));
			$target.find(".work_hour").val(minutesToHour(getworkminute(start,end,lbreak)));				
		}
		
		function getworkminute(start,end,lbreak) {		
			 var diff = ((Math.abs(new Date('2022-05-30 '+start) - new
					 Date('2022-05-30 '+end))/1000/60)- lbreak);			
			return diff;
			
		}
		
		function save(t,e) {
			var action = $(t).attr('saveaction');
			
			$target= $(e.target).closest('tr');
			
			var av_id = $target.find(".av_id").val();
			var desc= $target.find(".work_desc").val();
			var start = $target.find(".start").val();
			var end = $target.find(".end").val();
			var lbreak = $target.find(".lbreak").val();	
			var date = $target.find(".date").val();
			var status = $target.find(".status").val();	
			var wmint = $target.find(".wmint").val();
			var work_hour = $target.find(".work_hour").val();
			var obno = $target.find(".obno").val();
			var user_id = $("#this_user_id").val();
			
			
			if (validate(e)) {
				 $.post(action,
						  {
					 		status: status,
					 		from_date: date,
					 		work_start:start,
					 		work_end:end,
					 		lunch_hour:lbreak,
					 		work_desc:desc,
					 		work_minute:wmint,			 		
					 		work_hour:work_hour,
					 		obno:obno,
					 		userid:user_id,
					 		av_id:av_id
						  },
						  function(data, status){		    
						    
							$('.toast-header .title').html("Response");
							$('.toast-body .toast-message').html(data);
							$('.toast').removeClass("text-danger");
							$('.toast').toast('show');
							
							if (data.includes("Successfully") == true) {					
								$target= $(e.target).closest('tr').addClass("bg-warning");
								$target.find('.savebtn').attr("disabled","disabled");
								$target.find('.savebtn').html("PENDING");						
							}
							
						  });
			}
		}


		function validate(e){	
			$target= $(e.target).closest('tr');	
			var av_id = $target.find(".av_id").val();
			var desc= $target.find(".work_desc").val();
			var start = $target.find(".start").val();
			var end = $target.find(".end").val();
			var lbreak = $target.find(".lbreak").val();	
			var date = $target.find(".date").val();
			var status = $target.find(".status").val();	
			var wmint = $target.find(".wmint").val();
			
			if(wmint>480){
				$target.find(".end").focus();
				$('.toast-header .title').html("Alert!!");
				$('.toast-body .toast-message').html("Can not report for more than 8 hours!!");
				$('.toast').addClass("text-danger");
				$('.toast').toast('show');
				return false;
			}
			
			if(status==""){
				$target.find(".status").focus();
				$('.toast-header .title').html("Alert!!");
				$('.toast-body .toast-message').html("Select a Time Report type!!");
				$('.toast').addClass("text-danger");
				$('.toast').toast('show');
				return false;
			}
			
			if(status==1 && $.trim(desc).length>0 && $.trim(start).length>0 && $.trim(end).length>0){
				return true;
			} else if(status!=1  && $.trim(desc).length>0){
				return true;
			} else if($.trim(desc).length==0){
				$target.find(".work_desc").focus();
				$('.toast-header .title').html("Alert!!");
				$('.toast-body .toast-message').html("Job description can't be empty!!");
				$('.toast').addClass("text-danger");
				$('.toast').toast('show');
				return false;
			}else if($.trim(start).length==0 || $.trim(end).length==0){
				$('.toast-header .title').html("Alert!!");
				$('.toast-body .toast-message').html("Start/End Time can't be empty!!");
				$('.toast').addClass("text-danger");
				$('.toast').toast('show');
				return false;
			}
		}
		
		
		
		
		
		
		
		$('.form-check-input').change(function(e) {
			var action = $(this).attr("ajaxaction");			
			$target= $(e.target).closest('tr');	
			var av_id = $target.find(".av_id").val();
			var ob_id = $target.find(".ob_id").val();
			
			var desc= $target.find(".work_desc").val();
			var start = $target.find(".start").val();
			var end = $target.find(".end").val();
			var lbreak = $target.find(".lbreak").val();	
			var date = $target.find(".date").val();
			var status = $target.find(".status").val();	
			var wmint = $target.find(".wmint").val();
			var work_hour = $target.find(".work_hour").val();
			
			//alert(work_hour);
			
			if(validate(e)){
				$.get(action, 
						{	status: status,					 		
					 		work_start:start,
					 		work_end:end,
					 		lunch_hour:lbreak,
					 		work_desc:desc,
					 		work_minute:wmint,			 		
					 		work_hour:work_hour,					 		
					 		av_id:av_id,
					 		ob_id:ob_id}, 
			 		
			 		function (data, textStatus, jqXHR) {
				   // alert(data);
				});
			}else {
				$(this).prop('checked', false);
			}			
			
		});
		
		//setAutorowspan('#datatableDefaultz',2);
		//setAutorowspan('#datatableDefaultz',0);
