package vi_limited;


import java.util.*;

class Node {
    /**
        start index of node in the piece table (in one of buffers)
    **/
    public final int start;

    /**
        length of text in the piece table that belong to this node
    **/
    public final int length;

    /**
        this integer (0 or 1) show that the node is pointing to the original buffer (0)
        or the added buffer(2)
        why i didnt use enum?
        because i can simplyy write : buffers[node.type].get(..)
    **/
    public final int type; // zero for orginal and one for added text

    /**
        it is not neccesary in the first point
        but i added it to help improve speed ot going to specific line (not scrolling)
    **/
    public final int lineCount;

    /**
        an empty node that we have
        we use it in case of deleting empty nodes
        we check if they are mosave ti this or not
    **/
    public static final Node empty = new Node(0, 0, 0, 0);

    /**
        it is same as equals
        but it isnt
        i dodnt want to implement the hashcode but u had to implement equals
        for getting rid of the warning, i named it as "mosavi" instead of equals
        i use it when i want to delete delete empty nodes
    **/
    @SuppressWarnings("unchecked")
    public boolean mosavi(Object o) {
        if (o == null) return false;
        if (! (o instanceof Node )) return false;
        Node other = (Node) o;
        return other.start == this.start &&
               other.length == this.length &&
               other.type == this.type;
    }

    /**
        constructor of the node class
        it fill in all fields (all of them are fianl)
    **/
    public Node(int start, int length, int type, int lineCount) {
        this.start = start;
        this.length = length;
        this.type = type;
        this.lineCount = lineCount;
    }


    /**
        get the empty initial node
        when we do not have any text and original buffe is empty
    **/
    public static Node getInitialNode(int length, String text) {
        int toAddLines = 0;
        for (char c : text.toCharArray() ) {
            if (c == '\n') 
                toAddLines++;
        }
        return new Node(0, length, 0, toAddLines); // contains all of original
    }

    /**
        a method for debugging, we print (and logged ) the node duretly
        so this method will be called
        it return start and lenght and type and linecount ( all info of the node)
    **/
    @Override
    public String toString() {
        return "Node: {start : " + start +
               ", length : " + length +
               ", type : " + type +
               ", wc -l : " + lineCount +
               "}";
    }

    /**
        get information about added texxt
        and split the piece (i mean node) that the new text should be added
        it also count number of lines..!
    **/
    public List<Node> split(int splitPlace, int indexInBuffer, int newTextLen,
                            List<StringBuilder> buffers, int toAddLines) {

        StringBuilder chArr = buffers.get(this.type);

        int part1lines = 0;
        int part2lines = 0;

        for (int i = this.start; i < this.length + this.start ; i++ ) {
            char c = chArr.charAt(i);
            if (c == '\n') {
                if (i < splitPlace)
                    part1lines++;
                else
                    part2lines++;
            }
        }

        int part1len = splitPlace - this.start;
        int part2len = this.length - part1len;
        Logger.log("length of toSplit : " + this.length);

        return List.of(
                   new Node(this.start,   part1len,  this.type, part1lines),
                   new Node(indexInBuffer, newTextLen, 1, toAddLines),      //added text
                   new Node(splitPlace,   part2len,  this.type, part2lines)
               );
    }

}
