package core;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;

/*
code source: Dirk van der Jeetze; in context of the IASA project with focus "recognizing patterns in legacy projects"
Link: https://git.fslab.de/prosywis/cep/-/blob/djeetz2s_masterprojekt/patterndetection/src/main/java/de/hbrs/patterndetection/service
/ClassFinder.java
 */
public class LoadSourceFiles {

	private static final Logger LOG = Logger.getLogger(LoadSourceFiles.class);

	public static List<ClassOrInterfaceDeclaration> listClasses(String filePath) {
		File file = new File(filePath);
		List<ClassOrInterfaceDeclaration> classList = new ArrayList<>();
		// exclude all paths that do not lead to a directory or a .java file
		FileFilter filter = pathname -> pathname.isDirectory() || pathname.getName().endsWith(".java");
		if (file.isDirectory() && (file.listFiles(filter) != null)) {
			for (File fileInDirectory : Objects.requireNonNull(file.listFiles(filter))) {
				if (fileInDirectory.isDirectory()) {
					// recursion
					classList.addAll(listClasses(fileInDirectory.getPath()));
				} else {
					// find classes in file and add to classList
					classList.addAll(findClassesInFile(fileInDirectory));
				}
			}
		} else {
			if (file.getName().endsWith(".java")) {
				classList.addAll(findClassesInFile(file));
				LOG.info("classes of file are added to class list");
			} else {
				LOG.warn(file.toString() + " is an empty directory");
			}
		}
		return classList;
	}

	public static List<ClassOrInterfaceDeclaration> findClassesInFile(File file) {
		TypeSolver typeSolver = new CombinedTypeSolver();
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
		StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
		CompilationUnit cu = null;
		try {
			LOG.debug("parsing: " + file.getPath());
			cu = StaticJavaParser.parse(file);
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage(), e);
			return Collections.emptyList();
		}
		return cu.findAll(ClassOrInterfaceDeclaration.class);
	}
}
