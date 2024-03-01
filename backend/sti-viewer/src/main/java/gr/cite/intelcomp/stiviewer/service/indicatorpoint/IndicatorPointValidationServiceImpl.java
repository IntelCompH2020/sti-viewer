package gr.cite.intelcomp.stiviewer.service.indicatorpoint;

import gr.cite.intelcomp.stiviewer.common.enums.IndicatorFieldBaseType;
import gr.cite.intelcomp.stiviewer.common.enums.IndicatorPointValidationType;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.FieldEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.ValidationEntity;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequestScope
public class IndicatorPointValidationServiceImpl implements IndicatorPointValidationService {

	private final ConventionService conventionService;

	private Map.Entry<String, List<String>> fieldErrors;
	private List<Map.Entry<String, List<String>>> validationErrors;

	@Autowired
	public IndicatorPointValidationServiceImpl(ConventionService conventionService) {
		this.conventionService = conventionService;
		validationErrors = new ArrayList<>();
	}

	public void validateModel() {
		if (!validationErrors.isEmpty()) throw new MyValidationException("validation error", validationErrors);
	}

	public void validateField(List<ValidationEntity> validations, Map.Entry<String, Object> field, IndicatorFieldBaseType baseType) {
		fieldErrors = new AbstractMap.SimpleEntry<>(field.getKey(), new ArrayList<>());
		validations.forEach(validation -> {
			switch (validation.getType()) {
				case REQUIRED:
					this.checkRequiredField(field, baseType);
					break;
				case ALLOWEDVALUES:
					this.checkAllowedValues(field, validation, baseType);
					break;
				case VALUERANGE:
					this.checkValueRange(field, validation, baseType);
					break;
			}
		});
		if (!fieldErrors.getValue().isEmpty()) validationErrors.add(fieldErrors);
	}

	@Override
	public void checkIfMissingRequiredFields(Map<String, Object> modelProps, Map<String, FieldEntity> configProps) {
		List<String> props = new ArrayList<>(modelProps.keySet());
		List<String> requiredFields = configProps.entrySet().stream()
				.filter(f -> f.getValue().getValidation() != null && f.getValue().getValidation()
						.stream()
						.anyMatch(valid -> valid.getType().equals(IndicatorPointValidationType.REQUIRED)))
				.map(Map.Entry::getKey).collect(Collectors.toList());
		String missingFields = requiredFields.stream()
				.filter(field -> !props.contains(field))
				.collect(Collectors.joining(","));

		if (!missingFields.isEmpty()) throw new MyValidationException("Missing required fields: [ " + missingFields + " ]");

	}

	private void checkRequiredField(Map.Entry<String, Object> field, IndicatorFieldBaseType baseType) {
		String fieldName = field.getKey();
		Object value = field.getValue();
		boolean valid = true;
		switch (baseType) {
			case String:
			case Keyword:
			case Date:
				valid = !conventionService.isNullOrEmpty((String) value);
				break;
			case Double:
				if (value == null) break;
				valid = ((Double) value) > 0.0;
				break;
			case Integer:
				if (value == null) break;
				valid = ((Integer) value) > 0;
				break;
			case IntegerArray:
			case DoubleArray:
			case KeywordArray:
				if (value == null) break;
				valid = ((List) value).size() > 0;
				break;
			case IntegerMap:
			case DoubleMap:
				valid = !conventionService.isNullOrEmpty((String) value);
				if (!valid) break;
				String[] textItems = ((String) value).split(" ");
				for (String textItem : textItems) {
					String[] parts = textItem.split("\\|");
					if (parts.length <= 1) {
						valid = false;
						break;
					}
					if (conventionService.isNullOrEmpty(parts[0])) {
						valid = false;
						break;
					}
					if (conventionService.isNullOrEmpty(parts[1])) {
						valid = false;
						break;
					}
				}
				break;
			default:
				throw new MyApplicationException("invalid type " + baseType);
		}
		if (!valid) fieldErrors.getValue().add("required field " + fieldName + " is empty ");
	}

