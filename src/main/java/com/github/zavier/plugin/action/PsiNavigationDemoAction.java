package com.github.zavier.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtil;

public class PsiNavigationDemoAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final Project project = anActionEvent.getData(CommonDataKeys.PROJECT);

        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null) {
            return;
        }
        int offset = editor.getCaretModel().getOffset();

        StringBuilder sb = new StringBuilder();
        final StringBuilder infoBuilder = new StringBuilder();
        PsiElement element = psiFile.findElementAt(offset);

        if (element != null) {
            final PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            psiClass.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitField(PsiField field) {
                    super.visitField(field);
                    final String name = field.getName();
                    final PsiType type = field.getType();

                    sb.append("name: ").append(name).append(" ; ")
                            .append("type: ").append(type.getCanonicalText()).append(" ")
                            .append(type.getPresentableText()).append(" ");
                    if (type instanceof PsiClassType) {
                        final PsiType psiType = PsiUtil.extractIterableTypeParameter(type, false);
                        if (psiType != null) {
                            sb.append(psiType.getCanonicalText()).append("\r\n");

                            final PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(psiType.getCanonicalText(), GlobalSearchScope.allScope(project));
                            sb.append("findClass: ").append(aClass);
                        }


                    }

                }
            });

        }

        infoBuilder.append("Element at caret: ").append(element).append("\n");
        if (element != null) {
            PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
            infoBuilder
                    .append("Containing method: ")
                    .append(containingMethod != null ? containingMethod.getName() : "none")
                    .append("\n");
            if (containingMethod != null) {
                PsiClass containingClass = containingMethod.getContainingClass();
                infoBuilder
                        .append("Containing class: ")
                        .append(containingClass != null ? containingClass.getName() : "none")
                        .append("\n");

                infoBuilder.append("Local variables:\n");
                containingMethod.accept(new JavaRecursiveElementVisitor() {
                    @Override
                    public void visitLocalVariable(PsiLocalVariable variable) {
                        super.visitLocalVariable(variable);
                        infoBuilder.append(variable.getName()).append("\n");
                    }
                });
            }
        }
        infoBuilder.append("\r\n").append(sb.toString());
        Messages.showMessageDialog(anActionEvent.getProject(), infoBuilder.toString(), "PSI Info", null);
    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        e.getPresentation().setEnabled(editor != null && psiFile != null);
    }
}
