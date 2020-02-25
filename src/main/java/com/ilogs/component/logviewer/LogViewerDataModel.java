package com.ilogs.component.logviewer;

import java.io.File;

public interface LogViewerDataModel {

	public boolean isReversed();

	public void setReversed(boolean reversed);

	public int getMaxLines();

	public void setMaxLines(int maxLines);

	public File getLogFile();

	public void setLogFile(File logFile);

	public boolean isEmpty();
}
