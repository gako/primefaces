/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.export;

import java.io.IOException;
import java.util.Map;

import javax.el.MethodExpression;
import javax.faces.context.FacesContext;

import org.primefaces.component.treetable.TreeTable;
import org.primefaces.model.TreeNode;

/**
 * Extended Exporter supports TreeTables as well
 */
public abstract class ExtendedExporter extends Exporter {


    public abstract void export(FacesContext facesContext, TreeTable table, String outputFileName, boolean pageOnly,
	    boolean selectionOnly, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor,
	    ExporterOptions options, MethodExpression onTableRendered) throws IOException;



    protected void exportPageOnly(FacesContext context, TreeTable table, Object document) {
	int first = table.getFirst();
	TreeNode root = table.getValue();
	root.setExpanded(true);
	int totalRows = getTreeRowCount(root) - 1;
	int rows = table.getRows();
	if (rows == 0) {
	    rows = totalRows;
	}

	int rowsToExport = first + rows;
	if (rowsToExport > totalRows) {
	    rowsToExport = totalRows;
	}

	for (int rowIndex = first; rowIndex < rowsToExport; rowIndex++) {
	    exportRow(context, table, document, rowIndex);
	}
    }

    protected void exportAll(FacesContext context, TreeTable table, Object document) {
	int first = table.getFirst();
	TreeNode root = table.getValue();
	root.setExpanded(true);
	int rowCount = getTreeRowCount(root) - 1;

	for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
	    exportRow(context, table, document, rowIndex);
	}

	// restore
	table.setFirst(first);

    }

    protected void exportRow(FacesContext context, TreeTable table, Object document, int rowIndex) {
	Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

	Object origVar = requestMap.get(table.getVar());

	// rowIndex +1 because we are not interestedin rootNode
	Object data = traverseTree(table.getValue(), rowIndex + 1);

	requestMap.put(table.getVar(), data);

	preRowExport(table, document);
	exportCells(table, document);
	postRowExport(table, document);

	if (origVar != null) {
	    requestMap.put(table.getVar(), origVar);
	} else {
	    requestMap.remove(table.getVar());
	}
    }

    protected abstract void exportCells(TreeTable table, Object document);

    protected void preRowExport(TreeTable table, Object document) {};

    protected void postRowExport(TreeTable table, Object document) {};

    protected static Object traverseTree(TreeNode node, int rowIndex) {
	return traverseTree(node, new MutableInt(rowIndex));
    }

    /**
     * Traverses a tree and visitis all children until it finds the one with row index i
     *
     * @param node
     * @param rowIndex
     * @return data of found treenode
     */
    protected static Object traverseTree(TreeNode node, MutableInt rowIndex) {

	int index = rowIndex.getValue();
	rowIndex.decrement();
	if (index <= 0) {
	    return node.getData();
	}

	if (node.getChildren() != null) {
	    Object data = null;
	    for (TreeNode childNode : node.getChildren()) {
		data = traverseTree(childNode, rowIndex);
		if (data != null) {
		    break;
		}
	    }
	    return data;
	} else {
	    return null;
	}

    }

    protected static int getTreeRowCount(TreeNode node) {
	int count = 1;
	if (node.getChildren() != null) {
	    for (TreeNode childNode : node.getChildren()) {
		count += getTreeRowCount(childNode);
	    }
	    return count;
	}
	return count;
    }

    private static class MutableInt {

	    private int value;

	    public MutableInt(int value) {
	        super();
	        this.value = value;
	    }

	    public int getValue() {
	        return this.value;
	    }

	    public void decrement() {
	        value--;
	    }
	}

}
