package ${modelPath}.service;


import com.common.base.service.BaseService;
import ${modelPath}.dao.${modelName}Mapper;
import ${modelPath}.entity.${modelName};

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ${modelName} Service.
 *
 * @author: scaffold
 * @version: V 1.0
 * @date: ${createDate}
 */
@Service("${modelName?uncap_first}Service")
public class ${modelName}Service extends BaseService<${modelName}> {
    private static final Logger log= Logger.getLogger(${modelName}Service.class);
    
    @Autowired
    private ${modelName}Mapper<${modelName}> mapper;
    
    
    public ${modelName}Mapper<${modelName}> getDao() {
        return mapper;
    }
    
    //**********自定义方法***********
    
}
