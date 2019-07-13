package org.lonewolf2110.models;

public class ResponseEntity {
    private String webViewLink;

    public String getWebViewLink() {
        return webViewLink;
    }

    public void setWebViewLink(String webViewLink) {
        this.webViewLink = webViewLink;
    }

    public ResponseEntity(String webViewLink) {
        this.webViewLink = webViewLink;
    }
}
