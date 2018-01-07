package cn.edu.ruc.iir.pard.server;

import cn.edu.ruc.iir.pard.commons.exception.ParsingException;
import cn.edu.ruc.iir.pard.executor.connector.PardResultSet;
import cn.edu.ruc.iir.pard.executor.connector.Task;
import cn.edu.ruc.iir.pard.planner.PardPlanner;
import cn.edu.ruc.iir.pard.planner.Plan;
import cn.edu.ruc.iir.pard.scheduler.Job;
import cn.edu.ruc.iir.pard.scheduler.JobScheduler;
import cn.edu.ruc.iir.pard.scheduler.TaskScheduler;
import cn.edu.ruc.iir.pard.sql.parser.SqlParser;
import cn.edu.ruc.iir.pard.sql.tree.Statement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * pard
 *
 * @author guodong
 */
public class PardQueryHandler
        extends Thread
{
    private Socket socket;
    private Logger logger = Logger.getLogger("pard server");
    private JobScheduler jobScheduler;
    private ObjectOutputStream objectOutputStream;
    private SqlParser sqlParser = new SqlParser();
    private PardPlanner planner = new PardPlanner();
    private TaskScheduler taskScheduler;

    public PardQueryHandler(
            Socket socket,
            JobScheduler jobScheduler,
            TaskScheduler taskScheduler)
    {
        this.socket = socket;
        this.jobScheduler = jobScheduler;
        this.taskScheduler = taskScheduler;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        // todo fill map with nodes
    }

    @Override
    public void run()
    {
        try (BufferedReader input = new BufferedReader(
                new InputStreamReader(socket.getInputStream()))) {
            while (true) {
                String line = input.readLine();
                if (line.equalsIgnoreCase("EXIT") ||
                        line.equalsIgnoreCase("QUIT")) {
                    logger.info("CLIENT QUIT");
                    break;
                }
                logger.info("QUERY: " + line);
                PardResultSet result = executeQuery(line);
                objectOutputStream.writeObject(result);
                objectOutputStream.flush();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PardResultSet executeQuery(String sql)
    {
        // todo this logic should be abstracted as a automated state machine
        logger.info("Accepted query: " + sql);
        Job job = jobScheduler.newJob();
        if (job == null) {
            logger.log(Level.WARNING, "Cannot create job for sql: " + sql);
            return new PardResultSet(PardResultSet.ResultStatus.BEGIN_ERR);
        }
        job.setSql(sql);
        jobScheduler.updateJob(job.getJobId());
        logger.info("Created job for query, job id: " + job.getJobId());

        Statement statement;
        try {
            statement = sqlParser.createStatement(sql);
        }
        catch (ParsingException e) {
            return new PardResultSet(PardResultSet.ResultStatus.PARSING_ERR);
        }
        if (statement == null) {
            jobScheduler.failJob(job.getJobId());
            logger.log(Level.WARNING, "Cannot create statement for sql: " + sql);
            return new PardResultSet(PardResultSet.ResultStatus.PARSING_ERR);
        }
        job.setStatement(statement);
        jobScheduler.updateJob(job.getJobId());
        logger.info("Created statement for job[" + job.getJobId() + "], job state: " + job.getJobState());

        Plan plan = planner.plan(statement);
        if (plan == null) {
            jobScheduler.failJob(job.getJobId());
            logger.log(Level.WARNING, "Cannot create plan for sql: " + sql);
            return new PardResultSet(PardResultSet.ResultStatus.PLANNING_ERR);
        }
        job.setPlan(plan);
        jobScheduler.updateJob(job.getJobId());
        logger.info("Created plan for job[" + job.getJobId() + "], job state: " + job.getJobState());

        List<Task> tasks = taskScheduler.generateTasks(plan);
        if (tasks == null) {
            jobScheduler.failJob(job.getJobId());
            logger.log(Level.WARNING, "Cannot create tasks for sql: " + sql);
            return new PardResultSet(PardResultSet.ResultStatus.SCHEDULING_ERR);
        }
        if (!tasks.isEmpty()) {
            tasks.forEach(job::addTask);
        }
        jobScheduler.updateJob(job.getJobId());
        logger.info("Generated tasks for job[" + job.getJobId() + "], job state: " + job.getJobState());

        PardResultSet resultSet = taskScheduler.executeJob(job);
        if (resultSet.getStatus() != PardResultSet.ResultStatus.OK) {
            jobScheduler.failJob(job.getJobId());
            logger.log(Level.WARNING, "Failed to execute job for sql: " + sql);
        }
        jobScheduler.updateJob(job.getJobId());
        logger.info("Done executing job[" + job.getJobId() + "], job state: " + job.getJobState());

        return resultSet;
    }
}
