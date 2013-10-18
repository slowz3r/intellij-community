package com.jetbrains.python.inspections;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.python.PyNames;
import com.jetbrains.python.psi.*;
import com.jetbrains.python.psi.types.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yole
 */
public class PyCallingNonCallableInspection extends PyInspection {
  @Nls
  @NotNull
  @Override
  public String getDisplayName() {
    return "Trying to call a non-callable object";
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder,
                                        boolean isOnTheFly,
                                        @NotNull LocalInspectionToolSession session) {
    return new Visitor(holder, session);
  }

  public static class Visitor extends PyInspectionVisitor {
    public Visitor(@Nullable ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
      super(holder, session);
    }

    @Override
    public void visitPyCallExpression(PyCallExpression node) {
      super.visitPyCallExpression(node);
      checkCallable(node, node.getCallee(), null);
    }

    @Override
    public void visitPyDecoratorList(PyDecoratorList node) {
      super.visitPyDecoratorList(node);
      for (PyDecorator decorator : node.getDecorators()) {
        final PyExpression callee = decorator.getCallee();
        checkCallable(decorator, callee, null);
        if (decorator.hasArgumentList()) {
          checkCallable(decorator, decorator, null);
        }
      }
    }

    private void checkCallable(@NotNull PyElement node, @Nullable PyExpression callee, @Nullable PyType type) {
      final Boolean callable = callee != null ? isCallable(callee, myTypeEvalContext) : PyTypeChecker.isCallable(type);
      if (callable == null) {
        return;
      }
      if (!callable) {
        final PyType calleeType = callee != null ? myTypeEvalContext.getType(callee) : type;
        if (calleeType instanceof PyClassType) {
          registerProblem(node, String.format("'%s' object is not callable", calleeType.getName()));
        }
        else if (callee != null) {
          registerProblem(node, String.format("'%s' is not callable", callee.getName()));
        }
        else {
          registerProblem(node, "Expression is not callable");
        }
      }
    }
  }

  @Nullable
  private static Boolean isCallable(@NotNull PyExpression element, @NotNull TypeEvalContext context) {
    if (element instanceof PyQualifiedExpression && PyNames.CLASS.equals(element.getName())) {
      return true;
    }
    return PyTypeChecker.isCallable(context.getType(element));
  }
}
