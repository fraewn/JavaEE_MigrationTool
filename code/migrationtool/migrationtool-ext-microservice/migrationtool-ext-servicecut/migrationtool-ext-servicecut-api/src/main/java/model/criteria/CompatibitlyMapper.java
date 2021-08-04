package model.criteria;

import static model.criteria.CompatibitlyCharacteristics.CC_10_HIGH;
import static model.criteria.CompatibitlyCharacteristics.CC_10_LOW;
import static model.criteria.CompatibitlyCharacteristics.CC_10_NORMAL;
import static model.criteria.CompatibitlyCharacteristics.CC_11_HIGH;
import static model.criteria.CompatibitlyCharacteristics.CC_11_LOW;
import static model.criteria.CompatibitlyCharacteristics.CC_11_NORMAL;
import static model.criteria.CompatibitlyCharacteristics.CC_12_HIGH;
import static model.criteria.CompatibitlyCharacteristics.CC_12_LOW;
import static model.criteria.CompatibitlyCharacteristics.CC_12_NORMAL;
import static model.criteria.CompatibitlyCharacteristics.CC_13_HIGH;
import static model.criteria.CompatibitlyCharacteristics.CC_13_LOW;
import static model.criteria.CompatibitlyCharacteristics.CC_13_NORMAL;
import static model.criteria.CompatibitlyCharacteristics.CC_14_HIGH;
import static model.criteria.CompatibitlyCharacteristics.CC_14_LOW;
import static model.criteria.CompatibitlyCharacteristics.CC_14_NORMAL;
import static model.criteria.CompatibitlyCharacteristics.CC_9_HIGH;
import static model.criteria.CompatibitlyCharacteristics.CC_9_LOW;
import static model.criteria.CompatibitlyCharacteristics.CC_9_NORMAL;
import static model.criteria.CouplingCriteria.AVAILABILITY_CRITICALITY;
import static model.criteria.CouplingCriteria.CONSISTENCY_CRITICALITY;
import static model.criteria.CouplingCriteria.CONTENT_VOLATILITY;
import static model.criteria.CouplingCriteria.SECURITY_CRITICALITY;
import static model.criteria.CouplingCriteria.STORAGE_SIMILARITY;
import static model.criteria.CouplingCriteria.STRUCTURAL_VOLATILITY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Supported Characterisitcs for each {@link CouplingGroup#COMPATIBILITY}
 */
public enum CompatibitlyMapper {

	CC_9(STRUCTURAL_VOLATILITY, CC_9_LOW, CC_9_NORMAL, CC_9_HIGH),
	CC_10(CONTENT_VOLATILITY, CC_10_LOW, CC_10_NORMAL, CC_10_HIGH),
	CC_11(CONSISTENCY_CRITICALITY, CC_11_LOW, CC_11_NORMAL, CC_11_HIGH),
	CC_12(AVAILABILITY_CRITICALITY, CC_12_LOW, CC_12_NORMAL, CC_12_HIGH),
	CC_13(STORAGE_SIMILARITY, CC_13_LOW, CC_13_NORMAL, CC_13_HIGH),
	CC_14(SECURITY_CRITICALITY, CC_14_LOW, CC_14_NORMAL, CC_14_HIGH);

	private List<CompatibitlyCharacteristics> characterisitics;

	private CouplingCriteria relatedCriteria;

	CompatibitlyMapper(CouplingCriteria relatedCriteria, CompatibitlyCharacteristics... characterisitics) {
		this.relatedCriteria = relatedCriteria;
		this.characterisitics = characterisitics != null ? Arrays.asList(characterisitics) : new ArrayList<>();
	}

	/**
	 * @return the characterisitics
	 */
	public List<CompatibitlyCharacteristics> getCharacterisitics() {
		return this.characterisitics;
	}

	/**
	 * @return the default of the defined group
	 */
	public CompatibitlyCharacteristics getDefault() {
		for (CompatibitlyCharacteristics cc : this.characterisitics) {
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

	/**
	 * Get the mapper of a specific coupling criteria
	 *
	 * @param criteria coupling criteria
	 * @return compabilitymapper
	 */
	public static CompatibitlyMapper getMapperFromCriteria(CouplingCriteria criteria) {
		CompatibitlyMapper mapper = null;
		for (CompatibitlyMapper cm : CompatibitlyMapper.values()) {
			if (cm.getRelatedCriteria().equals(criteria)) {
				mapper = cm;
				break;
			}
		}
		return mapper;
	}
}
