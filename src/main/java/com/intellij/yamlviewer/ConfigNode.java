package com.intellij.yamlviewer;

import com.intellij.ui.JBColor;
import java.awt.*;

public class ConfigNode {
    private final String key;
    private final Object value;
    private final NodeType type;

    public enum NodeType {
        KEY, STRING, NUMBER, BOOLEAN, NULL, ARRAY, OBJECT
    }

    public ConfigNode(String key, Object value, NodeType type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public String getDisplayText() {
        if (key != null && value != null) {
            return key + ": " + value;
        } else if (key != null) {
            return key;
        }
        return String.valueOf(value);
    }

    public Color getColor() {
        return switch (type) {
            case KEY -> JBColor.BLUE;
            case STRING -> JBColor.GREEN.darker();
            case NUMBER -> JBColor.MAGENTA;
            case BOOLEAN -> JBColor.ORANGE;
            case NULL -> JBColor.GRAY;
            case ARRAY -> JBColor.CYAN.darker();
            case OBJECT -> JBColor.BLUE.darker();
        };
    }

    public String getKey() { return key; }
    public Object getValue() { return value; }
    public NodeType getType() { return type; }
}
