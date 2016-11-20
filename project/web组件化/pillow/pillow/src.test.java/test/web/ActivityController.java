package test.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import test.model.AjaxResponseInfo;


/**
 * The Class ActivityController.
 */
@Controller
public class ActivityController {

	/**
	 * 查询活动列表.
	 * 
	 * @param activity
	 *            the activity
	 * @param pageBean
	 *            the page bean
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 * @return
	 */
	@Autowired
	private AjaxResponseInfo info;
	@RequestMapping(value = "/mypillow1/test")
	public String queryActivity(HttpServletRequest request, HttpServletResponse response) {
//		AjaxResponseInfo info = new AjaxResponseInfo();
//		info.setCode("200");
//		info.setData("whh");
		try {
			response.getWriter().print(info.getMsg());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
