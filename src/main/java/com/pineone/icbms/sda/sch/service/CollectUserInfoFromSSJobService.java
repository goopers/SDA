package com.pineone.icbms.sda.sch.service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

import com.pineone.icbms.sda.comm.service.SchedulerJobComm;
import com.pineone.icbms.sda.comm.util.Utils;
import com.pineone.icbms.sda.kb.mapper.rdbms.UserInfoMapper;
import com.pineone.icbms.sda.sch.dto.SchDTO;
import com.pineone.icbms.sda.sf.service.TripleService;

@Service
public class CollectUserInfoFromSSJobService extends SchedulerJobComm implements Job {
	private final Log log = LogFactory.getLog(this.getClass());

	public void collect(String db_server, int db_port, String db_name, String db_user, String db_pass, String save_path,
			JobExecutionContext jec) throws Exception {
		String start_time = Utils.dateFormat.format(new Date());
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		log.info(
				"CollectUserInfoFromSSJobService(id : " + jec.getJobDetail().getName() + ") start.......................");

		String riot_mode = Utils.getSdaProperty("com.pineone.icbms.sda.riot.mode");
		StringBuffer sb = new StringBuffer();
		String startDate = "";
		String endDate = "";
		String triple_path_file = "";
		String triple_check_result = "";
		TripleService tripleService = new TripleService();

		startDate = start_time;
		endDate = start_time;

		log.debug("startDate : " + startDate);
		log.debug("endDate : " + endDate);

		// task_group_id, task_id에 대한 schDTO정보
		// schDTO = getSchDTO(jec);

		StringBuffer sql = new StringBuffer();
		sql.append(Utils.NEW_LINE);
		sql.append(" select concat('icbms:',user_id,' rdf:type ','foaf:Person .') as col from user_info_master ");
		sql.append(Utils.NEW_LINE);
		sql.append(" union all ");
		sql.append(Utils.NEW_LINE);
		sql.append(
				" select concat('icbms:',user_id,' foaf:phone ' ,'\"',phone_no,'\"^^xsd:string .') as col from user_info_master ");
		sql.append(Utils.NEW_LINE);
		sql.append(" union all ");
		sql.append(Utils.NEW_LINE);
		sql.append(
				" select concat('icbms:',user_id,' foaf:name ' ,'\"',user_name,'\"^^xsd:string .') as col from user_info_master ");
		sql.append(Utils.NEW_LINE);
		sql.append(" union all ");
		sql.append(Utils.NEW_LINE);
		sql.append(
				" select concat('icbms:',user_id,' foaf:gender ' ,'\"',gender,'\"^^xsd:string .') as col from user_info_master ");
		sql.append(Utils.NEW_LINE);
		sql.append(" union all ");
		sql.append(Utils.NEW_LINE);
		sql.append(
				" select concat('icbms:',user_id,' icbms:hasUserId ' ,'\"',user_id,'\"^^xsd:string .') as col from user_info_master ");
		sql.append(Utils.NEW_LINE);
		
		log.debug("sql ==>\n"+sql.toString());
		
		int cnt = 0;
		//int col_cnt = 4; // user_id, phone_no, user_name, gender

		try {
			Class.forName("org.mariadb.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mariadb://" + db_server + ":" + db_port + "/" + db_name, db_user,
					db_pass);

			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();

			while (rs.next()) {
				String col = rs.getString("col");

				// stringbuffer에 저장, 문제있는 row는 skip하고 정상적인것은 triple로 변환함
				try {
					sb.append(new UserInfoMapper(col).from());
					sb.append(Utils.NEW_LINE);
				} catch (Exception e) {
					e.printStackTrace();
				}

				cnt++;
			}

			// sch_hist테이블에 data insert
			insertSchHist(jec, -1, start_time, endDate);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException sqle) {
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException sqle) {
				}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqle) {
				}
			}
		}
		// endDate값을 sch테이블의 last_work_time에 update
		updateLastWorkTime(jec, endDate);

		// 스트링 버퍼에 있는 값을 파일에 기록한다.
		String file_name = jec.getJobDetail().getName() + "_WRK" + endDate + "_TT" + start_time;
		String triple_check_result_file = "";

		// 결과값이 있을때만 triple파일을 만듬
		if (sb.length() > 0) {
			triple_path_file = save_path + "/" + file_name + ".ttl";
			sb.insert(0, Utils.getHeaderForTripleFile());
			tripleService.makeTripleFile(triple_path_file, sb);

			// triple파일 체크
			if (!riot_mode.equals("--skip")) {
				String[] check_result = tripleService.checkTripleFile(triple_path_file, file_name);

				// 점검결과를 파일로 저장한다.(체크결과 오류가 있는 경우만 파일로 만듬)
				if (!check_result[1].trim().equals("")) {
					triple_check_result_file = file_name + ".bad";
					tripleService.makeResultFile(triple_check_result_file, check_result);

					// triple파일 체크결과값
					if (check_result[1].length() > 0) {
						triple_check_result = check_result[1];
					} else {
						triple_check_result = Utils.Valid;
					}
				}
			}
			// 파일 전송
			tripleService.sendTripleFile(triple_path_file);
			log.info("CollectUserInfoFromSSJobService(id : " + jec.getJobDetail().getName()
					+ ") end.......................");
		}

		// sch_hist테이블의 finish_time에 날짜 설정
		String finish_time = Utils.dateFormat.format(new Date());
		if (triple_check_result_file.equals(""))
			triple_check_result_file = Utils.None;
		updateFinishTime(jec, start_time, finish_time, "triple_check_result_file : " + triple_check_result_file,
				triple_path_file, triple_check_result);
	}

	@Override
	public void execute(JobExecutionContext arg0) {
		String db_server, db_name, db_user, db_pass, save_path;
		int db_port;

		db_server = Utils.getSdaProperty("com.pineone.icbms.sda.ss.db.server");
		db_port = Integer.parseInt(Utils.getSdaProperty("com.pineone.icbms.sda.ss.db.port"));
		db_name = Utils.getSdaProperty("com.pineone.icbms.sda.ss.db.name");
		db_user = Utils.getSdaProperty("com.pineone.icbms.sda.ss.db.user");
		db_pass = Utils.getSdaProperty("com.pineone.icbms.sda.ss.db.pass");
		save_path = Utils.getSdaProperty("com.pineone.icbms.sda.triple.save_path");

		// 폴더가 없으면 생성
		save_path = Utils.makeSavePath(save_path);		
		

		try {
			collect(db_server, db_port, db_name, db_user, db_pass, save_path, arg0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}