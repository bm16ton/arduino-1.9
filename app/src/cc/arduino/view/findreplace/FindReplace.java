/*
 * This file is part of Arduino.
 *
 * Copyright 2015 Arduino LLC (http://www.arduino.cc/)
 *
 * Arduino is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * As a special exception, you may use this file as part of a free software
 * library without restriction.  Specifically, if other files instantiate
 * templates or use macros or inline functions from this file, or you compile
 * this file and link it with other files to produce an executable, this
 * file does not by itself cause the resulting executable to be covered by
 * the GNU General Public License.  This exception does not however
 * invalidate any other reasons why the executable file might be covered by
 * the GNU General Public License.
 */

package cc.arduino.view.findreplace;

import processing.app.Base;
import processing.app.Editor;
import processing.app.EditorTab;
import processing.app.helpers.OSUtils;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JPopupMenu;
import javax.swing.Action;
import javax.swing.text.DefaultEditorKit;
import java.util.HashMap;
import java.util.Map;

import static java.awt.GraphicsDevice.WindowTranslucency.*;
import static processing.app.I18n.tr;

public class FindReplace extends javax.swing.JFrame {

  private static final String FIND_TEXT = "findText";
  private static final String REPLACE_TEXT = "replaceText";
  private static final String IGNORE_CASE = "ignoreCase";
  private static final String SEARCH_ALL_FILES = "searchAllFiles";
  private static final String WRAP_AROUND = "wrapAround";

  private final Editor editor;

  public FindReplace(Editor editor, Map<String, Object> state) {
    this.editor = editor;

    isTranslucencySupported();
    initComponents();

    if (OSUtils.isMacOS()) {
      buttonsContainer.removeAll();
      buttonsContainer.add(replaceAllButton);
      buttonsContainer.add(replaceButton);
      buttonsContainer.add(replaceFindButton);
      buttonsContainer.add(previousButton);
      buttonsContainer.add(findButton);
    }

    Base.registerWindowCloseKeys(getRootPane(), e -> {
      setAutoRequestFocus(true);
      setVisible(false);
      Base.FIND_DIALOG_STATE = findDialogState();
    });

    Base.setIcon(this);

    editor.addWindowListener(new WindowAdapter() {
      public void windowActivated(WindowEvent e) {
        toFront();
        setAutoRequestFocus(false);
      }
      public void windowDeactivated(WindowEvent e) {
          return;
      }
    });

    addWindowListener(new WindowAdapter() {
      public void windowActivated(WindowEvent e) {
          return;
      }
      public void windowDeactivated(WindowEvent e) {
      }
    });

    restoreFindDialogState(state);
  }

  @Override
  public void setVisible(boolean b) {
    getRootPane().setDefaultButton(findButton);
    // means we are restoring the window visibility
    setAutoRequestFocus(true);
    super.setVisible(b);
  }

  private boolean useTranslucency;

  private void isTranslucencySupported() {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();
    //If translucent windows aren't supported, exit.
    useTranslucency = gd.isWindowTranslucencySupported(TRANSLUCENT);
  }

  private Map<String, Object> findDialogState() {
    Map<String, Object> state = new HashMap<>();
    state.put(FIND_TEXT, findField.getText());
    state.put(REPLACE_TEXT, replaceField.getText());
    state.put(IGNORE_CASE, ignoreCaseBox.isSelected());
    state.put(WRAP_AROUND, wrapAroundBox.isSelected());
    state.put(SEARCH_ALL_FILES, searchAllFilesBox.isSelected());
    return state;
  }

