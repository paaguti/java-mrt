// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * ASPath is a linked list of elements which are either <br>
 * AS, ASSet or ASConfedSet
 * <br><br>
 * version 3.00: complete rewrite with ASPathSegment<br>
 *
 * @author paag
 * @version 3.00
 */
public class ASPath implements Attribute {

    private enum StringParser {
        SET("(", ")", (acc, elements) -> acc.add(new ASSet(elements))),
        CONFED_SET("{", "}", (acc, elements) -> acc.add(new ASConfedSet(elements))),
        CONFED_SEQ("[", "]", (acc, elements) -> acc.add(new ASConfedSequence(elements))),
        AS("", "", List::addAll);

        private final static Pattern PATH_ELEMENT_FINDER;
        private final static Pattern AS_NUMBER_FINDER;

        static {
            final List<String> expressionStrings = Arrays
                    .stream(StringParser.values())
                    .map(parseInfo -> String.format("%s((?:\\s?\\d\\s?)+)%s", parseInfo.start, parseInfo.end))
                    .collect(Collectors.toList());

            PATH_ELEMENT_FINDER = Pattern.compile(String.join("|", expressionStrings));
            AS_NUMBER_FINDER = Pattern.compile("\\d+");
        }

        private final String start;
        private final String end;
        private final BiConsumer<List<AS>, LinkedList<AS>> appender;

        StringParser(final String start, final String end, BiConsumer<List<AS>, LinkedList<AS>> appender) {
            this.start = Pattern.quote(start);
            this.end = Pattern.quote(end);
            this.appender = appender;
        }

        static List<AS> parse(final String input) {
            final List<AS> result = new LinkedList<>();
            final Matcher pathElementMatcher = PATH_ELEMENT_FINDER.matcher(input);
            while (pathElementMatcher.find()) {
                for (final StringParser parser : StringParser.values()) {
                    final String group = pathElementMatcher.group(1 + parser.ordinal());
                    if (group != null) {
                        final LinkedList<AS> elements = new LinkedList<>();
                        final Matcher asMatcher = AS_NUMBER_FINDER.matcher(group);
                        while (asMatcher.find()) {
                            elements.add(new AS(Long.parseLong(asMatcher.group())));
                        }

                        parser.appender.accept(result, elements);
                    }
                }
            }

            return result;
        }
    }

    protected LinkedList<AS> path;

    ASPath() {
        this.path = new LinkedList<>();
        this.prependers = null;
    }

    ASPath(final List<AS> path) {
        this.path = new LinkedList<>();
        this.path.addAll(path);
    }

//	ASPath(byte[] buffer) throws Exception {
//		decode(buffer, 2);
//	}

    ASPath(byte[] buffer, int asSize) throws Exception {
        decode(buffer, asSize);
    }


    private void decode(byte[] buffer, int asSize) throws Exception {
        this.path = new LinkedList<AS>();
        int offset = 0;
        // route_btoa.System_err_println(String.format("New ASPATH (%d bytes)",buffer.length));
        while (offset < buffer.length) {
            ASPathSegment segment = new ASPathSegment(buffer, offset, asSize);
            // route_btoa.System_err_println(String.format("  segment @%-2d [t %2d]: %s\n",offset,segment.bType(),segment.toString()));
            switch (segment.bType()) {
                case MRTConstants.asSequence:
                    this.path.addAll(segment.getASList());
                    break;
                case MRTConstants.asSet:
                    if (null != segment.getAS()) {
                        this.path.add(segment.getAS());
                    } else {
                        this.path.add(new ASSet(segment.getASList()));
                    }
                    break;
                case MRTConstants.asConfedSequence:
                    this.add(new ASConfedSequence(segment.getASList()));
                    break;
                case MRTConstants.asConfedSet:
                    this.add(new ASConfedSet(segment.getASList()));
                    break;
                default:
                    RecordAccess.dump(buffer);
                    throw new Exception(
                            String.format("Unknown ASPATH Segment Type = %d Len = %d",
                                          segment.bType(), segment.bLen())
                    );
            }
            offset += segment.bLen();
        }
        //
        // and now try to see if we have AS PATH Prepend
        //
        mkPrependers();
        // route_btoa.System_err_println("New ASPATH:"+path.toString());
    }

    public AS get(int i) {
        try {
            return path.get(i);
        } catch (IndexOutOfBoundsException iobe) {
            return null;
        }
    }

    public void set(int index, AS element) {
        path.set(index, element);
    }

    /**
     * Append an AS to the ASPATH
     *
     * @param as
     */
    public void append(AS as) {
        add(as);
    }

    /**
     * alias of append(AS as)
     *
     * @param as
     */
    public void add(AS as) {
        path.add(as);
        refreshPrependers(as);
    }

    public LinkedList<AS> getPath() {
        return this.path;
    }

    /**
     * @return the number of hops of the ASPATH
     */
    public int length() {
        return path.size();
    }

