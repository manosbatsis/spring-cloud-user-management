package com.github.manosbatsis.services.email.service

import com.github.manosbatsis.lib.core.log.loggerFor
import com.github.manosbatsis.services.email.model.EmailRecepient
import jakarta.mail.internet.InternetAddress
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Component
class EmailService(
    private val mailSender: JavaMailSender,
    private val environment: Environment,
    private val htmlTemplateEngine: TemplateEngine,
) {
    companion object {
        private const val TEMPLATE_NAME = "html/registration"
        private const val SPRING_LOGO_IMAGE = "templates/html/images/spring.png"
        private const val PNG_MIME = "image/png"
        private const val MAIL_SUBJECT = "Registration Confirmation"

        private val log = loggerFor<EmailService>()
    }

    @Value("\${properties.mail.smtp.from:'EMPTY_MAIL_SENDER'}")
    private lateinit var mailFrom: String
    fun sendRegistrationConfirmation(emailRecepient: EmailRecepient) {
        log.debug("sendRegistrationConfirmation, emailRecepient: {}", emailRecepient)
        val mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from")
        val mimeMessage = mailSender.createMimeMessage()
        val email: MimeMessageHelper
        try {
            email = MimeMessageHelper(mimeMessage, true, "UTF-8")
            email.setTo(emailRecepient.email)
            email.setSubject(MAIL_SUBJECT)
            email.setFrom(InternetAddress(mailFrom))
            val ctx = Context(LocaleContextHolder.getLocale())
            ctx.setVariable("email", emailRecepient.email)
            ctx.setVariable("name", emailRecepient.fullName)
            ctx.setVariable("springLogo", SPRING_LOGO_IMAGE)
            val htmlContent = htmlTemplateEngine.process(TEMPLATE_NAME, ctx)
            email.setText(htmlContent, true)
            val clr = ClassPathResource(SPRING_LOGO_IMAGE)
            email.addInline("springLogo", clr, PNG_MIME)
            mailSender.send(mimeMessage)
        } catch (e: Throwable) {
            log.error("Failed sending email", e)
        }
    }
}
