package br.com.rudimentarreport.reports;

import java.util.Arrays;
import java.util.List;

public class ReportGenerator {

	private List<?> list;
	private String title;
	private List<String> fields;

	
	public ReportGenerator(List<?> list, String title, String... fields) {
		this.list = list;
		this.title = title;
		this.fields = Arrays.asList(fields);
	}

	public byte[] generateXLS() {
		return new ReportXLS(fields).generateXLS(list, title);
	}

	public byte[] generatePDF() {
		return new ReportPDF(fields).generatePDF(list, title);
	}

}
