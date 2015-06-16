package com.fp.web;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.fp.domain.DataField;
import com.fp.domain.DataGroup;
import com.fp.domain.FunctionalProcess;
import com.fp.domain.FunctionalSubProcess;
import com.fp.domain.SystemContext;

@Controller
public class FunctionalProcessController {

	private JdbcTemplate jdbcTemplate;

	private SystemContext systemContext;

	private FunctionalProcess functionalProcess;
	
	private String err;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@RequestMapping(value = "/disp-functional-processes", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String dispFunctionalProcess(Model model, HttpServletRequest request,
			HttpSession session) {

		if (session.getAttribute("systemcontextname") != null) {
			String name = (String) session.getAttribute("systemcontextname");
			List<FunctionalProcess> actors = this.jdbcTemplate
					.query("select functionalprocessid, version, name, notes from functionalprocess where systemcontextid = ( select systemcontextid from systemcontext where name = '"
							+ name + "') and not deleteflag order by functionalprocessid",
							new RowMapper<FunctionalProcess>() {
								public FunctionalProcess mapRow(ResultSet rs, int rowNum)
										throws SQLException {
									FunctionalProcess actor = new FunctionalProcess();
									actor.setName(rs.getString("name"));
									actor.setNotes(rs.getString("notes"));
									return actor;
								}
							});

			model.addAttribute("functionalprocesslist", actors);
		}

		// this.dataGroup = null;
		// model.addAttribute("datagroup", this.dataGroup);

		return "/define-functional-processes";
	}

	@RequestMapping("/define-functional-processes")
	public String showFunctionalProcess(Model model, HttpServletRequest request,
			HttpSession session) {

		long functionalProcessId = 0l;
		
		List<FunctionalSubProcess> functionalsubprocesslist = null;
		
		List<FunctionalProcess> actors = null;

		if (session.getAttribute("systemcontextname") != null) {
			String name = (String) session.getAttribute("systemcontextname");
			actors = this.jdbcTemplate
					.query("select functionalprocessid, version, name, notes from functionalprocess where systemcontextid = ( select systemcontextid from systemcontext where name = '"
							+ name + "') and not deleteflag order by functionalprocessid",
							new RowMapper<FunctionalProcess>() {
								public FunctionalProcess mapRow(ResultSet rs, int rowNum)
										throws SQLException {
									FunctionalProcess actor = new FunctionalProcess();
									actor.setName(rs.getString("name"));
									actor.setNotes(rs.getString("notes"));
									return actor;
								}
							});

			model.addAttribute("functionalprocesslist", actors);

			if (actors.size() > 0) {
				functionalProcessId = actors.get(0).getFunctionalProcessId();
			}

			/*
			 * List<DataField> datafieldlist = this.jdbcTemplate .query(
			 * "select datafieldid, version, name from datafield where datagroupid in (select datagroupid from datagroup where systemcontextid = ( select systemcontextid from systemcontext where name = '"
			 * + name + "'))", new RowMapper<DataField>() { public DataField
			 * mapRow(ResultSet rs, int rowNum) throws SQLException { DataField
			 * actor = new DataField(); actor.setName(rs.getString("name"));
			 * return actor; } });
			 */
			functionalsubprocesslist = this.jdbcTemplate
					.query("select functionalsubprocessid, functionalprocessid, version, name from functionalsubprocess where not deleteflag and functionalprocessid = "
							+ functionalProcessId, new RowMapper<FunctionalSubProcess>() {
						public FunctionalSubProcess mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							FunctionalSubProcess actor = new FunctionalSubProcess();
							actor.setFunctionalSubProcessId(rs.getLong("functionalsubprocessid"));
							actor.setFunctionalProcessId(rs.getLong("functionalprocessid"));
							actor.setName(rs.getString("name"));
							return actor;
						}
					});

			model.addAttribute("functionalsubprocesslist", functionalsubprocesslist);
		}

		model.addAttribute("functionalprocess", this.functionalProcess);

		return "define-functional-processes";
	}

