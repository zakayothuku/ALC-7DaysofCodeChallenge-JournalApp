package alc.sevendayschallenge.journal.models;

import android.support.annotation.Nullable;

import com.google.firebase.database.PropertyName;

public class User {

    @PropertyName("google_id")
    public String googleId;

    @PropertyName("user_id")
    public String userId;

    @PropertyName("email")
    public String email;

    @PropertyName("display_name")
    public String displayName;

    @Nullable
    @PropertyName("photo_url")
    public String photoUrl;

    @PropertyName("created_at")
    public long createdAt;

    @PropertyName("updated_at")
    public long updatedAt;

    public User() {
    }

    public User(String googleId, String userId, String email, String displayName, String photoUrl, long createdAt, long updatedAt) {
        this.googleId = googleId;
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(@Nullable String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
