package com.ilogs.component.field;

import javax.faces.component.FacesComponent;

import org.primefaces.component.outputpanel.OutputPanel;

@FacesComponent(value = Field.COMPONENT_TYPE)
public class Field extends OutputPanel {

	public static final String COMPONENT_TYPE = "com.ilogs.component.field.Field";
	public static final String COMPONENT_FAMILY = "com.ilogs.component";
	public static final String DEFAULT_RENDERER = "com.ilogs.component.field.FieldRenderer";

	protected enum PropertyKeys {
		label, icon, iconPos, columnClass, disabled, readonly, editable, forLabel
	}

	public Field() {
		setRendererType(DEFAULT_RENDERER);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getFor() {
		return (String) getStateHelper().eval(PropertyKeys.forLabel, null);
	}

	public void setFor(String edit) {
		getStateHelper().put(PropertyKeys.forLabel, edit);
	}

	public String getLabel() {
		return (String) getStateHelper().eval(PropertyKeys.label, null);
	}

	public void setLabel(String edit) {
		getStateHelper().put(PropertyKeys.label, edit);
	}

	public String getIcon() {
		return (String) getStateHelper().eval(PropertyKeys.icon, null);
	}

	public void setIcon(String edit) {
		getStateHelper().put(PropertyKeys.icon, edit);
	}

	public String getIconPos() {
		return (String) getStateHelper().eval(PropertyKeys.iconPos, "left");
	}

	public void setIconPos(String edit) {
		getStateHelper().put(PropertyKeys.iconPos, edit);
	}

	public String getColumnClass() {
		return (String) getStateHelper().eval(PropertyKeys.columnClass, null);
	}

	public void setColumnClass(String edit) {
		getStateHelper().put(PropertyKeys.columnClass, edit);
	}

	/**
	 * <p>
	 * Return the value of the <code>disabled</code> property.
	 * </p>
	 * <p>
	 * Contents: Flag indicating that this element must never receive focus or be included in a subsequent submit.
	 */
	public boolean isDisabled() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
	}

	/**
	 * <p>
	 * Set the value of the <code>disabled</code> property.
	 * </p>
	 */
	public void setDisabled(boolean disabled) {
		getStateHelper().put(PropertyKeys.disabled, disabled);
	}

	public boolean isReadonly() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.readonly, false);
	}

	public void setReadonly(boolean disabled) {
		getStateHelper().put(PropertyKeys.readonly, disabled);
	}

	public boolean isEditable() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.editable, false);
	}

	public void setEditable(boolean disabled) {
		getStateHelper().put(PropertyKeys.editable, disabled);
	}
}
