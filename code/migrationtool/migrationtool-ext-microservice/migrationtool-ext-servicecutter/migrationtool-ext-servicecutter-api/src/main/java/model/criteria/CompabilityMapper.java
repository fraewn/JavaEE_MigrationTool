package model.criteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Supported Characterisitcs for each {@link CouplingGroup#COMPATIBILITY}
 */
public enum CompabilityMapper {

	CC_4(CouplingCriteria.STRUCTURAL_VOLATILITY, CompabilityCharacteristics.CC_4_LOW,
			CompabilityCharacteristics.CC_4_NORMAL, CompabilityCharacteristics.CC_4_HIGH),
	CC_6(CouplingCriteria.CONSISTENCY_CRITICALITY, CompabilityCharacteristics.CC_6_LOW,
			CompabilityCharacteristics.CC_6_NORMAL, CompabilityCharacteristics.CC_6_HIGH),
	CC_7(CouplingCriteria.AVAILABILITY_CRITICALITY, CompabilityCharacteristics.CC_7_LOW,
			CompabilityCharacteristics.CC_7_NORMAL, CompabilityCharacteristics.CC_7_HIGH),
	CC_8(CouplingCriteria.CONTENT_VOLATILITY, CompabilityCharacteristics.CC_8_LOW,
			CompabilityCharacteristics.CC_8_NORMAL, CompabilityCharacteristics.CC_8_HIGH),
	CC_10(CouplingCriteria.STORAGE_SIMILARITY, CompabilityCharacteristics.CC_10_LOW,
			CompabilityCharacteristics.CC_10_NORMAL, CompabilityCharacteristics.CC_10_HIGH),
	CC_13(CouplingCriteria.SECURITY_CRITICALITY, CompabilityCharacteristics.CC_13_LOW,
			CompabilityCharacteristics.CC_13_NORMAL, CompabilityCharacteristics.CC_13_HIGH);

	private List<CompabilityCharacteristics> characterisitics;

	private CouplingCriteria relatedCriteria;

	private CompabilityMapper(CouplingCriteria relatedCriteria, CompabilityCharacteristics... characterisitics) {
		this.relatedCriteria = relatedCriteria;
		this.characterisitics = characterisitics != null ? Arrays.asList(characterisitics) : new ArrayList<>();
	}

	/**
	 * @return the characterisitics
	 */
	public List<CompabilityCharacteristics> getCharacterisitics() {
		return this.characterisitics;
	}

	/**
	 * @return the default of the defined group
	 */
	public CompabilityCharacteristics getDefault() {
		for (CompabilityCharacteristics cc : this.characterisitics) {
			if (cc.isDefaultSelection()) {
				return cc;
			}
		}
		return null;
	}

	/**
	 * @return the relatedCriteria
	 */
	public CouplingCriteria getRelatedCriteria() {
		return this.relatedCriteria;
	}
}
