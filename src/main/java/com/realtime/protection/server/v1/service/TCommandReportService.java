package com.realtime.protection.server.v1.service;

import com.realtime.protection.server.v1.entity.TCommandReport;
import com.realtime.protection.server.v1.entity.TCommandTask;

import java.util.List;

public interface TCommandReportService {

    Integer infoUpdate(TCommandReport tCommandReport);

    TCommandReport queryInfo(Long id);

    Integer infoAdd(TCommandReport tCommandReport);

}
