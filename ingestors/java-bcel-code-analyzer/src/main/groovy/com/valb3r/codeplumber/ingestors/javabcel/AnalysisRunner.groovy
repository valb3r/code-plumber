package com.valb3r.codeplumber.ingestors.javabcel

import com.valb3r.codeplumber.ingestors.javabcel.analysis.AppRegistry
import com.valb3r.codeplumber.ingestors.javabcel.analysis.ClassRegistry
import com.valb3r.codeplumber.ingestors.javabcel.analysis.JarAnalyzer
import com.valb3r.codeplumber.ingestors.javabcel.analysis.JarRegistry
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.Neo4j
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.indexer.Indexer
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.calls.MethodBodyActionsPersistor
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.entity.AppPersistor
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.entity.ClassPersistor
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.entity.JarPersistor
import org.apache.bcel.classfile.JavaClass
import org.neo4j.batchinsert.BatchInserter

class AnalysisRunner {
    private final int SMALL_POOL_SIZE = 1
    private final int POOL_SIZE = 5

    private final List<String> projectsToJars
    private final String regexClassInclude
    private final String regexClassExclude
    private final String regexMethodCallInclude
    private final String regexMethodCallExclude
    private final Neo4j neo4j
    private final BatchInserter inserter

    AnalysisRunner(
            String dbPath,
            List<String> projectsToJars,
            String regexClassInclude,
            String regexClassExclude,
            String regexMethodCallInclude,
            String regexMethodCallExclude
    ) {
        this.projectsToJars = projectsToJars
        this.regexClassInclude = regexClassInclude
        this.regexClassExclude = regexClassExclude
        this.regexMethodCallInclude = regexMethodCallInclude
        this.regexMethodCallExclude = regexMethodCallExclude
        this.neo4j = new Neo4j(new File(dbPath))
        this.inserter = neo4j.getInserter()
    }

    static void main(String[] args) {
        runAnalysis(
                '/tmp/neo4j-data-' + UUID.randomUUID(),
                ['/home/valb3r/IdeaProjects/open-banking-gateway/opba-embedded-starter/target=opba'],
                '.+',
                '',
                '.+',
                ''
        )
    }

    static void runAnalysis( String dbPath,
                             List<String> projectsToJars,
                             String regexClassInclude,
                             String regexClassExclude,
                             String regexMethodCallInclude,
                             String regexMethodCallExclude) {
        new AnalysisRunner(
                dbPath,
                projectsToJars,
                regexClassInclude,
                regexClassExclude,
                regexMethodCallInclude,
                regexMethodCallExclude
        ).analyze()
    }

    void analyze() {
        try {
            doAnalyze()
        } finally {
            Benchmark.method("Batch inserter shutdown", "") {
                inserter.shutdown()
            }
        }
    }

    private void doAnalyze() {
        def apps = new AppRegistry()
        def jars = new JarRegistry()
        def classes = new ClassRegistry()

        def processJarClasses = { String jarPath, Set<JavaClass> found, JarRegistry.JarDto jar, String product ->
            found.stream()
                    .filter { it.className.matches(regexClassInclude) && !it.className.matches(regexClassExclude) }
                    .each { classes.add(jarPath, product, it as JavaClass) }
        }

        Benchmark.method("Parse jars", "") {
            projectsToJars.forEach { dirsAndProduct ->
                def dirAndProductSplit = dirsAndProduct.split('=')
                def dirs = dirAndProductSplit[0].split(",")
                def product = dirAndProductSplit[1]
                def app = apps.add(product)

                Set<File> jarFiles = []
                dirs.each { dir ->
                    new File(dir).eachFileRecurse { file ->
                        if (file.name.endsWith('.jar')) {
                            jarFiles.add(file)
                        }
                    }
                }

                jarFiles.each { file ->
                    def jar = jars.add(file.toString(), app.name)
                    println "Loading JAR $file"
                    def found = new JarAnalyzer(file.toPath(), POOL_SIZE).getClasses()
                    println "Found ${found.size()} classes in $file"
                    processJarClasses(file.toString(), found, jar, product)
                }
            }
        }

        Benchmark.method("Persists applications", "${apps.registry.size()}") {
            new AppPersistor(SMALL_POOL_SIZE, apps, inserter).persist({ true })

        }

        Benchmark.method("Persists jars", "${jars.registry.size()}") {
            new JarPersistor(SMALL_POOL_SIZE, apps, jars, inserter).persist({ true })
        }

        Benchmark.method("Persists classes", "${classes.registry.size()}") {
            new ClassPersistor(POOL_SIZE, jars, classes, inserter).persist({ true })
        }

        Benchmark.method("Persists method body calls", "Before filter ${classes.registry.size()}") {
            new MethodBodyActionsPersistor(POOL_SIZE, classes, inserter)
                    .persist({
                        it.matches(regexMethodCallInclude) && !it.matches(regexMethodCallExclude)
                    })
        }

        Benchmark.method("Indexing data", "deferred") {
            new Indexer(inserter).createIndexes()
        }
    }
}
