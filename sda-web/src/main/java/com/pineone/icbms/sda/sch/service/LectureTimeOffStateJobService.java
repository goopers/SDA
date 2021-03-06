package com.pineone.icbms.sda.sch.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;

import com.pineone.icbms.sda.comm.sch.dto.AggrDTO;
import com.pineone.icbms.sda.comm.util.Utils;
import com.pineone.icbms.sda.sch.comm.SchedulerJobComm;
import com.pineone.icbms.sda.sch.dao.AggrDAO;
import com.pineone.icbms.sda.sf.QueryService;
import com.pineone.icbms.sda.sf.QueryServiceFactory;
import com.pineone.icbms.sda.sf.SparqlFusekiQueryImpl;

/**
 * 강의시간 확인 서비스
 */
@Service
public class LectureTimeOffStateJobService extends SchedulerJobComm implements Job {
	private final Log log = LogFactory.getLog(this.getClass());
	private static AtomicInteger ai = new AtomicInteger();
	
	/**
	 * triple로 부터 집계를해서 domain에 값을 넣음
	 * @param jec
	 * @throws Exception
	 * @return void
	 */
	public void runner(JobExecutionContext jec) throws Exception {
		String start_time = Utils.dateFormat.format(new Date());
		start_time = start_time + "S"+String.format("%010d", ai.getAndIncrement());

		AggrDAO aggrDAO;
		StringBuffer msg = new StringBuffer();

		log.info("LectureTimeOffStateJobService(id : "+jec.getJobDetail().getName()+") start.......................");
		
		try {
			// AggrDTO정보
			List<AggrDTO> aggrList = new ArrayList<AggrDTO>();
			aggrDAO = getContext().getBean(AggrDAO.class);
			Map<String, String> commandMap = new HashMap<String, String>();
			commandMap.put("aggr_id", jec.getJobDetail().getName());
			
			// 집계정보 가져오기
			aggrList = (List<AggrDTO>)aggrDAO.selectList(commandMap);

			// sch_hist테이블에 data insert(work_cnt는 aggrList목록의 개수로 설정함)
			insertSchHist(jec, aggrList.size(), start_time, Utils.dateFormat.format(new Date()));
			
			// aggr테이블의 aggr_id에 설정된 개수만큼 아래를 수행한다.
			QueryService sparqlService = QueryServiceFactory.create(Utils.QUERY_GUBUN.FUSEKISPARQL);
			List<Map<String, String>> argsResultList;		// 대상목록
			// argsql로 대상및 값을 구함
			argsResultList = sparqlService.runQuery(aggrList.get(0).getArgsql());
			
			// 결과값 만큼 처리함
			for(int m = 0; m < argsResultList.size(); m++) {
				String lecture_loc = argsResultList.get(m).get("location_sub");
				
				//delete->insert
				((SparqlFusekiQueryImpl)sparqlService.getImplementClass()).updateSparql(aggrList.get(0).getDeleteql(), aggrList.get(0).getInsertql(), new String[]{lecture_loc}, Utils.QUERY_DEST.ALL.toString());
					msg.append(Utils.NEW_LINE);				
					msg.append("lecture_loc["+m+"](lecture time off)==>  ");
					msg.append(lecture_loc);
					msg.append(" updated by o:noClass.");

					log.debug(msg.toString());
			}
			
			if(argsResultList.size() == 0) {
				msg.append("No Result !");
			}

			String finish_time = Utils.dateFormat.format(new Date());
			updateFinishTime(jec, start_time, finish_time, msg.toString() , Utils.NoTripleFile, Utils.NotCheck);

			// finish_time값을 sch테이블의 last_work_time에 update
			updateLastWorkTime(jec, finish_time);
			log.info("LectureTimeOffStateJobService(id : "+jec.getJobDetail().getName()+") end.......................");			
		} catch (Exception e) {
			updateFinishTime(jec, start_time, Utils.dateFormat.format(new Date()), e.getMessage());
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext arg0)  throws JobExecutionException{
		try {
			runner(arg0);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Exception ("+this.getClass()+")  ....................................> "+e.toString());			
			throw new JobExecutionException(e);
		}
	}

}
