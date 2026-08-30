package com.crashdata.back.dao;

import com.crashdata.back.code.CodedEnum;

import java.sql.ResultSet;
import java.sql.SQLException;

final class Codes {
    static <E extends Enum<E> & CodedEnum> E of(ResultSet rs, String column, Class<E> type)
            throws SQLException {
        Short value = rs.getObject(column, Short.class);
        return value == null ? null : CodedEnum.fromCode(type, value);
    }
}
