package com.agimatec.dbmigrate.action;

import com.agimatec.dbmigrate.AutoMigrationTool;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 01.12.2008 <br/>
 * Time: 10:26:26 <br/>
 * Copyright: Agimatec GmbH
 */
public class OperationAction extends MigrateAction {
    private String operation;
    private String parameter;

    public OperationAction(AutoMigrationTool tool, String operationCmd, String operationParam) {
        super(tool);
        this.operation = operationCmd;
        this.parameter = operationParam;
    }

    public void doIt() throws Exception {
        tool.doMethodOperation(operation, parameter);
    }
}
