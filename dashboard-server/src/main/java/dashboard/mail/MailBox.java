package dashboard.mail;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import dashboard.domain.Action;
import dashboard.domain.ContactPerson;
import dashboard.domain.InviteRequest;
import dashboard.manage.EntityType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class MailBox {

    private JavaMailSender mailSender;
    private String emailFrom;
    private List<String> administrativeEmails;
    private String mailBaseUrl;

    private final MustacheFactory mustacheFactory = new DefaultMustacheFactory();

    public MailBox(JavaMailSender mailSender, String emailFrom, String administrativeEmails, String mailBaseUrl) {
        this.administrativeEmails = Arrays.asList(administrativeEmails.split(","));
        this.emailFrom = emailFrom;
        this.mailSender = mailSender;
        this.mailBaseUrl = mailBaseUrl;
    }

    public void sendInviteMail(InviteRequest inviteRequest, Action action) {
        String jiraKey = action.getJiraKey().orElseThrow(() -> new IllegalArgumentException("No jirKey in the ticket"));
        List<String> to = inviteRequest.getContactPersons().stream().filter(ContactPerson::isSabContact).map(ContactPerson::getEmailAddress).collect(toList());
        List<String> cc = inviteRequest.getContactPersons().stream().filter(cp -> !cp.isSabContact()).map(ContactPerson::getEmailAddress).collect(toList());

        Map<String, Object> variables = new HashMap<>();
        variables.put("title", "Uitnodiging voor een nieuwe SURFconext koppeling");
        variables.put("inviteRequest", inviteRequest);
        variables.put("action", action);
        variables.put("mailBaseUrl", mailBaseUrl);
        variables.put("salutation", inviteRequest.getContactPersons().stream()
                .filter(ContactPerson::isSabContact).map(cp -> cp.getName()).collect(Collectors.joining(", ")));
        variables.put("metaDataType", StringUtils.isEmpty(action.getTypeMetaData()) ? EntityType.saml20_sp.name() : action.getTypeMetaData());
        String html = this.mailTemplate("invite_request_nl.html", variables);
        String subject = String.format("Uitnodiging voor een nieuwe SURFconext koppeling met %s (ticket %s)", inviteRequest.getSpName(), jiraKey);
        try {
            sendMail(html, subject, to, cc, true, emailFrom);
        } catch (Exception e) {
            //anti-pattern but we don't want to crash because of mail problems
        }
    }

    public void sendAdministrativeMail(String body, String subject) throws MessagingException, IOException {
        sendMail(body, subject, administrativeEmails, Collections.emptyList(), false, emailFrom);
    }

    private void sendMail(String html, String subject, List<String> to, List<String> cc, boolean inHtml, String emailFrom) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false);
        helper.setSubject(subject);
        helper.setTo(to.toArray(new String[]{}));
        helper.setCc(cc.toArray(new String[]{}));
        setText(html, helper, inHtml);
        helper.setFrom(emailFrom);
        doSendMail(message);
    }

    protected void setText(String html, MimeMessageHelper helper, boolean isHtml) throws MessagingException, IOException {
        helper.setText(html, isHtml);
    }

    protected void doSendMail(MimeMessage message) {
        new Thread(() -> mailSender.send(message)).start();
    }

    private String mailTemplate(String templateName, Map<String, Object> context) {
        return mustacheFactory.compile("mail_templates/" + templateName).execute(new StringWriter(), context).toString();
    }

    public void sendInviteMailReminder(Action action, String comments) {
        String jiraKey = action.getJiraKey().orElseThrow(() -> new IllegalArgumentException("No jirKey in the ticket"));
        Map<String, Object> variables = new HashMap<>();
        variables.put("title", "Herinnering voor de uitnodiging voor een nieuwe SURFconext koppeling");
        variables.put("action", action);
        if (StringUtils.hasText(comments)) {
            variables.put("comments", HtmlUtils.htmlEscape(comments).replaceAll("\n", "<br/>"));
        }
        variables.put("hasComments", StringUtils.hasText(comments));
        variables.put("mailBaseUrl", mailBaseUrl);
        variables.put("metaDataType", StringUtils.isEmpty(action.getTypeMetaData()) ? EntityType.saml20_sp.name() : action.getTypeMetaData());
        List<String> emails = Stream.of(action.getEmailTo().split(",")).map(String::trim).collect(toList());
        String html = this.mailTemplate("resend_invite_request_nl.html", variables);
        String subject = String.format("Uitnodiging voor een nieuwe SURFconext koppeling met %s (ticket %s)", action.getSpName(), jiraKey);

        try {
            sendMail(html, subject, emails, Collections.emptyList(), true, emailFrom);
        } catch (Exception e) {
            //anti-pattern but we don't want to crash because of mail problems
        }
    }

    public void sendDashboardConnectWithoutInteractionEmail(List<String> emails, String idpName, String spName, String spEntityID, String type, String comments, String emailContactPerson) {
        String emailSubject = "Nieuwe SURFconext koppeling";
        Map<String, Object> variables = new HashMap<>();
        variables.put("title", "Nieuwe SURFconext koppeling");
        variables.put("idpName", idpName);
        variables.put("spName", spName);
        variables.put("spEntityID", spEntityID);
        variables.put("comments", comments);
        variables.put("hasComments", StringUtils.hasText(comments));
        variables.put("emailContactPerson", emailContactPerson);
        variables.put("hasEmailContactPerson", StringUtils.hasText(emailContactPerson));
        String html = mailTemplate("new_connection_without_interaction_" + type + "_nl.html", variables);
        try {
            sendMail(html, emailSubject, emails, Collections.emptyList(), true, emailFrom);
        } catch (Exception e) {
            //anti-pattern but we don't want to crash because of mail problems
        }
    }
}
