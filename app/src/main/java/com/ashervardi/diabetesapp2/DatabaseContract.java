package com.ashervardi.diabetesapp2;

import android.provider.BaseColumns;

/**
 * Created by Asher on 1/2/2017.
 * This class defines the schema for the diabetes database.
 */

public class DatabaseContract {
    private DatabaseContract() {
    }

    /* Inner class that defines the table contents */
    public static class DiabetesTable implements BaseColumns {
        public static final String SUGAR_TABLE_NAME = "SugarLevelTable";
        public static final String COLUMN_NAME_MONTH = "Month";
        public static final String COLUMN_NAME_DAY = "day";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_SUGAR = "Sugar";
        public static final String COLUMN_NAME_CARBO = "carbo";
        public static final String COLUMN_NAME_BOLUS = "bolus";
        public static final String COLUMN_NAME_INSULIN = "insulin";
        public static final String COLUMN_NAME_COMMENT = "comment";

        public static final String INIT_TABLE_NAME = "InitTable";
        public static final String COLUMN_NAME_TYPE = "Type";
        public static final String COLUMN_NAME_FROM = "Start";
        public static final String COLUMN_NAME_TO = "Stop";
        public static final String COLUMN_NAME_VALUE = "Value";

        public static final String MISC_TABLE_NAME = "MiscTable";
        public static final String COLUMN_NAME_PATIENT = "Name";
        public static final String COLUMN_NAME_MAIL = "Email";

        public static final String TYPE_NAME_SUGAR_TARGET = "SUGARTARGET";
        public static final String TYPE_NAME_CORRECTION_FACTOR = "C_FACTOR";
        public static final String TYPE_NAME_CARBO_INSULIN_RATIO = "CI_RATIO";



    }
}

