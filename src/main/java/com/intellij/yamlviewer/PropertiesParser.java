package com.intellij.yamlviewer;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.StringReader;
import java.util.*;

public class PropertiesParser {
    public static DefaultMutableTreeNode parse(String content, String fileName) {
        Properties props = new Properties();
        try {
            props.load(new StringReader(content));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse properties", e);
        }

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(
            new ConfigNode(fileName, null, ConfigNode.NodeType.OBJECT)
        );

        Map<String, DefaultMutableTreeNode> nodeMap = new HashMap<>();
        nodeMap.put("", root);

        List<String> sortedKeys = new ArrayList<>(props.stringPropertyNames());
        Collections.sort(sortedKeys);

        for (String key : sortedKeys) {
            String value = props.getProperty(key);
            String[] parts = key.split("\\.");
            
            StringBuilder currentPath = new StringBuilder();
            DefaultMutableTreeNode currentNode = root;

            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                String pathKey = currentPath.isEmpty() ? part : currentPath + "." + part;
                
                if (i == parts.length - 1) {
                    currentNode.add(new DefaultMutableTreeNode(
                        new ConfigNode(part, value, detectType(value))
                    ));
                } else {
                    if (!nodeMap.containsKey(pathKey)) {
                        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
                            new ConfigNode(part, null, ConfigNode.NodeType.OBJECT)
                        );
                        currentNode.add(newNode);
                        nodeMap.put(pathKey, newNode);
                    }
                    currentNode = nodeMap.get(pathKey);
                }
                
                currentPath.append(currentPath.isEmpty() ? part : "." + part);
            }
        }

        return root;
    }

    private static ConfigNode.NodeType detectType(String value) {
        if (value == null || value.isEmpty()) return ConfigNode.NodeType.NULL;
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return ConfigNode.NodeType.BOOLEAN;
        }
        try {
            Double.parseDouble(value);
            return ConfigNode.NodeType.NUMBER;
        } catch (NumberFormatException e) {
            return ConfigNode.NodeType.STRING;
        }
    }
}
