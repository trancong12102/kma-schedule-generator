package org.lonewolf2110.kma.schedule.enties;

public class ResponseEntity {
    private String webViewLink;

    public ResponseEntity(String webViewLink) {
        this.webViewLink = webViewLink;
    }

    public String getWebViewLink() {
        return webViewLink;
    }

    public void setWebViewLink(String webViewLink) {
        this.webViewLink = webViewLink;
    }
}
