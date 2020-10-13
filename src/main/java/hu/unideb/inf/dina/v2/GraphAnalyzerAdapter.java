package hu.unideb.inf.dina.v2;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import hu.unideb.inf.dina.commons.analysis.GraphAnalyzer;
import hu.unideb.inf.dina.commons.analysis.GraphAnalyzerDiscoverer;

public class GraphAnalyzerAdapter implements GraphAnalyzerDiscoverer {

	private Reflections ref;

	@Override
	public void init(@NotNull ClassLoader classLoader) {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage("hu.unideb.inf.dina.v2.algorithms", classLoader))
				.setScanners(new SubTypesScanner(false));
		configurationBuilder.addClassLoader(classLoader);
		ref = new Reflections(configurationBuilder);
	}

	@NotNull
	@Override
	public List<GraphAnalyzer> getGraphAnalyzers() {
		return getGraphAnalyzerClasses()
				.stream()
				.map(this::createInstance)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@NotNull
	@Override
	public GraphAnalyzer getGraphAnalyzer(@NotNull String id) {
		return getGraphAnalyzerClasses()
				.stream()
				.filter(clazz -> clazz.getName().equals(id))
				.findFirst()
				.map(this::createInstance)
				.orElseThrow(RuntimeException::new);
	}

	public Integer getAllTypes() {
		return ref.getSubTypesOf(GraphAnalyzer.class).size();
	}

	private Set<Class<? extends GraphAnalyzer>> getGraphAnalyzerClasses() {
		return ref.getSubTypesOf(GraphAnalyzer.class);
	}

	private GraphAnalyzer createInstance(Class<? extends GraphAnalyzer> clazz) {
		return Stream
				.of(clazz.getDeclaredConstructors())
				.filter(constructor -> constructor.getParameterCount() == 0)
				.findFirst()
				.map(constructor -> {
					try {
						return (GraphAnalyzer) constructor.newInstance();
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				})
				.orElseThrow(RuntimeException::new);
	}
}
