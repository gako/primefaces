package com.ilogs.component.grid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import org.primefaces.component.column.Column;
import org.primefaces.component.row.Row;
import org.primefaces.component.separator.UISeparator;
import org.primefaces.renderkit.CoreRenderer;

@FacesRenderer(componentFamily = Grid.COMPONENT_FAMILY, rendererType = Grid.DEFAULT_RENDERER)
public class GridRenderer extends CoreRenderer {

    private static final Map<Integer, String> GRID_COLUMN_MAP = new HashMap<Integer, String>();
    static {
	GRID_COLUMN_MAP.put(1, "ui-g-12");
	GRID_COLUMN_MAP.put(2, "ui-g-12 ui-md-6");
	GRID_COLUMN_MAP.put(3, "ui-g-12 ui-md-4");
	GRID_COLUMN_MAP.put(4, "ui-g-12 ui-md-6 ui-lg-3");
	GRID_COLUMN_MAP.put(6, "ui-g-12 ui-md-6 ui-lg-2");
	GRID_COLUMN_MAP.put(12, "ui-g-12 ui-md-2 ui-lg-1");
    }

    private static final Map<Integer, String> FLEX_COLUMN_MAP = new HashMap<Integer, String>();
    static {
	FLEX_COLUMN_MAP.put(1, "p-col-12");
	FLEX_COLUMN_MAP.put(2, "p-col-12 p-md-6");
	FLEX_COLUMN_MAP.put(3, "p-col-12 p-md-4");
	FLEX_COLUMN_MAP.put(4, "p-col-12 p-md-6 p-lg-3");
	FLEX_COLUMN_MAP.put(6, "p-col-12 p-md-6 p-lg-2");
	FLEX_COLUMN_MAP.put(12, "p-col-12 p-md-2 p-lg-1");
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
	Grid grid = (Grid) component;

	encodeGridLayout(context, grid);
    }

    public void encodeGridLayout(FacesContext context, Grid grid) throws IOException {
	ResponseWriter writer = context.getResponseWriter();
	String clientId = grid.getClientId(context);
	String style = grid.getStyle();
	String styleClass = grid.getStyleClass();

	String contentClass = grid.isFlex() ? Grid.FLEX_CONTENT_CLASS : Grid.GRID_CONTENT_CLASS;
	if (grid.isNoPad()) {
	    contentClass +=" ui-g-nopad";
	}

	writer.startElement("div", grid);
	writer.writeAttribute("id", clientId, "id");
	if (style != null) {
	    writer.writeAttribute("style", style, "style");
	}

	if (styleClass != null) {
	    writer.writeAttribute("class", styleClass + " "+contentClass, "styleClass");
	} else {
	    writer.writeAttribute("class", contentClass, "styleClass");
	}

	encodeGridFacet(context, grid, "header", Grid.GRID_HEADER_CLASS);
	encodeGridBody(context, grid);
	encodeGridFacet(context, grid, "footer", Grid.GRID_FOOTER_CLASS);

	writer.endElement("div");
    }

    public void encodeGridFacet(FacesContext context, Grid grid, String facet, String styleClass) throws IOException {
	UIComponent component = grid.getFacet(facet);

	if (component != null && component.isRendered()) {
	    ResponseWriter writer = context.getResponseWriter();

	    writer.startElement("div", null);
	    writer.writeAttribute("class", styleClass + " ui-widget-header", null);
	    component.encodeAll(context);
	    writer.endElement("div");
	}
    }

    public String getColumnClass(Grid grid, int columns) {
	if (grid.isFlex()) {
	    if (FLEX_COLUMN_MAP.containsKey(columns)) {
		return FLEX_COLUMN_MAP.get(columns);
	    } else {
		return "p-col-" + (12 / columns);
	    }
	} else {
	    if (GRID_COLUMN_MAP.containsKey(columns)) {
		return GRID_COLUMN_MAP.get(columns);
	    } else {
		return "ui-g-" + (12 / columns);
	    }
	}
    }

    public String getResponsiveClass(Grid grid, String styleClass) {
	if (grid.isFlex()) {

	    if (styleClass.equals("ui-g-1") || styleClass.equals("p-col-1")) {
		return "p-col-12 p-md-2 p-lg-1";
	    } else if (styleClass.equals("ui-g-2") || styleClass.equals("p-col-2")) {
		return "p-col-12 p-md-6 p-lg-2";
	    } else if (styleClass.equals("ui-g-3") || styleClass.equals("p-col-3")) {
		return "p-col-12 p-md-6 p-lg-3";
	    } else if (styleClass.equals("ui-g-4") || styleClass.equals("p-col-4")) {
		return "p-col-12 p-md-4";
	    } else if (styleClass.equals("ui-g-6") || styleClass.equals("p-col-6")) {
		return "p-col-12 p-md-6";
	    } else {
		return styleClass;
	    }
	} else {
	    if (styleClass.equals("ui-g-1") || styleClass.equals("p-col-1")) {
		return "ui-g-12 ui-md-2 ui-lg-1";
	    } else if (styleClass.equals("ui-g-2") || styleClass.equals("p-col-2")) {
		return "ui-g-12 ui-md-6 ui-lg-2";
	    } else if (styleClass.equals("ui-g-3") || styleClass.equals("p-col-3")) {
		return "ui-g-12 ui-md-6 ui-lg-3";
	    } else if (styleClass.equals("ui-g-4") || styleClass.equals("p-col-4")) {
		return "ui-g-12 ui-md-4";
	    } else if (styleClass.equals("ui-g-6") || styleClass.equals("p-col-6")) {
		return "ui-g-12 ui-md-6";
	    } else {
		return styleClass;
	    }
	}
    }

