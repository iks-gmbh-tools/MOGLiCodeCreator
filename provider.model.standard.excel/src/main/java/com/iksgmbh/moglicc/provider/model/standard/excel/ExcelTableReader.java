package com.iksgmbh.moglicc.provider.model.standard.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelTableReader
{
	private File excelFile;
	private XSSFWorkbook workbook;
	private XSSFSheet currentSheet;
	
	public ExcelTableReader(final File excelFile) throws IOException {
		this.excelFile = excelFile;
		FileInputStream fileInputStream = new FileInputStream(excelFile);
		workbook = new XSSFWorkbook(fileInputStream);
		setSheet(1);
	}

	public File getExcelFile()
	{
		return excelFile;
	}

	public XSSFWorkbook getWorkbook()
	{
		return workbook;
	}

	public XSSFSheet getCurrentSheet()
	{
		return currentSheet;
	}
	
	public void setSheet(final String worksheetName)
	{
		currentSheet = workbook.getSheet(worksheetName);
	}
	
	public void setSheet(final int sheetNumer)
	{
		currentSheet = workbook.getSheetAt(sheetNumer - 1); // -1 due to mapping on index
	}

	public Cell getNextEmptyCellInRow(final Cell firstCell)
	{
		Cell nextCellInColumn = firstCell.getRightNeighbour();
		while (! "".endsWith(getContent(nextCellInColumn)))
		{
			nextCellInColumn = nextCellInColumn.getRightNeighbour();
		}
		return nextCellInColumn;
	}
	
	public Cell getNextEmptyCellInColumn(final Cell firstCell)
	{
		Cell nextCellInColumn = firstCell.getNeighbourToTableBottom();
		while (! "".endsWith(getContent(nextCellInColumn)))
		{
			nextCellInColumn = nextCellInColumn.getNeighbourToTableBottom();
		}
		return nextCellInColumn;
	}

	
	public String getContent(final Cell cell)
	{
		final XSSFRow row = currentSheet.getRow(cell.rowNo - 1);  // -1 due to mapping on index
		if (row == null)
		{
			return "";
		}
		
		final XSSFCell xssfCell = row.getCell(cell.colNo - 1);    // -1 due to mapping on index
		if (xssfCell == null)
		{
			return "";			
		}
		
		xssfCell.setCellType(XSSFCell.CELL_TYPE_STRING);
		return xssfCell.getStringCellValue();
	}
	
	/**
	 * Returns data values of a squared part of the current sheet (matrix). 
	 * The last matrix cell is identified by a following empty row and column.  
	 * @param firstCell (first matrix cell - with minimum row and col number in the sheet table)
	 * @return matrix as string array of data values
	 */
	public String[][] getMatrix(final Cell firstCell)
	{
		final Cell nextEmptyCellInColumn = getNextEmptyCellInColumn(firstCell);
		final Cell nextEmptyCellInRow = getNextEmptyCellInRow(firstCell);
		
		return getMatrix(firstCell, new Cell(nextEmptyCellInRow.colNo - 1, 
				                             nextEmptyCellInColumn.rowNo - 1));
	}	

	/**
	 * Returns data values of a squared part of the current sheet (matrix).  
	 * @param firstCell (first matrix cell - with minimum row and col number in the sheet table)
	 * @param lastCell (last matrix cell - with maximum row and col number in the sheet table)
	 * @return matrix as string array of data values
	 */
	public String[][] getMatrix(final Cell firstCell, final Cell lastCell)
	{
		final int matrixWidth = lastCell.colNo - firstCell.colNo + 1;
		final int matrixHeight = lastCell.rowNo - firstCell.rowNo + 1;
		
		final String[][] toReturn = new String[matrixHeight][matrixWidth];
		for (int row = 0; row < matrixHeight; row++) 
		{
			for (int col = 0; col < matrixWidth; col++) 
			{
				final Cell cell = new Cell(firstCell.colNo + col, firstCell.rowNo + row);
				toReturn[row][col] = getContent(cell);
			}
		}
		
		return toReturn;
	}
	
	/**
	 * Subset of attributes out of a matrix defined by min and max index of the attributes 
	 * @author oberratr
	 */
	public static class AttributeSubset
	{
		public int minIndex;
		public int maxIndex;
		
		public AttributeSubset(int i1, int i2) 
		{
			if (i1 < i2)
			{				
				this.minIndex = i1;
				this.maxIndex = i2;
			}
			else
			{
				this.minIndex = i2;
				this.maxIndex = i1;
			}
		}
	}

	
	/**
	 * Coordinate of a cell within the table of a sheet. 
	 * @author oberratr
	 */
	public static class Cell
	{
		public int rowNo;  // starts with 1
		public int colNo;  // starts with 1
		
		public Cell(final int colNo, final int rowNo) 
		{
			this.rowNo = rowNo;
			this.colNo = colNo;
		}

		@Override
		public String toString()
		{
			return "Cell [rowNo=" + rowNo + ", colNo=" + colNo + "]";
		}
		
		public Cell getNeighbourToTableTop()
		{
			if (rowNo == 1)
			{
				return null;
			}
			return new Cell(colNo, rowNo - 1);
		}
		
		public Cell getNeighbourToTableBottom()
		{
			return new Cell(colNo, rowNo + 1);
		}
		
		public Cell getLeftNeighbour()
		{
			if (colNo == 1)
			{
				return null;
			}
			return new Cell(colNo - 1, rowNo);			
		}
		
		public Cell getRightNeighbour()
		{
			return new Cell(colNo + 1, rowNo);
		}
		
	}

	public String getSheetNameForSheetNumber(final int sheetNumber)
	{
		try {
			return workbook.getSheetName(sheetNumber - 1);	
		} catch (IllegalArgumentException e) {
			return null;
		}
		
	}


	public Integer getSheetNumberForSheetName(final String sheetName)
	{
		final int index = workbook.getSheetIndex(sheetName);
		if (index == -1)
		{
			return null;
		}
		return index + 1;
	}

}
