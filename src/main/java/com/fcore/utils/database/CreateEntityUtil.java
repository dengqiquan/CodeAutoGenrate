package com.fcore.utils.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fcore.commons.Constans;
import com.fcore.entity.DbEntity;
import com.fcore.entity.FileEntity;
import com.fcore.entity.PojoEntity;
import com.fcore.entity.TableEntity;
import com.fcore.utils.StringUtil;
import com.fcore.utils.freemarker.FreemarkerUtil;
import com.fcore.utils.map.ObjectMapUtil;


/**   
* @Title: GenEntityMysqlUtil.java 
* @Package com.gogotown.utils 
* @Description: 通过表生成java类
* @author zhangkui
* @date 2016年03月08日 下午10:15:38
* @version V1.0   
*/
public class CreateEntityUtil {
	/**
	 * 连接数据库，查询表字段、类型、写到指定目录
	 * @throws Exception 
	 */
	private static TableEntity entityGenerate(String tablename,DbEntity dbEntity,FileEntity fileEntity) throws Exception{
    	//创建连接
    	Connection conn = null;
		//查要生成实体类的表
    	String sql = "select * from " + tablename;
    	TableEntity table = new TableEntity();
    	PreparedStatement pStemt = null;
    	try {
			Class.forName(dbEntity.getDriver());
    		conn = DriverManager.getConnection(dbEntity.getUrl(),dbEntity.getUser(),dbEntity.getPassword());
			pStemt = conn.prepareStatement(sql);
			ResultSetMetaData rsmd = pStemt.getMetaData();
			int size = rsmd.getColumnCount();	//统计列
			//表备注
			String table_description = "";
			//mysql获取表备注
			if(dbEntity.getDriver().equals("com.mysql.jdbc.Driver")){
				table_description = MySqlTabComUtil.getTableCommentByName(tablename,dbEntity);
			}
			//表字段注释
			Map<String, String> columnComments = MySqlColComUtil.getTableColumnComments(tablename,dbEntity);
			String [] colnames = new String[]{};
			String [] colTypes = new String[]{};
			Integer [] colSizes = new Integer[]{};
			String [] remaks = new String[]{};
			String [] fieldNames = new String[]{};
			String [] filedTypes = new String[]{};
			String [] table_filelds = new String[]{};
			boolean f_util = false;
			boolean f_sql = false;
			boolean f_math = false;
			List<String> colnameslist = new ArrayList<String>();
			List<String> colTypeslist = new ArrayList<String>();
			List<Integer> colSizeslist = new ArrayList<Integer>();
			List<String> remakslist = new ArrayList<String>();
			List<String> fieldNameslist = new ArrayList<String>();
			List<String> filedTypeslist = new ArrayList<String>();
			List<String> table_fileldslist = new ArrayList<String>();
			//过滤字段类型
			for (int i = 0; i < size; i++) {
				colnameslist.add(rsmd.getColumnName(i + 1));
				colTypeslist.add(rsmd.getColumnTypeName(i + 1));
				colSizeslist.add(rsmd.getColumnDisplaySize(i + 1));
				String remark = columnComments.get(rsmd.getColumnName(i + 1));
				if(remark == null) remark = "";
				remakslist.add(remark);
				fieldNameslist.add(first2little(initcap(colnameslist.get(i))));
				table_fileldslist.add(colnameslist.get(i));
				filedTypeslist.add(sqlType2JavaType(colTypeslist.get(i)));
				
				
				//System.out.println(colnameslist.get(i) + " "+ colTypeslist.get(i)+" "+remakslist.get(i) + " "+colSizeslist.get(i));
				if(filedTypeslist.get(i).equalsIgnoreCase("Date")){
					f_util = true;
				}
				if(colTypeslist.get(i).equalsIgnoreCase("image") || colTypeslist.get(i).equalsIgnoreCase("text")){
					f_sql = true;
				}
				if(filedTypeslist.get(i).equalsIgnoreCase("BigDecimal")){
					f_math = true;
				}
				if(colSizeslist.get(i) >= 11 && "Integer".equalsIgnoreCase(filedTypeslist.get(i))){
					colTypeslist.set(i, "bigint");
				}
				filedTypeslist.set(i, sqlType2JavaType(colTypeslist.get(i)));
			}
			//去除排除字段
			String[] excludeArr = Constans.EXCLUDE_FIELD_ARR;
			
			if(null != excludeArr && excludeArr.length > 0){
				for (String exc : excludeArr) {
					for(int i = 0; i< fieldNameslist.size(); i++){
						if(fieldNameslist.get(i).equals(exc)){
							colnameslist.remove(i);
							colTypeslist.remove(i);
							colSizeslist.remove(i);
							remakslist.remove(i);
							fieldNameslist.remove(i);
							filedTypeslist.remove(i);
							table_fileldslist.remove(i);
						}
					}
				}
			}
			//list 2 array
			colnames = colnameslist.toArray(colnames);
			colTypes = colTypeslist.toArray(colTypes);
			colSizes = colSizeslist.toArray(colSizes);
			remaks = remakslist.toArray(remaks);
			fieldNames = fieldNameslist.toArray(fieldNames);
			filedTypes = filedTypeslist.toArray(filedTypes);
			table_filelds = table_fileldslist.toArray(table_filelds);
			
			table.setColSizes(colSizes);
			table.setF_math(f_math);
			table.setF_sql(f_sql);
			table.setF_util(f_util);
			table.setRemaks(remaks);
			table.setTable_description(table_description);
			table.setColnames(colnames);
			table.setColTypes(colTypes);
			table.setFieldNames(fieldNames);
			table.setFiledTypes(filedTypes);
			table.setTablename(tablename);
			table.setEntityName(initcap(tablename));
			table.setTable_filelds(table_filelds);
			//主键
			String primary_colmun = columnComments.get(Constans.PRIMARY_COLUMN_TAB);
			table.setPrimary_colmun1(primary_colmun);
			primary_colmun = first2little(initcap(primary_colmun));
			table.setPrimary_colmun(primary_colmun);
			String projectPath = fileEntity.getProjectPath();
				projectPath = projectPath + ((projectPath.endsWith("/") || projectPath.endsWith("\\")) ? "java" : "/java");
				String document = StringUtil.getFilePath(projectPath, fileEntity.getBasePackage(), Constans.TYPE_MODEL);
				String entityName = initcap(tablename);
				PojoEntity pojoEntity = new PojoEntity(fileEntity.getBasePackage(), fileEntity.getAuthorName(),table);
				Map<String, Object> datamap = ObjectMapUtil.obj2Map(pojoEntity);
				FreemarkerUtil.analysisTemplate(Constans.TEMPLATE_ENTITY, document, entityName + ".java", datamap,fileEntity);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				if(null != conn)
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    	return table;
	}
	
	/**
	 * 功能：将输入字符串的首字母改成大写
	 * @param str 表字段
	 * @return
	 */
	public static String initcap(String str) {
		StringBuilder sb = new StringBuilder();
		if(str.indexOf("_") > 0){
			String strArr[] = str.split("_");
			for (String string : strArr) {
				char[] ch = string.toCharArray();
				if(ch[0] >= 'a' && ch[0] <= 'z'){
					ch[0] = (char)(ch[0] - 32);
				}
				sb.append(new String(ch));
			}
		}else{
			char[] ch = str.toCharArray();
			if(ch[0] >= 'a' && ch[0] <= 'z'){
				ch[0] = (char)(ch[0] - 32);
			}
			sb.append(new String(ch));
		}
		return sb.toString();
	}
	
	/**
	 * 当首字母大写的时候，将首字母转化为小写
	 * @param str关键字
	 * @return
	 */
	public static String first2little(String str){
		char ch[]  = str.toCharArray();
		if(ch[0] >= 'A' && ch[0] <= 'Z'){
			ch[0] = (char)(ch[0] + 32);
			return new String(ch);
		}else{
			return str;
		}
	}

	/**
	 * 功能：获得列的数据类型
	 * @param sqlType 数据库中字段的类型
	 * @return
	 */
	private static String sqlType2JavaType(String sqlType) {
		if(sqlType.equalsIgnoreCase("bit")){
			return "boolean";
		}else if(sqlType.equalsIgnoreCase("tinyint")){
			return "Integer";
		}else if(sqlType.equalsIgnoreCase("smallint")){
			return "Integer";
		}else if(sqlType.equalsIgnoreCase("int")){
			return "Integer";
		}else if(sqlType.equalsIgnoreCase("bigint")){
			return "Long";
		}else if(sqlType.equalsIgnoreCase("float")){
			return "Float";
		}else if(sqlType.equalsIgnoreCase("decimal") || sqlType.equalsIgnoreCase("numeric") 
				|| sqlType.equalsIgnoreCase("real") || sqlType.equalsIgnoreCase("money") 
				|| sqlType.equalsIgnoreCase("smallmoney")){
			return "Double";
		}else if(sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char") 
				|| sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nchar") 
				|| sqlType.equalsIgnoreCase("text")){
			return "String";
		}else if(sqlType.equalsIgnoreCase("datetime")){
			return "Date";
		}else if(sqlType.equalsIgnoreCase("image")){
			return "Blod";
		}else if(sqlType.equalsIgnoreCase("timestamp")){
			return "Date";
		}else if(sqlType.equalsIgnoreCase("int unsigned")){
			return "Integer";
		}else if(sqlType.equalsIgnoreCase("tinyint unsigned")){
			return "Integer";
		}else if(sqlType.equalsIgnoreCase("mediumint unsigned")){
			return "Integer";
		}else if(sqlType.equalsIgnoreCase("date")){
			return "Date";
		}else if(sqlType.equalsIgnoreCase("smallint unsigned")){
			return "Integer";
		}else if(sqlType.equalsIgnoreCase("mediumint")){
			return "Integer";
		}else if(sqlType.equalsIgnoreCase("DECIMAL UNSIGNED")){
			return "BigDecimal";
		}else if(sqlType.equalsIgnoreCase("bpchar")){
			return "String";
		}else if(sqlType.equals("timestamptz")){
			return "Date";
		}else if(sqlType.equals("int2")){
			return "Integer";
		}
		return null;
	}
	
	/** 
	* @author zhangkui
	* @Title: generateEntityRun 
	* @Description: 通过表生成实体类方法入口
	* @param projectPath 代码生成到的位置 com的上一层
    * @param packagePath 指定实体生成所在包的路径
    * @param authorName 作者名字
    * @param tableName 表名
    * @param isRecover 是否覆盖已经生成的代码 true 覆盖   false 不覆盖
	* @return void    返回类型 
	*/
	public static TableEntity generateEntityRun(String tablename,DbEntity dbEntity,FileEntity file){
		TableEntity tableEntity = null;
		try {
			tableEntity = entityGenerate(tablename, dbEntity, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return tableEntity;
	}
	
	public static void main(String[] args) {
		DbEntity dbConstans = new DbEntity("com.mysql.jdbc.Driver", "jdbc:mysql://192.168.0.155:3306/lisendb", "root", "root");
		String tableName = "admin";
		FileEntity file = new FileEntity("E:/testpage/","com.lisen.entity","zhangkui",true);
		CreateEntityUtil.generateEntityRun(tableName,dbConstans,file);
		/*if(table != null){
			String[] filedArr = table.getFieldNames();
			StringBuilder sb = createToString(tableName, filedArr,table);
			System.out.println(sb);
		}*/
	}
}
