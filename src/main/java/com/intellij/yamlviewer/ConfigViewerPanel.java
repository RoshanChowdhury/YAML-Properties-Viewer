package com.intellij.yamlviewer;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class ConfigViewerPanel extends JPanel {
    private final Project project;
    private final Tree tree;
    private final JTextArea rawView;
    private VirtualFile currentFile;

    public ConfigViewerPanel(Project project) {
        this.project = project;
        setLayout(new BorderLayout());

        tree = new Tree(new DefaultMutableTreeNode("No file selected"));
        tree.setCellRenderer(new ColorfulTreeRenderer());
        
        rawView = new JTextArea();
        rawView.setEditable(false);
        rawView.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tree View", new JBScrollPane(tree));
        tabbedPane.addTab("Raw View", new JBScrollPane(rawView));

        JButton refreshButton = new JButton("Refresh Current File");
        refreshButton.addActionListener(e -> loadCurrentFile());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        loadCurrentFile();
    }

    public void loadFile(VirtualFile file) {
        this.currentFile = file;
        if (file == null) return;

        String fileName = file.getName();
        if (fileName.endsWith(".yml") || fileName.endsWith(".yaml")) {
            loadYamlFile(file);
        } else if (fileName.endsWith(".properties")) {
            loadPropertiesFile(file);
        }
    }

    private void loadCurrentFile() {
        VirtualFile[] files = FileEditorManager.getInstance(project).getSelectedFiles();
        if (files.length > 0) {
            loadFile(files[0]);
        }
    }

    private void loadYamlFile(VirtualFile file) {
        try {
            String content = new String(file.contentsToByteArray());
            rawView.setText(content);
            
            DefaultMutableTreeNode root = YamlParser.parse(content, file.getName());
            tree.setModel(new javax.swing.tree.DefaultTreeModel(root));
            expandTree();
        } catch (Exception e) {
            showError("Error parsing YAML: " + e.getMessage());
        }
    }

    private void loadPropertiesFile(VirtualFile file) {
        try {
            String content = new String(file.contentsToByteArray());
            rawView.setText(content);
            
            DefaultMutableTreeNode root = PropertiesParser.parse(content, file.getName());
            tree.setModel(new javax.swing.tree.DefaultTreeModel(root));
            expandTree();
        } catch (Exception e) {
            showError("Error parsing Properties: " + e.getMessage());
        }
    }

    private void expandTree() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    private void showError(String message) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(message);
        tree.setModel(new javax.swing.tree.DefaultTreeModel(root));
    }

    private static class ColorfulTreeRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                      boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            
            if (value instanceof DefaultMutableTreeNode node) {
                Object userObject = node.getUserObject();
                if (userObject instanceof ConfigNode configNode) {
                    setText(configNode.getDisplayText());
                    setForeground(configNode.getColor());
                }
            }
            return this;
        }
    }
}
