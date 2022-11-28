pageStart();
var signerform= true;
var signerarray=[];

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
	var type=$("#contract_type").find('option:selected').attr("stradd");
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



$("#invt_validity").change(function () {
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
	        	$target.find(".sempid").val(empid);	
	        	if(empid==""){
	        		$target.find(".sprofile").prop("disabled",true);
	        	}
	        	
	        	$hiddenInput.val( empid );
	            break;
	        }else{
	        	$target= $input.closest('tr');
	        	$target.find(".sempid").val(0);	
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

function validateContractForm(){
	signerform=true;	
	var signerData = validateSigners();
	var docval=validateDocument();
	
	if(docval && signerform){
		$("#validationmsg").hide();		
		$('#contract_modal .modal-title').html($("#document_name").val());
		$('#contract_modal #signerData').val(signerData);
		$('#contract_modal #formData').val(1);		
		$('#contract_modal').modal('show');
	}else{
		$("html, body").animate({
			scrollTop : 0
		}, "slow");
		$("#validationmsg").show();
		return;
	}
	
}

function validateDocument(){
	var bool=false;
	
	var doc_form= validateform("#doc_form");
	var invite_form=validateform("#invite_form");
	if(doc_form==true&&invite_form==true)bool=true;
	return bool;
}

function validateform(formid){
	
	var bool= true;
	$.each($(formid).find("input,select,textarea"), function() {
		if($(this).val()!=""){
			$(this).removeClass("is-invalid").addClass("is-valid");
		}else{
			$('.toast-header .title').html("Abort!");
			$('.toast-body .toast-message').html("Invalid contract information!");		
			$('.toast').toast('show');
			$(this).removeClass("is-valid").addClass("is-invalid");
			bool= false;
		}
		
	});	
	return bool;
}

function validateDocumentInputs(input){
	var val=$(input).val();
	if($.trim(val).length>0){
		$(input).removeClass("is-invalid").addClass("is-valid");
		return val;
	}else{
		$(input).removeClass("is-valid").addClass("is-invalid");
		return "";
		
	}
	
}

function validateSigners(){
	var array=[];
	var values1 = {};
	var values2 = {};
	var values3 = {};
	
	values1=validateSignersInput("#signerTR1");
	if(!jQuery.isEmptyObject(values1))array.push(values1);
	
	if($("#signerTR2").is(":visible")){
		values2=validateSignersInput("#signerTR2");
		if(!jQuery.isEmptyObject(values2))array.push(values2);
	}
	
	if($("#signerTR3").is(":visible")){
		values3=validateSignersInput("#signerTR3");
		if(!jQuery.isEmptyObject(values3))array.push(values3);
	}
	signerarray= array;
	var data= JSON.stringify(array);
	return data;
}

function validateSignersInput(trid){
	var values = {};	
	$.each($(trid).find("input").serializeArray(), function(i, field) {
	    values[field.name] = field.value;
	});
	
	$.each($(trid).find("td"), function() {
		var input =$(this).find("input");
		if(input.val()!=""){
			input.removeClass("is-invalid").addClass("is-valid");
		}else{
			$('.toast-header .title').html("Abort!");
			$('.toast-body .toast-message').html("Invalid signer information!");		
			$('.toast').toast('show');
			input.removeClass("is-valid").addClass("is-invalid");
			signerform=false;
		}
		
	});
	if(values.signer_name==""||values.signer_email==""||values.signer_phone==""||values.signer_company=="")values={};
	return values;
}

function saveAndSendContract(){	
	var form = $('.cForm')[0];
	var formData = new FormData(form);	
	
	let formData2 = new FormData($('.cForm')[1]);	
	for (var pair of formData2.entries()) {
	    formData.append(pair[0], pair[1]);
	}	
	formData.append('signers_str', $("#signerData").val());
	
	for (var pair of formData.entries()) {
	    console.log(pair[0]+ ' - ' + pair[1]); 
	}
	
    $.ajax({
		url : "/saveAndSendContract",
		type : "POST",
		data : formData,
		processData : false,
		contentType : false,
		success : function(data, status) {
			hideprocessview();
			$("#contract_modal .btn-close").click();
			  $('.toast-header .title').html("Response");
			  $('.toast-body .toast-message').html(data);		
			  $('.toast').toast('show');			  
			  if (data.indexOf("succesfully") >= 0){window.location.href = "/contractMonitoring?msg="+data;}
			  
			  return false;
		},
		error : function(xhr, desc, err) {
			hideprocessview();
			$("#contract_modal .btn-close").click();
			  $('.toast-header .title').html("Response");
			  $('.toast-body .toast-message').html(err);		
			  $('.toast').toast('show');
			return false;

		}
	});
    
}