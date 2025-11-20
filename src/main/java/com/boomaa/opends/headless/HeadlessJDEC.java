package com.boomaa.opends.headless;

import com.boomaa.opends.data.StatsFields;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.RobotMode;

public interface HeadlessJDEC {
    ConsoleTable STATUS_TABLE = new ConsoleTable(7, 2);
    ConsoleTable STATISTICS_TABLE = new ConsoleTable(StatsFields.values().length + 1, 2);
    OptionTable MAIN_ACTIONS_TABLE = new OptionTable(MainHAction.values().length + 1, true, false);
    OptionTable JS_ACTIONS_TABLE = new OptionTable(18, true, false); //TODO
    ConsoleTable SHUFFLEBOARD_TABLE = new ConsoleTable(7, 2); //TODO

    OptionTable CHANGE_MODE_TABLE = new OptionTable(RobotMode.values().length + 2, false, false);
    OptionTable CHANGE_ANUM_TABLE = new OptionTable(MainJDEC.ALLIANCE_NUM.getItemCount() + 2, false, false);
    OptionTable CHANGE_ACOLOR_TABLE = new OptionTable(MainJDEC.ALLIANCE_COLOR.getItemCount() + 2, false, false);
}
