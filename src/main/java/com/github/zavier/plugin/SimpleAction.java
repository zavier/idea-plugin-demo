package com.github.zavier.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;

public class SimpleAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }
        psiFile.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {
                super.visitLocalVariable(variable);
                System.out.println("Found a variable at offset " + variable.getTextRange().getStartOffset());
            }
        });

        Messages.showMessageDialog("信息body", "Title标题", Messages.getInformationIcon());
    }
}
