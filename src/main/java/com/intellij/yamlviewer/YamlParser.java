package com.intellij.yamlviewer;

import org.yaml.snakeyaml.Yaml;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.Map;

public class YamlParser {
    public static DefaultMutableTreeNode parse(String content, String fileName) {
        Yaml yaml = new Yaml();
        Object data = yaml.load(content);
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(
            new ConfigNode(fileName, null, ConfigNode.NodeType.OBJECT)
        );
        
        buildTree(root, data, null);
        return root;
    }

    private static void buildTree(DefaultMutableTreeNode parent, Object data, String key) {
        if (data instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) data;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String entryKey = String.valueOf(entry.getKey());
                Object entryValue = entry.getValue();
                
                if (entryValue instanceof Map || entryValue instanceof List) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(
                        new ConfigNode(entryKey, null, ConfigNode.NodeType.OBJECT)
                    );
                    parent.add(node);
                    buildTree(node, entryValue, entryKey);
                } else {
                    parent.add(new DefaultMutableTreeNode(
                        new ConfigNode(entryKey, entryValue, getNodeType(entryValue))
                    ));
                }
            }
        } else if (data instanceof List) {
            List<?> list = (List<?>) data;
            for (int i = 0; i < list.size(); i++) {
                Object item = list.get(i);
                if (item instanceof Map || item instanceof List) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(
                        new ConfigNode("[" + i + "]", null, ConfigNode.NodeType.ARRAY)
                    );
                    parent.add(node);
                    buildTree(node, item, "[" + i + "]");
                } else {
                    parent.add(new DefaultMutableTreeNode(
                        new ConfigNode("[" + i + "]", item, getNodeType(item))
                    ));
                }
            }
        }
    }

    private static ConfigNode.NodeType getNodeType(Object value) {
        if (value == null) return ConfigNode.NodeType.NULL;
        if (value instanceof Number) return ConfigNode.NodeType.NUMBER;
        if (value instanceof Boolean) return ConfigNode.NodeType.BOOLEAN;
        return ConfigNode.NodeType.STRING;
    }
}
