package com.x.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import com.x.quartz.job.Job;

/**
 * 基于Spring：扫描制定包名下的类
 * 
 * @author Jinpeng Cui
 *
 */
public class LoadPackageClasses {

    private static final Logger logger = LoggerFactory.getLogger(LoadPackageClasses.class);

	private static final String RESOURCE_PATTERN = "/**/*.class";

	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	private List<String> packagesList = new LinkedList<String>();
	
	private List<String> jobList = new LinkedList<String>();

	private List<TypeFilter> typeFilters = new LinkedList<TypeFilter>();

	private Set<Class<?>> classSet = new HashSet<Class<?>>();

	private static LoadPackageClasses loadPackageClasses;

	public LoadPackageClasses() {

	}

	public static LoadPackageClasses getInstance() {
		if (loadPackageClasses == null) {
			// loadPackageClasses = new LoadPackageClasses(new String[] { "com.x.quartz.job" }, Job.class);
			loadPackageClasses = new LoadPackageClasses(new String[] { "com.x.scheduler.job" }, Job.class);
		}
		return loadPackageClasses;
	}

	/**
	 * 构造函数
	 * 
	 * @param packagesToScan
	 *            指定哪些包需要被扫描,支持多个包"package.a,package.b"并对每个包都会递归搜索
	 * @param annotationFilter
	 *            指定扫描包中含有特定注解标记的bean,支持多个注解
	 */
	@SafeVarargs
	public LoadPackageClasses(String[] packagesToScan, Class<? extends Annotation>... annotationFilter) {
		if (packagesToScan != null) {
			for (String packagePath : packagesToScan) {
				this.packagesList.add(packagePath);
			}
		}
		if (annotationFilter != null) {
			for (Class<? extends Annotation> annotation : annotationFilter) {
				typeFilters.add(new AnnotationTypeFilter(annotation, false));
			}
		}
	}

	/**
	 * 将符合条件的Bean以Class集合的形式返回
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Set<Class<?>> getClassSet() throws IOException, ClassNotFoundException {
		this.classSet.clear();
		if (!this.packagesList.isEmpty()) {
			for (String pkg : this.packagesList) {
				String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
				Resource[] resources = this.resourcePatternResolver.getResources(pattern);
				MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
				for (Resource resource : resources) {
					if (resource.isReadable()) {
						MetadataReader reader = readerFactory.getMetadataReader(resource);
						String className = reader.getClassMetadata().getClassName();
						if (matchesEntityTypeFilter(reader, readerFactory)) {
							this.classSet.add(Class.forName(className));
						}
					}
				}
			}
		}
		// 输出日志
		if (logger.isInfoEnabled()) {
			for (Class<?> clazz : this.classSet) {
				logger.info(String.format("Found class:%s", clazz.getName()));
			}
		}
		return this.classSet;
	}

	/**
	 * 检查当前扫描到的Bean含有任何一个指定的注解标记
	 * 
	 * @param reader
	 * @param readerFactory
	 * @return
	 * @throws IOException
	 */
	private boolean matchesEntityTypeFilter(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
		if (!this.typeFilters.isEmpty()) {
			for (TypeFilter filter : this.typeFilters) {
				if (filter.match(reader, readerFactory)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		LoadPackageClasses lp = new LoadPackageClasses(new String[] { "com.x.quartz.job" }, Job.class);
		System.out.println(lp.getClassSet());
	}

}