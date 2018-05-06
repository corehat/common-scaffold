package ${modelPath}.controller;

import com.common.base.controller.BaseController;
import ${modelPath}.entity.${modelName};
import ${modelPath}.service.${modelName}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ${modelName} Controller.
 *
 * @author: scaffold
 * @version: V 1.0
 * @date: ${createDate}
 */
@Controller
public class ${modelName}Controller extends BaseController {

    @Autowired
    private ${modelName}Service ${modelName?uncap_first}Service;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/${modelName?uncap_first}/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="${modelName?uncap_first}/list";
        return new ModelAndView(returnView);
    }
}
