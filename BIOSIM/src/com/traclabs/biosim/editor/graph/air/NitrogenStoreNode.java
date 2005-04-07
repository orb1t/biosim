package com.traclabs.biosim.editor.graph.air;

import org.tigris.gef.base.Layer;
import org.tigris.gef.presentation.FigNode;

import com.traclabs.biosim.editor.graph.EditorNode;

public class NitrogenStoreNode extends EditorNode{
    public NitrogenStoreNode() {
        setText("NitrogenStore");
    }

    public FigNode makePresentation(Layer lay) {
        FigNitrogenStoreNode node = new FigNitrogenStoreNode();
        node.setOwner(this);
        return node;
    }
}
