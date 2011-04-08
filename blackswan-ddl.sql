
    alter table Company 
        drop constraint FK9BDFD45D8370E02C;

    alter table Company 
        drop constraint FK9BDFD45D912EEC1A;

    alter table CompanyPeriodReport 
        drop constraint FKF12227522B724E8D;

    alter table CompanyPeriodReport_PeriodEvent 
        drop constraint FKADC63FCC4BF9C7DC;

    alter table CompanyPeriodReport_PeriodEvent 
        drop constraint FKADC63FCC31DF3CC2;

    alter table CompanyPeriodReport_PeriodPartRumour 
        drop constraint FK816460634BF9C7DC;

    alter table CompanyPeriodReport_PeriodPartRumour 
        drop constraint FK8164606332671068;

    alter table PeriodEvent 
        drop constraint FK28C225392B724E8D;

    alter table PeriodEvent 
        drop constraint FK28C22539AF969615;

    alter table PeriodPartRumour 
        drop constraint FK2B0FD7162B724E8D;

    alter table PeriodPartRumour 
        drop constraint FK2B0FD716AF969615;

    alter table ShareParcel 
        drop constraint FK611540A8D1B4A736;

    alter table ShareParcel 
        drop constraint FK611540A879C9A78F;

    alter table TradeOrder 
        drop constraint FKE5BBC06A96A002ED;

    alter table TradeOrder 
        drop constraint FKE5BBC06AD9800AA7;

    alter table TradeRecord 
        drop constraint FKD62356B533F8C31B;

    alter table TradeRecord 
        drop constraint FKD62356B5889E41E7;

    alter table TradeRecord 
        drop constraint FKD62356B579C9A78F;

    alter table Trader 
        drop constraint FK95CB27AEDE29A6B0;

    alter table User 
        drop constraint FK285FEBD1B4A736;

    alter table UserSession 
        drop constraint FKC7BC0C2BDE29A6B0;

    drop table Company if exists;

    drop table CompanyPeriodReport if exists;

    drop table CompanyPeriodReport_PeriodEvent if exists;

    drop table CompanyPeriodReport_PeriodPartRumour if exists;

    drop table PeriodEvent if exists;

    drop table PeriodPartRumour if exists;

    drop table ShareParcel if exists;

    drop table Status if exists;

    drop table StockExchange if exists;

    drop table TradeOrder if exists;

    drop table TradeRecord if exists;

    drop table Trader if exists;

    drop table User if exists;

    drop table UserSession if exists;

    create table Company (
        company_id varchar(255) not null,
        name varchar(255),
        code varchar(255),
        sector varchar(255),
        profitModifierName varchar(255),
        assetValue bigint,
        debtValue bigint,
        capitalisation bigint,
        outstandingShares bigint,
        profit bigint,
        previousDividend bigint,
        alwaysPayDividend bit,
        neverPayDividend bit,
        dividendRate bigint,
        lastTradePrice bigint,
        stock_exchange_id varchar(255),
        current_period varchar(255),
        primary key (company_id)
    );

    create table CompanyPeriodReport (
        company_period_report_id varchar(255) not null,
        startDate timestamp,
        startingExpectedProfit bigint,
        finalProfit bigint,
        minimumEndDate timestamp,
        closeDate timestamp,
        open bit,
        generation bigint,
        company_id varchar(255),
        primary key (company_period_report_id)
    );

    create table CompanyPeriodReport_PeriodEvent (
        CompanyPeriodReport_company_period_report_id varchar(255) not null,
        periodEventList_period_event_id varchar(255) not null,
        primary key (CompanyPeriodReport_company_period_report_id, periodEventList_period_event_id),
        unique (periodEventList_period_event_id)
    );

    create table CompanyPeriodReport_PeriodPartRumour (
        CompanyPeriodReport_company_period_report_id varchar(255) not null,
        periodRumourList_period_rumour_id varchar(255) not null,
        primary key (CompanyPeriodReport_company_period_report_id, periodRumourList_period_rumour_id),
        unique (periodRumourList_period_rumour_id)
    );

    create table PeriodEvent (
        period_event_id varchar(255) not null,
        dateInformationAvailable timestamp,
        message varchar(255),
        eventType integer,
        expectedProfit bigint,
        forecastType varchar(255),
        company_period_report varchar(255),
        company_id varchar(255),
        primary key (period_event_id)
    );

    create table PeriodPartRumour (
        period_rumour_id varchar(255) not null,
        dateInformationAvailable timestamp,
        message varchar(255),
        eventType integer,
        forecastType varchar(255),
        company_period_report varchar(255),
        company_id varchar(255),
        primary key (period_rumour_id)
    );

    create table ShareParcel (
        share_parcel_id varchar(255) not null,
        shareCount bigint,
        trader_trader_id varchar(255),
        company_company_id varchar(255),
        primary key (share_parcel_id)
    );

    create table Status (
        status_id varchar(255) not null,
        status varchar(255),
        primary key (status_id)
    );

    create table StockExchange (
        stock_exchange_id varchar(255) not null,
        name varchar(255),
        description varchar(255),
        companyCount integer,
        eventGeneratorName varchar(255),
        companyPeriodLength bigint,
        primary key (stock_exchange_id)
    );

    create table TradeOrder (
        order_id varchar(255) not null,
        originalShareCount bigint,
        remainingShareCount bigint,
        offerPrice bigint,
        allowPartialOrder bit,
        dateCreated timestamp,
        active bit,
        executed bit,
        dateExecuted timestamp,
        orderType integer,
        company varchar(255),
        trader_id varchar(255),
        primary key (order_id)
    );

    create table TradeRecord (
        trade_record_id varchar(255) not null,
        shareCount bigint,
        sharePrice bigint,
        date timestamp,
        buyer_trader_id varchar(255),
        company_company_id varchar(255),
        seller_trader_id varchar(255),
        primary key (trade_record_id)
    );

    create table Trader (
        trader_id varchar(255) not null,
        name varchar(255),
        cash bigint,
        isAITrader bit,
        aiStrategyName varchar(255),
        isMarketMaker bit,
        experiencePoints bigint,
        user_trader_id varchar(255),
        primary key (trader_id)
    );

    create table User (
        trader_id varchar(255) not null,
        userName varchar(255),
        password varchar(255),
        userAdmin bit,
        trader_trader_id varchar(255),
        primary key (trader_id)
    );

    create table UserSession (
        status_id varchar(255) not null,
        date timestamp,
        sessionId varchar(255),
        active bit,
        user_trader_id varchar(255),
        primary key (status_id)
    );

    alter table Company 
        add constraint FK9BDFD45D8370E02C 
        foreign key (current_period) 
        references CompanyPeriodReport;

    alter table Company 
        add constraint FK9BDFD45D912EEC1A 
        foreign key (stock_exchange_id) 
        references StockExchange;

    alter table CompanyPeriodReport 
        add constraint FKF12227522B724E8D 
        foreign key (company_id) 
        references Company;

    alter table CompanyPeriodReport_PeriodEvent 
        add constraint FKADC63FCC4BF9C7DC 
        foreign key (CompanyPeriodReport_company_period_report_id) 
        references CompanyPeriodReport;

    alter table CompanyPeriodReport_PeriodEvent 
        add constraint FKADC63FCC31DF3CC2 
        foreign key (periodEventList_period_event_id) 
        references PeriodEvent;

    alter table CompanyPeriodReport_PeriodPartRumour 
        add constraint FK816460634BF9C7DC 
        foreign key (CompanyPeriodReport_company_period_report_id) 
        references CompanyPeriodReport;

    alter table CompanyPeriodReport_PeriodPartRumour 
        add constraint FK8164606332671068 
        foreign key (periodRumourList_period_rumour_id) 
        references PeriodPartRumour;

    alter table PeriodEvent 
        add constraint FK28C225392B724E8D 
        foreign key (company_id) 
        references Company;

    alter table PeriodEvent 
        add constraint FK28C22539AF969615 
        foreign key (company_period_report) 
        references CompanyPeriodReport;

    alter table PeriodPartRumour 
        add constraint FK2B0FD7162B724E8D 
        foreign key (company_id) 
        references Company;

    alter table PeriodPartRumour 
        add constraint FK2B0FD716AF969615 
        foreign key (company_period_report) 
        references CompanyPeriodReport;

    alter table ShareParcel 
        add constraint FK611540A8D1B4A736 
        foreign key (trader_trader_id) 
        references Trader;

    alter table ShareParcel 
        add constraint FK611540A879C9A78F 
        foreign key (company_company_id) 
        references Company;

    alter table TradeOrder 
        add constraint FKE5BBC06A96A002ED 
        foreign key (company) 
        references Company;

    alter table TradeOrder 
        add constraint FKE5BBC06AD9800AA7 
        foreign key (trader_id) 
        references Trader;

    alter table TradeRecord 
        add constraint FKD62356B533F8C31B 
        foreign key (buyer_trader_id) 
        references Trader;

    alter table TradeRecord 
        add constraint FKD62356B5889E41E7 
        foreign key (seller_trader_id) 
        references Trader;

    alter table TradeRecord 
        add constraint FKD62356B579C9A78F 
        foreign key (company_company_id) 
        references Company;

    alter table Trader 
        add constraint FK95CB27AEDE29A6B0 
        foreign key (user_trader_id) 
        references User;

    alter table User 
        add constraint FK285FEBD1B4A736 
        foreign key (trader_trader_id) 
        references Trader;

    alter table UserSession 
        add constraint FKC7BC0C2BDE29A6B0 
        foreign key (user_trader_id) 
        references User;
