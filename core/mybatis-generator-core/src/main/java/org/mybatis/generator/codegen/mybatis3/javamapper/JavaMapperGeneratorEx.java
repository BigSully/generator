/**
 *    Copyright 2006-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.javamapper;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.*;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author Jeff Butler
 * 
 */
public class JavaMapperGeneratorEx extends JavaMapperGenerator {

    /**
     *
     */
    public JavaMapperGeneratorEx() {
        super(true);
    }

    public JavaMapperGeneratorEx(boolean requiresMatchedXMLGenerator) {
        super(requiresMatchedXMLGenerator);
    }
    
    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaMapperType());
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(interfaze);

        String rootInterface = introspectedTable
            .getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration()
                .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
                    rootInterface);
            interfaze.addSuperInterface(fqjt);
            interfaze.addImportedType(fqjt);
        }
        //change order
        addSelectByPrimaryKeyMethod(interfaze);

        addUpdateByPrimaryKeySelectiveMethod(interfaze);
        addUpdateByPrimaryKeyWithBLOBsMethod(interfaze);
        addUpdateByPrimaryKeyWithoutBLOBsMethod(interfaze);

        addInsertMethod(interfaze);

        addDeleteByPrimaryKeyMethod(interfaze);

        addCountByExampleMethod(interfaze);
        addDeleteByExampleMethod(interfaze);
        addInsertSelectiveMethod(interfaze);
        addSelectByExampleWithBLOBsMethod(interfaze);
        addSelectByExampleWithoutBLOBsMethod(interfaze);
        addUpdateByExampleSelectiveMethod(interfaze);
        addUpdateByExampleWithBLOBsMethod(interfaze);
        addUpdateByExampleWithoutBLOBsMethod(interfaze);


        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (context.getPlugins().clientGenerated(interfaze, null,
                introspectedTable)) {
            answer.add(interfaze);
        }
        
        List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits();
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }

        return answer;
    }

    @Override
    public AbstractXmlGenerator getMatchedXMLGenerator() {
        return new XMLMapperGeneratorEx();
    }


    public class XMLMapperGeneratorEx extends XMLMapperGenerator {

        public XMLMapperGeneratorEx() {
            super();
        }

        protected XmlElement getSqlMapElement() {
            FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
            progressCallback.startTask(getString(
                    "Progress.12", table.toString())); //$NON-NLS-1$
            XmlElement answer = new XmlElement("mapper"); //$NON-NLS-1$
            String namespace = introspectedTable.getMyBatis3SqlMapNamespace();
            answer.addAttribute(new Attribute("namespace", //$NON-NLS-1$
                    namespace));

            context.getCommentGenerator().addRootComment(answer);

            //change order
            addSelectByPrimaryKeyElement(answer);

            addUpdateByPrimaryKeySelectiveElement(answer);
            addUpdateByPrimaryKeyWithBLOBsElement(answer);
            addUpdateByPrimaryKeyWithoutBLOBsElement(answer);

            addInsertElement(answer);
            addDeleteByPrimaryKeyElement(answer);


            addResultMapWithoutBLOBsElement(answer);
            addResultMapWithBLOBsElement(answer);
            addExampleWhereClauseElement(answer);
            addMyBatis3UpdateByExampleWhereClauseElement(answer);
            addBaseColumnListElement(answer);
            addBlobColumnListElement(answer);
            addSelectByExampleWithBLOBsElement(answer);
            addSelectByExampleWithoutBLOBsElement(answer);
            addDeleteByExampleElement(answer);
            addInsertSelectiveElement(answer);
            addCountByExampleElement(answer);
            addUpdateByExampleSelectiveElement(answer);
            addUpdateByExampleWithBLOBsElement(answer);
            addUpdateByExampleWithoutBLOBsElement(answer);


            return answer;
        }

    }
}
