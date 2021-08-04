package recommender.service;

import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import model.ModelRepresentation;
import model.data.ArchitectureInformation;
import operations.dto.AstDTO;
import recommender.model.Recommendation;
import recommender.processing.RecommenderProcessingSteps;

/**
 * Service interface to control the recommender service
 */
public interface RecommenderService {

	/**
	 * Import the defined model of the loading and analyzing step
	 *
	 * @param model    current model
	 * @param entities all entites
	 * @param useCases all use case
	 */
	void importArchitecture(ModelRepresentation model, List<AstDTO> entities,
			Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> useCases);

	/**
	 * Gets the recommendation of a processing step
	 *
	 * @param currentStep step
	 * @return map of recommandations
	 */
	Map<String, Recommendation> process(RecommenderProcessingSteps currentStep);

	/**
	 * Gets a list of the metric groups that are defined by the user
	 *
	 * @param currentStep step
	 * @return limits
	 */
	List<Integer> getInformation(RecommenderProcessingSteps currentStep);

	/**
	 * Convert the recommendations to the model
	 *
	 * @param list        recommendations
	 * @param currentStep step
	 * @return model object
	 */
	ArchitectureInformation convertRecommendations(List<Recommendation> list, RecommenderProcessingSteps currentStep);
}
