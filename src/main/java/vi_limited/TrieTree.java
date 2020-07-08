package vi_limited;

/**
    out tree class to handle search with good performance
**/
class TrieTree {

    /**
        tedad e horouf e alephba ro negah midaraim
        baraye array ha niaz darim
    **/
    final static int ALPHABET_SIZE = 256;

    TrieTree[] children = new TrieTree[ALPHABET_SIZE];

    List<Integer> indexes;

    /**
        only construcotr without paramter
        search text will be added later
    **/
    public TrieTree(){
        /**
         Create list for indexes of
         suffixes starting from this node
        */
        indexes = new List<Integer>();

        /**
            hame bache ha ro null bezarim
        **/
        for (int i = 0; i < ALPHABET_SIZE; i++)
            children[i] = null;
    }

    /**
        insert function,
        get a string and a index and add it
    **/
    void insert(String toAdd, int index) {

        // Store index in linked list
        indexes.add(index);

        if(toAdd.length() == 0) return; // shart e paye as masalan


        char cIndex = toAdd.charAt(0);

        if (children[cIndex] == null)
            children[cIndex] = new TrieTree();

        children[cIndex].insert(toAdd.substring(1), index + 1);
    }

    /**
        the function that actually search
    **/
    List<Integer> search(String toSearch) {

        // we are done, whole string processed
        // dar vaghe shart e paye as
        if (toSearch.length() == 0)
            return indexes;

        char avvali = toSearch.charAt(0);
        if( children[avvali] == null ) return null;

        String baghie = toSearch.substring(1); // engar head o tail ro joda kardim
        return (children[avvali]).search(baghie);

    }
}