	private void checkAllowedValues(Map.Entry<String, Object> field, ValidationEntity validation, IndicatorFieldBaseType baseType) {
		String fieldName = field.getKey();
		Object value = field.getValue();
		boolean valid = true;
		String allowedValues = "";
		switch (baseType) {
			case String:
			case Keyword:
				if (conventionService.isListNullOrEmpty(validation.getStringValues())) break;
				valid = validation.getStringValues().contains((String) value);
				allowedValues = String.join(",", validation.getStringValues());
				break;
			case Date:
				if (conventionService.isListNullOrEmpty(validation.getDateValues())) break;
				ZonedDateTime zonedDateTime = ZonedDateTime.parse((String) value);
				valid = validation.getDateValues().stream().anyMatch(date -> date.equals(zonedDateTime.toInstant()));
				allowedValues = validation.getDateValues().stream().map(Instant::toString).collect(Collectors.joining(","));
				break;
			case Double:
				if (conventionService.isListNullOrEmpty(validation.getDoubleValues())) break;
				valid = validation.getDoubleValues().contains((Double) value);
				allowedValues = validation.getDoubleValues().stream().map(String::valueOf).collect(Collectors.joining(","));
				break;
			case Integer:
				if (conventionService.isListNullOrEmpty(validation.getIntValues())) break;
				valid = validation.getIntValues().contains((Integer) value);
				allowedValues = validation.getIntValues().stream().map(String::valueOf).collect(Collectors.joining(","));
				break;
			case IntegerArray:
				if (conventionService.isListNullOrEmpty(validation.getIntValues())) break;
				valid = validation.getIntValues().containsAll((List<Integer>) value);
				allowedValues = validation.getIntValues().stream().map(String::valueOf).collect(Collectors.joining(","));
				break;
			case DoubleArray:
				if (conventionService.isListNullOrEmpty(validation.getDoubleValues())) break;
				valid = validation.getDoubleValues().containsAll((List<Double>) value);
				allowedValues = validation.getDoubleValues().stream().map(String::valueOf).collect(Collectors.joining(","));
				break;
			case KeywordArray:
				if (conventionService.isListNullOrEmpty(validation.getStringValues())) break;
				valid = validation.getStringValues().containsAll((List<String>) value);
				allowedValues = String.join(",", validation.getStringValues());
				;
				break;
			case IntegerMap:
				if (conventionService.isListNullOrEmpty(validation.getIntValues())) break;
				String[] textIntegerItems = ((String) value).split(" ");
				List<Integer> collelctedInts = Arrays.stream(textIntegerItems)
						.map(txt -> Integer.parseInt(txt.split("\\|")[1]))
						.collect(Collectors.toList());
				valid = validation.getIntValues().contains(collelctedInts);
				allowedValues = validation.getIntValues().stream().map(String::valueOf).collect(Collectors.joining(","));
				break;
			case DoubleMap:
				if (conventionService.isListNullOrEmpty(validation.getIntValues())) break;
				String[] textDoubleItems = ((String) value).split(" ");
				List<Integer> collelctedDoubles = Arrays.stream(textDoubleItems)
						.map(txt -> Integer.parseInt(txt.split("\\|")[1]))
						.collect(Collectors.toList());
				valid = validation.getDoubleValues().contains(collelctedDoubles);
				allowedValues = validation.getDoubleValues().stream().map(String::valueOf).collect(Collectors.joining(","));
				break;
			default:
				throw new MyApplicationException("invalid type " + baseType);
		}
		if (!valid) fieldErrors.getValue().add("Value of field " + fieldName + " is not contained in allowed values:[ " + allowedValues + " ]");
	}

