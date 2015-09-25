package geolab.dags.model;

/**
 * Created by geolabedu on 9/25/15.
 */
public class UserLikes {
    private String id;
    private String userId;
    private String markerId;

    public UserLikes(String id, String userId, String markerId) {
        this.id = id;
        this.userId = userId;
        this.markerId = markerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }
}
