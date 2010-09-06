/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework.components.batchjobs;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.components.batchjobs.exceptions.TaskSystemException;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.ConfigurationLocator;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.core.io.ClassPathResource;

public class TaskHelperIntegrationTest extends MifosIntegrationTestCase {

    MifosScheduler mifosScheduler;

    String jobName;

    public TaskHelperIntegrationTest() throws Exception {
        super();
    }

    @Before
    public void setUp() throws Exception {
        TestDatabase.resetMySQLDatabase();
        jobName = "ProductStatusJob";
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testIncompleteTaskHandling() throws Exception {
        mifosScheduler = getMifosScheduler("org/mifos/framework/components/batchjobs/catchUpMockTask2.xml");
        JobLauncher jobLauncher = mifosScheduler.getBatchJobLauncher();
        JobLocator jobLocator = mifosScheduler.getBatchJobLocator();
        for(int i = 0; i < 3; i++) {
            jobLauncher.run(jobLocator.getJob(jobName), MifosBatchJob.createJobParameters(new Date().getTime()));
            Thread.sleep(1000);
        }
        JobExplorer explorer = mifosScheduler.getBatchJobExplorer();
        List<JobInstance> jobInstances = explorer.getJobInstances(jobName, 0, 10);
        Assert.assertEquals(3, jobInstances.size());
        for(JobInstance jobInstance : jobInstances) {
            List<JobExecution> jobExecutions = explorer.getJobExecutions(jobInstance);
            Assert.assertEquals(1, jobExecutions.size());
            Assert.assertEquals(BatchStatus.FAILED, jobExecutions.get(0).getStatus());
        }
        mifosScheduler.runIndividualTask(jobName);
        Thread.sleep(1000);
        jobInstances = explorer.getJobInstances(jobName, 0, 10);
        Assert.assertEquals(4, jobInstances.size());
        for(JobInstance jobInstance : jobInstances) {
            List<JobExecution> jobExecutions = explorer.getJobExecutions(jobInstance);
            Assert.assertEquals(BatchStatus.COMPLETED, jobExecutions.get(0).getStatus());
        }
    }

    @Test
    public void testIncompleteTaskDelay() {
        // TODO QUARTZ: Integration test showing that when a job failed to execute several times,
        // the scheduler would launch each failed run with a correct (previously scheduled) date
    }

    private MifosScheduler getMifosScheduler(String taskConfigurationPath) throws TaskSystemException, IOException, FileNotFoundException {
        ConfigurationLocator mockConfigurationLocator = createMock(ConfigurationLocator.class);
        expect(mockConfigurationLocator.getFile(SchedulerConstants.CONFIGURATION_FILE_NAME)).andReturn(
                new ClassPathResource(taskConfigurationPath).getFile());
        expectLastCall().times(2);
        replay(mockConfigurationLocator);
        MifosScheduler mifosScheduler = new MifosScheduler();
        mifosScheduler.setConfigurationLocator(mockConfigurationLocator);
        mifosScheduler.initialize();
        return mifosScheduler;
    }

}
