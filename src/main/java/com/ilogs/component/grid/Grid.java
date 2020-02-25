package com.ilogs.component.grid;

/*
 * Generated, Do Not Modify
 */
/*
 * Copyright 2009-2013 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javax.faces.component.FacesComponent;
import javax.faces.component.UIPanel;

@FacesComponent(value = Grid.COMPONENT_TYPE)
public class Grid extends UIPanel {

	public static final String COMPONENT_TYPE = "com.ilogs.component.grid.Grid";
	public static final String COMPONENT_FAMILY = "com.ilogs.component";
	public static final String DEFAULT_RENDERER = "com.ilogs.component.grid.GridRenderer";

	protected enum PropertyKeys {

		columns, style, styleClass, columnClasses, role, noPad, flex;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {
		}

		@Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
		}
	}

	public Grid() {
		setRendererType(DEFAULT_RENDERER);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public boolean isNoPad() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.noPad, Boolean.FALSE);
	}

	public void setNoPad(boolean _nopad) {
		getStateHelper().put(PropertyKeys.noPad, _nopad);
	}

	public boolean isFlex() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.flex, Boolean.FALSE);
	}

	public void setFlex(boolean _flex) {
		getStateHelper().put(PropertyKeys.flex, _flex);
	}

	public int getColumns() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.columns, 0);
	}

	public void setColumns(int _columns) {
		getStateHelper().put(PropertyKeys.columns, _columns);
	}

	public java.lang.String getStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
	}

	public void setStyle(java.lang.String _style) {
		getStateHelper().put(PropertyKeys.style, _style);
	}

	public java.lang.String getStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}

	public void setStyleClass(java.lang.String _styleClass) {
		getStateHelper().put(PropertyKeys.styleClass, _styleClass);
	}

	public java.lang.String getColumnClasses() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.columnClasses, null);
	}

	public void setColumnClasses(java.lang.String _columnClasses) {
		getStateHelper().put(PropertyKeys.columnClasses, _columnClasses);
	}

	public java.lang.String getRole() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.role, "grid");
	}

	public void setRole(java.lang.String _role) {
		getStateHelper().put(PropertyKeys.role, _role);
	}

	public static final String FLEX_CONTENT_CLASS = "ui-grid p-grid";

	public static final String GRID_CONTENT_CLASS = "ui-grid ui-g";
	public final static String GRID_HEADER_CLASS = "ui-grid-header";
	public final static String GRID_FOOTER_CLASS = "ui-grid-footer";
	public static final String EVEN_ROW_CLASS = "ui-g-even";
	public static final String ODD_ROW_CLASS = "ui-g-odd";
}