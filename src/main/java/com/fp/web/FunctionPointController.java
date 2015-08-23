package com.fp.web;

import com.fp.dao.SystemContextRepository;
import com.fp.domain.SystemContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class FunctionPointController {

	private SystemContextRepository repository;


	@Autowired
	public void setRepository(SystemContextRepository repository)
	{
		this.repository = repository;
	}


	@RequestMapping("/index/rest")
	public List<SystemContext> showSystemContext(Model model, HttpServletRequest request) {

		List<SystemContext> systemContextList = repository.getSystemContexts();

		model.addAttribute("systemcontextlist", systemContextList);

		return systemContextList;
	}
}