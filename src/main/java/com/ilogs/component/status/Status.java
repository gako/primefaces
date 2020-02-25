package com.ilogs.component.status;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;

@FacesComponent(value = Status.COMPONENT_TYPE)
public class Status extends UIOutput {

	public static final String COMPONENT_TYPE = "com.ilogs.component.status.Status";
	public static final String COMPONENT_FAMILY = "com.ilogs.component";
	public static final String DEFAULT_RENDERER = "com.ilogs.component.status.StatusRenderer";

	protected enum PropertyKeys {
		icon, style, styleClass, label, escape
	}

	public Status() {
		setRendererType(DEFAULT_RENDERER);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getIcon() {
		return (String) getStateHelper().eval(PropertyKeys.icon, "fa fas fa-circle");
	}

	public void setIcon(String edit) {
		getStateHelper().put(PropertyKeys.icon, edit);
	}

	public Boolean isLabel() {
		return (Boolean) getStateHelper().eval(PropertyKeys.label, Boolean.FALSE);
	}

	public void setLabel(Boolean edit) {
		getStateHelper().put(PropertyKeys.label, edit);
	}

	public Boolean isEscape() {
		return (Boolean) getStateHelper().eval(PropertyKeys.escape, Boolean.FALSE);
	}

	public void setEscape(Boolean edit) {
		getStateHelper().put(PropertyKeys.escape, edit);
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

	@Override
	public String toString() {
		if (getValue() != null) {
			return getValue().toString();
		} else {
			return "";
		}
	}

}
