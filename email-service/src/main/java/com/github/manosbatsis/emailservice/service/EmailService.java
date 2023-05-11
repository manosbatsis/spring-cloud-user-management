package com.github.manosbatsis.emailservice.service;

import com.github.manosbatsis.emailservice.model.EmailRecepient;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Component
public class EmailService {
  private static final String TEMPLATE_NAME = "html/registration";
  private static final String SPRING_LOGO_IMAGE = "templates/html/images/spring.png";
  private static final String PNG_MIME = "image/png";
  private static final String MAIL_SUBJECT = "Registration Confirmation";

  private final Environment environment;

  private final JavaMailSender mailSender;

  private final TemplateEngine htmlTemplateEngine;

  @Value("${properties.mail.smtp.from:'EMPTY_MAIL_SENDER'}")
  private String mailFrom;

  public EmailService(
      JavaMailSender mailSender, Environment environment, TemplateEngine htmlTemplateEngine) {
    this.mailSender = mailSender;
    this.environment = environment;
    this.htmlTemplateEngine = htmlTemplateEngine;
  }

  public void sendRegistrationConfirmation(EmailRecepient emailRecepient) {

    log.debug("sendRegistrationConfirmation, emailRecepient: {}", emailRecepient);

    String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");

    final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
    final MimeMessageHelper email;
    try {
      email = new MimeMessageHelper(mimeMessage, true, "UTF-8");

      email.setTo(emailRecepient.getEmail());
      email.setSubject(MAIL_SUBJECT);
      email.setFrom(new InternetAddress(mailFrom));

      final Context ctx = new Context(LocaleContextHolder.getLocale());
      ctx.setVariable("email", emailRecepient.getEmail());
      ctx.setVariable("name", emailRecepient.getFullName());
      ctx.setVariable("springLogo", SPRING_LOGO_IMAGE);

      final String htmlContent = this.htmlTemplateEngine.process(TEMPLATE_NAME, ctx);

      email.setText(htmlContent, true);

      ClassPathResource clr = new ClassPathResource(SPRING_LOGO_IMAGE);

      email.addInline("springLogo", clr, PNG_MIME);

      mailSender.send(mimeMessage);
    } catch (Throwable e) {
      log.error("Failed sending email", e);
    }
  }
}
