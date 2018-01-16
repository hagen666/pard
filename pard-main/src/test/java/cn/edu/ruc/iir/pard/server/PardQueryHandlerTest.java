package cn.edu.ruc.iir.pard.server;

import cn.edu.ruc.iir.pard.executor.connector.JoinTask;
import cn.edu.ruc.iir.pard.executor.connector.SendDataTask;
import cn.edu.ruc.iir.pard.executor.connector.Task;
import cn.edu.ruc.iir.pard.planner.PardPlanner;
import cn.edu.ruc.iir.pard.planner.Plan;
import cn.edu.ruc.iir.pard.planner.ddl.UsePlan;
import cn.edu.ruc.iir.pard.planner.dml.QueryPlan;
import cn.edu.ruc.iir.pard.planner.dml.QueryTestPlan;
import cn.edu.ruc.iir.pard.scheduler.TaskScheduler;
import cn.edu.ruc.iir.pard.sql.parser.SqlParser;
import cn.edu.ruc.iir.pard.sql.tree.Statement;
import cn.edu.ruc.iir.pard.web.PardServlet;
import org.testng.annotations.Test;

import java.util.List;

public class PardQueryHandlerTest
{
    SqlParser parser = new SqlParser();
    @Test
    public void executeQuery()
    {
        UsePlan.setCurrentSchema("book");
        String sql = "select Book.title,Book.copies,Publisher.name,Publisher.nation from Book,Publisher where Book.publisher_id=Publisher.id and Publisher.nation='USA' and Book.copies > 1000";
        //String sql = "select * from book@pard0";
        Statement stmt = parser.createStatement(sql);
        PardPlanner planner = new PardPlanner();
        Plan plan = planner.plan(stmt);
        plan.setJobId("aa");
        List<Task> task = TaskScheduler.INSTANCE().generateTasks(plan);
        PardServlet.planList.add((QueryPlan) plan);
        for (Task t : task) {
            System.out.println(t.getTaskId());
            if (t instanceof SendDataTask) {
                QueryPlan p = new QueryTestPlan(((SendDataTask) t).getNode(), "send_Data_" + t.getTaskId());
                PardServlet.planList.add(p);
            }
            else if (t instanceof JoinTask) {
                QueryPlan p = new QueryTestPlan(((JoinTask) t).getNode(), "Join_" + t.getTaskId());
                PardServlet.planList.add(p);
            }
        }
        System.out.println(PardServlet.planList.size());
        PardWebServer.main(new String[0]);
        /*
        Job job = JobScheduler.INSTANCE().newJob();
        task.forEach(job::addTask);
        job.setJobState(JobState.EXECUTING);
        job.setPlan(plan);
        TaskScheduler.INSTANCE().executeJob(job);*/
    }
}
