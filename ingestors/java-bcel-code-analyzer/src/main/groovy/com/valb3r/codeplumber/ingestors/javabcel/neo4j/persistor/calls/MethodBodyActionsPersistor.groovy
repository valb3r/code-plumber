package com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.calls

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.calls.external.ExternalCallPersistor
import com.valb3r.codeplumber.ingestors.javabcel.analysis.ClassFileAnalyzer
import com.valb3r.codeplumber.ingestors.javabcel.analysis.ClassRegistry
import com.valb3r.codeplumber.ingestors.javabcel.analysis.method.MethodAnalyzer
import com.valb3r.codeplumber.ingestors.javabcel.analysis.method.finegrained.ClassNameReferenceAnalyzer
import com.valb3r.codeplumber.ingestors.javabcel.analysis.method.finegrained.ExternalCallAnalyzer
import com.valb3r.codeplumber.ingestors.javabcel.analysis.method.finegrained.FieldCallAnalyzer
import com.valb3r.codeplumber.ingestors.javabcel.analysis.method.finegrained.InMethodBodyAction
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.AbstractPersistor
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.calls.clazz.NameReferencerPersistor
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.calls.fields.FieldsCallPersistor
import org.neo4j.batchinsert.BatchInserter

class MethodBodyActionsPersistor extends AbstractPersistor<String, ClassRegistry.ClassDto> {

    private final ClassRegistry classRegistry

    MethodBodyActionsPersistor(int poolSize, ClassRegistry classRegistry,
                               BatchInserter inserter) {
        super(poolSize, ImmutableList.of(
                new Persistor("Method body actions", inserter, classRegistry)
        ))

        this.classRegistry = classRegistry
    }

    @Override
    Map<String, ClassRegistry.ClassDto> getObjectsByKey() {
        return classRegistry.getRegistry()
    }

    private static class Persistor extends AbstractPersistor.PersistStage<String, ClassRegistry.ClassDto,
            Map<ClassRegistry.MethodKey, List<InMethodBodyAction>>> {

        private final ClassRegistry classes

        private final Map<Class, AbstractInMethodActionPersistor> dispatchers = ImmutableMap.builder()
                .put(ExternalCallAnalyzer.ExternalMethodCallDto.class, new ExternalCallPersistor(inserter, classes))
                .put(FieldCallAnalyzer.FieldCallDto.class, new FieldsCallPersistor(inserter, classes))
                .put(ClassNameReferenceAnalyzer.ClassNameReferenceDto.class, new NameReferencerPersistor(inserter, classes))
                .build()

        Persistor(String name, BatchInserter batchInserter, ClassRegistry classes) {
            super(name, batchInserter)
            this.classes = classes
        }

        @Override
        protected Map<ClassRegistry.MethodKey, List<InMethodBodyAction>> doAnalyze(String className, ClassRegistry.ClassDto clazz) {
            def analyzer = new ClassFileAnalyzer(clazz.assignedClass)

            return clazz.assignedClass.methods.toList().collectEntries { method ->
                [(new ClassRegistry.MethodKey(method.getName(), method.getArgumentTypes())):
                         new MethodAnalyzer(analyzer, method).analyze()]
            }
        }

        @Override
        protected void doPersist(String keyName, ClassRegistry.ClassDto clazz,
                                 Map<ClassRegistry.MethodKey, List<InMethodBodyAction>> instructionsByMethod) {
            instructionsByMethod.forEach { method, instructions ->
                def actionsByDispatcher = instructions.groupBy {dispatchers.get(it.getClass())}

                actionsByDispatcher.forEach {dispatcher, calls ->
                    dispatcher?.persist(clazz.methods[method], classes, calls)
                }
            }
        }
    }
}
