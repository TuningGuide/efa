/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui.util;

import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.util.*;
import java.util.*;

public class AutoCompleteList {

    private IDataAccess dataAccess;
    private long dataAccessSCN = -1;
    private long validAt = -1;
    private Vector<String> data = new Vector<String>();;
    private Hashtable<String,String> lower2real = new Hashtable<String,String>();;
    private int pos = 0;
    private String lastPrefix;
    private long scn = 0;

    public AutoCompleteList() {
    }

    public AutoCompleteList(IDataAccess dataAccess) {
        setDataAccess(dataAccess);
    }

    public AutoCompleteList(IDataAccess dataAccess, long validAt) {
        setDataAccess(dataAccess, validAt);
    }

    public synchronized void setDataAccess(IDataAccess dataAccess) {
        setDataAccess(dataAccess, -1);
    }

    public synchronized void setValidAt(long validAt) {
        setDataAccess(dataAccess, validAt);
    }

    public synchronized void setDataAccess(IDataAccess dataAccess, long validAt) {
        this.dataAccess = dataAccess;
        this.dataAccessSCN = -1;
        this.validAt = validAt;
        scn++;
    }

    /**
     * Synchronize this list with the uderlying DataAccess, if necessary
     */
    public synchronized void update() {
        try {
            if (dataAccess != null && dataAccess.isStorageObjectOpen() && dataAccess.getSCN() != dataAccessSCN) {
                dataAccessSCN = dataAccess.getSCN();
                data = new Vector<String>();
                lower2real = new Hashtable<String,String>();
                DataKeyIterator it = dataAccess.getStaticIterator();
                DataKey k = it.getFirst();
                while (k != null) {
                    DataRecord r = dataAccess.get(k);
                    boolean valid = (r != null) &&
                            (validAt < 0 || (validAt >= r.getValidFrom() && validAt < r.getInvalidFrom()));
                    if (valid) {
                        String s = r.getQualifiedName();
                        if (s.length() > 0) {
                            add(s);
                        }
                    }
                    k = it.getNext();
                }
                sort();
            }
        } catch (Exception e) {
        }
    }

    public synchronized void add(String s) {
        data.add(s);
        lower2real.put(s.toLowerCase(), s);
        scn++;
    }

    public synchronized void delete(String s) {
        data.remove(s);
        lower2real.remove(s.toLowerCase());
        scn++;
    }

    public synchronized void sort() {
        String[] a = data.toArray(new String[0]);
        Arrays.sort(a);
        data = new Vector(a.length);
        for (int i=0; i<a.length; i++) {
            data.add(a[i]);
        }
    }

    public synchronized String getExact(String s) {
        s = s.toLowerCase();
        if (lower2real.containsKey(s)) {
            return lower2real.get(s);
        } else {
            return null;
        }
    }

    public synchronized String getNext() {
        if (pos < data.size() - 1) {
            return data.get(++pos);
        }
        return null;
    }

    public synchronized String getPrev() {
        if (pos > 0) {
            return data.get(--pos);
        }
        return null;
    }

    public synchronized String getFirst(String prefix) {
        prefix = prefix.toLowerCase();
        lastPrefix = prefix;
        for (pos = 0; pos < data.size(); pos++) {
            if (data.get(pos).toLowerCase().startsWith(prefix)) {
                return data.get(pos);
            }
        }
        return null;
    }

    public synchronized String getLast(String prefix) {
        prefix = prefix.toLowerCase();
        lastPrefix = prefix;
        for (pos = data.size()-1; pos >= 0; pos--) {
            if (data.get(pos).toLowerCase().startsWith(prefix)) {
                return data.get(pos);
            }
        }
        return null;
    }

    public synchronized String getNext(String prefix) {
        prefix = prefix.toLowerCase();
        if (lastPrefix == null || !prefix.equals(lastPrefix)) {
            return getFirst(prefix);
        }
        if (pos < data.size() - 1) {
            String s = data.get(++pos);
            if (s.toLowerCase().startsWith(prefix)) {
                return s;
            }
        }
        return null;
    }

