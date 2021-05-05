package service.extension;

import data.TargetTypes;
import operations.ModelService;
import operations.dto.ClassDTO;
import operations.dto.GenericDTO;
import parser.visitors.AnnotationVisitor;

import java.util.List;
import java.util.Optional;

public class GetData extends ModelService {
	@Override
	public void save() {

	}

	@Override
	public void setDTO(GenericDTO<?> dto) {
		List<ClassDTO> classDTOList = (List<ClassDTO>) dto.getObject();
		// Type means class - jetzt sucht der am Klassenkopf die Annotation und sagt dann ob er da eine gefunden hat
		AnnotationVisitor annotationVisitor = new AnnotationVisitor("javax.persistence.Entity", TargetTypes.TYPE);


		System.out.println("----------starting reading from dto");
		for(ClassDTO classDTO: classDTOList){
			System.out.println(classDTO.getFullName());
			// wenn der true zurück gibt gibt es in der klasse die annotation; wenn der null zurück gibt wird das zu false gemacht und es
			// gab sie nicht
			System.out.println(Optional.ofNullable(classDTO.getJavaClass().accept(annotationVisitor, null)).orElse(false));
		}


	}

	@Override
	public GenericDTO<?> buildDTO() {
		return null;
	}
}
