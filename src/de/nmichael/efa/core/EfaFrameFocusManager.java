/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core;

import de.nmichael.efa.*;
import de.nmichael.efa.core.Boote;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.util.Dialog;
import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.DefaultKeyboardFocusManager;

// @i18n complete

public class EfaFrameFocusManager extends DefaultFocusManager {

  EfaFrame efaFrame;
//  KeyboardFocusManager fm;
  FocusManager fm;
  JTextField[] feld = new JTextField[1 + Fahrtenbuch.ANZ_MANNSCH];
  JButton[] button = new JButton[9];

  public EfaFrameFocusManager(EfaFrame efaFrame, FocusManager fm) {
//  public EfaFrameFocusManager(EfaFrame efaFrame, KeyboardFocusManager fm) {
    this.efaFrame = efaFrame;
    this.fm = fm;
    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) feld[i+1] = efaFrame.mannsch[i];
    for (int i=0; i<8; i++) button[i+1] = efaFrame.mannschButton[i];
  }


  void goTo(JComponent c, KeyEvent e) {
    if (Daten.efaConfig != null && efaFrame.abfahrt.equals(c) && Daten.efaConfig.skipUhrzeit) goTo(efaFrame.ziel, e);
    else if (Daten.efaConfig != null && efaFrame.ankunft.equals(c) && Daten.efaConfig.skipUhrzeit) goTo(efaFrame.ziel,e);
    else if (Daten.efaConfig != null && efaFrame.ziel.equals(c) && Daten.efaConfig.skipZiel) goTo(efaFrame.bootskm,e);
    else if (Daten.efaConfig != null && efaFrame.mannschkm.equals(c) && Daten.efaConfig.skipMannschKm) {
      efaFrame.mannschkm_focusGained(null);
      goTo(efaFrame.bemerk,e);
    }
    else if (Daten.efaConfig != null && efaFrame.bemerk.equals(c) && Daten.efaConfig.skipBemerk) goTo(efaFrame.addButton,e);
    else if (c.isEnabled() && c.isVisible()) c.requestFocus();
    else processKeyEvent(c, e);
  }


  public void processKeyEvent(Component cur, KeyEvent e) {
/*
System.out.println("================= "+System.currentTimeMillis()+" =================");
System.out.println("efaFrame.isActive()    : "+efaFrame.isActive());
System.out.println("efaFrame.isFocused()   : "+efaFrame.isFocused());
System.out.println("efaFrame.isFocusOwner(): "+efaFrame.isFocusOwner());
System.out.println("efaFrame.isShowing()   : "+efaFrame.isShowing());
System.out.println("efaFrame.hasFocus()    : "+efaFrame.hasFocus());
System.out.println(e);
*/
    if (Dialog.frameCurrent() != efaFrame
         || efaFrame == null || !efaFrame.isActive()
        ) {
      // Bugfix: Unter Windows konnten JOptionPane-Dialoge nicht mit der Tastatur bedient werden.
      // Grund war, daß efaFrame TopOfStack war, und daher der EfaFrameFocusManager zugeschlagen hat.
      // Anhand von isActive() und isFocused() läßt sich feststellen, ob efaFrame tatsächlich aktiv
      // ist oder nicht (weil z.B. ein JOptionPane-Dialog aktiv ist). Die Methoden isFocusOwner(),
      // isShowing() und hasFocus() eignen sich *nicht*. Aus diesem Grund wurde als Bugfix die
      // Abfrage "!efaFrame.isActive()" mit in die oben stehende if-Klausel aufgenommen.
      try { // try-catch-Block wegen Fehlermeldung Gerhard Engelmann 17.11.2003
//System.out.println("Invoking original key handler...\n");
        fm.processKeyEvent(cur,e);
//super.processKeyEvent(cur,e);
      } catch(Exception ee) {
//System.out.println("EXCEPTION while invoking original key handler...\n");
      }
      return;
    }

    if (e == null || (e.getKeyCode() == 9 && !e.isShiftDown() && e.getID() == e.KEY_PRESSED)) { // <TAB> (oder bei null Aufruf aus NeuesMitgliedFrame und Co)

      // LFDNR
      if (cur == efaFrame.lfdnr) {
        goTo(efaFrame.datum,e);
        if (e != null) e.consume(); return;
      }
      // DATUM
      if (cur == efaFrame.datum) {
        goTo(efaFrame.boot,e);
        if (e != null) e.consume(); return;
      }
      // BOOT
      if (cur == efaFrame.boot) {
        efaFrame.boot_focusLostGetBoot();
        if (efaFrame.bootButton.getBackground() == Color.red && !efaFrame.isDirectMode()) goTo(efaFrame.bootButton,e);
        else if (efaFrame.aktBoot != null && efaFrame.aktBoot.get(Boote.STM).equals(EfaTypes.TYPE_COXING_COXLESS)) goTo(efaFrame.mannsch[0],e);
        else goTo(efaFrame.stm,e);
        if (e != null) e.consume(); return;
      }
      // STEUERMANN
      if (cur == efaFrame.stm) {
        if (efaFrame.stmButton.getBackground() == Color.red && !efaFrame.isDirectMode()) goTo(efaFrame.stmButton,e);
        else goTo(efaFrame.mannsch[efaFrame.mannschAuswahl*8],e);
        if (e != null) e.consume(); return;
      }
      // MANNSCHAFT 1-8
      for (int i=1; i<9; i++)
        if (cur == feld[i]) {
          if (button[i].getBackground() == Color.red && !efaFrame.isDirectMode()) goTo(button[i],e);
          else if (feld[i].getText().trim().equals("")) goTo(efaFrame.abfahrt,e);
          else if (efaFrame.aktBoot != null && i==1 && efaFrame.aktBoot.get(Boote.ANZAHL).equals(EfaTypes.TYPE_NUMSEATS_1)) goTo(efaFrame.abfahrt,e);
          else if (efaFrame.aktBoot != null && i==2 && efaFrame.aktBoot.get(Boote.ANZAHL).equals(EfaTypes.TYPE_NUMSEATS_2)) goTo(efaFrame.abfahrt,e);
          else if (efaFrame.aktBoot != null && i==3 && efaFrame.aktBoot.get(Boote.ANZAHL).equals(EfaTypes.TYPE_NUMSEATS_3)) goTo(efaFrame.abfahrt,e);
          else if (efaFrame.aktBoot != null && i==4 && efaFrame.aktBoot.get(Boote.ANZAHL).equals(EfaTypes.TYPE_NUMSEATS_4)) goTo(efaFrame.abfahrt,e);
          else if (efaFrame.aktBoot != null && i==5 && efaFrame.aktBoot.get(Boote.ANZAHL).equals(EfaTypes.TYPE_NUMSEATS_5)) goTo(efaFrame.abfahrt,e);
          else if (efaFrame.aktBoot != null && i==6 && efaFrame.aktBoot.get(Boote.ANZAHL).equals(EfaTypes.TYPE_NUMSEATS_6)) goTo(efaFrame.abfahrt,e);
//          else if (efaFrame.aktBoot != null && i<=6 && efaFrame.aktBoot.get(Boote.ANZAHL).equals(Daten.bAnzahl[i-1])) goTo(efaFrame.abfahrt);
          else if (i<8) goTo(feld[i+1],e);
          else goTo(efaFrame.abfahrt,e);
          if (e != null) e.consume(); return;
        }
      // MANNSCHAFT 9-16
      for (int i=9; i<17; i++)
        if (cur == feld[i]) {
          if (button[i-8].getBackground() == Color.red && !efaFrame.isDirectMode()) goTo(button[i-8],e);
          else if (feld[i].getText().trim().equals("")) goTo(efaFrame.abfahrt,e);
          else if (i<16) goTo(feld[i+1],e);
          else goTo(efaFrame.abfahrt,e);
          if (e != null) e.consume(); return;
        }
      // MANNSCHAFT 17-24
      for (int i=17; i<25; i++)
        if (cur == feld[i]) {
          if (button[i-16].getBackground() == Color.red && !efaFrame.isDirectMode()) goTo(button[i-16],e);
          else if (feld[i].getText().trim().equals("")) goTo(efaFrame.abfahrt,e);
          else if (i<24) goTo(feld[i+1],e);
          else goTo(efaFrame.abfahrt,e);
          if (e != null) e.consume(); return;
        }
      // ABFAHRT
      if (cur == efaFrame.abfahrt) {
        goTo(efaFrame.ankunft,e);
        if (e != null) e.consume(); return;
      }
      // ANKUNFT
      if (cur == efaFrame.ankunft) {
        goTo(efaFrame.ziel,e);
        if (e != null) e.consume(); return;
      }
      // ZIEL
      if (cur == efaFrame.ziel) {
        if (efaFrame.zielButton.getBackground() != Color.red) efaFrame.setZielKm();
        if (efaFrame.zielButton.getBackground() == Color.red && !efaFrame.isDirectMode()) goTo(efaFrame.zielButton,e);
        else goTo(efaFrame.bootskm,e);
        if (e != null) e.consume(); return;
      }
      // BOOTS-KM
      if (cur == efaFrame.bootskm) {
        goTo(efaFrame.mannschkm,e);
        if (e != null) e.consume(); return;
      }
      // MANNSCH-KM
      if (cur == efaFrame.mannschkm) {
        goTo(efaFrame.bemerk,e);
        if (e != null) e.consume(); return;
      }

      // BOOT_BUTTON
      if (cur == efaFrame.bootButton) {
        efaFrame.boot_focusLostGetBoot();
        if (efaFrame.aktBoot != null && efaFrame.aktBoot.get(Boote.STM).equals(EfaTypes.TYPE_COXING_COXLESS)) goTo(efaFrame.mannsch[0],e);
        else goTo(efaFrame.stm,e);
        if (e != null) e.consume(); return;
      }
      // MANNSCHAFT-BUTTON
      for (int i=1; i<9; i++)
        if (efaFrame.mannschAuswahl == 0) {// Mannsch 1-8
          if (cur == button[i]) {
            if (efaFrame.aktBoot != null && i==1 && efaFrame.aktBoot.get(Boote.ANZAHL).equals(EfaTypes.TYPE_NUMSEATS_1)) goTo(efaFrame.abfahrt,e);
            else if (efaFrame.aktBoot != null && i==2 && efaFrame.aktBoot.get(Boote.ANZAHL).equals(EfaTypes.TYPE_NUMSEATS_2)) goTo(efaFrame.abfahrt,e);
            else if (efaFrame.aktBoot != null && i==3 && efaFrame.aktBoot.get(Boote.ANZAHL).equals(EfaTypes.TYPE_NUMSEATS_3)) goTo(efaFrame.abfahrt,e);
            else if (efaFrame.aktBoot != null && i==4 && efaFrame.aktBoot.get(Boote.ANZAHL).equals(EfaTypes.TYPE_NUMSEATS_4)) goTo(efaFrame.abfahrt,e);
            else if (efaFrame.aktBoot != null && i==5 && efaFrame.aktBoot.get(Boote.ANZAHL).equals(EfaTypes.TYPE_NUMSEATS_5)) goTo(efaFrame.abfahrt,e);
            else if (efaFrame.aktBoot != null && i==6 && efaFrame.aktBoot.get(Boote.ANZAHL).equals(EfaTypes.TYPE_NUMSEATS_6)) goTo(efaFrame.abfahrt,e);
//            if (efaFrame.aktBoot != null && i<=6 && efaFrame.aktBoot.get(Boote.ANZAHL).equals(Daten.bAnzahl[i-1])) goTo(efaFrame.abfahrt);
            else if (i<8) goTo(feld[i+1],e);
            else goTo(efaFrame.abfahrt,e);
            if (e != null) e.consume(); return;
          }
        } else { // Mannsch 9-16, 17-24
          if (cur == button[i]) {
            if (i<8) goTo(feld[i + efaFrame.mannschAuswahl*8 + 1],e);
            else goTo(efaFrame.abfahrt,e);
            if (e != null) e.consume(); return;
          }
        }
      // WEITERE MANNSCH-BUTTON
      if (cur == efaFrame.weitereMannschButton) {
        goTo(efaFrame.mannsch[efaFrame.mannschAuswahl*8],e);
        if (e != null) e.consume(); return;
      }
      // ADD-BUTTON
      if (cur == efaFrame.addButton && efaFrame.mode != EfaFrame.MODE_FULL) {
        goTo(efaFrame.lfdnr,e);
        if (e != null) e.consume(); return;
      }
    }


    if (e != null && e.getKeyCode() == 9 && e.isShiftDown() && e.getID() == e.KEY_PRESSED) { // <Shift>-<TAB>
      if (cur == efaFrame.stm) {
        goTo(efaFrame.boot,e);
        e.consume(); return;
      }
      for (int i=0; i<8; i++) if (cur == efaFrame.mannsch[efaFrame.mannschAuswahl*8 + i]) {
        goTo( (i==0 ? efaFrame.stm : efaFrame.mannsch[efaFrame.mannschAuswahl*8 + i -1]) ,e);
        e.consume(); return;
      }
      if (cur == efaFrame.abfahrt) {
        for (int i=1; i<9; i++) {
          if (feld[i + efaFrame.mannschAuswahl*8].getText().trim().length()==0 || i ==8) {
            goTo(feld[i + efaFrame.mannschAuswahl*8],e);
            e.consume(); return;
          }
        }
      }
      if (cur == efaFrame.weitereMannschButton) {
        goTo(efaFrame.mannsch[efaFrame.mannschAuswahl * 8],e);
        e.consume(); return;
      }
      if (cur == efaFrame.bootskm) {
        goTo(efaFrame.ziel,e);
        e.consume(); return;
      }
      if (cur == efaFrame.mannschkm) {
        goTo(efaFrame.bootskm,e);
        e.consume(); return;
      }
      if (cur == efaFrame.bemerk) {
        goTo(efaFrame.mannschkm,e);
        e.consume(); return;
      }
    }

    if (e != null)
      try { // try-catch-Block wegen Fehlermeldung Gerhard Engelmann 17.11.2003
        fm.processKeyEvent(cur,e);
      } catch(Exception ee) {
      }
  }

  public void focusNextComponent(Component cur) {
//    System.out.println("focusNextComponent(cur)");
    fm.focusNextComponent(cur);
  }
  public void focusPreviousComponent(Component cur) {
//    System.out.println("focusPreviousComponent(cur)");
    fm.focusPreviousComponent(cur);

  }



}