    public synchronized String getPrev(String prefix) {
        prefix = prefix.toLowerCase();
        if (lastPrefix == null || !prefix.equals(lastPrefix)) {
            return getFirst(prefix);
        }
        if (pos > 0) {
            String s = data.get(--pos);
            if (s.toLowerCase().startsWith(prefix)) {
                return s;
            }
        }
        return null;
    }

    public String[] getData() {
        return data.toArray(new String[0]);
    }

    public long getSCN() {
        update();
        return scn;
    }

    /**
     * Creates a vector containing all neigbours of a String. The distance
     * is measured by using EditDistance - number of keboard-hits to transform
     * name into neighbour.

     * @param name String who's neighbours are searched
     * @param radius
     * @return Vector containing neighbour strings
     * @author Thilo A. Coblenzer (original implementation)
     */
    public synchronized Vector<String> getNeighbours(String name, int radius, int maxPermutations) {
        Vector<String> neighbours = new Vector<String>();
        name = name.toLowerCase();
        Vector<String> namePerm = null;
        if (maxPermutations > 0) {
            namePerm = getPermutations(name, maxPermutations);
        }

        int lowestDist = Integer.MAX_VALUE;
        for (int i=data.size()-1; i>=0; i--) {
            String neighbour = data.get(i);
            String neighbourlc = neighbour.toLowerCase();

            int dist = EditDistance.getDistance(neighbour.toLowerCase(), name);
            if (dist <= radius) {
                if (dist < lowestDist) {
                    neighbours.add(0, neighbour);
                } else {
                    neighbours.add(neighbour);
                }
                lowestDist = dist;
            } else {
                if (namePerm != null) {
                    // check for neighbours for each of the name parts
                    Vector<String> neighbourPerm = getPermutations(neighbourlc, maxPermutations);
                    boolean found = false;
                    for (int x = 0; !found && x < namePerm.size() && x < maxPermutations; x++) {
                        for (int y = 0; !found && y < neighbourPerm.size() && y < maxPermutations; y++) {
                            dist = EditDistance.getDistance(neighbourPerm.get(y).toLowerCase(), namePerm.get(x));
                            if (dist <= radius) {
                                if (dist < lowestDist) {
                                    neighbours.add(0, neighbour);
                                } else {
                                    neighbours.add(neighbour);
                                }
                                lowestDist = dist;
                                found = true;
                            }
                        }
                    }
                }
            }
        }

        if (neighbours.size() == 0) {
            return null;
        } else {
            return neighbours;
        }
    }

    private static Vector<String> getPermutations(String s, int maxPermutations) {
        Vector<String> parts = splitString(s);
        Vector<String> perms = new Vector<String>();
        addPermutation(perms, parts, "", maxPermutations);
        return perms;
    }

    private static void addPermutation(Vector<String> perms, Vector<String> parts, String perm, int maxPermutations) {
        for (int i=0; i<parts.size(); i++) {
            if (perms.size() >= maxPermutations) {
                return;
            }
            String newPerm = (perm.length() > 0 ? perm + " " : "") + parts.get(i);
            if (parts.size() == 1) {
                perms.add(newPerm);
            } else {
                Vector<String> remainingParts = new Vector<String>(parts);
                remainingParts.remove(i);
                addPermutation(perms, remainingParts, newPerm, maxPermutations);
            }
        }
    }

    private static Vector<String> splitString(String s) {
        Vector<String> v = new Vector<String>();
        StringBuilder sb = new StringBuilder();
        for (int i=0; s != null && i<s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetter(c)) {
                sb.append(c);
            } else {
                if (sb.length() > 0) {
                    v.add(sb.toString());
                    sb = new StringBuilder();
                }
            }
        }
        if (sb.length() > 0) {
            v.add(sb.toString());
        }
        return v;
    }

    public static void main(String[] args) {
        Vector<String> v = getPermutations("a b c", 7);
        for (int i=0; i<v.size(); i++) {
            System.out.println(v.get(i));
        }
    }
    
}
