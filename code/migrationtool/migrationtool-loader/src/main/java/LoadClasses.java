import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import org.apache.log4j.Logger;

/*
code source: Dirk van der Jeetze; in context of the IASA project with focus "recognizing patterns in legacy projects"
Link: https://git.fslab.de/prosywis/cep/-/blob/djeetz2s_masterprojekt/patterndetection/src/main/java/de/hbrs/patterndetection/service
/ClassFinder.java
 */
public class LoadClasses {
	//private static final Logger LOG = Logger.getLogger(LoadClasses.class);

	public List<ClassOrInterfaceDeclaration> listClasses(String filePath) {
		//LOG.info("Starting list class process");

		File file = new File(filePath);
		List<ClassOrInterfaceDeclaration> classList = new ArrayList<>();

		// exclude all paths that do not lead to a directory or a .java file
		FileFilter filter = pathname -> pathname.isDirectory() || pathname.getName().endsWith(".java");
		System.out.println("is directory: " + file.isFile() + " file name: " + file.getName());
		System.out.println(filter);
		if(file.isDirectory() && file.listFiles(filter) != null) {
				for (File fileInDirectory : Objects.requireNonNull(file.listFiles(filter))) {
					if (fileInDirectory.isDirectory()) {
						// recursion
						System.out.println("hello from recursion");
						System.out.println(fileInDirectory.toString());
						classList.addAll(listClasses(fileInDirectory.getPath()));
					} else {
						// find classes in file and add to classList
						classList.addAll(findClassesInFile(fileInDirectory));
					}
				}
		}
		else {
				if (file.getName().endsWith(".java")) {
					classList.addAll(findClassesInFile(file));
					System.out.println("classes of file are added to class list");
				} else {
					System.out.println("empty dir");
					//LOG.info(file.toString() + " is an empty directory");
				}
		}
		return classList;
	}

	public List<ClassOrInterfaceDeclaration> findClassesInFile(File file){
		TypeSolver typeSolver = new CombinedTypeSolver();
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
		StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

		CompilationUnit cu = null;
		try {
			System.out.println("parsing: " + file.getPath());
			cu = StaticJavaParser.parse(file);
			System.out.println("parsing: done");
		}
		catch(FileNotFoundException e){
			//LOG.error(e.getMessage());
			System.out.println(e.getMessage());
			return Collections.emptyList();
		}
		return cu.findAll(ClassOrInterfaceDeclaration.class);
	}
}
