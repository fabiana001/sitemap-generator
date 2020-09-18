package it.kdde.sequentialpatterns.model.fast;

import it.kdde.sequentialpatterns.model.Itemset;
import it.kdde.sequentialpatterns.model.Sequence;
import it.kdde.sequentialpatterns.model.sid.SparseIdList;
import it.kdde.sequentialpatterns.model.tree.ItemsetNode;
import it.kdde.sequentialpatterns.model.tree.ItemsetTree;
import it.kdde.sequentialpatterns.model.tree.SequenceNode;
import it.kdde.sequentialpatterns.model.tree.SequenceTree;
import it.kdde.sequentialpatterns.model.vil.VerticalIdList;
import it.kdde.util.Statistics;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Fast {

    private FastDataset ds;

    private SequenceTree sequenceTree;

    public final Statistics statistics = new Statistics();

    public Fast(FastDataset ds) {
        this.ds = ds;
    }

    /**
     * 1. itemset extension
     * 2. sequence extension
     */
    public void run() {
        statistics.startMemory();
        statistics.startTimeItemset();
        itemsetExtension();
        statistics.endTimeItemset();

        statistics.startTimeSequence();
        sequenceTree = sequenceExtension();
        statistics.endTimeSequence();
        statistics.endMemory();
    }

    public List<SequenceNode> getFrequentSequences() {
        return SequenceTree.visit(sequenceTree);
    }

    private void itemsetExtension() {
        final ItemsetTree tree = new ItemsetTree();
        final ItemsetNode root = tree.getRoot();

        final Queue<ItemsetNode> queue = new LinkedList<>();
        int pos = 0;
        ItemsetNode node;
        for (Map.Entry<String, SparseIdList> entry : ds.getFrequentItemsets().entrySet()) {
            node = tree.addChild(root, new Itemset(entry.getKey()), entry.getValue(), pos++);
            queue.add(node);
        }

        // explore the other levels, expand the tree
        while (!queue.isEmpty()) {
            node = queue.remove();
            itemsetExtension(tree, node);
            queue.addAll(node.getChildren());
        }
    }

    /**
     * @param tree
     * @param n    do the itemset extension on a node. It extends the tree by
     *             adding its children which contain a frequent itemset
     */
    private void itemsetExtension(ItemsetTree tree, ItemsetNode n) {

        //SparseIdList newSparseIdList = new SparseIdList(ds.getNumRows());
        //float support = 0;
        int pos = 0;
        // get the children list of the parent node, which are the brothers of
        // the current node
        List<ItemsetNode> children = n.getParent().getChildren();

        for (int i = n.getPosition() + 1; i < children.size(); i++) {
            ItemsetNode rightBrother = children.get(i);

            //SparseIdListHashMap sil = SparseIdListHashMap.IStep(n.getSil(), rightBrother.getSil());
            SparseIdList sil = n.getSil().IStep(rightBrother.getSil());


            if (sil.getAbsoluteSupport() >= ds.getAbsMinSup()) {
                // create the new sequence as replica
                Itemset newItemset = n.getItemset().clone();
                newItemset.addItem(rightBrother.getItemset().getLast());

                ds.getFrequentItemsets().put(newItemset.concatenate(), sil);
                tree.addChild(n, newItemset, sil, pos);
                pos++;
            }
        }

    }

    private SequenceTree sequenceExtension() {
        sequenceTree = new SequenceTree(ds.getNumRows());

        // create a queue to read the tree
        Queue<SequenceNode> queue = new LinkedList<>();

        Sequence s;
        SequenceNode node;
        for (Map.Entry<String, SparseIdList> entry : ds.getFrequentItemsets().entrySet()) {
            s = new Sequence(new Itemset(entry.getKey().split(" ")));
            VerticalIdList vil = entry.getValue().getStartingVIL();
            node = sequenceTree.addChild(sequenceTree.getRoot(), s, vil, entry.getValue().getAbsoluteSupport());
            queue.add(node);
        }

        // explore the other levels, expand the tree
        while (!queue.isEmpty()) {
            node = queue.remove();
            sequenceExtension(sequenceTree, node);
            queue.addAll(node.getChildren());
        }
        return sequenceTree;

    }

    public SequenceTree getTree(){
        return this.sequenceTree;
        
    }

//    private void sequenceExtension(SequenceTree tree, SequenceNode node) {
//        int count = 0;
//        ListNode[] newPosList;
//        ListNode listNode, listNodeBrother;
//
//        VerticalIdListHashMap vilNode = node.getVerticalIdList();
//        VerticalIdListHashMap vilBrother;
//
//        List<SequenceNode> brothers = node.getParent().getChildren();
//        for (SequenceNode brotherNode : brothers) {
//
//            newPosList = new ListNode[vilNode.getElements().length];
//            vilBrother = brotherNode.getVerticalIdList();
//
//            for (int i = 0; i < vilNode.getElements().length; i++) {
//
//                listNode = vilNode.getElements()[i];
//                listNodeBrother = vilBrother.getElements()[i];
//                // when i found a null element I exit the for
//                if ((listNode == null) || (listNodeBrother == null)) {
//                    continue;
//                }
//
//                // case 1:
//                if ((listNode.getColumn() < listNodeBrother.getColumn())) {
//                    newPosList[i] = listNodeBrother;
//                    count++;
//                    // case 2:
//                } else if ((listNode.getColumn() >= listNodeBrother.getColumn())) {
//                    while ((listNodeBrother != null) && (listNode.getColumn() >= listNodeBrother.getColumn())) {
//                        listNodeBrother = listNodeBrother.next();
//                    }
//                    if (listNodeBrother != null) {
//                        newPosList[i] = listNodeBrother;
//                        count++;
//                    }
//                }
//            }
//            //finally
//            if (count >= ds.getAbsMinSup()) {
//                Sequence sequence = node.getSequence().clone();
//                sequence.add(brotherNode.getSequence().getLastItemset());
//                tree.addChild(node, sequence, new VerticalIdList(newPosList, count), count);
//            }
//            count = 0;
//        }
//    }

    private void sequenceExtension(SequenceTree tree, SequenceNode node) {

        VerticalIdList vilNode = node.getVerticalIdList();
        VerticalIdList vilBrother;

        List<SequenceNode> brothers = node.getParent().getChildren();
        for (SequenceNode brotherNode : brothers) {
            vilBrother = brotherNode.getVerticalIdList();
            VerticalIdList newVil = vilNode.SStep(vilBrother);
            //finally
            if (newVil.getAbsoluteSupport() >= ds.getAbsMinSup()) {
                Sequence sequence = node.getSequence().clone();
                sequence.add(brotherNode.getSequence().getLastItemset());
                tree.addChild(node, sequence, newVil, (int) newVil.getAbsoluteSupport());
            }
        }
    }

    private void writeStatistic(String datasetName, float minSupp, int absMinSup, String statisticsFile) throws IOException {
        statistics.printFrequentSequencesStat("FAST",datasetName, minSupp, absMinSup, statisticsFile);
    }

    private void writePatterns(Path outputFile) throws IOException {

        final BufferedWriter out = Files.newBufferedWriter(outputFile);

        List<SequenceNode> nodes = getFrequentSequences();

        for (SequenceNode node : nodes) {
            out.write(node.toString() + "\n");
        }
        out.flush();
        out.close();
        statistics.setNumFrequentSequences(nodes.size());
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        if (args.length < 3) {
            System.err.println("the need parameters are sequence_file, min_supp, statistics_file");
            System.err.println("sequences.txt 0.2 statistics.txt");
        } else {
            String inputFile = args[0];
            float minSupp = Float.parseFloat(args[1]);
            String statisticsFile = args[2];
            FastDataset.Type datasetType = FastDataset.Type.DENSE;
            if(args.length == 4)
                datasetType =  FastDataset.Type.valueOf(args[3]);

            String outputFile = inputFile + "_" + minSupp + ".txt";
            System.out.println("Start loading the dataset");
            FastDataset ds = FastDataset.fromPrefixspanSource(Paths.get(inputFile), minSupp, datasetType);
            System.out.println("End loading the dataset");

            Fast fast = new Fast(ds);
            System.out.println("Start sequence extraction");
            fast.run();
            System.out.println("End sequence extraction");
            
            for(SequenceNode s:fast.getFrequentSequences()){
                System.out.println(s.getSequence().toString()+" "+s.getVerticalIdList().toString());
                
            }

            //save patterns
            fast.writePatterns(Paths.get(outputFile));
            //print statistics
            fast.writeStatistic(inputFile, minSupp, ds.getAbsMinSup(),statisticsFile);

            //try to write and read a serialized tree
            fast.serializeTree("./serializedTree.ser");

            FileInputStream fin = new FileInputStream("./serializedTree.ser");
            ObjectInputStream ois = new ObjectInputStream(fin);
            SequenceTree newFastTree = (SequenceTree) ois.readObject();
            ois.close();
        }
    }

    public void serializeTree(String outputFile) {
        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(this.getTree());
            oos.close();
            System.out.println("Serialization done");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveOnFile(Path outputFile, String databaseName, Float minSupp, String statisticsFile){
        try {
            writePatterns(outputFile);
            writeStatistic(databaseName,minSupp,ds.getAbsMinSup(),statisticsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}