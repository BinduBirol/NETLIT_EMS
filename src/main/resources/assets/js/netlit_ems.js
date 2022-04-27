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