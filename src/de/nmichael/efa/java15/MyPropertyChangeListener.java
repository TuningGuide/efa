package de.nmichael.efa.java15;


public class MyPropertyChangeListener implements java.beans.PropertyChangeListener {
  javax.swing.JEditorPane editorPane;
  
  public void setEditorPane(javax.swing.JEditorPane editorPane) {
    this.editorPane = editorPane;
  }
  
  public void propertyChange(java.beans.PropertyChangeEvent evt) {
    javax.swing.text.EditorKit kit = editorPane.getEditorKit();
    try {
      ((javax.swing.text.html.HTMLEditorKit)kit).setAutoFormSubmission(false);
    } catch(Exception e) {
    }
  }
}

