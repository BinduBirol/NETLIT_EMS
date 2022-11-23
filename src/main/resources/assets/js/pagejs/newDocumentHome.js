pageStart();

function setfileName(){
	var filename = $('#document_file').val().split('\\').pop();
	$('#document_name').val(filename);
	
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
}

function setSignerProfile(e, z){
	var email = $('option:selected', z).attr('email');
	var empid = $(z).val();
	$target= $(e.target).closest('tr');
	$target.find(".semail").val(email);	
	$target.find(".sprofile").attr("emp-id",empid);
	$target.find(".sprofile").prop("disabled",false);
	if(empid==""){
		$target.find(".sprofile").prop("disabled",true);
	}
}

$("input[name=invt_validity]").click(function () {
	var days= $(this).val();
	$("#expireDate").val(moment().add(days, 'day').format('YYYY-MM-DD'));
})

function pageStart(){
	$("#expireDate").val(moment().add(14, 'day').format('YYYY-MM-DD'));
}