    private List<UIComponent> getGridChildren(Grid grid) {
	List<UIComponent> children = new ArrayList<UIComponent>();

	for (UIComponent child : grid.getChildren()) {

	    if (!child.isRendered()) {
		continue;
	    }

	    if (child instanceof Row) {

		for (UIComponent rowChild : ((Row) child).getChildren()) {

		    if (!rowChild.isRendered()) {
			continue;
		    }

		    children.add(rowChild);
		}
	    } else {
		children.add(child);
	    }
	}

	return children;
    }

    public void encodeGridBody(FacesContext context, Grid grid) throws IOException {
	ResponseWriter writer = context.getResponseWriter();

	String columnClassesValue = grid.getColumnClasses();
	String[] columnClasses;
	if (columnClassesValue != null) {
	    columnClasses = columnClassesValue == null ? new String[0] : columnClassesValue.split(",");
	} else if (grid.getColumns() > 0) {
	    columnClasses = new String[grid.getColumns()];
	    Arrays.fill(columnClasses, getColumnClass(grid, grid.getColumns()));
	} else {
	    columnClasses = new String[] { getColumnClass(grid, 1) };
	}

	int i = 0;
	for (UIComponent child : getGridChildren(grid)) {

	    // IPCP Feature Add Start if child has columnClass defined use this to always
	    // overwrite cell column class
	    String columnClass;
	    boolean containerDiv = true;

	    Object columnWrapExpression = child.getAttributes().get("columnWrap");

	    // in case of columnwrap move the counter to start of columnClasses
	    if (columnWrapExpression != null) {
		String wrap = columnWrapExpression.toString();
		try {
		    i += Integer.parseInt(wrap);
		} catch (NumberFormatException e) {
		    if (Boolean.parseBoolean(wrap)) {
			i += columnClasses.length - (i % columnClasses.length);
		    }
		}

	    }

	    if (child instanceof UISeparator) {
		containerDiv = false;
	    }

	    Object columnClassExpression = child.getAttributes().get("columnClass");
	    if (columnClassExpression != null) {
		columnClass = columnClassExpression.toString();
		// IPCP Feature Add End
		containerDiv = false;

		columnClass = getResponsiveClass(grid, columnClass);
	    } else {
		columnClass = columnClasses[i % columnClasses.length].trim();
	    }

	    try {

		if (child instanceof Column) {
		    writer.startElement("div", null);
		    writer.writeAttribute("class", columnClass, null);
		    renderChildren(context, child);
		    writer.endElement("div");
		} else {
		    if (containerDiv) {
			writer.startElement("div", null);
			writer.writeAttribute("class", columnClass, null);
			child.encodeAll(context);
			writer.endElement("div");
		    } else {

			ValueExpression childStyleClass = child.getValueExpression("styleClass");
			if (childStyleClass != null) {
			    String childClass = childStyleClass.getValue(context.getELContext()).toString();
			    if (!childClass.contains(columnClass)) {

				ValueExpression styleClassVE = context.getApplication().getExpressionFactory()
					.createValueExpression(context.getELContext(),
						childStyleClass.getExpressionString() + " " + columnClass,
						String.class);

				child.setValueExpression("styleClass", styleClassVE);
			    }
			} else {
			    // if we have styleClass as string attribute keep it and add columnClass to it
			    if (child.getAttributes().get("styleClass") != null) {
				String childClass = child.getAttributes().get("styleClass").toString();
				if (!childClass.contains(columnClass)) {

				    ValueExpression styleClassVE = context.getApplication().getExpressionFactory()
					    .createValueExpression(context.getELContext(),
						    childClass + " " + columnClass, String.class);

				    child.setValueExpression("styleClass", styleClassVE);
				}
			    } else {

				ValueExpression styleClassVE = context.getApplication().getExpressionFactory()
					.createValueExpression(context.getELContext(), columnClass, String.class);
				child.setValueExpression("styleClass", styleClassVE);
			    }
			}

			child.encodeAll(context);
		    }
		}
	    } catch (Exception e) {
		writer.startElement("div", null);
		writer.writeAttribute("class", columnClass, null);
		child.encodeAll(context);
		writer.endElement("div");
	    }
	    i++;
	}

    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
	// Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
	return true;
    }

}
