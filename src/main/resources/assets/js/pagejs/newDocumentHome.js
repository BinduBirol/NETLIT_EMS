pageStart();


function setfileName(){
	
	var filetype=$('#document_file').val().split('.').pop().toLowerCase();
	if(filetype!="pdf"){
		$('.toast-header .title').html("Abort!");
		$('.toast-body .toast-message').html("Please upload a .pdf file!");		
		$('.toast').toast('show');
		$('#document_name').val("");
		$('#document_file').removeClass("is-valid").addClass("is-invalid");
		return;
	}	
	var type=$("#contract_type").val();
	var filename = $('#document_file').val().split('\\').pop().replace(".pdf", "");
	if(type!="")type+="_";	
	$('#document_name').val(type+filename);
	$('#document_file').removeClass("is-invalid").addClass("is-valid");
}

function addSigner(){	
	if($("#signerTR2").is(":visible")){
		$("#signerTR3").removeClass("d-none").show(500);
		$(".btn-add-signer").prop("disabled",true);
	}else{
		$(".btn-add-signer").prop("disabled",false);
		$("#signerTR2").removeClass("d-none").show(500);
	}
}

function deleteSigner(e){
	$target= $(e.target).closest('tr');
	$target.find("input, select").val("");
	
	$target.addClass("d-none").hide(500);
	$(".btn-add-signer").prop("disabled",false);
	$target.find(".sprofile").prop("disabled",true);
	
}



$("input[name=invt_validity]").click(function () {
	var days= $(this).val();
	$("#expireDate").val(moment().add(days, 'day').format('YYYY-MM-DD'));
})

function pageStart(){
	
	$('input[list]').on('keyup', function(e) {
		var $input = $(e.target),			
	        $options = $('#' + $input.attr('list') + ' option'),
	        $hiddenInput = $('#' + $input.attr('id') + '-hidden'),
	        label = $input.val();	
	    for(var i = 0; i < $options.length; i++) {
	        var $option = $options.eq(i);
	        if($option.text() === label) {	 
	        	var email = $option.attr('email');
	        	var empid = $option.attr('data-value'); 
	        	var phone = $option.attr('phone');
	        	var company = $option.attr('company');
	        	$target= $input.closest('tr');
	        	$target.find(".semail").val(email);	
	        	$target.find(".sphone").val(phone);
	        	$target.find(".scompany").val(company);
	        	$target.find(".sprofile").attr("emp-id",empid);
	        	$target.find(".sprofile").prop("disabled",false);
	        	if(empid==""){
	        		$target.find(".sprofile").prop("disabled",true);
	        	}
	        	
	        	$hiddenInput.val( empid );
	            break;
	        }else{
	        	$target= $input.closest('tr');
	        	$target.find(".semail").val("");
	        	$target.find(".sphone").val("");
	        	$target.find(".scompany").val("");
	        	$target.find(".sprofile").attr("emp-id","");
	        	$target.find(".sprofile").prop("disabled",true);
	        }        
	        
	    }
	});
	
	
	$("#expireDate").val(moment().add(14, 'day').format('YYYY-MM-DD'));
}

function setmailmsg(z){
	var msg = $('option:selected', z).attr('msg');
	$("#mailbody").val(msg);
}
