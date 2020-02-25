package com.ilogs.component.logviewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.faces.application.FacesMessage;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.facelets.ComponentConfig;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;



@FacesComponent("com.ilogs.component.logviewer.LogViewerComponent")
public class LogViewerComponent extends UINamingContainer {

	public static int DEFAULT_MAX_LINES = 100000;

	enum PropertyKeys {
		collapsed, controls, reversed, maxLines, file, model
	}

	public enum Level {
		ALL, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF
	}

	public LogViewerComponent() {

	}

	public LogViewerComponent(ComponentConfig componentConfig) {

	}

	public LogViewerDataModel getModel() {
		LogViewerDataModel logModel = (LogViewerDataModel) getStateHelper().eval(PropertyKeys.model, null);

		if (logModel != null) {
			logModel.setLogFile(getLogFile());
			logModel.setMaxLines(getMaxLines());
			logModel.setReversed(isReversed());
		}
		return logModel;
	}

	public void setModel(LogViewerDataModel model) {
		getStateHelper().put(PropertyKeys.model, model);
	}

	protected File getLogFile() {
		return getFile();
	}

	/**
	 * Convert the string passed as argument to a level. If the conversion fails, then this method returns the value of <code>defaultLevel</code>.
	 */
	protected static Level toLevel(String sArg, Level defaultLevel) {
		if (sArg == null) {
			return defaultLevel;
		}

		String s = sArg.toUpperCase();

		if (s.equals("ALL")) {
			return Level.ALL;
		}
		if (s.equals("DEBUG")) {
			return Level.DEBUG;
		}
		if (s.equals("INFO")) {
			return Level.INFO;
		}
		if (s.equals("WARN") || s.equals("WARNING")) {
			return Level.WARN;
		}
		if (s.equals("ERROR")) {
			return Level.ERROR;
		}
		if (s.equals("FATAL") || s.equals("SEVERE")) {
			return Level.FATAL;
		}
		if (s.equals("OFF")) {
			return Level.OFF;
		}
		if (s.equals("TRACE")) {
			return Level.TRACE;
		}
		//
		// For Turkish i problem, see bug 40937
		//
		if (s.equals("\u0130NFO")) {
			return Level.INFO;
		}
		return defaultLevel;
	}

	public boolean filterByLevel(Object value, Object filter) {

		String filterString = (filter == null) ? null : filter.toString().trim().toUpperCase();
		if (filterString == null || filterString.equals("")) {
			return true;
		}
		if (value == null) {
			return false;
		}

		Level level = toLevel(value.toString(), Level.ALL);
		Level filterLevel = toLevel(filterString, Level.ALL);

		return level.ordinal() >= (filterLevel.ordinal());
	}

	public int sortByLevel(Object v1, Object v2) {
		if (v1 instanceof Level && v2 instanceof Level) {
			Level lvl1 = (Level) v1;
			Level lvl2 = (Level) v2;

			return lvl1.ordinal() - lvl2.ordinal();
		} else {
			final Comparable<Object> comparable = (Comparable<Object>) v1;
			return comparable.compareTo(v2);
		}
	}

	public StreamedContent downloadLogFile() throws FileNotFoundException {
		try {
			StreamedContent file = null;
			if (getLogFile() != null) {
				FileInputStream stream = new FileInputStream(getLogFile());
				file = new DefaultStreamedContent(stream, null, getLogFile().getName());
			}
			return file;
		} catch (FileNotFoundException e) {
			if (FacesContext.getCurrentInstance() != null) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("File not found", getLogFile().getName()));
				return null;
			} else {
				throw e;
			}
		}
	}

	public File getFile() {
		return (File) getStateHelper().eval(PropertyKeys.file, null);
	}

	public void setFile(File value) {
		getStateHelper().put(PropertyKeys.file, value);
		getModel().setLogFile(value);
	}

	public boolean isControls() {
		return (Boolean) getStateHelper().eval(PropertyKeys.controls, Boolean.TRUE);
	}

	public void setControls(boolean value) {
		getStateHelper().put(PropertyKeys.controls, value);
	}

	public boolean isCollapsed() {
		return (Boolean) getStateHelper().eval(PropertyKeys.collapsed, Boolean.FALSE);
	}

	public void setCollapsed(boolean collapsed) {
		getStateHelper().put(PropertyKeys.collapsed, collapsed);
	}

	public boolean isReversed() {
		return (Boolean) getStateHelper().eval(PropertyKeys.reversed, Boolean.TRUE);
	}

	public void setReversed(boolean value) {
		getStateHelper().put(PropertyKeys.reversed, value);

		getModel().setReversed(value);
	}

	public int getMaxLines() {
		return (Integer) getStateHelper().eval(PropertyKeys.maxLines, DEFAULT_MAX_LINES);
	}

	public void setMaxLines(int rows) {
		getStateHelper().put(PropertyKeys.maxLines, rows);

		getModel().setMaxLines(rows);
	}

	public void toggle(ActionEvent e) {
		setCollapsed(!isCollapsed());
	}

	public boolean isEmpty() {
		return getModel().isEmpty();
	}

}
