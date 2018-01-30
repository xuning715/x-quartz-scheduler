package com.x.scheduler.client;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.x.scheduler.model.Request;
import com.x.framework.dao.BaseDao;

public class SqlClient extends BaseDao implements Client {
	@Override
	public Map<String, Object> request(Request request) throws Exception {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		RowMapper<Map<String, Object>> mapper = new RowMapper<Map<String, Object>>() {
			@Override
			public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
				Map<String, Object> map = new HashMap<String, Object>();
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					String columnName = rsmd.getColumnName(i).toUpperCase();
					Object columnValue = rs.getObject(i);
					if (columnValue != null) {
						map.put(columnName, columnValue);
					}
				}
				return map;
			}
		};
		List<Map<String, Object>> list = this.queryForList(request.getRequestUrl(), mapper);
		if (list != null && list.size() > 0) {
			jsonMap = list.get(0);
		}
		return jsonMap;
	}

}
