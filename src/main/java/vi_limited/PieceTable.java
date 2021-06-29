package vi_limited;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.util.*;

/**
    the class which handle keeping al text in memory
    main part of project!
**/
public class PieceTable {

    /**
        2 list for buffers
        one for original buffer
        and one for add buffers
    **/
    public List<StringBuilder> buffers; // original , added

    /**
        the nodes list
        it hand;e adding node and splitting and etc
    **/
    public List<Node> nodes; // list e masalan


    /**
        one instacne of TrieTree to do search stuff
        it is null, if text is modified and tree should me re created
    **/
    public TrieTree searchTree;

    /**
        the main constructor
        it make buffers and first noe!
    **/
    @SuppressWarnings({"unchecked", "rawtypes"})
    public PieceTable() {
        buffers = List.of(
                      new StringBuilder(),
                      new StringBuilder());

        nodes = new LinkedList<Node>();
        nodes.add(Node.getInitialNode(0, ""));

        makeSearchTree();
    }

    /**
        the constructor with fileaddress
        it read all file content and add to original buffer
    **/
    public PieceTable(String fileAdd) {
        this();
        if (fileAdd == null) return;

        String initText = FilesUtil.getAddressContext(fileAdd);
        buffers.get(0).append(initText);

        nodes.clear();
        nodes.add(
            Node.getInitialNode(
                buffers.get(0).length(),
                initText
            )
        );

        updateSearchTree(initText, 0);
    }

    /**
        make a debug friendly string for debugging
        not for prpintg or saving
    **/
    @Override
    @SuppressWarnings("unchecked")
    public String toString() {
        StringBuilder ans = new StringBuilder();

        ans.append("\n---this is pieceTable toString-------------\nnodes : \n");

        for (Node n : nodes) {
            ans.append("\n" + n + "\n nodeText : \n" );
            ans.append(getNodeText(n));
            ans.append("\n\n");
        }
        ans.append("\n----nodes complete------\n");

        ans.append("\nbuffer[0] :\n");
        ans.append(buffers.get(0));

        ans.append("\nbuffer[1] :\n");
        ans.append(buffers.get(1));

        ans.append("\n-----------=======------------------\n");

        return ans.toString();
    }

    /**
        calacualte count of \n  of all text
        by adding linecount pof every node
    **/
    public int getTextLen() {
        return nodes.stream().mapToInt(node -> node.length).sum();
    }

    /**
        get all of text of piece table as an String
        is is costly
        it only called when saving ot searching
        or statistics
    **/
    public String getAllText() {
        StringBuilder ans = new StringBuilder();
        for (Node n : nodes) {
            ans.append(
                getNodeText(n)
            );
        }
        return ans.toString();
    }

    /**
        convert a node to text which is pointing to
    **/
    public String getNodeText(Node node) {
        StringBuilder ans = new StringBuilder();
        for (int i = node.start; i < node.start + node.length; i++ ) {
            ans.append(
                buffers.get(node.type).charAt(i)
            );
        }
        return ans.toString();
    }

    /**
        find location (index) of nth line
        in the node, to use in sublist
    **/
    private int nThLinePostion(Node node, int n) {
        n--;
        if (n == 0)
            return 0;
        for (int i = node.start; i < node.start + node.length; i++) {
            if (n == 0)
                return i;
            if (buffers.get(node.type).charAt(i) == '\n')
                n--;
        }
        return node.start + node.length; // not found
    }

    /**
        get a part of text
        from the line : fromLine
        to line : fromLine + height
        it is used for updating screen
        it is not explicit to be height line
        because screen class can ignore longer text
    **/
    public String getText(int fromLine, int height) { // TODO some bugs
        if (fromLine < 0)
            fromLine = 0;
        int nodeInd = 0;

        while (nodes.get(nodeInd).lineCount < fromLine && fromLine > 0) {
            fromLine -= nodes.get(nodeInd).lineCount;
            nodeInd++;
            if (nodeInd >= nodes.size())
                break;
        }

        StringBuilder ans = new StringBuilder();

        for (int i = nodeInd; i < nodes.size(); i++) {
            Node currNode = nodes.get(i);
            ans.append(
                getNodeText(currNode).substring(
                    nThLinePostion(currNode, fromLine)
                    , ' ')
            );
            height -= currNode.lineCount;
            if (height <= 0)
                break;
        }

        return ans.toString();

    }

