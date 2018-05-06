package ${modelPath}.entity;

import com.common.base.entity.BaseEntity;

/**
 * .
 * 
 * @author: scaffold
 * @version: V 1.0
 * @date: ${createDate}
 */
public class ${modelName} extends BaseEntity {
    
    /**
     * 序列化Id.
     */
    private static final long serialVersionUID = 1L;

<#list columnList as column>
    /**
     * ${column.Comment}
     */
    private ${column.Jtype} ${nameFmt(column.Field)};
</#list>

<#list columnList as column>
    /**
     * @return <#if column.Comment!="">${column.Comment}</#if><#if column.Comment=="">${nameFmt(column.Field)}</#if>
     */
    public ${column.Jtype} get${nameFmt(column.Field)?cap_first}() {
        return ${nameFmt(column.Field)};
    }
    /**
     * @param ${nameFmt(column.Field)} ${column.Comment}
     */
    public void set${nameFmt(column.Field)?cap_first}(final ${column.Jtype} ${nameFmt(column.Field)}) {
        this.${nameFmt(column.Field)} = ${nameFmt(column.Field)};
    }
</#list>

}
