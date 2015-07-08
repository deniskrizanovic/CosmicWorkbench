package com.fp.web;

import com.fp.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FunctionalModelController {

	private JdbcTemplate jdbcTemplate;

	private SystemContext systemContext;

	private String err;

	private DataGroup dataGroup;

	private FunctionalProcess functionalProcess;

	private FunctionalModel functionalModel;

	private int finalScore = 0;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@RequestMapping("/define-functional-model")
	public String showFunctionalModel(Model model, HttpServletRequest request,
			HttpSession session) {

		List<FunctionalSubProcess> functionalsubprocesslist = null;

		List<FunctionalProcess> actors = null;

		List<FunctionalProcess> actors1 = null;

		Long functionalprocessname = 0l;

		List<Report> report = null;
		List<FunctionalModel> functionalmodellist = null;
		List<FunctionalModel> distinctfunctionalmodellist = null;

		List<FunctionalModelDataField> functionalmodeldatafieldlist = null;

		List<FunctionalModelFunctionalSubProcess> functionalmodelfunctionalsubprocesslist = null;

		long systemContextId = (long) session.getAttribute("systemcontextid");

		if (session.getAttribute("systemcontextid") != null) {
			Long name = (Long) session.getAttribute("systemcontextid");
			actors = this.jdbcTemplate
					.query("select functionalprocessid, version, name, notes from functionalprocess where systemcontextid = "
									+ systemContextId
									+ " and not deleteflag order by functionalprocessid",
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

			if (actors.size() == 0) {
				session.setAttribute("functionalprocessname",
						functionalprocessname);
				return "define-functional-model";
			}


			if (session.getAttribute("functionalprocessname") != null) {


				functionalprocessname = Long.parseLong(session
						.getAttribute("functionalprocessname") + "");

				actors1 = this.jdbcTemplate
						.query(
								"select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = "
										+ name
										+ " and functionalprocessid = "
										+ functionalprocessname + "",
								new RowMapper<FunctionalProcess>() {
									public FunctionalProcess mapRow(
											ResultSet rs, int rowNum)
											throws SQLException {
										FunctionalProcess actor = new FunctionalProcess();
										actor.setFunctionalProcessId(rs
												.getLong("functionalprocessid"));
										actor.setName(rs.getString("name"));
										actor.setNotes(rs.getString("notes"));
										return actor;
									}
								});


				if (actors1.size() > 0) {
					this.functionalProcess = actors1.get(0);
				}

			} else {

			if (actors.size() > 0) {
				//	functionalprocessname = actors.get(0).getName();
				functionalprocessname = actors.get(0).getFunctionalProcessId();
				this.functionalProcess = actors.get(0);
				session.setAttribute("functionalprocessname",
						functionalprocessname);

			}
			}

			model.addAttribute("functionalprocesslist", actors);

			functionalsubprocesslist = this.jdbcTemplate
					.query("select functionalsubprocessid, functionalprocessid, version, name from functionalsubprocess where functionalprocessid = "
									+ this.functionalProcess.getFunctionalProcessId()
									+ " and not deleteflag and version = 0 order by functionalsubprocessid",
							new RowMapper<FunctionalSubProcess>() {
								public FunctionalSubProcess mapRow(
										ResultSet rs, int rowNum)
										throws SQLException {
									FunctionalSubProcess actor = new FunctionalSubProcess();
									actor.setFunctionalSubProcessId(rs
											.getLong("functionalsubprocessid"));
									actor.setFunctionalProcessId(rs
											.getLong("functionalprocessid"));
									actor.setName(rs.getString("name"));
									return actor;
								}
							});

			distinctfunctionalmodellist = this.jdbcTemplate
					.query("select max(a.functionalmodelid) as functionalmodelid, max(a.functionalprocessid) as functionalprocessid, max(c.functionalsubprocessid) as functionalsubprocessid, max(c.name) as functionalsubprocessname, max(a.datagroupid) as datagroupid, max(b.name) as datagroupname from functionalmodel a, datagroup b, functionalsubprocess c where a.datagroupid = b.datagroupid and a.functionalsubprocessid = c.functionalsubprocessid and a.systemcontextid = "
							+ systemContextId
							+ " and a.functionalprocessid = "
							+ this.functionalProcess.getFunctionalProcessId()
							+ " and not a.deleteflag group by a.datagroupid",
							new RowMapper<FunctionalModel>() {
								public FunctionalModel mapRow(ResultSet rs,
										int rowNum) throws SQLException {
									FunctionalModel actor = new FunctionalModel();
									actor.setFunctionalModelId(rs
											.getLong("functionalmodelid"));
									actor.setFunctionalProcessId(rs
											.getLong("functionalprocessid"));
									actor.setFunctionalSubProcessId(rs
											.getLong("functionalsubprocessid"));
									actor.setDataGroupName(rs
											.getString("datagroupname"));
									actor.setDisplaydataGroupName(rs
											.getString("datagroupname").replace("'", "\\'"));
									actor.setDataGroupId(rs
											.getLong("datagroupid"));
									return actor;
								}
							});

			model.addAttribute("distinctfunctionalmodellist",
					distinctfunctionalmodellist);

			functionalmodellist = this.jdbcTemplate
					.query("select a.functionalmodelid, a.functionalprocessid, c.functionalsubprocessid, c.name as functionalsubprocessname, a.datagroupid, b.name as datagroupname, a.grade, a.notes from functionalmodel a, datagroup b, functionalsubprocess c where a.datagroupid = b.datagroupid and a.functionalsubprocessid = c.functionalsubprocessid and a.systemcontextid = "
							+ systemContextId
							+ " and a.functionalprocessid = "
									+ this.functionalProcess.getFunctionalProcessId()
									+ " and not a.deleteflag order by a.datagroupid, a.functionalsubprocessid",
							new RowMapper<FunctionalModel>() {
								public FunctionalModel mapRow(ResultSet rs,
										int rowNum) throws SQLException {
									FunctionalModel actor = new FunctionalModel();
									actor.setFunctionalModelId(rs
											.getLong("functionalmodelid"));
									actor.setFunctionalProcessId(rs
											.getLong("functionalprocessid"));
									actor.setFunctionalSubProcessName(rs
											.getString("functionalsubprocessname"));
									actor.setFunctionalSubProcessId(rs
											.getLong("functionalsubprocessid"));
									actor.setDataGroupName(rs
											.getString("datagroupname"));
									actor.setDisplaydataGroupName(rs
											.getString("datagroupname").replace("'", "\\'"));
									actor.setDataGroupId(rs
											.getLong("datagroupid"));
									actor.setGrade(rs.getString("grade"));
									actor.setNotes(rs.getString("notes").replace("'", "\\'"));
									return actor;
								}
							});

			model.addAttribute("functionalmodellist", functionalmodellist);

			functionalmodeldatafieldlist = this.jdbcTemplate
					.query("select functionalmodelid, datafieldid from functionalmodeldatafield where functionalmodelid in ( select a.functionalmodelid from functionalmodel a, datagroup b, functionalsubprocess c where a.datagroupid = b.datagroupid and a.functionalsubprocessid = c.functionalsubprocessid and a.systemcontextid = "
									+ systemContextId
									+ " and a.functionalprocessid = "
									+ this.functionalProcess.getFunctionalProcessId()
									+ " and not a.deleteflag) order by functionalmodelid",
							new RowMapper<FunctionalModelDataField>() {
								public FunctionalModelDataField mapRow(ResultSet rs,
																	   int rowNum) throws SQLException {
									FunctionalModelDataField actor = new FunctionalModelDataField();
									actor.setFunctionalModelId(rs
											.getLong("functionalmodelid"));

									actor.setDatafieldId(rs
											.getLong("datafieldid"));
									return actor;
								}
							});
			

			for (FunctionalSubProcess functionalSubProcess : functionalsubprocesslist) {
				List<FunctionalModel> temp = new ArrayList<FunctionalModel>();
				for (FunctionalModel functionalModel : functionalmodellist) {
					if (functionalSubProcess.getFunctionalSubProcessId() == functionalModel
							.getFunctionalSubProcessId()) {

						FunctionalModel functionalmodel = new FunctionalModel();
						functionalmodel = functionalModel;
						String value = "";

						for (FunctionalModelDataField functionalmodeldatafield : functionalmodeldatafieldlist) {
							if (functionalModel.getFunctionalModelId() == functionalmodeldatafield.getFunctionalModelId()) {
								if (value.equals("")) {
									value = value + functionalmodeldatafield.getDatafieldId();
								} else {
									value = value + "," + functionalmodeldatafield.getDatafieldId();
								}
							}
						}
						functionalmodel.setDatafieldList(value);
						temp.add(functionalmodel);
					}
				}
				if (temp != null) {
					functionalSubProcess.setFunctionalModel(temp);
				}

			}


			model.addAttribute("functionalsubprocesslist",
					functionalsubprocesslist);

			List<Score> scores = this.jdbcTemplate
					.query("select sum(score) as score, max(a.datagroupid) as datagroupid from functionalmodel a, datagroup b, functionalsubprocess c where a.datagroupid = b.datagroupid and a.functionalsubprocessid = c.functionalsubprocessid and a.systemcontextid = "
							+ systemContextId
							+ " and a.functionalprocessid = "
									+ this.functionalProcess.getFunctionalProcessId()
									+ " and not a.deleteflag group by a.datagroupid order by a.datagroupid",
							new RowMapper<Score>() {
								public Score mapRow(ResultSet rs, int rowNum)
										throws SQLException {
									Score actor = new Score();
									actor.setScore(rs.getInt("score"));
									actor.setDataGroupId(rs
											.getLong("datagroupid"));
									return actor;
								}
							});

			model.addAttribute("scores", scores);

			this.finalScore = 0;

			for (Score score : scores) {
				this.finalScore = this.finalScore + score.getScore();
			}

			List<DataGroup> datagrouplist = this.jdbcTemplate
					.query("select datagroupid, version, name, notes from datagroup where systemcontextid = "
									+ name
									+ " and not deleteflag order by datagroupid",
							new RowMapper<DataGroup>() {
								public DataGroup mapRow(ResultSet rs, int rowNum)
										throws SQLException {
									DataGroup actor = new DataGroup();
									actor.setDataGroupId(rs.getLong("datagroupid"));
									actor.setName(rs.getString("name"));
									actor.setNotes(rs.getString("notes"));
									return actor;
								}
							});

			model.addAttribute("datagrouplist", datagrouplist);

			model.addAttribute("finalScore", this.finalScore);

			List<DataField> datafieldlist = this.jdbcTemplate
					.query("select datafieldid, datagroupid, version, name from datafield where not deleteflag and version = 0",
							new RowMapper<DataField>() {
								public DataField mapRow(ResultSet rs, int rowNum)
										throws SQLException {
									DataField datafield = new DataField();
									datafield.setDataFieldId(rs
											.getLong("datafieldid"));
									datafield.setDataGroupId(rs
											.getLong("datagroupid"));
									datafield.setVersion(rs.getInt("version"));
									datafield.setName(rs.getString("name"));
									return datafield;
								}
							});

			model.addAttribute("datafieldlist", datafieldlist);

		}

		return "define-functional-model";
	}

	@RequestMapping(value = "/create-new-functional-model", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String createFunctionalModel(Model model,
			HttpServletRequest request, HttpSession session) {

		String datagroupname = "";

		if (request.getParameter("datagroupname") != null) {
			datagroupname = request.getParameter("datagroupname");
			session.setAttribute("datagroupname", datagroupname);
		}

		Long functionalprocessname = (Long) session
				.getAttribute("functionalprocessname");

		int version = 0;
		long dataGroupId = 0l;
		long functionalProcessId = 0l;
		long systemContextId = (long) session.getAttribute("systemcontextid");

		if (systemContextId != 0l) {

			List<DataGroup> dataGroupList = this.jdbcTemplate
					.query("select datagroupid, version, name, notes from datagroup where not deleteflag and systemcontextid = "
							+ systemContextId
							+ " and name = '"
							+ datagroupname.replace("'", "''")
							+ "'", new RowMapper<DataGroup>() {
						public DataGroup mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							DataGroup actor = new DataGroup();
							actor.setDataGroupId(rs.getLong("datagroupid"));
							actor.setName(rs.getString("name"));
							actor.setNotes(rs.getString("notes"));
							return actor;
						}
					});

			if (dataGroupList.size() > 0) {

				dataGroupId = dataGroupList.get(0).getDataGroupId();

			}

			List<FunctionalProcess> functionalProcessList = this.jdbcTemplate
					.query("select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = "
							+ systemContextId
									+ " and functionalprocessid = "
									+ functionalprocessname + "",
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

			if (functionalProcessList.size() > 0) {

				functionalProcessId = functionalProcessList.get(0)
						.getFunctionalProcessId();

			}

			List<FunctionalModel> functionalModel = this.jdbcTemplate
					.query("select functionalmodelid from functionalmodel where not deleteflag and systemcontextid = "
							+ systemContextId
							+ " and functionalprocessid = "
							+ functionalProcessId
							+ " and datagroupid = "
							+ dataGroupId, new RowMapper<FunctionalModel>() {
						public FunctionalModel mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							FunctionalModel actor = new FunctionalModel();
							actor.setFunctionalModelId(rs
									.getLong("functionalmodelid"));
							return actor;
						}
					});

			if (request.getParameter("option") != null
					&& request.getParameter("option").equals("1")) {

				if (functionalModel.size() > 0) {
					err = "Data group already exist";
					model.addAttribute("err", err);

					// return getDataAttributeList(model, request, session);

				} else {
					this.jdbcTemplate
							.update(" insert into functionalmodel ( systemcontextid, functionalprocessid, datagroupid , functionalsubprocessid, version, grade, notes, score, userid ) select "
									+ systemContextId
									+ ","
									+ functionalProcessId
									+ ","
									+ dataGroupId
									+ ", a.functionalsubprocessid, "
									+ version
									+ ",'','',0,'"
									+ session.getAttribute("username")
									+ ""
									+ "'"
									+ " from functionalsubprocess a where a.version = 0 and a.functionalprocessid = "
									+ functionalProcessId);

				}

			} else if (request.getParameter("option") != null
					&& request.getParameter("option").equals("0")) {

				List<FunctionalModel> functionalModeltmp = this.jdbcTemplate
						.query("select functionalmodelid from functionalmodel where not deleteflag and systemcontextid = "
								+ systemContextId
								+ " and functionalprocessid = "
								+ functionalProcessId
								+ " and datagroupid = "
								+ dataGroupId,
								new RowMapper<FunctionalModel>() {
									public FunctionalModel mapRow(ResultSet rs,
											int rowNum) throws SQLException {
										FunctionalModel actor = new FunctionalModel();
										actor.setFunctionalModelId(rs
												.getLong("functionalmodelid"));
										return actor;
									}
								});

				if (functionalModeltmp.size() > 0) {
					/*
					 * this.jdbcTemplate .update(
					 * " delete from functionalmodel where systemcontextid = " +
					 * systemContextId + " and functionalprocessid = " +
					 * functionalProcessId + " and datagroupid = " +
					 * dataGroupId);
					 */
					this.jdbcTemplate
							.update(" update functionalmodel set deleteflag = true where systemcontextid = "
									+ systemContextId
									+ " and functionalprocessid = "
									+ functionalProcessId
									+ " and datagroupid = " + dataGroupId);

				}
			} else if (request.getParameter("option") != null
					&& request.getParameter("option").equals("2")) {

				long functionalmodelid = Long.parseLong(request
						.getParameter("functionalmodelid"));

				this.jdbcTemplate
						.update(" update functionalmodel set grade = ''"
								+ ", notes = '',"
								+ " score = 0 where functionalmodelid = "
								+ functionalmodelid);

				this.jdbcTemplate
						.update(" delete from functionalmodeldatafield where functionalmodelid = "
								+ functionalmodelid);

			} else if (request.getParameter("option") != null
					&& request.getParameter("option").equals("3")) {

				String notes = request.getParameter("notes");
				String grade = request.getParameter("grade");
				String[] checkbox = request.getParameterValues("datafields");

				long functionalmodelid = Long.parseLong(request
						.getParameter("functionalmodelid"));

				this.jdbcTemplate
						.update(" update functionalmodel set grade = '" + grade
								+ "'" + ", notes = '" + notes.replace("'", "''") + "', "
								+ " score = 1 where functionalmodelid = "
								+ functionalmodelid);

				int len = 0;

				long datafieldid = 0l;

				if (checkbox != null) {
					//	String[] parts = checkbox.split(",");

					this.jdbcTemplate
							.update(" delete from functionalmodeldatafield where functionalmodelid = "
									+ functionalmodelid);

					while (len < checkbox.length) {
						datafieldid = Long.parseLong(checkbox[len]);
						this.jdbcTemplate
								.update(" insert into functionalmodeldatafield (functionalmodelid, datafieldid, version, userid) values ( "
										+ functionalmodelid
										+ ","
										+ datafieldid
										+ ",0,'"
										+ session.getAttribute("username")
										+ "')");
						len = len + 1;
					}
				} else {
					this.jdbcTemplate
							.update(" delete from functionalmodeldatafield where functionalmodelid = "
									+ functionalmodelid);
				}

			}

		}

		return showFunctionalModel(model, request, session);

	}

	@RequestMapping(value = "/show-functional-model", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String getFunctionalModel(Model model, HttpServletRequest request,
			HttpSession session) {

		List<FunctionalModel> functionalmodellist = null;


		List<FunctionalModelDataField> functionalmodeldatafieldlist = null;

		long systemContextId = (long) session.getAttribute("systemcontextid");

		Long functionalprocessname = 0l;

		if (request.getParameter("functionalprocessname") != null) {
			functionalprocessname = Long.parseLong(request.getParameter("functionalprocessname"));
			session.setAttribute("functionalprocessname", functionalprocessname);
		} else {
			if (session.getAttribute("functionalprocessname") != null) {
				functionalprocessname = (Long) session
						.getAttribute("functionalprocessname");
			}
		}

		List<FunctionalModel> distinctfunctionalmodellist = null;

		if (session.getAttribute("systemcontextid") != null) {
			Long name = (Long) session.getAttribute("systemcontextid");

			List<FunctionalProcess> actors = this.jdbcTemplate
					.query("select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = "
							+ name + "", new RowMapper<FunctionalProcess>() {
						public FunctionalProcess mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							FunctionalProcess actor = new FunctionalProcess();
							actor.setFunctionalProcessId(rs.getLong("functionalprocessid"));
							actor.setName(rs.getString("name"));
							actor.setNotes(rs.getString("notes"));
							return actor;
						}
					});

			model.addAttribute("functionalprocesslist", actors);

			if (functionalprocessname != null) {

				this.functionalProcess = this.jdbcTemplate
						.queryForObject(
								"select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = "
										+ name
										+ " and functionalprocessid = "
										+ functionalprocessname + "",
								new RowMapper<FunctionalProcess>() {
									public FunctionalProcess mapRow(
											ResultSet rs, int rowNum)
											throws SQLException {
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
					.query("select functionalsubprocessid, functionalprocessid, version, name from functionalsubprocess where functionalprocessid = "
									+ this.functionalProcess.getFunctionalProcessId()
									+ " and not deleteflag and version = 0 order by functionalsubprocessid",
							new RowMapper<FunctionalSubProcess>() {
								public FunctionalSubProcess mapRow(
										ResultSet rs, int rowNum)
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

			distinctfunctionalmodellist = this.jdbcTemplate
					.query("select max(a.functionalmodelid) as functionalmodelid, max(a.functionalprocessid) as functionalprocessid, max(c.functionalsubprocessid) as functionalsubprocessid, max(c.name) as functionalsubprocessname, max(a.datagroupid) as datagroupid, max(b.name) as datagroupname from functionalmodel a, datagroup b, functionalsubprocess c where a.datagroupid = b.datagroupid and a.functionalsubprocessid = c.functionalsubprocessid and a.systemcontextid = "
							+ systemContextId
							+ " and a.functionalprocessid = "
							+ this.functionalProcess.getFunctionalProcessId()
							+ " and not a.deleteflag group by a.datagroupid",
							new RowMapper<FunctionalModel>() {
								public FunctionalModel mapRow(ResultSet rs,
										int rowNum) throws SQLException {
									FunctionalModel actor = new FunctionalModel();
									actor.setFunctionalModelId(rs
											.getLong("functionalmodelid"));
									actor.setFunctionalProcessId(rs
											.getLong("functionalprocessid"));
									actor.setFunctionalSubProcessId(rs
											.getLong("functionalsubprocessid"));
									actor.setDataGroupName(rs
											.getString("datagroupname"));
									actor.setDisplaydataGroupName(rs
											.getString("datagroupname").replace("'", "\\'"));
									actor.setDataGroupId(rs
											.getLong("datagroupid"));
									return actor;
								}
							});

			model.addAttribute("distinctfunctionalmodellist",
					distinctfunctionalmodellist);

			functionalmodellist = this.jdbcTemplate
					.query("select a.functionalmodelid, a.functionalprocessid, c.functionalsubprocessid, c.name as functionalsubprocessname, a.datagroupid, b.name as datagroupname, a.grade, a.notes from functionalmodel a, datagroup b, functionalsubprocess c where a.datagroupid = b.datagroupid and a.functionalsubprocessid = c.functionalsubprocessid and a.systemcontextid = "
							+ systemContextId
							+ " and a.functionalprocessid = "
									+ this.functionalProcess.getFunctionalProcessId()
									+ " and not a.deleteflag order by a.datagroupid, a.functionalsubprocessid",
							new RowMapper<FunctionalModel>() {
								public FunctionalModel mapRow(ResultSet rs,
										int rowNum) throws SQLException {
									FunctionalModel actor = new FunctionalModel();
									actor.setFunctionalModelId(rs
											.getLong("functionalmodelid"));
									actor.setFunctionalProcessId(rs
											.getLong("functionalprocessid"));
									actor.setFunctionalSubProcessName(rs
											.getString("functionalsubprocessname"));
									actor.setFunctionalSubProcessId(rs
											.getLong("functionalsubprocessid"));
									actor.setDataGroupName(rs
											.getString("datagroupname"));
									actor.setDisplaydataGroupName(rs
											.getString("datagroupname").replace("'", "\\'"));
									actor.setDataGroupId(rs
											.getLong("datagroupid"));
									actor.setGrade(rs.getString("grade"));
									actor.setNotes(rs.getString("notes").replace("'", "\\'"));
									return actor;
								}
							});

			model.addAttribute("functionalmodellist", functionalmodellist);

			functionalmodeldatafieldlist = this.jdbcTemplate
					.query("select functionalmodelid, datafieldid from functionalmodeldatafield where functionalmodelid in ( select a.functionalmodelid from functionalmodel a, datagroup b, functionalsubprocess c where a.datagroupid = b.datagroupid and a.functionalsubprocessid = c.functionalsubprocessid and a.systemcontextid = "
									+ systemContextId
									+ " and a.functionalprocessid = "
									+ this.functionalProcess.getFunctionalProcessId()
									+ " and not a.deleteflag) order by functionalmodelid",
							new RowMapper<FunctionalModelDataField>() {
								public FunctionalModelDataField mapRow(ResultSet rs,
																	   int rowNum) throws SQLException {
									FunctionalModelDataField actor = new FunctionalModelDataField();
									actor.setFunctionalModelId(rs
											.getLong("functionalmodelid"));

									actor.setDatafieldId(rs
											.getLong("datafieldid"));
									return actor;
								}
							});

			for (FunctionalSubProcess functionalSubProcess : functionalsubprocesslist) {
				List<FunctionalModel> temp = new ArrayList<FunctionalModel>();
				for (FunctionalModel functionalModel : functionalmodellist) {
					if (functionalSubProcess.getFunctionalSubProcessId() == functionalModel
							.getFunctionalSubProcessId()) {

						FunctionalModel functionalmodel = new FunctionalModel();
						functionalmodel = functionalModel;
						String value = "";

						for (FunctionalModelDataField functionalmodeldatafield : functionalmodeldatafieldlist) {
							if (functionalModel.getFunctionalModelId() == functionalmodeldatafield.getFunctionalModelId()) {
								if (value.equals("")) {
									value = value + functionalmodeldatafield.getDatafieldId();
								} else {
									value = value + "," + functionalmodeldatafield.getDatafieldId();
								}
							}
						}
						functionalmodel.setDatafieldList(value);
						temp.add(functionalmodel);
					}
				}
				if (temp != null) {
					functionalSubProcess.setFunctionalModel(temp);
				}

			}
			
			/*

			for (FunctionalSubProcess functionalSubProcess : functionalsubprocesslist) {
				List<FunctionalModel> temp = new ArrayList<FunctionalModel>();
				for (FunctionalModel functionalModel : functionalmodellist) {
					if (functionalSubProcess.getFunctionalSubProcessId() == functionalModel
							.getFunctionalSubProcessId()) {
						temp.add(functionalModel);
					}
				}
				if (temp != null) {
					functionalSubProcess.setFunctionalModel(temp);
				}

			}
			*/

			List<Score> scores = this.jdbcTemplate
					.query("select sum(score) as score, max(a.datagroupid) as datagroupid from functionalmodel a, datagroup b, functionalsubprocess c where a.datagroupid = b.datagroupid and a.functionalsubprocessid = c.functionalsubprocessid and a.systemcontextid = "
							+ systemContextId
							+ " and a.functionalprocessid = "
									+ this.functionalProcess.getFunctionalProcessId()
									+ " and not a.deleteflag group by a.datagroupid order by a.datagroupid",
							new RowMapper<Score>() {
								public Score mapRow(ResultSet rs, int rowNum)
										throws SQLException {
									Score actor = new Score();
									actor.setScore(rs.getInt("score"));
									actor.setDataGroupId(rs
											.getLong("datagroupid"));
									return actor;
								}
							});

			model.addAttribute("scores", scores);

			this.finalScore = 0;

			for (Score score : scores) {
				this.finalScore = this.finalScore + score.getScore();
			}

			model.addAttribute("finalScore", this.finalScore);

			model.addAttribute("functionalsubprocesslist",
					functionalsubprocesslist);

			List<DataGroup> datagrouplist = this.jdbcTemplate
					.query("select datagroupid, version, name, notes from datagroup where systemcontextid = "
									+ name
									+ " and not deleteflag order by datagroupid",
							new RowMapper<DataGroup>() {
								public DataGroup mapRow(ResultSet rs, int rowNum)
										throws SQLException {
									DataGroup actor = new DataGroup();
									actor.setDataGroupId(rs.getLong("datagroupid"));
									actor.setName(rs.getString("name"));
									actor.setNotes(rs.getString("notes"));
									return actor;
								}
							});

			model.addAttribute("datagrouplist", datagrouplist);

			List<DataField> datafieldlist = this.jdbcTemplate
					.query("select datafieldid, datagroupid, version, name from datafield where not deleteflag and version = 0",
							new RowMapper<DataField>() {
								public DataField mapRow(ResultSet rs, int rowNum)
										throws SQLException {
									DataField datafield = new DataField();
									datafield.setDataFieldId(rs
											.getLong("datafieldid"));
									datafield.setDataGroupId(rs
											.getLong("datagroupid"));
									datafield.setVersion(rs.getInt("version"));
									datafield.setName(rs.getString("name"));
									return datafield;
								}
							});

			model.addAttribute("datafieldlist", datafieldlist);

		}

		return "define-functional-model";

	}

	@RequestMapping("/list-of-data-groups")
	public String getDataGroupList(Model model, HttpServletRequest request,
			HttpSession session) {

		List<DataGroup> actors = null;

		if (session.getAttribute("systemcontextid") != null) {
			Long name = (Long) session.getAttribute("systemcontextid");
			actors = this.jdbcTemplate
					.query("select datagroupid, version, name, notes from datagroup where not deleteflag and systemcontextid = "
									+ name + " order by datagroupid",
							new RowMapper<DataGroup>() {
								public DataGroup mapRow(ResultSet rs, int rowNum)
										throws SQLException {
									DataGroup actor = new DataGroup();
									actor.setDataGroupId(rs.getLong("datagroupid"));
									actor.setName(rs.getString("name"));
									actor.setNotes(rs.getString("notes"));
									return actor;
								}
							});

			model.addAttribute("datagrouplist", actors);

		}

		return "list-of-data-groups";

	}

	@RequestMapping(value = "/get-data-attribute-list", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String getDataAttributeList(Model model, HttpServletRequest request,
			HttpSession session) {

		if (session.getAttribute("systemcontextid") != null) {
			Long name = (Long) session.getAttribute("systemcontextid");
			String datagroupname = request.getParameter("datagroupname");
			session.setAttribute("datagroupname", datagroupname);
			List<DataGroup> actors = this.jdbcTemplate
					.query("select datagroupid, version, name, notes from datagroup where not deleteflag and systemcontextid = "
							+ name + "", new RowMapper<DataGroup>() {
						public DataGroup mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							DataGroup actor = new DataGroup();
							actor.setDataGroupId(rs.getLong("datagroupid"));
							actor.setName(rs.getString("name"));
							actor.setNotes(rs.getString("notes"));
							return actor;
						}
					});

			model.addAttribute("datagrouplist", actors);

			if (datagroupname != null && !datagroupname.equals("")) {

				this.dataGroup = this.jdbcTemplate
						.queryForObject(
								"select datagroupid, version, name, notes from datagroup where not deleteflag and systemcontextid = "
										+ name
										+ " and name = '"
										+ datagroupname.replace("'", "''") + "'",
								new RowMapper<DataGroup>() {
									public DataGroup mapRow(ResultSet rs,
											int rowNum) throws SQLException {
										DataGroup actor = new DataGroup();
										actor.setDataGroupId(rs
												.getLong("datagroupid"));
										actor.setName(rs.getString("name"));
										actor.setNotes(rs.getString("notes"));
										return actor;
									}
								});
			}

			model.addAttribute("datagroup", this.dataGroup);

			List<DataField> datafieldlist = this.jdbcTemplate
					.query("select datafieldid, datagroupid, version, name from datafield where not deleteflag and version = 0 and datagroupid = "
							+ this.dataGroup.getDataGroupId(),
							new RowMapper<DataField>() {
								public DataField mapRow(ResultSet rs, int rowNum)
										throws SQLException {
									DataField datafield = new DataField();
									datafield.setDataFieldId(rs
											.getLong("datafieldid"));
									datafield.setDataGroupId(rs
											.getLong("datagroupid"));
									datafield.setVersion(rs.getInt("version"));
									datafield.setName(rs.getString("name"));
									return datafield;
								}
							});

			model.addAttribute("datafieldlist", datafieldlist);

		}

		return "list-of-data-groups";
	}
}