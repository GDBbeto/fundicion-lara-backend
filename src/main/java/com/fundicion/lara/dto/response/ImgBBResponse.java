package com.fundicion.lara.dto.response;

import lombok.Data;

@Data
public class ImgBBResponse {
    private Data data;
    private boolean success;
    private int status;
    // Getters y Setters
    public static class Data {
        private String id;
        private String title;
        private String url;
        private String displayUrl;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDisplayUrl() {
            return displayUrl;
        }

        public void setDisplayUrl(String displayUrl) {
            this.displayUrl = displayUrl;
        }
    }
}
