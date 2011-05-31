package net.fidoandfido.engine.profitmodifers;

import net.fidoandfido.engine.quarter.QuarterData;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.util.Constants.QuarterPerformanceType;

public interface EventProfitModifier {

	public String getName();

	public QuarterData adjustProfit(QuarterPerformanceType eventType, QuarterData eventData, Company company, CompanyPeriodReport currentPeriodReport, long eventCount);

}
