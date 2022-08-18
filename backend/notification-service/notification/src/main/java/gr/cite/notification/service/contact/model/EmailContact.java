package gr.cite.notification.service.contact.model;

import java.util.List;

public class EmailContact implements Contact{
    private List<String> emails;
    private List<String> ccEmails;
    private List<String> bccEmails;

    public EmailContact() {
    }

    public EmailContact(List<String> emails, List<String> ccEmails, List<String> bccEmails) {
        this.emails = emails;
        this.ccEmails = ccEmails;
        this.bccEmails = bccEmails;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getCcEmails() {
        return ccEmails;
    }

    public void setCcEmails(List<String> ccEmails) {
        this.ccEmails = ccEmails;
    }

    public List<String> getBccEmails() {
        return bccEmails;
    }

    public void setBccEmails(List<String> bccEmails) {
        this.bccEmails = bccEmails;
    }
}