    /**
     * @return a Vector with the AS's which are doing prepending.
     * @author paag
     */
    public Vector<AS> getPrependers() {
        return prependers;
    }

    /**
     * @return
     */
    public boolean hasAsPathPrepend() {
        return prependers != null;
    }

    /**
     * @author paag
     *         rebuild the PREPENDER list
     *         <br>Called from decode() or when the AS4PATH attribute is decoded
     */
    public void mkPrependers() {
        int asPathLen;

        this.prependers = null;
        if ((asPathLen = this.path.size()) == 0) {
            return;
        }
        for (int i = 0; i < asPathLen; i++) {
            AS ahora = this.path.get(i);
            if (i != this.path.lastIndexOf(ahora)) {
                if (prependers == null) {
                    prependers = new Vector<AS>();
                }
                prependers.add(ahora);
                i = this.path.lastIndexOf(ahora);
            }
        }
    }

    /**
     * refresh the prependers list when an AS is appended to the ASPATH
     *
     * @param ultimo the AS which was appended to the ASPATH
     * @author paag
     */
    private void refreshPrependers(AS ultimo) {
        //
        // is the argument involved in AS PATH prepending?
        //
        if (this.path.indexOf(ultimo) == this.path.lastIndexOf(ultimo)) {
            return;
        }
        try {
            //
            // did we already register AS PATH prepending for the argument?
            //
            if (prependers.contains(ultimo)) {
                return;
            }
        } catch (Exception e) {
            //
            // no prependers yet
            //
            prependers = new Vector<AS>();
        }
        prependers.add(ultimo);
    }

    /**
     * @param as: an AS
     * @return the index of the first occurrence of AS in the ASPATH
     */
    public int indexOf(AS as) {
        return this.path.indexOf(as);
    }

    /**
     * @param as: an AS
     * @return the index of the last occurrence of AS in the ASPATH
     */
    public int lastIndexOf(AS as) {
        return this.path.lastIndexOf(as);
    }

    public boolean contains(AS as) {
        return this.path.indexOf(as) != -1;
    }

    /**
     * @return a textual representation of the ASPATH
     * @author paag
     */
    public String toString() {
        try {
            if (this.path.size() == 0) {
                return "";
            }

            String result = null;
            for (int i = 0; i < this.path.size(); i++)
                try {
                    result = result.concat(" " + this.path.get(i).toString());
                } catch (Exception e) {
                    result = this.path.get(i).toString();
                }
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Construct an ASPath from the textual representation
     *
     * @param input The input to parse.
     * @return The parsed ASPath
     */
    public static ASPath fromString(final String input) {
        return new ASPath(StringParser.parse(input));
    }

    /**
     * Shortcut for equals(Object o)
     *
     * @param other: an other ASPath
     * @return true if both have the same length and all AS's in them are in the same place.
     * @author paag
     */
    public boolean equals(ASPath other) {
        if (this.path.size() != other.path.size()) {
            return false;
        }

        for (int i = 0; i < this.path.size(); i++)
            if (!this.path.get(i).equals(other.path.get(i))) {
                return false;
            }

        return true;
    }

    /**
     * This is the canonical implementation of the equals() method
     *
     * @param o <br>
     */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o instanceof ASPath) {
            return this.equals((ASPath) o);
        }
        return false;
    }

    /**
     * @param as: as Autonomous System
     * @return true if as generated the prefix
     */
    public boolean isGenerator(AS as) {
        return as.equals(this.path.getLast());
    }

    /**
     * @return the As which generated the prefix
     */
    public AS generator() {
        try {
            return this.path.getLast();
        } catch (Exception e) {
            return AS.NullAS;
        }
    }

    /**
     * Build a copy of the AS_PATH but without prepends
     *
     * @author paag
     */

    public ASPath canonicalPath() {
        ASPath canonical = new ASPath();

        for (int i = 0; i < this.path.size(); i++) {
            AS as = this.path.get(i);
            canonical.add(as);
            i = this.path.lastIndexOf(as);
        }
        return canonical;
    }

    private Vector<AS> prependers;

    /**
     * @return true if the originating AS is prepending
     */
    public boolean hasOriginPrepend() {
        try {
            return this.prependers.contains(this.generator());
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public int compareTo(ASPath aspath) {
        int result = 0;
        if (this.equals(aspath)) {
            return 0;
        }
        if (this.length() != aspath.length()) {
            return this.length() > aspath.length() ? 1 : -1;
        }
        for (int i = 0; i < this.length(); i++) {
            result = this.get(i).compareTo(aspath.get(i));
            if (result == 0) {
                return result;
            }
        }
        return result;
    }

    public AS getOriginating() {
        return path.isEmpty() ? null : path.getLast();
    }

    public List<AS> getTransiting() {
        return path.isEmpty() ? Collections.emptyList() : path.subList(0, path.size() - 1);
    }
}
