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

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;

public class ExcelExporter extends ExtendedExporter {

    protected CellStyle cellStyle;
    protected CellStyle facetStyle;

    @Override
    public void export(FacesContext context, DataTable table, String filename, boolean pageOnly, boolean selectionOnly, String encodingType,
                       MethodExpression preProcessor, MethodExpression postProcessor, ExporterOptions options,
                       MethodExpression onTableRender) throws IOException {

        Workbook wb = createWorkBook();
        String sheetName = getSheetName(context, table);
        if (sheetName == null) {
            sheetName = table.getId();
        }

        sheetName = WorkbookUtil.createSafeSheetName(sheetName);
        if (sheetName.equals("empty") || sheetName.equals("null")) {
            sheetName = "Sheet";
        }

        Sheet sheet = createSheet(wb, sheetName);

        if (preProcessor != null) {
            preProcessor.invoke(context.getELContext(), new Object[]{wb});
        }

        applyOptions(wb, table, sheet, options);
        exportTable(context, table, sheet, pageOnly, selectionOnly);

        for (int i = 0; i < table.getColumnsCount(); i++) {
            sheet.autoSizeColumn((short) i);
        }

        if (postProcessor != null) {
            postProcessor.invoke(context.getELContext(), new Object[]{wb});
        }

        writeExcelToResponse(context.getExternalContext(), wb, filename);
    }

    @Override
    public void export(FacesContext context, TreeTable table, String filename, boolean pageOnly, boolean selectionOnly,
	    String encodingType, MethodExpression preProcessor, MethodExpression postProcessor, ExporterOptions options,
	    MethodExpression onTableRendered) throws IOException {
	Workbook wb = createWorkBook();
	String sheetName = getSheetName(context, table);
	if (sheetName == null) {
	    sheetName = table.getId();
	}

	sheetName = WorkbookUtil.createSafeSheetName(sheetName);
	if (sheetName.equals("empty") || sheetName.equals("null")) {
	    sheetName = "Sheet";
	}

	Sheet sheet = wb.createSheet(sheetName);

	if (preProcessor != null) {
	    preProcessor.invoke(context.getELContext(), new Object[] { wb });
	}

	applyOptions(wb, null, sheet, options);
	exportTable(context, table, sheet, pageOnly, selectionOnly);

	for (int i = 0; i < table.getColumnsCount(); i++) {
	    sheet.autoSizeColumn((short) i);
	}

	if (postProcessor != null) {
	    postProcessor.invoke(context.getELContext(), new Object[] { wb });
	}

	writeExcelToResponse(context.getExternalContext(), wb, filename);

    }


    @Override
    public void export(FacesContext context, String filename, List<DataTable> tables, boolean pageOnly, boolean selectionOnly,
                       String encodingType, MethodExpression preProcessor, MethodExpression postProcessor, ExporterOptions options,
                       MethodExpression onTableRender) throws IOException {

        Workbook wb = createWorkBook();

        if (preProcessor != null) {
            preProcessor.invoke(context.getELContext(), new Object[]{wb});
        }

        for (int i = 0; i < tables.size(); i++) {
            DataTable table = tables.get(i);
            String sheetName = getSheetName(context, table);
            if (sheetName == null) {
                sheetName = table.getId();
            }

            sheetName = WorkbookUtil.createSafeSheetName(sheetName);
            if (sheetName.equals("empty") || sheetName.equals("null")) {
                sheetName = "Sheet" + String.valueOf(i + 1);
            }

            Sheet sheet = createSheet(wb, sheetName);
            applyOptions(wb, table, sheet, options);
            exportTable(context, table, sheet, pageOnly, selectionOnly);

            for (int j = 0; j < table.getColumnsCount(); j++) {
                sheet.autoSizeColumn((short) j);
            }
        }

        if (postProcessor != null) {
            postProcessor.invoke(context.getELContext(), new Object[]{wb});
        }

        writeExcelToResponse(context.getExternalContext(), wb, filename);
    }

    @Override
    public void export(FacesContext context, List<String> clientIds, String filename, boolean pageOnly, boolean selectionOnly,
                       String encodingType, MethodExpression preProcessor, MethodExpression postProcessor, ExporterOptions options,
                       MethodExpression onTableRender) throws IOException {

        Workbook wb = createWorkBook();

        if (preProcessor != null) {
            preProcessor.invoke(context.getELContext(), new Object[]{wb});
        }

        VisitContext visitContext = VisitContext.createVisitContext(context, clientIds, null);
        VisitCallback visitCallback = new ExcelExportVisitCallback(this, wb, pageOnly, selectionOnly);
        context.getViewRoot().visitTree(visitContext, visitCallback);

        if (postProcessor != null) {
            postProcessor.invoke(context.getELContext(), new Object[]{wb});
        }

        writeExcelToResponse(context.getExternalContext(), wb, filename);
    }

