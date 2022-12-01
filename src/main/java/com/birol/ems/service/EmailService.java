package com.birol.ems.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.birol.ems.contract.signer.Signer_DTO;
import com.birol.ems.dto.Mail;
import com.birol.persistence.model.User;
import com.birol.registration.OnRegistrationCompleteEvent;
import com.birol.service.IUserService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;
    @Autowired
	private Environment env;
    @Autowired
    private MessageSource messagesSource;
    @Autowired
	IUserService userService;

    public void sendRegMailMessage(final OnRegistrationCompleteEvent event, final User user, final String token) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        
        final String recipientAddress = user.getEmail();
        final String subject = "Registration Confirmation";
        //final String confirmationUrl = event.getAppUrl() + "/registrationConfirm?token=" + token;
        final String confirmationUrl = "https://ems.netlit.se" + "/registrationConfirm?token=" + token;
        String text= "Dear "+user.getFirstName()+" "+user.getLastName()+",<br/>";
        text+= messagesSource.getMessage("message.regSuccLink", null, "To confirm your registration, please click on the below link.", event.getLocale());
        text+="<br/>"+confirmationUrl;
        text+= "<br/>Your SECRET for Google Authenticator: "+user.getSecret();
        //text+= "\nYour QR code for Google Authenticator is Attached.";
        text+= "<br/>Scan this QR code using Google Authenticator app on your Android or iPhone device";
        
        //helper.addAttachment("favicon.ico", new ClassPathResource("favicon.ico"));        
        //String qrlink="https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth%3A%2F%2Ftotp%2FNETLIT_EMS_Application%3Abirolkarmaker%40gmail.com%3Fsecret%3D3HPBJIBV6UPCIIYZ%26issuer%3DNETLIT_EMS_Application";
        String qrimage=userService.generateQRUrl(user);
        String inlineImage = "<div style=\"text-align: left\"><img  src=\""+qrimage+"\"></img></div>";
        String qrcode_link= "You can also find the QR code to this link: <a href='"+qrimage+"'>QR Image</a>";

        helper.setText(text+inlineImage+qrcode_link, true);
        helper.setSubject(subject);
        helper.setTo(recipientAddress);
        helper.setFrom(env.getProperty("support.email"));

        emailSender.send(message);
    }
    
    public void sendSimpleMessage(Mail mail) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        helper.addAttachment("logo.png", new ClassPathResource("memorynotfound-logo.png"));
        String inlineImage = "<img src=\"cid:logo.png\"></img><br/>";

        helper.setText(inlineImage + mail.getContent(), true);
        helper.setSubject(mail.getSubject());
        helper.setTo(mail.getTo());
        helper.setFrom(env.getProperty("support.email"));

        emailSender.send(message);
    }
    
	public void sendTextMail(Mail mail) {
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(mail.getTo());
		email.setSubject(mail.getSubject());
		email.setText(mail.getContent());
		System.out.println(env.getProperty("support.email"));
		email.setFrom(env.getProperty("support.email"));
		emailSender.send(email);
	}
	
	public boolean sendContractSigningMail( Signer_DTO signer, String mailbody) throws MessagingException, UnsupportedEncodingException {
		boolean issent= false;	
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        
        final String recipientAddress = signer.getSigner_email();
        final String subject = "Invitation to sign a document";        
        final String signurl= "https://ems.netlit.se" + "/signDocument.html";
        
        String text= "Hello "+signer.getSigner_name()+",<br/>you are invited to sign a document on Netlit EMS.<br/>";
        text+="Copy the token bellow. You will need it to sign the document.";
        text+="<h2 style='font-style: italic;'>"+signer.getSigning_password()+"</h2>";        
        text+="<button><a style='text-decoration: none;padding: 10px 20px;text-align: center;display: inline-block;' href='"+signurl+"'>Accept invitation and sign the document</a></button><br/></br>";       
        text+="Message from invitantion sender<hr/>";        
        text+=stringToHtml(mailbody);
        
        helper.setText(text, true);
        helper.setSubject(subject);
        helper.setTo(recipientAddress);
        helper.setFrom(env.getProperty("support.email"));

        try {
        	emailSender.send(message);
        	issent= true;	
		} catch (Exception e) {
			issent= false;	
			e.printStackTrace();
		}
        
        return issent;
    }
	
	public static String stringToHtml(String s) {
	    StringBuilder builder = new StringBuilder();
	    boolean previousWasASpace = false;
	    for( char c : s.toCharArray() ) {
	        if( c == ' ' ) {
	            if( previousWasASpace ) {
	                builder.append("&nbsp;");
	                previousWasASpace = false;
	                continue;
	            }
	            previousWasASpace = true;
	        } else {
	            previousWasASpace = false;
	        }
	        switch(c) {
	            case '<': builder.append("&lt;"); break;
	            case '>': builder.append("&gt;"); break;
	            case '&': builder.append("&amp;"); break;
	            case '"': builder.append("&quot;"); break;
	            case '\n': builder.append("<br>"); break;
	            // We need Tab support here, because we print StackTraces as HTML
	            case '\t': builder.append("&nbsp; &nbsp; &nbsp;"); break;  
	            default:
	                if( c < 128 ) {
	                    builder.append(c);
	                } else {
	                    builder.append("&#").append((int)c).append(";");
	                }    
	        }
	    }
	    return builder.toString();
	}

}