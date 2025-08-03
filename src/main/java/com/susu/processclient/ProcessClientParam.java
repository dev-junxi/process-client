package com.susu.processclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ProcessClientParam(ProcessParam processParam, Map<String, Object> context) {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public ProcessClientParam {

        Assert.notNull(processParam, "request cannot be null");
        Assert.notNull(context, "context cannot be null");
        Assert.noNullElements(context.keySet(), "context cannot contain null keys");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private ProcessParam processParam;

        private Map<String, Object> context = new HashMap<>();

        private Builder() {
        }

        public Builder request(ProcessParam processParam) {
            Assert.notNull(processParam, "request cannot be null");
            this.processParam = processParam;
            return this;
        }

        public Builder context(Map<String, Object> context) {
            Assert.notNull(context, "context cannot be null");
            Assert.noNullElements(context.keySet(), "context cannot contain null keys");
            this.context.putAll(context);
            return this;
        }

        public ProcessClientParam build() {
            return new ProcessClientParam(this.processParam, this.context);
        }

    }

    public <T> T getResult(Class<T> clazz) {
        T t = OBJECT_MAPPER.convertValue(context.get("processResult"), clazz);
        context.put("processResult", t);
        return t;
    }

    public ProcessResult getResult() {
        return (ProcessResult) context.get("processResult");
    }

    public void setResult(ProcessResult processResult) {
        context.put("processResult", processResult);
    }

    public <T> T getParam(Class<T> clazz) {
        return OBJECT_MAPPER.convertValue(processParam, clazz);
    }

    public List<ProcessSnapshot> getSnapshots() {
        Object obj = context.get("processSnapshot");
        if (obj instanceof List<?> list) {
            // 检查列表元素类型
            if (!list.isEmpty() && !(list.get(0) instanceof ProcessSnapshot)) {
                return new ArrayList<>();
            }
            @SuppressWarnings("unchecked")
            List<ProcessSnapshot> result = (List<ProcessSnapshot>) list;
            return result;
        }
        List<ProcessSnapshot> emptyList = new ArrayList<>();
        context.put("processSnapshot", emptyList);
        return emptyList;
    }

    public void setCurrentSnapshot(ProcessSnapshot processSnapshot) {
        context.put("currentSnapshot", processSnapshot);
    }

    public ProcessSnapshot getCurrentSnapshot() {
        return context.get("currentSnapshot") != null ? (ProcessSnapshot) context.get("currentSnapshot") : ProcessSnapshot.builder().build();
    }


}
