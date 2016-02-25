/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iksgmbh.moglicc.provider.model.standard.excel;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.provider.model.standard.excel.ExcelTableReader.Cell;
import com.iksgmbh.moglicc.test.ExcelStandardModelProviderTestParent;

public class ExcelTableReaderUnitTest extends ExcelStandardModelProviderTestParent
{
	private ExcelTableReader excelTableReader;
	
	@Before
	public void setup() 
	{
		super.setup();
		try {
			excelTableReader = new ExcelTableReader(new File(getProjectTestResourcesDir(), "TestDataExample.xlsx"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void returnsContentOfCellFromDefaultSheet() throws Exception
	{
		// arrange
		final Cell cell1 = new Cell(1, 1);
		final Cell cell2 = new Cell(2, 10);
		
		// act
		final String cellContent1 = excelTableReader.getContent(cell1);
		final String cellContent2 = excelTableReader.getContent(cell2);
		
		// assert
		assertEquals("cellContent1", "First cell of matrix 1", cellContent1);
		assertEquals("cellContent2", "First cell of matrix 2", cellContent2);
	}
	
	@Test
	public void returnsContentOfCellFromDefinedSheet() throws Exception
	{
		// arrange
		excelTableReader.setSheet("SecondSheet");
		final Cell cell1 = new Cell(1, 1);
		final Cell cell2 = new Cell(2, 3);
		
		// act
		final String cellContent1 = excelTableReader.getContent(cell1);
		final String cellContent2 = excelTableReader.getContent(cell2);
		
		// assert
		assertEquals("cellContent1", "", cellContent1);
		assertEquals("cellContent2", "First cell of matrix 3", cellContent2);
	}
	
	@Test
	public void returnsNextEmptyCellInColumn() throws Exception
	{
		// arrange
		final Cell firstCell = new Cell(2, 10);
		
		// act
		final Cell result = excelTableReader.getNextEmptyCellInColumn(firstCell);
		
		// assert
		assertEquals("colNo", 2, result.colNo);  // remains unchanged
		assertEquals("rowNo", 14, result.rowNo);
		assertEquals("cellContent", "", excelTableReader.getContent(result));
		assertEquals("cellContent", "Container", excelTableReader.getContent(result.getNeighbourToTableTop()));
	}
	
	
	@Test
	public void returnsNextEmptyRowInRow() throws Exception
	{
		// arrange
		final Cell firstCell = new Cell(2, 10);
		
		// act
		final Cell result = excelTableReader.getNextEmptyCellInRow(firstCell);
		
		// assert
		assertEquals("colNo", 5, result.colNo);  
		assertEquals("rowNo", 10, result.rowNo);  // remains unchanged
		assertEquals("cellContent", "", excelTableReader.getContent(result));
		assertEquals("cellContent", "Form", excelTableReader.getContent(result.getLeftNeighbour()));
	}
	
	@Test
	public void returnsDataMatrixByFirstCell() throws Exception
	{
		// arrange
		final Cell firstCell = new Cell(2, 10);
		
		// act
		final String[][] dataMatrix = excelTableReader.getMatrix(firstCell);
		
		// assert
		assertEquals("matrixWidth", 4, dataMatrix.length);
		assertEquals("matrixHeight", 3, dataMatrix[0].length);
		assertEquals("Content of first cell", "First cell of matrix 2", dataMatrix[0][0]);
		assertEquals("Content of last Cell", "cube", dataMatrix[3][2]);
	}
	
	@Test
	public void returnsDataMatrixByCellRange() throws Exception
	{
		// arrange
		final Cell firstCell = new Cell(1, 1);
		final Cell lastCell = new Cell(5, 4);
		
		// act
		final String[][] dataMatrix = excelTableReader.getMatrix(firstCell, lastCell);
		
		// assert
		assertEquals("matrixWidth", 4, dataMatrix.length);
		assertEquals("matrixHeight", 5, dataMatrix[0].length);
		assertEquals("Content of first cell", "First cell of matrix 1", dataMatrix[0][0]);
		assertEquals("Content of last Cell", "Last cell of matrix 1", dataMatrix[3][4]);
	}

	@Test
	public void returnsSheetNumberForSheetName() throws Exception
	{
		assertEquals("SheetNumber", 2, excelTableReader.getSheetNumberForSheetName("SecondSheet").intValue());		
		assertEquals("SheetNumber", null, excelTableReader.getSheetNumberForSheetName("notExisting"));		
	}

	@Test
	public void returnsSheetNameForSheetNumber() throws Exception
	{
		assertEquals("SheetName", "SecondSheet", excelTableReader.getSheetNameForSheetNumber(2));		
		assertEquals("SheetName", null, excelTableReader.getSheetNameForSheetNumber(99999));		
	}
	
}