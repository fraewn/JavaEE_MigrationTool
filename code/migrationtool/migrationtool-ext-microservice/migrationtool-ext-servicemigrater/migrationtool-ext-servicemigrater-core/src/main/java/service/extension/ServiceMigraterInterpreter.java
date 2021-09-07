package service.extension;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.Option;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.printer.Printer;
import com.github.javaparser.printer.configuration.DefaultConfigurationOption;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration.ConfigOption;
import com.github.javaparser.printer.configuration.PrinterConfiguration;

import exceptions.MigrationToolRuntimeException;
import migration.Converter;
import migration.api.MemberVisitorOrder;
import migration.api.MigrationConfig;
import migration.model.MigrationModel;
import migration.model.data.UseCase;
import migration.model.erm.Entity;
import migration.model.serviceDefintion.MicroService;
import migration.model.serviceDefintion.ServiceRelation;
import migration.utils.JsonConverter;
import migration.validation.ModelValidator;
import model.Configurations;
import operations.InterpreterService;
import operations.dto.AstDTO;

/**
 * Implementation of the process step to create the real cut of the services
 */
public class ServiceMigraterInterpreter extends InterpreterService<Configurations, Object> {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	@Option(name = "-serviceId", usage = "Name of generated service")
	private String serviceId;

	@Option(name = "-pathResultFolder", usage = "path to result folder with the solved cluster")
	private String pathResultFolder;

	@Option(name = "-resultFolder", usage = "name of the result folder")
	private String resultFolder;
	/** Reference to the used model file of previous step */
	private File modelFile;
	/** Reference to the used model */
	private MigrationModel model;
	/** Reference to the used configuration */
	private MigrationConfig config;
	/** Reference to all classes */
	private List<AstDTO> classes;

	public ServiceMigraterInterpreter() {
		this.config = MigrationConfig.SPRING;
		this.classes = new ArrayList<>();
	}

	@Override
	public Object run(Configurations input) {
		initialize(input.getModelFile());
		this.classes = input.getInput();
		try {
			execute();
		} catch (IOException e) {
			LOG.error(e.getMessage());
			throw new MigrationToolRuntimeException(e.getMessage());
		}
		return null;
	}

	private void initialize(String input) {
		this.modelFile = new File(input);
		this.model = createModel();
		this.pathResultFolder = this.pathResultFolder == null ? "" : this.pathResultFolder;
		this.resultFolder = this.resultFolder == null ? "result" : this.resultFolder;
	}

	private MigrationModel createModel() {
		MigrationModel model = JsonConverter.readJsonFromFile(this.modelFile, MigrationModel.class);
		ModelValidator.validate(model);
		return model;
	}

	private void execute() throws IOException {
		File f = new File(this.pathResultFolder + this.resultFolder);
		if (f.isDirectory()) {
			deleteDirAndContent(f.getPath());
		}
		f.mkdirs();

		Converter cc = this.config.getConverter();
		for (MicroService service : this.model.getServices()) {
			if ((this.serviceId == null) || this.serviceId.equals(service.getName())) {
				File serviceFolder = new File(f, service.getName());
				serviceFolder.mkdirs();

				List<CompilationUnit> allClasses = new ArrayList<>();
				// Entites
				for (Entity entity : service.getEntities()) {
					AstDTO dto = findClass(entity.getName());
					CompilationUnit entityClass = cc.createEntity(entity, dto);
					CompilationUnit repository = cc.createRepository(entity);
					allClasses.add(entityClass);
					allClasses.add(repository);
				}
				// Use Cases
				Map<String, List<UseCase>> map = useCasesPerClass(service);
				for (Entry<String, List<UseCase>> entry : map.entrySet()) {
					allClasses.addAll(cc.createService(entry.getKey(), entry.getValue()));
				}
				// published language
				for (ServiceRelation relation : service.getRelatedRelations()) {
					CompilationUnit unit = cc.publishLanguage(service.getName(), relation, service.getEntities());
					if (unit != null) {
						allClasses.add(unit);
					}
				}
				// Start class
				CompilationUnit unit = cc.createStartClass();
				allClasses.add(unit);

				cc.processAllClasses(allClasses);

				File javaFolder = new File(serviceFolder, "/src/main/java/");
				javaFolder.mkdirs();
				createJavaFiles(javaFolder.getPath(), allClasses);

				Map<String, String> resources = cc.generateSpecificFiles();
				for (Entry<String, String> entry : resources.entrySet()) {
					String path = entry.getKey().contains("/")
							? entry.getKey().substring(0, entry.getKey().lastIndexOf("/"))
							: "";
					String name = entry.getKey().contains("/")
							? entry.getKey().substring(entry.getKey().lastIndexOf("/") + 1)
							: entry.getKey();
					createResources(serviceFolder + (!path.isBlank() ? "/" + path : ""), name, entry.getValue());
				}
			}
		}
	}

	private Map<String, List<UseCase>> useCasesPerClass(MicroService service) {
		Map<String, List<UseCase>> map = new HashMap<>();
		for (UseCase useCase : service.getRelatedUseCases()) {
			String context = useCase.getName().substring(0, useCase.getName().lastIndexOf("."));
			if (map.containsKey(context)) {
				map.get(context).add(useCase);
			} else {
				map.put(context, new ArrayList<>(List.of(useCase)));
			}
		}
		return map;
	}

	private void createJavaFiles(String path, List<CompilationUnit> allClasses) throws IOException {
		PrinterConfiguration conf = new DefaultPrinterConfiguration();
		conf.addOption(new DefaultConfigurationOption(ConfigOption.ORDER_IMPORTS));
		Printer prettyPrinter = new DefaultPrettyPrinter(conf);
		for (CompilationUnit compilationUnit : allClasses) {
			MemberVisitorOrder.order(compilationUnit);
			String name = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).get().getNameAsString();
			String tmp = compilationUnit.getPackageDeclaration().get().getNameAsString().replace(".", "/");
			File packageDef = new File(path + "/" + tmp);
			packageDef.mkdirs();
			Path file = Paths.get(packageDef + "/" + name + ".java");
			Files.write(file, Arrays.asList(prettyPrinter.print(compilationUnit).split(System.lineSeparator())),
					StandardCharsets.UTF_8);
		}
	}

	private void createResources(String path, String fileName, String content) throws IOException {
		File resource = new File(path);
		resource.mkdirs();
		Path file = Paths.get(path + "/" + fileName);
		List<String> lines = Arrays.asList(content.split(System.lineSeparator()));
		Files.write(file, lines, StandardCharsets.UTF_8);
	}

	private void deleteDirAndContent(String path) throws IOException {
		Path directory = Paths.get(path);
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private AstDTO findClass(String name) {
		for (AstDTO astDTO : this.classes) {
			if (astDTO.getFullName().equals(name)) {
				return astDTO;
			}
		}
		return null;
	}
}