  private void restoreFindDialogState(Map<String, Object> state) {
    if (state.containsKey(FIND_TEXT)) {
      findField.setText((String) state.get(FIND_TEXT));
    }
    if (state.containsKey(REPLACE_TEXT)) {
      replaceField.setText((String) state.get(REPLACE_TEXT));
    }
    if (state.containsKey(IGNORE_CASE)) {
      ignoreCaseBox.setSelected((Boolean) state.get(IGNORE_CASE));
    }
    if (state.containsKey(SEARCH_ALL_FILES)) {
      searchAllFilesBox.setSelected((Boolean) state.get(SEARCH_ALL_FILES));
    }
    if (state.containsKey(WRAP_AROUND)) {
      wrapAroundBox.setSelected((Boolean) state.get(WRAP_AROUND));
    }
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    javax.swing.JLabel findLabel = new javax.swing.JLabel();
    findField = new javax.swing.JTextField();
    javax.swing.JLabel replaceLabel = new javax.swing.JLabel();
    replaceField = new javax.swing.JTextField();
    ignoreCaseBox = new javax.swing.JCheckBox();
    wrapAroundBox = new javax.swing.JCheckBox();
    searchAllFilesBox = new javax.swing.JCheckBox();
    buttonsContainer = new javax.swing.JPanel();
    findButton = new javax.swing.JButton();
    previousButton = new javax.swing.JButton();
    replaceFindButton = new javax.swing.JButton();
    replaceButton = new javax.swing.JButton();
    replaceAllButton = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(tr("Find"));
    setResizable(false);

    findLabel.setText(tr("Find:"));

    findField.setColumns(20);

    replaceLabel.setText(tr("Replace with:"));

    replaceField.setColumns(20);

    ignoreCaseBox.setSelected(true);
    ignoreCaseBox.setText(tr("Ignore Case"));

    wrapAroundBox.setSelected(true);
    wrapAroundBox.setText(tr("Wrap Around"));

    searchAllFilesBox.setText(tr("Search all Sketch Tabs"));

    JPopupMenu menu = new JPopupMenu();
    Action cut = new DefaultEditorKit.CutAction();
    cut.putValue(Action.NAME, tr("Cut"));
    menu.add( cut );

    Action copy = new DefaultEditorKit.CopyAction();
    copy.putValue(Action.NAME, tr("Copy"));
    menu.add( copy );

    Action paste = new DefaultEditorKit.PasteAction();
    paste.putValue(Action.NAME, tr("Paste"));
    menu.add( paste );

    findField.setComponentPopupMenu( menu );
    replaceField.setComponentPopupMenu( menu );

    findButton.setText(tr("Find"));
    findButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        findButtonActionPerformed(evt);
      }
    });
    buttonsContainer.add(findButton);

    previousButton.setText(tr("Previous"));
    previousButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        previousButtonActionPerformed(evt);
      }
    });
    buttonsContainer.add(previousButton);

    replaceFindButton.setText(tr("Replace & Find"));
    replaceFindButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        replaceFindButtonActionPerformed(evt);
      }
    });
    buttonsContainer.add(replaceFindButton);

    replaceButton.setText(tr("Replace"));
    replaceButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        replaceButtonActionPerformed(evt);
      }
    });
    buttonsContainer.add(replaceButton);

    replaceAllButton.setText(tr("Replace All"));
    replaceAllButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        replaceAllButtonActionPerformed(evt);
      }
    });
    buttonsContainer.add(replaceAllButton);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addContainerGap()
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(replaceLabel)
                .addComponent(findLabel))
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(findField)
                .addComponent(replaceField)
                .addGroup(layout.createSequentialGroup()
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchAllFilesBox)
                    .addComponent(wrapAroundBox)
                    .addComponent(ignoreCaseBox))
                  .addGap(0, 0, Short.MAX_VALUE))))
            .addComponent(buttonsContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addContainerGap()
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(findLabel)
            .addComponent(findField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(replaceLabel)
            .addComponent(replaceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(ignoreCaseBox)
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(wrapAroundBox)
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(searchAllFilesBox)
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(buttonsContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void findButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findButtonActionPerformed
    findNext();
  }//GEN-LAST:event_findButtonActionPerformed

  private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousButtonActionPerformed
    findPrevious();
  }//GEN-LAST:event_previousButtonActionPerformed

  private void replaceFindButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceFindButtonActionPerformed
    replaceAndFindNext();
  }//GEN-LAST:event_replaceFindButtonActionPerformed

  private void replaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceButtonActionPerformed
    replace();
  }//GEN-LAST:event_replaceButtonActionPerformed

  private void replaceAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceAllButtonActionPerformed
    replaceAll();
  }//GEN-LAST:event_replaceAllButtonActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel buttonsContainer;
  private javax.swing.JButton findButton;
  private javax.swing.JTextField findField;
  private javax.swing.JCheckBox ignoreCaseBox;
  private javax.swing.JButton previousButton;
  private javax.swing.JButton replaceAllButton;
  private javax.swing.JButton replaceButton;
  private javax.swing.JTextField replaceField;
  private javax.swing.JButton replaceFindButton;
  private javax.swing.JCheckBox searchAllFilesBox;
  private javax.swing.JCheckBox wrapAroundBox;
  // End of variables declaration//GEN-END:variables

  private boolean find(boolean wrap, boolean backwards, boolean searchTabs, int originTab) {
    String search = findField.getText();

    if (search.length() == 0) {
      return false;
    }

    String text = editor.getCurrentTab().getText();

    if (ignoreCaseBox.isSelected()) {
      search = search.toLowerCase();
      text = text.toLowerCase();
    }

    int nextIndex;
    if (!backwards) {
      // int selectionStart = editor.textarea.getSelectionStart();
      int selectionEnd = editor.getCurrentTab().getSelectionStop();

      nextIndex = text.indexOf(search, selectionEnd);
    } else {
      // int selectionStart = editor.textarea.getSelectionStart();
      int selectionStart = editor.getCurrentTab().getSelectionStart() - 1;

      if (selectionStart >= 0) {
        nextIndex = text.lastIndexOf(search, selectionStart);
      } else {
        nextIndex = -1;
      }
    }

    if (nextIndex == -1) {
      // Nothing found on this tab: Search other tabs if required
      if (searchTabs) {
        int numTabs = editor.getTabs().size();
        if (numTabs > 1) {
          int realCurrentTab = editor.getCurrentTabIndex();

          if (originTab != realCurrentTab) {
            if (originTab < 0) {
              originTab = realCurrentTab;
            }

            if (!wrap) {
              if ((!backwards && realCurrentTab + 1 >= numTabs)
                  || (backwards && realCurrentTab - 1 < 0)) {
                return false; // Can't continue without wrap
              }
            }

            if (backwards) {
              editor.selectPrevTab();
              this.setVisible(true);
              int l = editor.getCurrentTab().getText().length() - 1;
              editor.getCurrentTab().setSelection(l, l);
            } else {
              editor.selectNextTab();
              this.setVisible(true);
              editor.getCurrentTab().setSelection(0, 0);
            }

            return find(wrap, backwards, true, originTab);
          }
        }
      }

      if (wrap) {
        nextIndex = backwards ? text.lastIndexOf(search) : text.indexOf(search, 0);
      }
    }

    if (nextIndex != -1) {
      EditorTab currentTab = editor.getCurrentTab();
      currentTab.getTextArea().getFoldManager().ensureOffsetNotInClosedFold(nextIndex);
      currentTab.setSelection(nextIndex, nextIndex + search.length());
      currentTab.getTextArea().getCaret().setSelectionVisible(true);
      return true;
    }

    return false;
  }

  /**
   * Replace the current selection with whatever's in the replacement text
   * field.
   */
  private void replace() {
    if (findField.getText().length() == 0) {
      return;
    }

    int newpos = editor.getCurrentTab().getSelectionStart() - findField.getText().length();
    if (newpos < 0) {
      newpos = 0;
    }
    editor.getCurrentTab().setSelection(newpos, newpos);

    boolean foundAtLeastOne = false;

    if (find(false, false, searchAllFilesBox.isSelected(), -1)) {
      foundAtLeastOne = true;
      editor.getCurrentTab().setSelectedText(replaceField.getText());
    }

    if (!foundAtLeastOne) {
      Toolkit.getDefaultToolkit().beep();
    }

  }

  /**
   * Replace the current selection with whatever's in the replacement text
   * field, and then find the next match
   */
  private void replaceAndFindNext() {
    replace();
    findNext();
  }

  /**
   * Replace everything that matches by doing find and replace alternately until
   * nothing more found.
   */
  private void replaceAll() {
    if (findField.getText().length() == 0) {
      return;
    }

    if (searchAllFilesBox.isSelected()) {
      editor.selectTab(0); // select the first tab
    }

    editor.getCurrentTab().setSelection(0, 0); // move to the beginning

    boolean foundAtLeastOne = false;
    while (true) {
      if (find(false, false, searchAllFilesBox.isSelected(), -1)) {
        foundAtLeastOne = true;
        editor.getCurrentTab().setSelectedText(replaceField.getText());
      } else {
        break;
      }
    }
    if (!foundAtLeastOne) {
      Toolkit.getDefaultToolkit().beep();
    }
  }

  public void findNext() {
    if (!find(wrapAroundBox.isSelected(), false, searchAllFilesBox.isSelected(), -1)) {
      Toolkit.getDefaultToolkit().beep();
    }
  }

  public void findPrevious() {
    if (!find(wrapAroundBox.isSelected(), true, searchAllFilesBox.isSelected(), -1)) {
      Toolkit.getDefaultToolkit().beep();
    }
  }

  public void setFindText(String text) {
    if (text == null) {
      return;
    }
    findField.setText(text);
  }
}
