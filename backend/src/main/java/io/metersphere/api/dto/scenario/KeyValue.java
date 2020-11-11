package io.metersphere.api.dto.scenario;

import io.metersphere.api.dto.scenario.request.BodyFile;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class KeyValue {
    private String name;
    private String value;
    private String type;
    private List<BodyFile> files;
    private String description;
    private String contentType;
    private boolean enable;
    private boolean encode = true;

    public KeyValue() {
        this.enable = true;
    }

    public KeyValue(String name, String value) {
        this.name = name;
        this.value = value;
        this.enable = true;
    }

    public KeyValue(String name, String value, String description) {
        this.name = name;
        this.value = value;
        this.enable = true;
        this.description = description;
    }

    public boolean isValid() {
        return (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(value)) && !StringUtils.equalsIgnoreCase(type, "file");
    }

    public boolean isFile() {
        return (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(value)) && StringUtils.equalsIgnoreCase(type, "file");
    }
}
