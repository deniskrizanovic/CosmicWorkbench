package com.fp.web;

import com.fp.domain.SystemContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Controller
public class HomeController {

	private JdbcTemplate jdbcTemplate;

	private SystemContext systemContext;

	private List<SystemContext> systemContextList;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@RequestMapping("/")
	public String showSystemContext(Model model, HttpServletRequest request,
			HttpSession session) {

		List<SystemContext> actors = this.jdbcTemplate
				.query("select systemcontextid, version, name, notes, diagram from systemcontext where not deleteflag",
						new RowMapper<SystemContext>() {
							public SystemContext mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								SystemContext actor = new SystemContext();
								actor.setName(rs.getString("name"));
								actor.setNotes(rs.getString("notes"));
								actor.setSystemContextId(rs
										.getLong("systemcontextid"));
								return actor;
							}
						});

		model.addAttribute("systemcontextlist", actors);

		if (session.getAttribute("username") != null) {
			model.addAttribute("username",
					session.getAttribute("username"));
		} else {
			model.addAttribute("username", "");
		}

		return "index";
	}

	@RequestMapping(value = "/system-context-ATPExample", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String show(Model model, HttpServletRequest request,
			HttpSession session) {

		String username = request.getParameter("username");
		String name = request.getParameter("name");

		model.addAttribute("username", username);
		model.addAttribute("systemcontextid", name);
		session.setAttribute("username", username);
		session.setAttribute("systemcontextid", name);

		this.systemContext = this.jdbcTemplate
				.queryForObject(
						"select systemcontextid, version, name, notes, diagram from systemcontext where not deleteflag and systemcontextid = "
								+ name + "", new RowMapper<SystemContext>() {
							public SystemContext mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								SystemContext actor = new SystemContext();
								actor.setSystemContextId(rs
										.getLong("systemcontextid"));
								actor.setName(rs.getString("name"));
								actor.setNotes(rs.getString("notes"));
								return actor;
							}
						});

		if (this.systemContext != null) {
			session.setAttribute("systemcontextid",
					this.systemContext.getSystemContextId());
			model.addAttribute("contextname", this.systemContext.getName());
			session.setAttribute("systemcontextname",
					this.systemContext.getName());

		}

		return "system-context-ATPExample";
	}

	@RequestMapping(value = "/create-new-system-context", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String createSystemContext(Model model,
									  MultipartHttpServletRequest request, HttpSession session) {

		String contextname = request.getParameter("contextname");
		String notes = request.getParameter("notes");
		MultipartFile filetest = request.getFile("diagram");

		int version = 0;

		if (contextname != null) {

			if (request.getParameter("option").equals("1")) {

				try {

					File convFile = null;

					InputStream imageIs = null;

					LobHandler lobHandler = new DefaultLobHandler();

					if (filetest != null && !filetest.isEmpty()) {

						convFile = new File(filetest.getOriginalFilename());

						filetest.transferTo(new File(System
								.getProperty("java.io.tmpdir") + "/" + convFile));

						imageIs = new FileInputStream(
								System.getProperty("java.io.tmpdir") + "/"
										+ convFile);


					}

					this.jdbcTemplate
							.update(" insert into systemcontext ( version, name, notes, userid ) values ( "
									+ version
									+ ",'"
									+ contextname.replace("'", "''")
									+ "','"
									+ notes.replace("'", "''")
									+ "','"
									+ session.getAttribute("username")
									+ ""
									+ "')");

					session.setAttribute("systemcontextname", contextname);

					this.systemContext = this.jdbcTemplate
							.queryForObject(
									"select systemcontextid, version, name, notes, diagram from systemcontext where not deleteflag and name = '"
											+ contextname.replace("'", "''")
											+ "'",
									new RowMapper<SystemContext>() {
										public SystemContext mapRow(
												ResultSet rs, int rowNum)
												throws SQLException {
											SystemContext actor = new SystemContext();
											actor.setSystemContextId(rs
													.getLong("systemcontextid"));
											actor.setName(rs.getString("name"));
											actor.setNotes(rs
													.getString("notes"));
											return actor;
										}
									});

					if (this.systemContext != null) {
						session.setAttribute("systemcontextid",
								this.systemContext.getSystemContextId());

						if (filetest != null && !filetest.isEmpty()) {

							this.jdbcTemplate.update(
									" update systemcontext set diagram = ? where systemcontextid = "
											+ this.systemContext
											.getSystemContextId(),
									new Object[]{

											new SqlLobValue(imageIs, (int) convFile
													.length(), lobHandler)},
									new int[]{

											Types.BLOB});

						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				return "system-context-ATPExample";
			} else {

				this.jdbcTemplate
						.update(" update functionalmodeldatafield set deleteflag = true where functionalmodelid in (select functionalmodelid from functionalmodel where systemcontextid in "
								+ " (select systemcontextid from systemcontext where name = '"
								+ contextname + "'" + "))");

				this.jdbcTemplate
						.update(" update functionalmodel set deleteflag = true where systemcontextid in (select systemcontextid from systemcontext where name = '"
								+ contextname + "')");

				this.jdbcTemplate
						.update(" update functionalsubprocess set deleteflag = true where version = 0 and functionalprocessid in (select functionalprocessid from functionalprocess where systemcontextid in "
								+ " (select systemcontextid from systemcontext where name = '"
								+ contextname + "'" + "))");

				this.jdbcTemplate
						.update(" update functionalprocess set deleteflag = true where systemcontextid in (select systemcontextid from systemcontext where name = '"
								+ contextname + "')");

				this.jdbcTemplate
						.update(" update datafield set deleteflag = true where version = 0 and datagroupid in (select datagroupid from datagroup where systemcontextid in "
								+ " (select systemcontextid from systemcontext where name = '"
								+ contextname + "'" + "))");

				this.jdbcTemplate
						.update(" update datagroup set deleteflag = true where systemcontextid in (select systemcontextid from systemcontext where name = '"
								+ contextname + "')");

				this.jdbcTemplate
						.update(" update systemcontext set deleteflag = true where name = '"
								+ contextname + "'");

				session.setAttribute("systemcontextname", contextname);

				model.addAttribute("systemContext", this.systemContext);

				return "create-new-system-context";
			}
		} else {

			model.addAttribute("systemContext", this.systemContext);
			return "/";

		}
	}

	@RequestMapping(value = "/show-system-context", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String showCreateSystemContext(Model model,
			HttpServletRequest request, HttpSession session) {

		String username = request.getParameter("username");

		model.addAttribute("username", username);

		session.setAttribute("username", username);

		model.addAttribute("systemContext", this.systemContext);

		return "create-new-system-context";
	}

	@RequestMapping("/getdata")
	public String getData(Model model, HttpServletRequest request,
			HttpSession session) {

		String contextname = request.getParameter("contextname");

		if (contextname != null) {

			this.systemContextList = this.jdbcTemplate
					.query("select systemcontextid, version, name, notes, diagram from systemcontext where not deleteflag and name = '"
									+ contextname.replace("'", "''") + "'",
							new RowMapper<SystemContext>() {
								public SystemContext mapRow(ResultSet rs,
															int rowNum) throws SQLException {
									SystemContext systemContexttmp = new SystemContext();
									systemContexttmp.setName(rs
											.getString("name"));
									systemContexttmp.setNotes(rs
											.getString("notes"));
									return systemContexttmp;
								}
							});

			if (this.systemContextList.size() > 0) {
				this.systemContext = this.systemContextList.get(0);
			} else {
				this.systemContext = new SystemContext();
				this.systemContext.setName(contextname);
			}

			model.addAttribute("systemcontext", this.systemContext);

		}

		return "create-new-system-context";

	}

	@RequestMapping("/delete-system-context")
	public String deleteSystemContext(Model model, HttpServletRequest request) {

		String contextname = request.getParameter("contextname");

		if (contextname != null) {
			/*
			 * this.jdbcTemplate
			 * .update(" delete from systemcontext where name = '" + contextname
			 * + "'");
			 */
			this.jdbcTemplate
					.update(" update systemcontext set deleteflag = true where name = '"
							+ contextname + "'");

		}

		return "create-new-system-context";
	}

}