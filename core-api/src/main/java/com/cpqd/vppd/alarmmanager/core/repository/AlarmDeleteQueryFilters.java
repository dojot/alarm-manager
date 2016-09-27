package com.cpqd.vppd.alarmmanager.core.repository;

import com.cpqd.vppd.alarmmanager.core.model.AlarmQueryType;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSortOrder;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Date;
import java.util.List;

/**
 * Class which holds parameter values to be used to filter current alarm deletion queries.
 */
public class AlarmDeleteQueryFilters {
    private final String instance_id;
    private final String module_name;

    public AlarmDeleteQueryFilters(String instance_id,
                                   String module_name
                                   ) {
        this.instance_id = instance_id;
        this.module_name = module_name;
    }

    public String getInstance_id() {
        return instance_id;
    }

    public String getModule_name() {
        return module_name;
    }

}