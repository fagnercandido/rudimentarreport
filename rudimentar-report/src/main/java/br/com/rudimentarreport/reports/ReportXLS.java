package br.com.rudimentarreport.reports;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.lowagie.text.Font;

class ReportXLS {

	private Workbook workBook;
	@SuppressWarnings("unused")
	private CellStyle style;
	private ByteArrayOutputStream baos = null;
	private ProccessTableExport proccessor;

	public ReportXLS(List<String> fields) {
		init();
		proccessor = new ProccessTableExport(fields);
	}

	public byte[] generateXLS(List<?> list, String title) {
		createTable(list, title);
		finalizeProccess();
		return baos.toByteArray();
	}

	private void autoResize() {
		int numberOfSheets = workBook.getNumberOfSheets();
		for (int i = 0; i < numberOfSheets; i++) {
			Sheet sheet = workBook.getSheetAt(i);
			if (sheet.getPhysicalNumberOfRows() > 0) {
				Row row = sheet.getRow(0);
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					int columnIndex = cell.getColumnIndex();
					sheet.autoSizeColumn(columnIndex);
				}
			}
		}
	}

	private void finalizeProccess() {
		try {
			workBook.write(baos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void init() {
		baos = new ByteArrayOutputStream();
		workBook = new HSSFWorkbook();
	}

	private void createTable(List<?> lista, String titulo) {
		Sheet sheet = configReport();
		sheet = createColumns(sheet, lista);
		try {
			sheet = createLines(sheet, lista, titulo);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
	}

	private Sheet configReport() {
		Sheet sheet = workBook.createSheet("exportacao");
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		style = workBook.createCellStyle();
		return sheet;
	}

	private Sheet createColumns(Sheet sheet, List<?> list) {
		Row headerRow = sheet.createRow(1);
		headerRow.setHeightInPoints(40);
		int count = 0;
		for (String coluna : proccessor.getColumns(list.get(0))) {
			Cell headerCell = headerRow.createCell(count);
			headerCell.setCellValue(coluna);
			int columnIndex = headerCell.getColumnIndex();
			sheet.autoSizeColumn(columnIndex);
			count++;

		}
		return sheet;
	}

	private Sheet createLines(Sheet sheet, List<?> lista, String titulo)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		List<String> lines = proccessor.getLines(lista);
		List<String> columns = proccessor.getColumns(lista.get(0));
		int position = 0;
		int line = 2;

		Row headerRowTitle = sheet.createRow(0);
		Cell headerCellTitle = headerRowTitle.createCell(0);
		headerCellTitle.setCellValue(titulo);
		headerCellTitle.setCellType(Font.BOLD);

		for (int count = 0; count < lista.size(); count++) {
			Row headerRow = sheet.createRow(line);

			headerRow.setHeightInPoints(40);
			for (int aux = 0; aux < columns.size(); aux++) {
				Cell headerCell = headerRow.createCell(aux);
				if (position >= lines.size()) {
					position = 0;
				} else {
					headerCell.setCellValue(lines.get(position));
					int columnIndex = headerCell.getColumnIndex();
					sheet.autoSizeColumn(columnIndex);
				}
				position++;
			}
			line++;
		}
		return sheet;
	}

}
