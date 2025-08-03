package com.susu.processclient;

public class ProcessSnapshot {

    private String chainId;
    private String hostMethod;
    private String stepIndex;
    private String stepName;
    private String stepParam;
    private String stepResult;
    private String stepException;
    private String stepExceptionMessage;
    private String stepExceptionStackTrace;
    private String isSuccess;
    private String stepStartTime;
    private String stepEndTime;

    public static Builder builder() {
        return new Builder();
    }

    public String getHostMethod() {
        return hostMethod;
    }

    public String getChainId() {
        return chainId;
    }

    public String getStepIndex() {
        return stepIndex;
    }

    public String getStepName() {
        return stepName;
    }

    public String getStepParam() {
        return stepParam;
    }

    public String getStepResult() {
        return stepResult;
    }

    public String getStepException() {
        return stepException;
    }

    public String getStepExceptionMessage() {
        return stepExceptionMessage;
    }

    public String getStepExceptionStackTrace() {
        return stepExceptionStackTrace;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public String getStepStartTime() {
        return stepStartTime;
    }

    public String getStepEndTime() {
        return stepEndTime;
    }

    public static class Builder {

        private ProcessSnapshot processSnapshot;

        public Builder() {
            this.processSnapshot = new ProcessSnapshot();
        }

        public Builder chainId(String chainId) {
            this.processSnapshot.chainId = chainId;
            return this;
        }

        public Builder stepIndex(String stepIndex) {
            this.processSnapshot.stepIndex = stepIndex;
            return this;
        }

        public Builder stepName(String stepName) {
            this.processSnapshot.stepName = stepName;
            return this;
        }

        public Builder stepParam(String stepParam) {
            this.processSnapshot.stepParam = stepParam;
            return this;
        }

        public Builder stepResult(String stepResult) {
            this.processSnapshot.stepResult = stepResult;
            return this;
        }

        public Builder stepException(String stepException) {
            this.processSnapshot.stepException = stepException;
            return this;
        }

        public Builder stepExceptionMessage(String stepExceptionMessage) {
            this.processSnapshot.stepExceptionMessage = stepExceptionMessage;
            return this;
        }

        public Builder stepExceptionStackTrace(String stepExceptionStackTrace) {
            this.processSnapshot.stepExceptionStackTrace = stepExceptionStackTrace;
            return this;
        }

        public Builder isSuccess(String isSuccess) {
            this.processSnapshot.isSuccess = isSuccess;
            return this;
        }

        public Builder stepStartTime(String stepStartTime) {
            this.processSnapshot.stepStartTime = stepStartTime;
            return this;
        }

        public Builder stepEndTime(String stepEndTime) {
            this.processSnapshot.stepEndTime = stepEndTime;
            return this;
        }

        public Builder hostMethod(String hostMethod) {
            this.processSnapshot.hostMethod = hostMethod;
            return this;
        }

        public ProcessSnapshot build() {
            return this.processSnapshot;
        }


    }
}
