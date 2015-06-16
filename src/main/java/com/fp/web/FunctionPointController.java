package com.fp.web;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.fp.domain.SystemContext;

@RestController
public class FunctionPointController {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@RequestMapping("/index/rest")
	public List<SystemContext> showSystemContext(Model model, HttpServletRequest request) {

		List<SystemContext> actors = this.jdbcTemplate
				.query("select systemcontextid, version, name, notes, diagram from systemcontext where not deleteflag",
						new RowMapper<SystemContext>() {
							public SystemContext mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								SystemContext actor = new SystemContext();
								actor.setName(rs.getString("name"));
								actor.setNotes(rs.getString("notes"));
								return actor;
							}
						});

		model.addAttribute("systemcontextlist", actors);

		return actors;
	}
}