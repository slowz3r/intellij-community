package com.jetbrains.python.hierarchy;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.roots.ui.util.CompositeAppearance;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyClass;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey.Ivanov
 * Date: Jul 31, 2009
 * Time: 6:26:37 PM
 */
public class PyTypeHierarchyNodeDescriptor extends HierarchyNodeDescriptor {

  public PyTypeHierarchyNodeDescriptor(final NodeDescriptor parentDescriptor, @NotNull final PsiElement element, final boolean isBase) {
    super(element.getProject(), parentDescriptor, element, isBase);
  }

  public PyClass getClassElement() {
    return (PyClass)myElement;
  }

  public boolean isValid() {
    return myElement != null && myElement.isValid();
  }

  @Override
  public boolean update() {
    boolean changes = super.update();
    final CompositeAppearance oldText = myHighlightedText;

    myHighlightedText = new CompositeAppearance();
    TextAttributes classNameAttributes = null;
    if (myColor != null) {
      classNameAttributes = new TextAttributes(myColor, null, null, null, Font.PLAIN);
    }

    NavigatablePsiElement element = (NavigatablePsiElement)myElement;
    if (element == null) {
      final String invalidPrefix = IdeBundle.message("node.hierarchy.invalid");
      if (!myHighlightedText.getText().startsWith(invalidPrefix)) {
        myHighlightedText.getBeginning().addText(invalidPrefix, HierarchyNodeDescriptor.getInvalidPrefixAttributes());
      }
      return true;
    }

    final ItemPresentation presentation = element.getPresentation();
    if (presentation != null) {
      final PyClass cl = getClassElement();
      myHighlightedText.getEnding().addText(cl.getName(), classNameAttributes);
      myHighlightedText.getEnding()
        .addText(" (" + cl.getContainingFile().getName() + ")", HierarchyNodeDescriptor.getPackageNameAttributes());
    }
    myName = myHighlightedText.getText();

    if (!Comparing.equal(myHighlightedText, oldText)) {
      changes = true;
    }
    return changes;
  }
}
