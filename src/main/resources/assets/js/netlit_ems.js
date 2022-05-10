//$(".toast").toast({ autohide: false });
//setting up active menu
//alert(window.location.pathname);
$( "[href='"+window.location.pathname+"']" ).parent().addClass('active');
$( "[href='"+window.location.pathname+"']" ).parent().parent().parent().addClass('active');

function googleTranslateElementInit() {
    new google.translate.TranslateElement({
      pageLanguage: 'en',
      //includedLanguages: 'sv,en',
      layout: google.translate.TranslateElement.InlineLayout.SIMPLE
    }, 'google_translate_element');
  }

function showprocessview(){
	$(".processview").prop("disabled", true);
    // add spinner to button
    $(".processview").html(
      "<span class='spinner-border spinner-border-sm' role='status' aria-hidden='true'></span> Loading..."
    );
}

function hideprocessview(){
	$(".processview").prop("disabled", false);
    $(".processview").html(
      "Try Submit"
    );
}

$("#toast-reload-btn").click(function() {
	location.reload();
})