    /**
        addting a String tp the text in the location of iterator
    **/
    public void add(String toAdd, PTIter iter) {
        updateSearchTree(
            toAdd,
            iter.indexInNode + iter.currentNode.start // starting index
        );

        int toAddLines = ETCUtil.charCounter(toAdd, '\n');

        int beginInBuffer = buffers.get(1).length();
        buffers.get(1).append(toAdd);

        int newTextLen = toAdd.length();
        int splitIndex = iter.currentNode.start + iter.indexInNode;
        Logger.log("split index(should be 0) : " + splitIndex);

        Node toSplit = iter.currentNode;
        List<Node> threeNewNodes = toSplit.split(splitIndex, beginInBuffer, newTextLen, buffers, toAddLines);

        nodes = IntStream.range(0,nodes.size())
            .mapToObj(i-> i == iter.currentNodeIndex ? threeNewNodes : List.of(nodes.get(i)))
            .flatMap(a -> a.stream())
            .collect(Collectors.toList());
        //nodes.replaceOneWithThree(
        //    iter.currentNodeIndex,
        //    threeNewNodes
        //);

        iter.currentNode = threeNewNodes.get(2); /// bad az matn e insert shode
        iter.currentNodeIndex = iter.currentNodeIndex + 2; // 2
        iter.indexInNode = 0; // aval e oun node
        iter.currentLine = countOfLinesBeforeNode(iter.currentNodeIndex);

        //Logger.log("\niter is :\n"+iter);

    }

    private int countOfLinesBeforeNode(int index) {
        int sum = 0;
        for (int i = 0; i < index; i++) {
            sum += nodes.get(i).lineCount;
        }
        return sum;
    }


    /**
        calcualte number of lines based on linecount in every node
    **/
    public int linesCount() {
        int ans = 1;
        for (Node n : nodes)
            ans += n.lineCount;
        return ans;
    }

    /**
        it returns a text in string
        less than 10 lines
        which contains statisticsabout text
        it is effiecient because it calcalte all test just one time
        it also split text just one time
        finding 10 longest and 10 shortest is O(n)
        it implemented in ETCUtils
    **/
    public String getStatistics() {
        String allText = getAllText();

        StringBuilder ans = new StringBuilder();

        ans.append(
            "number of words:" + ETCUtil.wordsCount(allText) + " and "
        );

        ans.append(
            "number of lines:" + linesCount() + "\n"
        );

        String[] allTextSplited = allText.split("\\s+");

        ans.append("ten shortest words: \n");
        String[] smallWords = ETCUtil.tenShortWords(allTextSplited);
        for (int i = 0; i < 10; i++) {
            String word = smallWords[i];
            if (word != null)
                ans.append(word + "\n");
        }

        ans.append("ten longest words: \n");
        String[] longWords = ETCUtil.tenLongWords(allTextSplited);
        for (int i = 0; i < 10; i++) {
            String word = longWords[i];
            if (word != null)
                ans.append(word + "\n");
        }

        return ans.toString();
    }

    private void updateSearchTree(String newText, int startIndex) {
        this.searchTree.update(newText, startIndex);
      //  for (int i = 0; i < newText.length(); i++) {
      //      searchTree.insert(newText.substring(i), i + startIndex);
      //  }
    }

    private void makeSearchTree() {
        searchTree = new TrieTree();
    }

    public String search(String toSearch) {
        List<Integer> result = searchTree.search(toSearch);
        if (result == null)
            return "nothing found dont worry!\n";

        StringBuilder ans = new StringBuilder();
        ans.append("searching for " + toSearch + "\n");
        ans.append("number of finds: " + result.size() + "\n");
        int len = result.size();
        for (int i = 0; i < len; i++) {
            ans.append(
                "found that on line: " +
                charIndToLine(result.get(i))
                + "\n"
            );
        }

        return ans.toString();
    }

    private int charIndToLine(int index) {
        int index_bpk = index;
        int line = 1;

        for (Node n : nodes) {
            if (n.length <= index) {
                index -= n.length;
                line += n.lineCount;
            } else {
                for (int i = 0; i <= index_bpk; i++) {
                    if (buffers.get(n.type).charAt(i) == '\n')
                        line++;
                }
                break;
            }

        }

        return line;
    }


}
