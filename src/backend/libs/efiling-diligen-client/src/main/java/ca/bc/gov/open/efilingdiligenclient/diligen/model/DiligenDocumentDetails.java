package ca.bc.gov.open.efilingdiligenclient.diligen.model;

import ca.bc.gov.open.jag.efilingdiligenclient.api.model.ProjectFieldsResponse;

import java.math.BigDecimal;
import java.util.List;

public class DiligenDocumentDetails {
    private String fileStatus;
    private Object mlJson;
    private String fileName;
    private Boolean isOcr;
    private Boolean isConverted;
    private String executionStatus;
    private Object extractedDocument;
    private BigDecimal outOfScope;
    private List<DiligenAnswerField> answers;
    private ProjectFieldsResponse projectFieldsResponse;

    public String getFileStatus() {  return fileStatus; }

    public Object getMlJson() { return mlJson;  }

    public String getFileName() { return fileName; }

    public Boolean getOcr() {  return isOcr; }

    public Boolean getConverted() { return isConverted;  }

    public String getExecutionStatus() { return executionStatus; }

    public Object getExtractedDocument() { return extractedDocument; }

    public BigDecimal getOutOfScope() { return outOfScope; }

    public List<DiligenAnswerField> getAnswers() { return answers;  }

    public ProjectFieldsResponse getProjectFieldsResponse() { return projectFieldsResponse; }

    public DiligenDocumentDetails(DiligenDocumentDetails.Builder builder) {
        this.fileStatus = builder.fileStatus;
        this.mlJson = builder.mlJson;
        this.fileName = builder.fileName;
        this.isOcr = builder.isOcr;
        this.isConverted = builder.isConverted;
        this.executionStatus = builder.executionStatus;
        this.extractedDocument = builder.extractedDocument;
        this.outOfScope = builder.outOfScope;
        this.answers = builder.answers;
        this.projectFieldsResponse = builder.projectFieldsResponse;
    }

    public static DiligenDocumentDetails.Builder builder() {
        return new DiligenDocumentDetails.Builder();
    }

    public static class Builder {

        private String fileStatus;
        private Object mlJson;
        private String fileName;
        private Boolean isOcr;
        private Boolean isConverted;
        private String executionStatus;
        private Object extractedDocument;
        private BigDecimal outOfScope;
        private List<DiligenAnswerField> answers;
        private ProjectFieldsResponse projectFieldsResponse;


        public DiligenDocumentDetails.Builder fileStatus(String fileStatus) {
            this.fileStatus = fileStatus;
            return this;
        }

        public DiligenDocumentDetails.Builder mlJson(Object mlJson) {
            this.mlJson = mlJson;
            return this;
        }

        public DiligenDocumentDetails.Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public DiligenDocumentDetails.Builder isOcr(Boolean isOcr) {
            this.isOcr = isOcr;
            return this;
        }

        public DiligenDocumentDetails.Builder isConverted(Boolean isConverted) {
            this.isConverted = isConverted;
            return this;
        }

        public DiligenDocumentDetails.Builder executionStatus(String executionStatus) {
            this.executionStatus = executionStatus;
            return this;
        }

        public DiligenDocumentDetails.Builder extractedDocument(Object extractedDocument) {
            this.extractedDocument = extractedDocument;
            return this;
        }

        public DiligenDocumentDetails.Builder outOfScope(BigDecimal outOfScope) {
            this.outOfScope = outOfScope;
            return this;
        }

        public DiligenDocumentDetails.Builder answers(List<DiligenAnswerField> answers) {
            this.answers = answers;
            return this;
        }

        public DiligenDocumentDetails.Builder projectFieldsResponse(ProjectFieldsResponse projectFieldsResponse) {
            this.projectFieldsResponse = projectFieldsResponse;
            return this;
        }

        public DiligenDocumentDetails create() {
            return new DiligenDocumentDetails(this);
        }
    }
}
