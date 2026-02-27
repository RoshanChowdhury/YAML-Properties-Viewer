package com.intellij.yamlviewer;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

public class ViewInConfigViewerAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (file == null) return;

        ToolWindow toolWindow = ToolWindowManager.getInstance(e.getProject()).getToolWindow("Config Viewer");
        if (toolWindow != null) {
            toolWindow.show(() -> {
                ConfigViewerPanel panel = (ConfigViewerPanel) toolWindow.getContentManager()
                        .getContent(0).getComponent();
                panel.loadFile(file);
            });
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        boolean enabled = file != null && (file.getName().endsWith(".yml") || 
                                          file.getName().endsWith(".yaml") || 
                                          file.getName().endsWith(".properties"));
        e.getPresentation().setEnabledAndVisible(enabled);
    }
}
