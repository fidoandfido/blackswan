package net.fidoandfido.engine.profitmodifers;

import net.fidoandfido.engine.event.EventData;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.util.Constants.EventType;

public interface EventProfitModifier {

	public String getName();

	public EventData adjustProfit(EventType eventType, EventData eventData, Company company, CompanyPeriodReport currentPeriodReport, long eventCount);

}
