$("#daterangeselect").change(function() {
		$c = $(this).val();

		if ($c == "t") {
			$("#from_date").val(moment().format('YYYY-MM-DD'));
			$("#to_date").val(moment().format('YYYY-MM-DD'));

		} else if ($c == "y") {
			$("#from_date").val(moment().subtract(1, 'days').format('YYYY-MM-DD'));
			$("#to_date").val(moment().subtract(1, 'days').format('YYYY-MM-DD')); 

		} else if ($c == "n2d") {
			$("#from_date").val(moment().format('YYYY-MM-DD'));
			$("#to_date").val(moment().add(2, 'days').format('YYYY-MM-DD')); 

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
		
		setAutorowspan('#datatableDefaultz',0);