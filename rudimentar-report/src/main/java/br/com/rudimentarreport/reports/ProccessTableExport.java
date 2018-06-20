package br.com.rudimentarreport.reports;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ProccessTableExport {

	private List<String> fieldsInsert;
	private List<FieldsComplex> listNameColumns;
	private List<FieldsComplex> listNamesLines;

	public ProccessTableExport(List<String> campos) {
		this.fieldsInsert = campos;
		this.listNameColumns = new ArrayList<FieldsComplex>();
		this.listNamesLines = new ArrayList<FieldsComplex>();
	}

	public List<String> getColumns(Object clazz) {
		for (Field field : clazz.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(TableExport.class)) {
				field.setAccessible(true);
				TableExport anotacao = field.getAnnotation(TableExport.class);
				if (isFieldAdd(anotacao)) {
					listNameColumns.add(new FieldsComplex(anotacao.nameField(), anotacao.titleField()));
				}
			}
		}
		for (Method method : clazz.getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(TableExport.class)) {
				TableExport annotation = method.getAnnotation(TableExport.class);
				if (isFieldAdd(annotation)) {
					listNameColumns.add(new FieldsComplex(annotation.nameField(), annotation.titleField()));
				}
			}
		}
		return createColumnInOrder();
	}

	private List<String> createColumnInOrder() {
		List<String> columnsOrdered = new ArrayList<String>();
		for (String field : fieldsInsert) {
			columnsOrdered.add(getElementByNameField(field));
		}
		return columnsOrdered;
	}

	private String getElementByNameField(String field) {
		for (FieldsComplex columnsComplex : listNameColumns) {
			if (columnsComplex.getFieldName().equals(field)) {
				return columnsComplex.getValueField();
			}
		}
		return null;
	}

	private boolean isFieldAdd(TableExport annotation) {
		return isFieldNotEmpty() || isFieldPossibleAdd(annotation);
	}

	private boolean isFieldPossibleAdd(TableExport anotacao) {
		return fieldsInsert.contains(anotacao.nameField());
	}

	private boolean isFieldNotEmpty() {
		return fieldsInsert == null || fieldsInsert.isEmpty();
	}

	public List<String> getLines(List<?> list)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		for (Object object : list) {
			for (Field field : object.getClass().getDeclaredFields()) {
				if (field.isAnnotationPresent(TableExport.class)) {
					field.setAccessible(true);
					if (isFieldAdd(field)) {
						listNamesLines.add(new FieldsComplex(field.getAnnotation(TableExport.class).nameField(),
								field.get(object) != null ? proccessValueField(object, field) : ""));
					}
				}
			}
			for (Method method : object.getClass().getDeclaredMethods()) {
				if (method.isAnnotationPresent(TableExport.class)) {
					if (isCampoAdicionavel(method)) {
						listNamesLines.add(new FieldsComplex(method.getAnnotation(TableExport.class).nameField(),
								proccessValueMethod(object, method)));
					}
				}
			}
		}
		return createLinesOrdered();
	}

	private List<String> createLinesOrdered() {
		List<String> linhasOrdenadas = new ArrayList<String>();
		List<List<FieldsComplex>> subListas = new ArrayList<List<FieldsComplex>>();
		int start = 0;
		List<FieldsComplex> temporaria = new ArrayList<FieldsComplex>();
		for (int auxiliar = 0; auxiliar < listNamesLines.size(); auxiliar++) {
			if (auxiliar != 0 && auxiliar % (fieldsInsert.size()) == 0) {
				subListas.add(temporaria);
				temporaria = new ArrayList<FieldsComplex>();
			}
			temporaria.add(listNamesLines.get(auxiliar));
		}
		subListas.add(temporaria);
		for (List<FieldsComplex> lista : subListas) {
			for (String colunas : fieldsInsert) {
				linhasOrdenadas.add(lista.get(lista.indexOf(new FieldsComplex(colunas))).valueField);
			}
		}
		return linhasOrdenadas;
	}

	private boolean isCampoAdicionavel(Method metodo) {
		TableExport anotacao = metodo.getAnnotation(TableExport.class);
		return isFieldAdd(anotacao);
	}

	private boolean isFieldAdd(Field field) {
		field.setAccessible(true);
		TableExport annotation = field.getAnnotation(TableExport.class);
		return isFieldAdd(annotation);
	}

	private String proccessValueField(Object object, Field field)
			throws IllegalAccessException, InstantiationException {
		String value = "";
		if (field.isAnnotationPresent(TableExport.class)) {
			field.setAccessible(true);
			TableExport anotacao = field.getAnnotation(TableExport.class);
			Class clazz = anotacao.preProccessField();
			if (isDifferentNullAndNotDefault(clazz)) {
				value = ((TypeProccess) clazz.newInstance()).proccess(field.get(object));
			} else {
				value = field.get(object).toString();
			}
		}
		return value;
	}

	private boolean isDifferentNullAndNotDefault(Class clazz) {
		return clazz != null && clazz != String.class;
	}

	private String proccessValueMethod(Object object, Method method)
			throws IllegalAccessException, InvocationTargetException, InstantiationException {
		String value = "";
		if (method.isAnnotationPresent(TableExport.class)) {
			TableExport annotation = method.getAnnotation(TableExport.class);
			Class clazz = annotation.preProccessField();
			if (isDifferentNullAndNotDefault(clazz)) {
				value = ((TypeProccess) clazz.newInstance()).proccess(method.invoke(object));
			} else {
				value = (String) method.invoke(object);
			}
		}
		return value;
	}

	class FieldsComplex {
		@Getter
		@Setter
		private String fieldName;
		@Getter
		@Setter
		private String valueField;

		public FieldsComplex(String nameField, String valueField) {
			this.fieldName = nameField;
			this.valueField = valueField;
		}

		public FieldsComplex(String nameField) {
			this.fieldName = nameField;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FieldsComplex other = (FieldsComplex) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (fieldName == null) {
				if (other.fieldName != null)
					return false;
			} else if (!fieldName.equals(other.fieldName))
				return false;
			return true;
		}

		private ProccessTableExport getOuterType() {
			return ProccessTableExport.this;
		}

		@Override
		public String toString() {
			return "Field Name:[" + this.fieldName + "]\n Value Field:[" + this.valueField + "]";
		}

	}

}
