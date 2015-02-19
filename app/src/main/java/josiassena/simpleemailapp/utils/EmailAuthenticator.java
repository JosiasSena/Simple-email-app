package josiassena.simpleemailapp.utils;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class EmailAuthenticator extends Authenticator {
    private final String emailAddress;
    private final String password;

    public EmailAuthenticator(String username, String password) {
        this.emailAddress = username;
        this.password = password;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(emailAddress, password);
    }
}
