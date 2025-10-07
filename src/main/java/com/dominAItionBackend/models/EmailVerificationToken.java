package com.dominAItionBackend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "email_tokens")
public class EmailVerificationToken {

    @Id
    private String id;
    private String userId;
    private String token;
    private Date expiryDate;

    public EmailVerificationToken(String userId, String token, Date expiryDate) {
        this.userId = userId;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public String getUserId() { return userId; }
    public String getToken() { return token; }
    public Date getExpiryDate() { return expiryDate; }
}
