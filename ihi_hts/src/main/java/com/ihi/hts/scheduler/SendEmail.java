package com.ihi.hts.scheduler;

import java.io.IOException;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class SendEmail {
	@Autowired
	private JavaMailSender javaMailSender;

	void sendEmailHBar(long amount) throws IOException, MessagingException {
		// SimpleMailMessage msg = new SimpleMailMessage();
		// msg.setFrom("aamir@colanonline.com");
		// msg.setTo("vignesh@hederacoe.com","venkat@hederacoe.com","sadeed@hederacoe.com","vijay@hederacoe.com");

		MimeMessage msg = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(msg, true);
		helper.setFrom("aamir@hederacoe.com");
		// helper.setTo("aamirmalickoffice2021@gmail.com");
		String[] mailId = { "aamir@hederacoe.com", "venkat@hederacoe.com", "sadeed@hederacoe.com",
				"vijay@hederacoe.com", "vignesh@hederacoe.com" };
		helper.setTo(mailId);
		helper.setSubject("Ihi Hbar Balance InstaAlerts");
		String CurrentDateTime = new Date().toLocaleString();
		String message = "<h4><b>Hi,</b></h4>";
		message += "<h3> Available Hbar balance in your wallet <b> xx0726 </b>  has gone below your specified limit of <b> 5000 </b>  <b> ♄ </b>  hbar balance.";
		message += " Now currently available <b style='color:red;'>" + amount + "</b> <b> ♄ </b>  hbar balance "
				+ CurrentDateTime + ".";
		message += " Ihi service have low ♄ Hbar balance. Kindly recharge the HBar balance. </br></h3>";
		message += "<h3><b> Warm Regards </b> </br></h3>";
		message += "</h3><b style='color:blue;'>Ihi Admin</b></h3>";
		helper.setText(message, true);
		javaMailSender.send(msg);

	}

}
