package vi_limited;


class TrieTree {

    final static int MAX_CHAR = 256;

    TrieTree[] children = new TrieTree[MAX_CHAR];
    List<Integer> indexes;

    TrieTree() // Constructor
    {
        // Create an empty linked list for indexes of
        // suffixes starting from this node
        indexes = new List<Integer>();

        // Initialize all child pointers as NULL
        for (int i = 0; i < MAX_CHAR; i++)
            children[i] = null;
    }

    // A recursive function to insert a suffix of
    // the text in subtree rooted with this node
    void insert(String s, int index) {

        // Store index in linked list
        indexes.add(index);

        // If string has more characters
        if (s.length() > 0) {

            // Find the first character
            char cIndex = s.charAt(0);

            // If there is no edge for this character,
            // add a new edge
            if (children[cIndex] == null)
                children[cIndex] = new TrieTree();

            // Recur for next suffix
            children[cIndex].insert(s.substring(1),
                                              index + 1);
        }
    }

    // A function to search a pattern in subtree rooted
    // with this node.The function returns pointer to a
    // linked list containing all indexes where pattern
    // is present. The returned indexes are indexes of
    // last characters of matched text.
    List<Integer> search(String s) {

        // If all characters of pattern have been
        // processed,
        if (s.length() == 0)
            return indexes;

        // if there is an edge from the current node of
        // suffix tree, follow the edge.
        if (children[s.charAt(0)] != null)
            return (children[s.charAt(0)]).search(s.substring(1));

        // If there is no edge, pattern doesnt exist in
        // text
        else
            return null;
    }
}
