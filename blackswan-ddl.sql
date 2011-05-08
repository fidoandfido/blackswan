
    alter table Company 
        drop 
        foreign key FK9BDFD45D8370E02C;

    alter table Company 
        drop 
        foreign key FK9BDFD45D912EEC1A;

    alter table Company 
        drop 
        foreign key FK9BDFD45D3AD658EE;

    alter table CompanyPeriodReport 
        drop 
        foreign key FKF12227522B724E8D;

    alter table CompanyPeriodReport_PeriodEvent 
        drop 
        foreign key FKADC63FCC4BF9C7DC;

    alter table CompanyPeriodReport_PeriodEvent 
        drop 
        foreign key FKADC63FCC31DF3CC2;

    alter table CompanyPeriodReport_PeriodPartRumour 
        drop 
        foreign key FK816460634BF9C7DC;

    alter table CompanyPeriodReport_PeriodPartRumour 
        drop 
        foreign key FK8164606332671068;

    alter table GameUser 
        drop 
        foreign key FK9A67C6FDD1B4A736;

    alter table PeriodEvent 
        drop 
        foreign key FK28C225392B724E8D;

    alter table PeriodEvent 
        drop 
        foreign key FK28C22539AF969615;

    alter table PeriodPartRumour 
        drop 
        foreign key FK2B0FD7162B724E8D;

    alter table PeriodPartRumour 
        drop 
        foreign key FK2B0FD716AF969615;

    alter table ReputationItem_ReputationEffect 
        drop 
        foreign key FK83DFF81DEBECF1ED;

    alter table ReputationItem_ReputationEffect 
        drop 
        foreign key FK83DFF81D71D7F768;

    alter table ShareParcel 
        drop 
        foreign key FK611540A8D1B4A736;

    alter table ShareParcel 
        drop 
        foreign key FK611540A879C9A78F;

    alter table StockExchange 
        drop 
        foreign key FK4B98A3399F79C876;

    alter table StockExchangePeriod 
        drop 
        foreign key FK26DDF65A385AE894;

    alter table TradeOrder 
        drop 
        foreign key FKE5BBC06A96A002ED;

    alter table TradeOrder 
        drop 
        foreign key FKE5BBC06AD9800AA7;

    alter table TradeRecord 
        drop 
        foreign key FKD62356B533F8C31B;

    alter table TradeRecord 
        drop 
        foreign key FKD62356B5889E41E7;

    alter table TradeRecord 
        drop 
        foreign key FKD62356B579C9A78F;

    alter table Trader 
        drop 
        foreign key FK95CB27AEDE29A6B0;

    alter table TraderEvent 
        drop 
        foreign key FKB03F78CD1B4A736;

    alter table TraderEvent 
        drop 
        foreign key FKB03F78C662D2E86;

    alter table TraderEvent 
        drop 
        foreign key FKB03F78C79C9A78F;

    alter table Trader_ReputationItem 
        drop 
        foreign key FKE1744FFD15E7CA32;

    alter table Trader_ReputationItem 
        drop 
        foreign key FKE1744FFDD1B4A736;

    alter table UserSession 
        drop 
        foreign key FKC7BC0C2BDE29A6B0;

    drop table if exists Company;

    drop table if exists CompanyPeriodReport;

    drop table if exists CompanyPeriodReport_PeriodEvent;

    drop table if exists CompanyPeriodReport_PeriodPartRumour;

    drop table if exists GameUser;

    drop table if exists PeriodEvent;

    drop table if exists PeriodPartRumour;

    drop table if exists ReputationEffect;

    drop table if exists ReputationItem;

    drop table if exists ReputationItem_ReputationEffect;

    drop table if exists ShareParcel;

    drop table if exists Status;

    drop table if exists StockExchange;

    drop table if exists StockExchangePeriod;

    drop table if exists TradeOrder;

    drop table if exists TradeRecord;

    drop table if exists Trader;

    drop table if exists TraderEvent;

    drop table if exists Trader_ReputationItem;

    drop table if exists UserSession;

    create table Company (
        company_id varchar(255) not null,
        name varchar(255),
        code varchar(255),
        sector varchar(255),
        profitModifierName varchar(255),
        assetValue bigint,
        debtValue bigint,
        outstandingShares bigint,
        keepBorrowing bit,
        alwaysPayDividend bit,
        minimumDividend bigint,
        neverPayDividend bit,
        dividendRate bigint,
        revenueRate bigint,
        expenseRate bigint,
        lastTradePrice bigint,
        lastTradeChange bigint,
        previousDividend bigint,
        remainingPeriodsOfGoldenAge bigint,
        remainingPeriodsOfDarkAge bigint,
        companyStatus varchar(255),
        previous_period varchar(255),
        current_period varchar(255),
        stock_exchange_id varchar(255),
        primary key (company_id)
    );

    create table CompanyPeriodReport (
        company_period_report_id varchar(255) not null,
        startDate datetime,
        startingExpectedProfit bigint,
        startingExpectedRevenue bigint,
        startingExpectedExpenses bigint,
        startingExpectedInterest bigint,
        startingAssets bigint,
        startingDebt bigint,
        finalProfit bigint,
        finalRevenue bigint,
        finalExpenses bigint,
        finalInterest bigint,
        minimumEndDate datetime,
        closeDate datetime,
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

    create table GameUser (
        trader_id varchar(255) not null,
        userName varchar(255),
        password varchar(255),
        userAdmin bit,
        trader_trader_id varchar(255),
        primary key (trader_id)
    );

    create table PeriodEvent (
        period_event_id varchar(255) not null,
        dateInformationAvailable datetime,
        message varchar(255),
        eventType integer,
        profit bigint,
        revenue bigint,
        expenses bigint,
        interest bigint,
        runningProfit bigint,
        runningRevenue bigint,
        runningExpenses bigint,
        runningInterest bigint,
        announcementType varchar(255),
        company_id varchar(255),
        company_period_report varchar(255),
        primary key (period_event_id)
    );

    create table PeriodPartRumour (
        period_rumour_id varchar(255) not null,
        dateInformationAvailable datetime,
        dateRumourExpires datetime,
        reputationRequired integer,
        sector varchar(255),
        message varchar(255),
        eventType integer,
        forecastType varchar(255),
        company_id varchar(255),
        company_period_report varchar(255),
        primary key (period_rumour_id)
    );

    create table ReputationEffect (
        reputation_item_id varchar(255) not null,
        sector varchar(255),
        points integer,
        primary key (reputation_item_id)
    );

    create table ReputationItem (
        reputation_item_id varchar(255) not null,
        name varchar(255),
        cost bigint,
        image varchar(255),
        isLimited bit,
        remainingCount bigint,
        primary key (reputation_item_id)
    );

    create table ReputationItem_ReputationEffect (
        ReputationItem_reputation_item_id varchar(255) not null,
        effectList_reputation_item_id varchar(255) not null,
        primary key (ReputationItem_reputation_item_id, effectList_reputation_item_id),
        unique (effectList_reputation_item_id)
    );

    create table ShareParcel (
        share_parcel_id varchar(255) not null,
        shareCount bigint,
        purchasePrice bigint,
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
        defaultPrimeInterestRateBasisPoints bigint,
        economicModifierName varchar(255),
        companyModifierName varchar(255),
        updating bit,
        maxSharePrice bigint,
        currentPeriod_stock_exchange_id varchar(255),
        primary key (stock_exchange_id)
    );

    create table StockExchangePeriod (
        stock_exchange_id varchar(255) not null,
        interestRateBasisPointsDelta bigint,
        revenueRateDelta bigint,
        expenseRateDelta bigint,
        economicConditions varchar(255),
        startDate datetime,
        minimumEndDate datetime,
        closeDate datetime,
        open bit,
        generation bigint,
        stockExchange_stock_exchange_id varchar(255),
        primary key (stock_exchange_id)
    );

    create table TradeOrder (
        order_id varchar(255) not null,
        originalShareCount bigint,
        remainingShareCount bigint,
        offerPrice bigint,
        allowPartialOrder bit,
        dateCreated datetime,
        active bit,
        executed bit,
        dateExecuted datetime,
        orderType integer,
        company varchar(255),
        trader_id varchar(255),
        primary key (order_id)
    );

    create table TradeRecord (
        trade_record_id varchar(255) not null,
        shareCount bigint,
        sharePrice bigint,
        date datetime,
        seller_trader_id varchar(255),
        company_company_id varchar(255),
        buyer_trader_id varchar(255),
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

    create table TraderEvent (
        trader_id varchar(255) not null,
        eventType varchar(255),
        date datetime,
        shareCount bigint,
        amountTransferred bigint,
        startingCash bigint,
        endingCash bigint,
        company_company_id varchar(255),
        item_reputation_item_id varchar(255),
        trader_trader_id varchar(255),
        primary key (trader_id)
    );

    create table Trader_ReputationItem (
        Trader_trader_id varchar(255) not null,
        reputationItems_reputation_item_id varchar(255) not null,
        primary key (Trader_trader_id, reputationItems_reputation_item_id)
    );

    create table UserSession (
        status_id varchar(255) not null,
        date datetime,
        sessionId varchar(255),
        active bit,
        user_trader_id varchar(255),
        primary key (status_id)
    );

    alter table Company 
        add index FK9BDFD45D8370E02C (current_period), 
        add constraint FK9BDFD45D8370E02C 
        foreign key (current_period) 
        references CompanyPeriodReport (company_period_report_id);

    alter table Company 
        add index FK9BDFD45D912EEC1A (stock_exchange_id), 
        add constraint FK9BDFD45D912EEC1A 
        foreign key (stock_exchange_id) 
        references StockExchange (stock_exchange_id);

    alter table Company 
        add index FK9BDFD45D3AD658EE (previous_period), 
        add constraint FK9BDFD45D3AD658EE 
        foreign key (previous_period) 
        references CompanyPeriodReport (company_period_report_id);

    alter table CompanyPeriodReport 
        add index FKF12227522B724E8D (company_id), 
        add constraint FKF12227522B724E8D 
        foreign key (company_id) 
        references Company (company_id);

    alter table CompanyPeriodReport_PeriodEvent 
        add index FKADC63FCC4BF9C7DC (CompanyPeriodReport_company_period_report_id), 
        add constraint FKADC63FCC4BF9C7DC 
        foreign key (CompanyPeriodReport_company_period_report_id) 
        references CompanyPeriodReport (company_period_report_id);

    alter table CompanyPeriodReport_PeriodEvent 
        add index FKADC63FCC31DF3CC2 (periodEventList_period_event_id), 
        add constraint FKADC63FCC31DF3CC2 
        foreign key (periodEventList_period_event_id) 
        references PeriodEvent (period_event_id);

    alter table CompanyPeriodReport_PeriodPartRumour 
        add index FK816460634BF9C7DC (CompanyPeriodReport_company_period_report_id), 
        add constraint FK816460634BF9C7DC 
        foreign key (CompanyPeriodReport_company_period_report_id) 
        references CompanyPeriodReport (company_period_report_id);

    alter table CompanyPeriodReport_PeriodPartRumour 
        add index FK8164606332671068 (periodRumourList_period_rumour_id), 
        add constraint FK8164606332671068 
        foreign key (periodRumourList_period_rumour_id) 
        references PeriodPartRumour (period_rumour_id);

    alter table GameUser 
        add index FK9A67C6FDD1B4A736 (trader_trader_id), 
        add constraint FK9A67C6FDD1B4A736 
        foreign key (trader_trader_id) 
        references Trader (trader_id);

    alter table PeriodEvent 
        add index FK28C225392B724E8D (company_id), 
        add constraint FK28C225392B724E8D 
        foreign key (company_id) 
        references Company (company_id);

    alter table PeriodEvent 
        add index FK28C22539AF969615 (company_period_report), 
        add constraint FK28C22539AF969615 
        foreign key (company_period_report) 
        references CompanyPeriodReport (company_period_report_id);

    alter table PeriodPartRumour 
        add index FK2B0FD7162B724E8D (company_id), 
        add constraint FK2B0FD7162B724E8D 
        foreign key (company_id) 
        references Company (company_id);

    alter table PeriodPartRumour 
        add index FK2B0FD716AF969615 (company_period_report), 
        add constraint FK2B0FD716AF969615 
        foreign key (company_period_report) 
        references CompanyPeriodReport (company_period_report_id);

    alter table ReputationItem_ReputationEffect 
        add index FK83DFF81DEBECF1ED (ReputationItem_reputation_item_id), 
        add constraint FK83DFF81DEBECF1ED 
        foreign key (ReputationItem_reputation_item_id) 
        references ReputationItem (reputation_item_id);

    alter table ReputationItem_ReputationEffect 
        add index FK83DFF81D71D7F768 (effectList_reputation_item_id), 
        add constraint FK83DFF81D71D7F768 
        foreign key (effectList_reputation_item_id) 
        references ReputationEffect (reputation_item_id);

    alter table ShareParcel 
        add index FK611540A8D1B4A736 (trader_trader_id), 
        add constraint FK611540A8D1B4A736 
        foreign key (trader_trader_id) 
        references Trader (trader_id);

    alter table ShareParcel 
        add index FK611540A879C9A78F (company_company_id), 
        add constraint FK611540A879C9A78F 
        foreign key (company_company_id) 
        references Company (company_id);

    alter table StockExchange 
        add index FK4B98A3399F79C876 (currentPeriod_stock_exchange_id), 
        add constraint FK4B98A3399F79C876 
        foreign key (currentPeriod_stock_exchange_id) 
        references StockExchangePeriod (stock_exchange_id);

    alter table StockExchangePeriod 
        add index FK26DDF65A385AE894 (stockExchange_stock_exchange_id), 
        add constraint FK26DDF65A385AE894 
        foreign key (stockExchange_stock_exchange_id) 
        references StockExchange (stock_exchange_id);

    alter table TradeOrder 
        add index FKE5BBC06A96A002ED (company), 
        add constraint FKE5BBC06A96A002ED 
        foreign key (company) 
        references Company (company_id);

    alter table TradeOrder 
        add index FKE5BBC06AD9800AA7 (trader_id), 
        add constraint FKE5BBC06AD9800AA7 
        foreign key (trader_id) 
        references Trader (trader_id);

    alter table TradeRecord 
        add index FKD62356B533F8C31B (buyer_trader_id), 
        add constraint FKD62356B533F8C31B 
        foreign key (buyer_trader_id) 
        references Trader (trader_id);

    alter table TradeRecord 
        add index FKD62356B5889E41E7 (seller_trader_id), 
        add constraint FKD62356B5889E41E7 
        foreign key (seller_trader_id) 
        references Trader (trader_id);

    alter table TradeRecord 
        add index FKD62356B579C9A78F (company_company_id), 
        add constraint FKD62356B579C9A78F 
        foreign key (company_company_id) 
        references Company (company_id);

    alter table Trader 
        add index FK95CB27AEDE29A6B0 (user_trader_id), 
        add constraint FK95CB27AEDE29A6B0 
        foreign key (user_trader_id) 
        references GameUser (trader_id);

    alter table TraderEvent 
        add index FKB03F78CD1B4A736 (trader_trader_id), 
        add constraint FKB03F78CD1B4A736 
        foreign key (trader_trader_id) 
        references Trader (trader_id);

    alter table TraderEvent 
        add index FKB03F78C662D2E86 (item_reputation_item_id), 
        add constraint FKB03F78C662D2E86 
        foreign key (item_reputation_item_id) 
        references ReputationItem (reputation_item_id);

    alter table TraderEvent 
        add index FKB03F78C79C9A78F (company_company_id), 
        add constraint FKB03F78C79C9A78F 
        foreign key (company_company_id) 
        references Company (company_id);

    alter table Trader_ReputationItem 
        add index FKE1744FFD15E7CA32 (reputationItems_reputation_item_id), 
        add constraint FKE1744FFD15E7CA32 
        foreign key (reputationItems_reputation_item_id) 
        references ReputationItem (reputation_item_id);

    alter table Trader_ReputationItem 
        add index FKE1744FFDD1B4A736 (Trader_trader_id), 
        add constraint FKE1744FFDD1B4A736 
        foreign key (Trader_trader_id) 
        references Trader (trader_id);

    alter table UserSession 
        add index FKC7BC0C2BDE29A6B0 (user_trader_id), 
        add constraint FKC7BC0C2BDE29A6B0 
        foreign key (user_trader_id) 
        references GameUser (trader_id);
