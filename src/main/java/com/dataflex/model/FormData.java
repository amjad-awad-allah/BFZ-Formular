package com.dataflex.model;

import java.util.Map;

public class FormData {
    private String formTitle;
    private String version;
    private String submittedAt;
    private Map<String, Object> values;

    // Getters and Setters
    public String getFormTitle() { return formTitle; }
    public void setFormTitle(String formTitle) { this.formTitle = formTitle; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
    public Map<String, Object> getValues() { return values; }
    public void setValues(Map<String, Object> values) { this.values = values; }
}
