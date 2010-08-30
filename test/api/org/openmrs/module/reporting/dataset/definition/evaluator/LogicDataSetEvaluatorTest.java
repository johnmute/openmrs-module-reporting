package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.LogicDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class LogicDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}

	/**
     * @see {@link LogicDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
     */
    @Test
    @Verifies(value = "should evaluate a logic data set definition", method = "evaluate(DataSetDefinition,EvaluationContext)")
    public void evaluate_shouldEvaluateALogicDataSetDefinition() throws Exception {
    	Cohort cohort = new Cohort();
    	cohort.addMember(7);
    	cohort.addMember(20);
    	cohort.addMember(21);
    	EvaluationContext evalContext = new EvaluationContext();
    	evalContext.setBaseCohort(cohort);
    	
    	LogicService ls = Context.getLogicService();
    	ls.addRule("gender", ls.getRule("%%person.gender"));
    	ls.addRule("birthdate", ls.getRule("%%person.birthdate"));
    	ls.addRule("CD4", ls.getRule("%%obs.CD4 COUNT"));
    	
    	LogicDataSetDefinition def = new LogicDataSetDefinition();
    	def.addColumn("gender", "Gender", "gender");
    	def.addColumn("birthdate", "Birth Date", "birthdate");
    	def.addColumn("cd4", "CD4 Count", "CD4");
    	
    	DataSet result = Context.getService(DataSetDefinitionService.class).evaluate(def, evalContext);
    	String csv = ReportUtil.toCsv(result);
    	Assert.assertEquals("\"gender\",\"birthdate\",\"cd4\",\n\"F\",\"25/08/1976\",\"150.0,175.0\",\n\"F\",\"08/02/1925\",\"45.0\",\n\"M\",\"08/06/1959\",\"48.0,50.0\",\n", csv);
    }

}