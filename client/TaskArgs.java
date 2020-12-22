package client;

import com.beust.jcommander.Parameter;

public class TaskArgs {
    @Parameter(names = {"-t", "-type"})
    String type;

    @Parameter(names = {"-k", "-index"})
    String key;

    @Parameter(names = {"-v", "-message"})
    String value;

    @Parameter(names = {"-in"})
    String input;

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getInput() {
        return input;
    }

    public String getValue() {
        return value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getJSONCommand() {
        return "{ \"type\": \"" + type + "\", " +
                "\"key\": " + keyJSON() +
                (value != null ? ", \"value\": " + valueJSON() + " }" : "}");
    }

    private String valueJSON() {
        if (value.charAt(0) == '{') {
            return value;
        } else {
            return "\"" + value + "\"";
        }
    }

    private String keyJSON() {
        if (key.charAt(0) == '[') {
            return key;
        } else {
            return "\"" + key + "\"";
        }
    }
}