    @Override
    protected void exportCells(DataTable table, Object document) {
        Sheet sheet = (Sheet) document;
        int sheetRowIndex = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(sheetRowIndex);

        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            if (col.isRendered() && col.isExportable()) {
                addColumnValue(row, col.getChildren(), col);
            }
        }
    }

    @Override
    protected void exportCells(TreeTable table, Object document) {
	Sheet sheet = (Sheet) document;
	int sheetRowIndex = sheet.getLastRowNum() + 1;
	Row row = sheet.createRow(sheetRowIndex);

	for (UIColumn col : table.getColumns()) {
	    if (col instanceof DynamicColumn) {
		((DynamicColumn) col).applyStatelessModel();
	    }

	    if (col.isRendered() && col.isExportable()) {
		addColumnValue(row, col.getChildren(), col);
	    }
	}
    }

    protected void addColumnFacets(DataTable table, Sheet sheet, Exporter.ColumnType columnType) {
	addColumnFacets(table.getColumns(), sheet, columnType);
    }

    protected void addColumnFacets(TreeTable table, Sheet sheet, Exporter.ColumnType columnType) {
	addColumnFacets(table.getColumns(), sheet, columnType);
    }

    protected void addColumnFacets(List<UIColumn> columns, Sheet sheet, Exporter.ColumnType columnType) {
        int sheetRowIndex = columnType.equals(Exporter.ColumnType.HEADER) ? 0 : (sheet.getLastRowNum() + 1);
        Row rowHeader = sheet.createRow(sheetRowIndex);

        for (UIColumn col : columns) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            if (col.isRendered() && col.isExportable()) {
                UIComponent facet = col.getFacet(columnType.facet());
                String textValue;
                switch (columnType) {
                    case HEADER:
                        textValue = (col.getExportHeaderValue() != null) ? col.getExportHeaderValue() : col.getHeaderText();
                        break;

                    case FOOTER:
                        textValue = (col.getExportFooterValue() != null) ? col.getExportFooterValue() : col.getFooterText();
                        break;

                    default:
                        textValue = null;
                        break;
                }

                if (textValue != null) {
                    addColumnValue(rowHeader, textValue);
                }
                else if (facet != null) {
                    addColumnValue(rowHeader, facet);
                }
                else {
                    addColumnValue(rowHeader, "");
                }
            }
        }
    }

    protected void addColumnValue(Row row, UIComponent component) {
        String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);
        addColumnValue(row, value);
    }

    protected void addColumnValue(Row row, String value) {
        int cellIndex = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
        Cell cell = row.createCell(cellIndex);

        cell.setCellValue(createRichTextString(value));

        if (facetStyle != null) {
            cell.setCellStyle(facetStyle);
        }
    }

    protected void addColumnValue(Row row, List<UIComponent> components, UIColumn column) {
        int cellIndex = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
        Cell cell = row.createCell(cellIndex);
        FacesContext context = FacesContext.getCurrentInstance();

        if (column.getExportFunction() != null) {
            cell.setCellValue(createRichTextString(exportColumnByFunction(context, column)));
        }
        else {
            StringBuilder builder = new StringBuilder();
            for (UIComponent component : components) {
                if (component.isRendered()) {
                    String value = exportValue(context, component);

                    if (value != null) {
                        builder.append(value);
                    }
                }
            }

            cell.setCellValue(createRichTextString(builder.toString()));
        }

        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
    }

    protected RichTextString createRichTextString(String value) {
        return new HSSFRichTextString(value);
    }

    protected Workbook createWorkBook() {
        return new HSSFWorkbook();
    }

    protected Sheet createSheet(Workbook wb, String sheetName) {
        return wb.createSheet(sheetName);
    }

    protected void writeExcelToResponse(ExternalContext externalContext, Workbook generatedExcel, String filename) throws IOException {
        externalContext.setResponseContentType(getContentType());
        externalContext.setResponseHeader("Expires", "0");
        externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        externalContext.setResponseHeader("Pragma", "public");
        externalContext.setResponseHeader("Content-disposition", getContentDisposition(filename));
        externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", Collections.<String, Object>emptyMap());

        OutputStream out = externalContext.getResponseOutputStream();
        generatedExcel.write(out);
    }

    protected String getContentType() {
        return "application/vnd.ms-excel";
    }

    protected String getContentDisposition(String filename) {
        return ComponentUtils.createContentDisposition("attachment", filename + ".xls");
    }

    public void exportTable(FacesContext context, DataTable table, Sheet sheet, boolean pageOnly, boolean selectionOnly) {
        addColumnFacets(table, sheet, Exporter.ColumnType.HEADER);

        if (pageOnly) {
            exportPageOnly(context, table, sheet);
        }
        else if (selectionOnly) {
            exportSelectionOnly(context, table, sheet);
        }
        else {
            exportAll(context, table, sheet);
        }

        if (table.hasFooterColumn()) {
            addColumnFacets(table, sheet, Exporter.ColumnType.FOOTER);
        }

        table.setRowIndex(-1);
    }

    public void exportTable(FacesContext context, TreeTable table, Sheet sheet, boolean pageOnly, boolean selectionOnly) {
        addColumnFacets(table, sheet, Exporter.ColumnType.HEADER);

        if (pageOnly) {
            exportPageOnly(context, table, sheet);
        }
        else if (selectionOnly) {
            throw new IllegalArgumentException("Selection Export not supported for TreeTable");
        }
        else {
            exportAll(context, table, sheet);
        }

        if (table.hasFooterColumn()) {
            addColumnFacets(table, sheet, Exporter.ColumnType.FOOTER);
        }

    }

    protected void applyOptions(Workbook wb, DataTable table, Sheet sheet, ExporterOptions options) {
        facetStyle = wb.createCellStyle();
        facetStyle.setAlignment(HorizontalAlignment.CENTER);
        facetStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        facetStyle.setWrapText(true);
        applyFacetOptions(wb, options, facetStyle);

        cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        applyCellOptions(wb, options, cellStyle);

        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
        sheet.setPrintGridlines(true);
    }

    protected void applyFacetOptions(Workbook wb, ExporterOptions options, CellStyle facetStyle) {
        Font facetFont = wb.createFont();

        if (options != null) {
            String facetFontStyle = options.getFacetFontStyle();
            if (facetFontStyle != null) {
                if (facetFontStyle.equalsIgnoreCase("BOLD")) {
                    facetFont.setBold(true);
                }
                if (facetFontStyle.equalsIgnoreCase("ITALIC")) {
                    facetFont.setItalic(true);
                }
            }

            HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();
            Color color = null;

            String facetBackground = options.getFacetBgColor();
            if (facetBackground != null) {
                color = Color.decode(facetBackground);
                HSSFColor backgroundColor = palette.findSimilarColor(color.getRed(), color.getGreen(), color.getBlue());
                ((HSSFCellStyle) facetStyle).setFillForegroundColor(backgroundColor.getIndex());
                facetStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }

            String facetFontColor = options.getFacetFontColor();
            if (facetFontColor != null) {
                color = Color.decode(facetFontColor);
                HSSFColor facetColor = palette.findSimilarColor(color.getRed(), color.getGreen(), color.getBlue());
                ((HSSFFont) facetFont).setColor(facetColor.getIndex());
            }

            String facetFontSize = options.getFacetFontSize();
            if (facetFontSize != null) {
                facetFont.setFontHeightInPoints(Short.valueOf(facetFontSize));
            }
        }

        facetStyle.setFont(facetFont);
    }

    protected void applyCellOptions(Workbook wb, ExporterOptions options, CellStyle cellStyle) {
        Font cellFont = wb.createFont();

        if (options != null) {
            String cellFontColor = options.getCellFontColor();
            if (cellFontColor != null) {
                HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();
                Color color = Color.decode(cellFontColor);
                HSSFColor cellColor = palette.findSimilarColor(color.getRed(), color.getGreen(), color.getBlue());
                ((HSSFFont) cellFont).setColor(cellColor.getIndex());
            }

            String cellFontSize = options.getCellFontSize();
            if (cellFontSize != null) {
                cellFont.setFontHeightInPoints(Short.valueOf(cellFontSize));
            }

            String cellFontStyle = options.getCellFontStyle();
            if (cellFontStyle != null) {
                if (cellFontStyle.equalsIgnoreCase("BOLD")) {
                    cellFont.setBold(true);
                }
                if (cellFontStyle.equalsIgnoreCase("ITALIC")) {
                    cellFont.setItalic(true);
                }
            }
        }

        cellStyle.setFillPattern(FillPatternType.NO_FILL);
        cellStyle.setFont(cellFont);
    }


}