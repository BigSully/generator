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
package org.mybatis.generator.plugins;

import org.apache.log4j.Logger;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.*;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class CustomizeNames extends PluginAdapter {

    static Logger log = Logger.getLogger(PluginAdapter.class);

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        //change default method name and sql mapper name
        introspectedTable.setSelectAllStatementId(System.getProperty("selectAllStatementId", "findAll"));
        introspectedTable.setDeleteByPrimaryKeyStatementId(System.getProperty("deleteByPrimaryKeyStatementId", "deleteById"));
        introspectedTable.setInsertStatementId(System.getProperty("insertStatementId", "add"));
        introspectedTable.setSelectByPrimaryKeyStatementId(System.getProperty("selectByPrimaryKeyStatementId", "findById"));
        introspectedTable.setUpdateByPrimaryKeyStatementId(System.getProperty("updateByPrimaryKeyStatementId", "updateById"));
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return true;  // false to disable updateByPrimaryKeySelective(interface method)
    }
    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return true;  // false to disable updateByPrimaryKeySelective(mapper xml)
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;  // false to disable insertSelective(interface method)
    }
    
    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;  // false to disable insertSelective(mapper xml)
    }

    // add additional mapper methods here
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        StringBuilder columnNames = new StringBuilder();
        List<IntrospectedColumn> cols = introspectedTable.getAllColumns();
        for(IntrospectedColumn col : cols){
            String colName = col.getActualColumnName();
            columnNames.append(colName);
            columnNames.append(", ");

            //如果表的列名中包含name，那么就生成按该列进行查找的语句
            if(colName.contains("name")){
                Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
                importedTypes.add(FullyQualifiedJavaType.getNewListInstance());

                Method method = new Method();
                method.setVisibility(JavaVisibility.PUBLIC);

                FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getNewListInstance();
                FullyQualifiedJavaType listType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());

                importedTypes.add(listType);
                returnType.addTypeArgument(listType);
                method.setReturnType(returnType);
                method.addParameter(new Parameter(new FullyQualifiedJavaType("java.lang.String"), colName));
                String capitalize = Character.toUpperCase(colName.charAt(0)) + colName.substring(1);
                method.setName("findBy"+capitalize);

                interfaze.addMethod(method); // add method clause
                interfaze.addImportedTypes(importedTypes);  // add package clause
            }
        }
        interfaze.addJavaDocLine("//cols:" + columnNames);

        log.debug("#context available: ");
        log.debug("#full model type name:" + introspectedTable.getBaseRecordType());
        log.debug("#table name with schema:" + introspectedTable.getFullyQualifiedTableNameAtRuntime());
        log.debug("#short table name: " + introspectedTable.getFullyQualifiedTable());

        return true;
    }

    // add additional mapper sql here
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> cols = introspectedTable.getAllColumns();
        for(IntrospectedColumn col : cols) {
            String colName = col.getActualColumnName();

            //如果表的列名中包含name，那么就生成按该列进行查找的语句
            if (colName.contains("name")) {
                String capitalize = Character.toUpperCase(colName.charAt(0)) + colName.substring(1);
                String name = "findBy"+capitalize;
                addElements(document.getRootElement(), introspectedTable, name, col);
            }
        }
        return true;
    }


    public void addElements(XmlElement parentElement, IntrospectedTable introspectedTable, String id, IntrospectedColumn introspectedColumn) {
        XmlElement answer = new XmlElement("select");

        answer.addAttribute(new Attribute("id", id));
        answer.addAttribute(new Attribute("resultMap",
                introspectedTable.getBaseResultMapId()));

        String parameterType;
        // PK fields are in the base class. If more than on PK
        // field, then they are coming in a map.
        if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
            parameterType = "map";
        } else {
            parameterType = introspectedColumn.getFullyQualifiedJavaType().toString();
        }

        answer.addAttribute(new Attribute("parameterType",
                parameterType));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("select ");

        if (stringHasValue(introspectedTable.getSelectByPrimaryKeyQueryId())) {
            sb.append('\'');
            sb.append(introspectedTable.getSelectByPrimaryKeyQueryId());
            sb.append("' as QUERYID,");
        }

        Iterator<IntrospectedColumn> iter = introspectedTable.getAllColumns()
                .iterator();
        while (iter.hasNext()) {
            sb.append(MyBatis3FormattingUtilities.getSelectListPhrase(iter
                    .next()));

            if (iter.hasNext()) {
                sb.append(", ");
            }

            if (sb.length() > 80) {
                answer.addElement(new TextElement(sb.toString()));
                sb.setLength(0);
            }
        }

        if (sb.length() > 0) {
            answer.addElement(new TextElement(sb.toString()));
        }

        sb.setLength(0);
        sb.append("from ");
        sb.append(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        boolean and = false;
        sb.setLength(0);
        if (and) {
            sb.append("  and ");
        } else {
            sb.append("where ");
            and = true;
        }

        sb.append(MyBatis3FormattingUtilities
                .getAliasedEscapedColumnName(introspectedColumn));
        sb.append(" = ");
        sb.append(MyBatis3FormattingUtilities
                .getParameterClause(introspectedColumn));
        answer.addElement(new TextElement(sb.toString()));

        if (context.getPlugins().sqlMapSelectByPrimaryKeyElementGenerated(
                answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
