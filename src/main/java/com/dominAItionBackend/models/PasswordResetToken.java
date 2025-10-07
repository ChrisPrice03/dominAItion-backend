package com.dominAItionBackend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "passwordResetTokens")
public class PasswordResetToken {

    @Id
    private String id;
    private String userId;
    private String token;
    private Date expiryDate;

    public PasswordResetToken() {}

    public PasswordResetToken(String userId, String token, Date expiryDate) {
        this.userId = userId;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }

    public String getUserId() { return userId; }
    public String getToken() { return token; }
    public Date getExpiryDate() { return expiryDate; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setToken(String token) { this.token = token; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
}
