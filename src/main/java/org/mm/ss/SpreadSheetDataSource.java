package org.mm.ss;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import org.mm.core.DataSource;
import org.mm.exceptions.MappingMasterException;
import org.mm.parser.MappingMasterParserConstants;
import org.mm.parser.node.ReferenceNode;
import org.mm.parser.node.SourceSpecificationNode;
import org.mm.renderer.InternalRendererException;
import org.mm.renderer.RendererException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpreadSheetDataSource implements DataSource, MappingMasterParserConstants
{
  private final Workbook workbook;
  private Optional<SpreadsheetLocation> currentLocation;

  public SpreadSheetDataSource(Workbook workbook) throws MappingMasterException
  {
    this.workbook = workbook;
    this.currentLocation = Optional.empty();
  }

  public void setCurrentLocation(SpreadsheetLocation currentLocation)
  {
    this.currentLocation = Optional.of(currentLocation);
  }

  public Optional<SpreadsheetLocation> getCurrentLocation()
  {
    return this.currentLocation;
  }

  public boolean hasCurrentLocation()
  {
    return this.currentLocation != null;
  }

  public Workbook getWorkbook()
  {
    return this.workbook;
  }

  public boolean hasWorkbook()
  {
    return this.workbook != null;
  }

  public List<Sheet> getSheets()
  {
    if (hasWorkbook())
      return Arrays.asList(this.workbook.getSheets());
    else
      return Collections.emptyList();
  }

  public List<String> getSubSourceNames()
  {
    if (hasWorkbook())
      return Arrays.asList(this.workbook.getSheetNames());
    else
      return Collections.emptyList();
  }

  public String getLocationValue(SpreadsheetLocation location, ReferenceNode referenceNode) throws RendererException
  {
    String locationValue = getLocationValue(location);

    if (referenceNode.getActualShiftDirective() != MM_NO_SHIFT)
      locationValue = getLocationValueWithShifting(location, referenceNode);

    return locationValue;
  }

  public String getLocationValue(SpreadsheetLocation location) throws RendererException
  {
    int columnNumber = location.getColumnNumber();
    int rowNumber = location.getRowNumber();
    Sheet sheet = this.workbook.getSheet(location.getSheetName());
    Cell cell;


    try {
      cell = sheet.getCell(columnNumber - 1, rowNumber - 1);
    } catch (Exception e) {
      throw new RendererException(
        "error accessing sheet " + location.getSheetName() + " at location " + location + ": " + e.getMessage());
    }

    if (cell.getType() == CellType.EMPTY)
      return null;
    else
      return cell.getContents();
  }

  public String getLocationValueWithShifting(SpreadsheetLocation location, ReferenceNode referenceNode)
    throws RendererException
  {
    String shiftedLocationValue = getLocationValue(location);
    Sheet sheet = this.workbook.getSheet(location.getSheetName());

    if (shiftedLocationValue == null || shiftedLocationValue.isEmpty()) {
      if (referenceNode.getActualShiftDirective() == MM_SHIFT_LEFT) {
        for (int currentColumn = location.getColumnNumber() - 1; currentColumn >= 1; currentColumn--) {
          shiftedLocationValue = getLocationValue(
            new SpreadsheetLocation(location.getSheetName(), currentColumn, location.getRowNumber()));
          if (shiftedLocationValue != null)
            return shiftedLocationValue;
        }
      } else if (referenceNode.getActualShiftDirective() == MM_SHIFT_RIGHT) {
        for (int currentColumn = location.getColumnNumber() + 1;
             currentColumn <= sheet.getColumns(); currentColumn++) {
          shiftedLocationValue = getLocationValue(
            new SpreadsheetLocation(location.getSheetName(), currentColumn, location.getRowNumber()));
          if (shiftedLocationValue != null)
            return shiftedLocationValue;
        }
      } else if (referenceNode.getActualShiftDirective() == MM_SHIFT_DOWN) {
        for (int currentRow = location.getRowNumber() + 1; currentRow <= sheet.getRows(); currentRow++) {
          shiftedLocationValue = getLocationValue(
            new SpreadsheetLocation(location.getSheetName(), location.getColumnNumber(), currentRow));
          if (shiftedLocationValue != null)
            return shiftedLocationValue;
        }
      } else if (referenceNode.getActualShiftDirective() == MM_SHIFT_UP) {
        for (int currentRow = location.getRowNumber() - 1; currentRow >= 1; currentRow--) {
          shiftedLocationValue = getLocationValue(
            new SpreadsheetLocation(location.getSheetName(), location.getColumnNumber(), currentRow));
          if (shiftedLocationValue != null)
            return shiftedLocationValue;
        }
      }
      throw new InternalRendererException("unknown shift setting " + referenceNode.getActualShiftDirective());
    } else {
      referenceNode.setShiftedLocation(location);
      return shiftedLocationValue;
    }
  }

  public SpreadsheetLocation resolveLocation(SourceSpecificationNode sourceSpecification) throws RendererException
  {
    Pattern p = Pattern.compile("(\\*|[a-zA-Z]+)(\\*|[0-9]+)"); // ( \* | [a-zA-z]+ ) ( \* | [0-9]+ )
    Matcher m = p.matcher(sourceSpecification.getLocation());
    Sheet sheet;
    SpreadsheetLocation resolvedLocation;

    if (!this.currentLocation.isPresent())
      throw new RendererException("current location not set");

    if (sourceSpecification.hasSource()) {
      String sheetName = sourceSpecification.getSource();

      if (!hasWorkbook())
        throw new RendererException("sheet name " + sheetName + " specified but there is no active workbook");

      sheet = getWorkbook().getSheet(sheetName);

      if (sheet == null)
        throw new RendererException("invalid sheet name " + sheetName);
    } else
      sheet = getWorkbook().getSheet(getCurrentLocation().get().getSheetName());

    if (m.find()) {
      String columnSpecification = m.group(1);
      String rowSpecification = m.group(2);

      if (columnSpecification == null)
        throw new RendererException("missing column specification in location " + sourceSpecification);
      if (rowSpecification == null)
        throw new RendererException("missing row specification in location " + sourceSpecification);

      boolean isColumnWildcard = "*".equals(columnSpecification);
      boolean isRowWildcard = "*".equals(rowSpecification);
      int columnNumber, rowNumber;

      try {
        if (isColumnWildcard)
          columnNumber = getCurrentLocation().get().getColumnNumber();
        else
          columnNumber = SpreadSheetUtil.getColumnNumber(sheet, columnSpecification);

        if (isRowWildcard)
          rowNumber = this.currentLocation.get().getRowNumber();
        else
          rowNumber = SpreadSheetUtil.getRowNumber(sheet, rowSpecification);
      } catch (MappingMasterException e) {
        throw new RendererException("invalid source specification " + sourceSpecification + " - " + e.getMessage());
      }
      resolvedLocation = new SpreadsheetLocation(sheet.getName(), columnNumber, rowNumber);
    } else
      throw new RendererException("invalid source specification " + sourceSpecification);

    return resolvedLocation;
  }
}