	@RequestMapping(value = "/create-new-functional-process", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String createSystemContext(Model model, HttpServletRequest request,
			HttpSession session) {

		String contextname = (String) session.getAttribute("systemcontextname");

		String functionalprocessname = request.getParameter("functionalprocessname");
		String functionalprocessnotes = request.getParameter("functionalprocessnotes");
		String functionalsubprocessname = request.getParameter("functionalsubprocessname");
		int version = 0;
		long systemContextId = 0l;
		long functionalProcessId = 0l;

		if (contextname != null) {

			this.systemContext = this.jdbcTemplate
					.queryForObject(
							"select systemcontextid, version, name, notes, diagram from systemcontext where not deleteflag and name = '"
									+ contextname + "'",
							new RowMapper<SystemContext>() {
								public SystemContext mapRow(ResultSet rs,
										int rowNum) throws SQLException {
									SystemContext systemContexttmp = new SystemContext();
									systemContexttmp.setSystemContextId(rs
											.getLong("systemcontextid"));
									systemContexttmp.setName(rs
											.getString("name"));
									systemContexttmp.setNotes(rs
											.getString("notes"));
									return systemContexttmp;
								}
							});

			if (this.systemContext != null
					&& this.systemContext.getSystemContextId() != 0l) {
				systemContextId = this.systemContext.getSystemContextId();
			}

			if (systemContextId != 0l) {
				if (request.getParameter("option") != null
						&& request.getParameter("option").equals("1")) {

					String name = (String) session
							.getAttribute("systemcontextname");
					
					List<FunctionalProcess> functionalProcessList = this.jdbcTemplate
							.query(
									"select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = ( select systemcontextid from systemcontext where name = '"
											+ name
											+ "') and name = '"
											+ functionalprocessname + "'",
									new RowMapper<FunctionalProcess>() {
										public FunctionalProcess mapRow(ResultSet rs,
												int rowNum) throws SQLException {
											FunctionalProcess actor = new FunctionalProcess();
											actor.setFunctionalProcessId(rs
													.getLong("functionalprocessid"));
											actor.setName(rs.getString("name"));
											actor.setNotes(rs
													.getString("notes"));
											return actor;
										}
									});
					
					if (functionalProcessList.size() > 0)
					{
						err = "Data group already exist";
						model.addAttribute("err", err);
						return getFunctionalProcess(model, request, session);
					}
					
					this.jdbcTemplate
							.update(" insert into functionalprocess ( version, systemcontextid, name, notes, userid ) values ( "
									+ version
									+ ","
									+ systemContextId
									+ ",'"
									+ functionalprocessname
									+ "','"
									+ functionalprocessnotes
									+ "','"
									+ session.getAttribute("username") + "" 
									+ "')");

					session.setAttribute("functionalprocessname", functionalprocessname);

					this.functionalProcess = this.jdbcTemplate
							.queryForObject(
									"select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = ( select systemcontextid from systemcontext where name = '"
											+ name
											+ "') and name = '"
											+ functionalprocessname + "'",
									new RowMapper<FunctionalProcess>() {
										public FunctionalProcess mapRow(ResultSet rs,
												int rowNum) throws SQLException {
											FunctionalProcess actor = new FunctionalProcess();
											actor.setFunctionalProcessId(rs
													.getLong("functionalprocessid"));
											actor.setName(rs.getString("name"));
											actor.setNotes(rs
													.getString("notes"));
											return actor;
										}
									});

					if (this.functionalProcess != null
							&& this.functionalProcess.getFunctionalProcessId() != 0l) {
						functionalProcessId = this.functionalProcess.getFunctionalProcessId();
					}

					if (functionalProcessId != 0l && functionalsubprocessname != null
							&& !functionalsubprocessname.isEmpty()) {

						this.jdbcTemplate
						.update(" update functionalsubprocess set version = version + 1 where functionalprocessid = "
								+ functionalProcessId
								);

						
						
						
						this.jdbcTemplate
								.update(" insert into functionalsubprocess ( version, functionalprocessid, name, userid ) values ( "
										+ version
										+ ","
										+ functionalProcessId
										+ ",'"
										+ functionalsubprocessname 
										+ "','"
										+ session.getAttribute("username") + "" 
										+ "')");

					}

				} else if (request.getParameter("option") != null
						&& request.getParameter("option").equals("0")) {

				/*	this.jdbcTemplate
							.update(" delete from functionalsubprocess where functionalprocessid in (select functionalprocessid from functionalprocess where systemcontextid = "
									+ systemContextId
									+ " and name = '"
									+ functionalprocessname + "')");

					this.jdbcTemplate
							.update(" delete from functionalprocess where name = '"
									+ functionalprocessname
									+ "' and systemcontextid = "
									+ systemContextId);  */
					
					this.jdbcTemplate
					.update(" update functionalsubprocess set deleteflag = true where functionalprocessid in (select functionalprocessid from functionalprocess where systemcontextid = "
							+ systemContextId
							+ " and name = '"
							+ functionalprocessname + "')");

			this.jdbcTemplate
					.update(" update functionalprocess set deleteflag = true where name = '"
							+ functionalprocessname
							+ "' and systemcontextid = "
							+ systemContextId);
					
					return dispFunctionalProcess(model,request,session);
				}

				else if (request.getParameter("option") != null
						&& request.getParameter("option").equals("2")) {
					return "define-functional-processes";
					
				} else if (request.getParameter("option") != null
						&& request.getParameter("option").equals("3")) {
					return "define-functional-processes";
				} else if (request.getParameter("option") != null
						&& request.getParameter("option").equals("4")) {

					String name = (String) session
							.getAttribute("systemcontextname");

					this.functionalProcess = this.jdbcTemplate
							.queryForObject(
									"select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = ( select systemcontextid from systemcontext where name = '"
											+ name
											+ "') and name = '"
											+ functionalprocessname + "'",
									new RowMapper<FunctionalProcess>() {
										public FunctionalProcess mapRow(ResultSet rs,
												int rowNum) throws SQLException {
											FunctionalProcess actor = new FunctionalProcess();
											actor.setFunctionalProcessId(rs
													.getLong("functionalprocessid"));
											actor.setName(rs.getString("name"));
											actor.setNotes(rs
													.getString("notes"));
											return actor;
										}
									});

					if (this.functionalProcess != null
							&& this.functionalProcess.getFunctionalProcessId() != 0l) {
						functionalProcessId = this.functionalProcess.getFunctionalProcessId();
					}

					if (functionalProcessId != 0l && functionalsubprocessname != null
							&& !functionalsubprocessname.isEmpty()) {

						this.jdbcTemplate
						.update(" update functionalsubprocess set version = version + 1 where functionalprocessid = "
								+ functionalProcessId
								);
						
						this.jdbcTemplate
								.update(" insert into functionalsubprocess ( version, functionalprocessid, name, userid ) values ( "
										+ version
										+ ","
										+ functionalProcessId
										+ ",'"
										+ functionalsubprocessname 
										+ "','"
										+ session.getAttribute("username") + ""
										+ "')");

					}
				}
				
				else if (request.getParameter("option") != null
						&& request.getParameter("option").equals("5")) {

					long functionalSubProcessId = 0l;
					
					if (request.getParameter("delete") != null) {
						functionalSubProcessId = Long.parseLong(request
								.getParameter("delete"));
					}

			/*		this.jdbcTemplate
							.update(" delete from functionalsubprocess where functionalsubprocessid = " + functionalSubProcessId);    */
					
							this.jdbcTemplate
					.update(" update functionalsubprocess set deleteflag = true where functionalsubprocessid = " + functionalSubProcessId);    

				}

			}

		}

		this.functionalProcess = null;

		if (functionalprocessname == null || functionalprocessname.equals("")
				|| functionalprocessnotes == null || functionalprocessnotes.equals("") || (request.getParameter("option") != null
						&& request.getParameter("option").equals("0"))) {
			return "define-functional-processes";
		} else {
			return getFunctionalProcess(model, request, session);
		}

	}

	@RequestMapping(value = "/show-functional-processes", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String getFunctionalProcess(Model model, HttpServletRequest request,
			HttpSession session) {

		if (session.getAttribute("systemcontextname") != null) {
			String name = (String) session.getAttribute("systemcontextname");
			String functionalprocessname = request.getParameter("functionalprocessname");
			List<FunctionalProcess> actors = this.jdbcTemplate
					.query("select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = ( select systemcontextid from systemcontext where name = '"
							+ name + "')", new RowMapper<FunctionalProcess>() {
						public FunctionalProcess mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							FunctionalProcess actor = new FunctionalProcess();
							actor.setName(rs.getString("name"));
							actor.setNotes(rs.getString("notes"));
							return actor;
						}
					});

			model.addAttribute("functionalprocesslist", actors);

			if (functionalprocessname != null && !functionalprocessname.equals("")) {

				this.functionalProcess = this.jdbcTemplate
						.queryForObject(
								"select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = ( select systemcontextid from systemcontext where name = '"
										+ name
										+ "') and name = '"
										+ functionalprocessname + "'",
								new RowMapper<FunctionalProcess>() {
									public FunctionalProcess mapRow(ResultSet rs,
											int rowNum) throws SQLException {
										FunctionalProcess actor = new FunctionalProcess();
										actor.setFunctionalProcessId(rs
												.getLong("functionalprocessid"));
										actor.setName(rs.getString("name"));
										actor.setNotes(rs.getString("notes"));
										return actor;
									}
								});
			}

			model.addAttribute("functionalprocess", this.functionalProcess);

			List<FunctionalSubProcess> functionalsubprocesslist = this.jdbcTemplate
					.query("select functionalsubprocessid, functionalprocessid, version, name from functionalsubprocess where not deleteflag and functionalprocessid = "
							+ this.functionalProcess.getFunctionalProcessId(),
							new RowMapper<FunctionalSubProcess>() {
								public FunctionalSubProcess mapRow(ResultSet rs, int rowNum)
										throws SQLException {
									FunctionalSubProcess datafield = new FunctionalSubProcess();
									datafield.setFunctionalSubProcessId(rs
											.getLong("functionalsubprocessid"));
									datafield.setFunctionalProcessId(rs
											.getLong("functionalprocessid"));
									datafield.setVersion(rs.getInt("version"));
									datafield.setName(rs.getString("name"));
									return datafield;
								}
							});

			model.addAttribute("functionalsubprocesslist", functionalsubprocesslist);

		}

		return "define-functional-processes";
	}

}