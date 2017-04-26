/**
 *    Copyright 2006-2016 the original author or authors.
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

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

public class CustomizeNames extends PluginAdapter {

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
        return false;  //disable updateByPrimaryKeySelective(interface method)
    }
    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;  //disable updateByPrimaryKeySelective(mapper xml)
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;  //disable insertSelective(interface method)
    }
    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;  //disable insertSelective(mapper xml)
    }

    // add additional mapper methods here
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return true;
    }

    // add additional mapper sql here
    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        return true;
    }
}
