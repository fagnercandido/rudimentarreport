package br.com.rudimentarreport.reports;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class ReportPDF {

	private Document document = null;
	private ByteArrayOutputStream baos = null;
	private ProccessTableExport proccessor;

	public ReportPDF(List<String> fields) {
		proccessor = new ProccessTableExport(fields);
	}

	public byte[] generatePDF(List<?> list, String title) {
		try {
			init(title);
			document.add(createTable(list));
			finalizeProccess();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}

	private void init(String title) throws DocumentException {
		document = new Document();
		baos = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, baos);
		document.setPageSize(PageSize.A4.rotate());
		mountHeader(title);
		document.open();
	}

	private void finalizeProccess() throws IOException {
		baos.flush();
		baos.close();
		document.close();
	}

	private void mountHeader(String title) {
		HeaderFooter header = new HeaderFooter(new Phrase(title), false);
		header.setAlignment(Element.ALIGN_CENTER);
		document.setHeader(header);
	}

	private PdfPTable createTable(List<?> list) throws InstantiationException {
		PdfPTable tabela = createColumns(list);
		tabela = createLines(tabela, list);
		return tabela;
	}

	private PdfPTable createLines(PdfPTable table, List<?> lines) throws InstantiationException {
		List<String> listValues = new ArrayList<String>();
		try {
			listValues = proccessor.getLines(lines);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		for (String value : listValues) {
			table.addCell(mountCellColumn(value));
		}
		return table;
	}

	private PdfPTable createColumns(List<?> list) {
		List<String> listValues = proccessor.getColumns(list.get(0));
		PdfPTable tabela = new PdfPTable(listValues.size());
		tabela.setWidthPercentage(100);
		for (String column : listValues) {
			tabela.addCell(mountCellColumn(column));
		}
		return tabela;
	}

	private static PdfPCell mountCellColumn(String label) {
		PdfPCell cell = new PdfPCell(new Phrase(label, new Font(Font.HELVETICA, 8)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		return cell;
	}

}