	private void checkValueRange(Map.Entry<String, Object> field, ValidationEntity validation, IndicatorFieldBaseType baseType) {
		String fieldName = field.getKey();
		Object value = field.getValue();
		boolean valid = true;
		String ValueRange = "";
		switch (baseType) {
			case String:
			case Keyword:
			case KeywordArray:
				break;
			case Date:
				ZonedDateTime zonedDateTime = ZonedDateTime.parse((String) value);
				Date dateToSave = new Date(zonedDateTime.toInstant().toEpochMilli());
				if (validation.getMinDate() != null && validation.getMaxDate() != null) {
					Date minDate = new Date(validation.getMinDate().toEpochMilli());
					Date maxDate = new Date(validation.getMaxDate().toEpochMilli());
					valid = dateToSave.after(minDate) && dateToSave.before(maxDate);
					ValueRange = "Min Date: " + minDate + " and MaxDate: " + maxDate;
				} else if (validation.getMinDate() != null) {
					Date minDate = new Date(validation.getMinDate().toEpochMilli());
					valid = dateToSave.after(minDate);
					ValueRange = "Min Date: " + minDate;
				} else if (validation.getMaxDate() != null) {
					Date maxDate = new Date(validation.getMaxDate().toEpochMilli());
					valid = dateToSave.before(maxDate);
					ValueRange = " and MaxDate: " + maxDate;
				}
				break;
			case Double:
				if (value == null) break;
				Double doubleToSave = (Double) value;
				valid = checkValidDoubleRange(validation, doubleToSave);
				ValueRange = DoubleValuesRangeToString(validation);
				break;
			case Integer:
				if (value == null) break;
				int intToSave = (Integer) value;
				valid = checkValidIntegerRange(validation, intToSave);
				ValueRange = integerValuesRangeToString(validation);
				break;
			case IntegerArray:
				if (value == null) break;
				List<Integer> integers = (List<Integer>) value;
				if (integers.size() <= 0) break;
				int intIndex = 0;
				while (valid && integers.size() > intIndex) {
					valid = checkValidIntegerRange(validation, integers.get(intIndex));
					intIndex++;
				}
				ValueRange = integerValuesRangeToString(validation);
				break;
			case DoubleArray:
				if (value == null) break;
				List<Double> doubles = (List<Double>) value;
				if (doubles.size() <= 0) break;
				int doubleIndex = 0;
				while (valid && doubles.size() > doubleIndex) {
					valid = checkValidDoubleRange(validation, doubles.get(doubleIndex));
					doubleIndex++;
				}
				ValueRange = DoubleValuesRangeToString(validation);
				break;
			case IntegerMap:
				if (value == null) break;
				String[] textIntegerItems = ((String) value).split(" ");
				List<Integer> collelctedInts = Arrays.stream(textIntegerItems)
						.map(txt -> Integer.parseInt(txt.split("\\|")[1]))
						.collect(Collectors.toList());
				if (this.conventionService.isListNullOrEmpty(collelctedInts)) break;
				intIndex = 0;
				while (valid && collelctedInts.size() > intIndex) {
					valid = checkValidDoubleRange(validation, collelctedInts.get(intIndex));
					intIndex++;
				}
				ValueRange = integerValuesRangeToString(validation);
				break;
			case DoubleMap:
				if (value == null) break;
				String[] textDoubleItems = ((String) value).split(" ");
				List<Double> collelctedDoubles = Arrays.stream(textDoubleItems)
						.map(txt -> Double.parseDouble(txt.split("\\|")[1]))
						.collect(Collectors.toList());
				if (this.conventionService.isListNullOrEmpty(collelctedDoubles)) break;
				doubleIndex = 0;
				while (valid && collelctedDoubles.size() > doubleIndex) {
					valid = checkValidDoubleRange(validation, collelctedDoubles.get(doubleIndex));
					doubleIndex++;
				}
				ValueRange = DoubleValuesRangeToString(validation);
				break;
			default:
				throw new MyApplicationException("invalid type " + baseType);
		}
		if (!valid) fieldErrors.getValue().add("Value of field " + fieldName + " is not in value Range: [ " + ValueRange + " ]");
	}

	private String integerValuesRangeToString(ValidationEntity validation) {
		return "Min Integer: " + validation.getMinInt() + " and Max Integer : " + validation.getMaxInt();
	}

	private String DoubleValuesRangeToString(ValidationEntity validation) {
		return "Min Double: " + validation.getMinDouble() + " and Max Double : " + validation.getMaxDouble();
	}

	private boolean checkValidIntegerRange(ValidationEntity validation, int intToSave) {
		if (validation.getMinInt() != 0 && validation.getMaxInt() != 0) {
			return intToSave >= validation.getMinInt() && intToSave <= validation.getMaxInt();
		} else if (validation.getMinInt() != 0) {
			return intToSave >= validation.getMinInt();
		} else if (validation.getMaxInt() != 0) {
			return intToSave <= validation.getMaxInt();
		}
		return true;
	}

	private boolean checkValidDoubleRange(ValidationEntity validation, double doubleToSave) {
		if ((validation.getMinDouble() != null && validation.getMinDouble() != 0.0)
				&& (validation.getMaxDouble() != null && validation.getMaxDouble() != 0.0)) {
			return doubleToSave >= validation.getMinDouble() && doubleToSave <= validation.getMaxDouble();
		} else if (validation.getMinDouble() != null && validation.getMinDouble() != 0.0) {
			return doubleToSave >= validation.getMinDouble();
		} else if (validation.getMaxDouble() != null && validation.getMaxDouble() != 0.0) {
			return doubleToSave <= validation.getMaxDouble();
		}
		return true;
	}
}
