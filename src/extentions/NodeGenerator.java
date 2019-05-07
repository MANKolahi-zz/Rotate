package extentions;



public class NodeGenerator {

    private final String name;
    private final nodeCreator nodeCreator;

    public NodeGenerator(String name, extentions.nodeCreator nodeCreator) {
        this.name = name;
        this.nodeCreator = nodeCreator;
    }

    public String getName() {
        return name;
    }

    public extentions.nodeCreator getNodeCreator() {
        return nodeCreator;
    }
}
