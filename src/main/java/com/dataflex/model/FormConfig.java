package com.dataflex.model;

import java.util.List;

public class FormConfig {
    private String formTitle;
    private String version;
    private List<FormField> fields;

    // Getters and Setters
    public String getFormTitle() { return formTitle; }
    public void setFormTitle(String formTitle) { this.formTitle = formTitle; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public List<FormField> getFields() { return fields; }
    public void setFields(List<FormField> fields) { this.fields = fields; }
}
