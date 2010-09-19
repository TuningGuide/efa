/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.types;

import de.nmichael.efa.core.types.ItemTypeLabelValue;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import javax.swing.*;

// @i18n complete

public class ItemTypeString extends ItemTypeLabelValue {

    private String value;
    private String allowedCharacters;
    private String replacementCharacter;
    private boolean notNull = false;

    public ItemTypeString(String name, String value, int type,
            String category, String description) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public void parseValue(String value) {
        if (allowedCharacters != null && value != null) {
            int i = 0;
            while (i < value.length()) {
                char c = value.charAt(i);
                if (allowedCharacters.indexOf(c) < 0) {
                    value = (i > 0 ? value.substring(0, i) : "") +
                            (replacementCharacter != null ? replacementCharacter : "") +
                            (i+1 < value.length() ? value.substring(i+1) : "");
                    if (replacementCharacter != null) {
                        i++;
                    }
                } else {
                    i++;
                }
            }
        }
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setAllowedCharacters(String allowedCharacters) {
        this.allowedCharacters = allowedCharacters;
    }

    public void setReplacementCharacter(char replacementCharacter) {
        this.replacementCharacter = Character.toString(replacementCharacter);
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isNotNullSet() {
        return notNull;
    }

    public boolean isValidInput() {
        if (isNotNullSet()) {
            if (value == null || value.length() == 0) {
                return false;
            }
        }
        return true;
    }
}
