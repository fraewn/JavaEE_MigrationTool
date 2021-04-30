package Loading;

import operations.dto.ClassDTO;

import java.util.List;

public class ClassDTOTest {
	public void printClassProperties(List<ClassDTO> classes){
		classes.forEach(classDTO -> {
			System.out.println("full path: " + classDTO.getFullName());
			System.out.println("amount of methods: " + classDTO.getMethods().size());
			System.out.println("amount of imports: " + classDTO.getImports().size());
			System.out.println("\n");
		});

	}
}
