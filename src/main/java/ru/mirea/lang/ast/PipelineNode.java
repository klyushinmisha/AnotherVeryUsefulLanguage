package ru.mirea.lang.ast;

import java.util.ArrayList;
import java.util.List;

public class PipelineNode extends ExprNode {
    List<ExprNode> pipeline;

    public PipelineNode() {
        this.pipeline = new ArrayList<>();
    }

    public void addNode(ExprNode node) {
        this.pipeline.add(node);
    }

    public List<ExprNode> getPipeline() {
        return pipeline;
    }
}
